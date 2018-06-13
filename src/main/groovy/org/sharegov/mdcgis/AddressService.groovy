/*******************************************************************************
 * Copyright 2014 Miami-Dade County
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.sharegov.mdcgis

import groovy.json.JsonBuilder
import org.sharegov.mdcgis.model.Address
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable

class AddressService {

	private static Logger _log = LoggerFactory.getLogger(AddressService.class);
	CandidateService candidateService
	PropertyInfoService propertyInfoService
	CommonLocationsService commonLocationsService
	GisService gisService
	FeatureService featureService
	CacheManager cacheManager
	GisConfig gisConfig

	/**
	 * get X and Y coordinates for given address. It will return EITHER X, Y coordinates OR null.
	 *
	 * @param street
	 * @param zip
	 * @param municipalityId
     * @return candidate == 1 returns candidate's X and Y coordinates, if the candidate > 1 returns null.
     */
	@Cacheable(value="xy-coordinates-for-address")
	Map getXYCoordinatesForAddress(String street, String zip, Integer municipalityId = null){

		// Get candidates
		List candidates = candidateService.getCandidates(street, zip, municipalityId)

		Map candidateXY = [:]

		// One candidate. Build full candidate address.
		if(candidates.size() == 1){
			candidates.each {candidate ->
				candidateXY << candidate.get("location")
			}
		} else if(candidates.size() > 1){
			//More than 1 candidates. So return null
			return null;
		}

		return candidateXY;
	}


	/**
	 * Using the CandidateService get a fully populated Address Object
	 * @param street - represents the street string to look for (ie:111 nw 1st st)
	 * @param zip - ie:33128
	 * @return - a fully populated address object, if there is a single match for
	 * the - street, zip -. Return null if more than one candidate is found
	 */
	Address getAddress(String street, String zip){

		// Get candidates
		List candidates = candidateService.getCandidates(street, zip)

		// too many candidates or no candidate. Return a null address.
		if(candidates.size() != 1){
			_log.debug("Too many candidates or no candidates found: ${candidates?.size()}. No address will be return!.")
			return null
		}

		// build the address by parsing and hitting
		// all necessary layers.
		buildAddressFromCandidate(candidates[0])

	}

	Address getCondoAddress(String street, String zip, String unit, Integer municipalityId = null){

		// Get candidates
		List candidates = candidateService.getCandidates(street, zip, municipalityId)

		// too many candidates. Return a null address.
		if(candidates.size() != 1){
			_log.debug("Too many candidates or no candidates found: ${candidates?.size()}. No address will be return!.")
			return null
		}

		// build the address by parsing and hitting
		// all necessary layers.
		buildCondoAddressFromCandidate(candidates[0], unit)

	}

	/**
	 * Using the CandidateService get a partially populated Address Object
	 * @param street - represents the street string to look for (ie:111 nw 1st st)
	 * @param zip - ie:33128
	 * @return - a partially populated address object, if there is a single match for
	 * the - street, zip -. Return null if more than one candidate is found
	 */
	Address getPartialAddress(String street, String zip){

		// Get candidates
		List candidates = candidateService.getCandidates(street, zip)

		// too many candidates or no candidate. Return a null address.
		if(candidates.size() != 1){
			_log.debug("Too many candidates or no candidates found: ${candidates?.size()}. No address will be return!.")
			return null
		}

		// build the address by parsing and hitting
		// all necessary layers.
		buildPartialAddressFromCandidate(candidates[0])

	}

	/**
	 * Obtains a fully populated Address Object for the given coordinates (xCoord, yCoord)	 
	 * It follows the following logic:
	 // The coords are in a property
	 // There is street information attached to the property. Build address by street, zip info
	 // There is NO street information attached to the property.
	 // Calculate Approx Address. (by reverse geocoding)
	 // Add the rest of the data including property information data.
	 // The coords are NOT in a property
	 // Calculate Approx Address. (by reverse geocoding)
	 // Add the rest of the data excluding property information data.
	 * @param xCoord - the x coordinate
	 * @param yCoord - the y coordinate
	 * @return Address - a fully populated address object. Returns null if no address is found.
	 */
	Address getAddressFromCoord(Double xCoord, Double yCoord){

		Address address = null
		Map streetData = propertyInfoService.getStreetZipByCoord(xCoord, yCoord)

		//coords in parcel
		if(streetData){
			// parcel has a street
			if(streetData.street)
				address = getAddress(streetData.street, streetData.zip as String)
			// parcel does not have a street. Recenter the x y to the center of the parcel
			else
				address = buildAddressFromCoord(streetData.x, streetData.y)
		}
		// coords not in parcel
		else
			address = buildAddressFromCoord(xCoord, yCoord)

		return address
	}

	/**
	 * Get an address object for the given folio, including property information.
	 * It deals with folio of parent property as well as condos. (A parent property can either be 
	 * a 'house' or a building. The building has its own folio.
	 * @param folio
	 * @return Address - the address that corresponds to property with folio 'folio'. It returns
	 * null if no such property is found.
	 */
	Address getAddressByFolio(String folio){

		// Get street and zip from a property layer
		Map data = propertyInfoService.getStreetZipUnitByFolio(folio)

		// no folio -> no address. Return null.
		if (!data) return null

		// Get a fully populated address for the found address, zip
		Address address = null
		if (data.unit)
			address = getCondoAddress(data.street, data.zip as String, data.unit)
		else if(data.street)
			address = getAddress(data.street, data.zip as String)
		else {
			Map coords = propertyInfoService.folio2Coord(folio)
			address = getAddressFromCoord(coords.x, coords.y)
		}

		return address

	}

	/**
	 * Produces a list of address candidates with address, zip. If only one
	 * candidate is match a fully populated address object is returned. If
	 * several candidates are found, the address objecs are only populated partially.
	 *
	 * @param street
	 * @param zip
	 * @param municipalityId
	 * @param districtNumber
	 * @return List<Address> - List of candidate address object. Empty list if no candidates are found.
	 */
	@Cacheable(value="candidates")
	List getCandidateAddresses (String street, String zip, Integer municipalityId = null, Integer districtNumber = null){

		// Get candidates
		List candidates = candidateService.getCandidates(street, zip, municipalityId)

		List<Address> candidateAddresses = []

		// One candidate. Build full candidate address.
		if(candidates.size() == 1){
			candidates.each {candidate ->
				candidateAddresses << buildAddressFromCandidate(candidate)
			}
		} else if(candidates.size() > 1) {
			// Several candidates. Build minimal candidate address.
			candidates.each { candidate ->
				candidateAddresses << buildPartialAddressFromCandidate(candidate, districtNumber != null)
			}
		}

		//filter by district number
		if(districtNumber != null){
			candidateAddresses = candidateAddresses.findAll{ candidate -> candidate.districtNumber == districtNumber }
		}

		return candidateAddresses
	}

	/**
	 * Produces a list of address candidates with address, zip. The address
	 * objecs are only populated partially regardless of the amount of candidates found.
	 * @param address
	 * @param zip
	 * @return List<Address> - List of candidate address objects. 
	 * Empty list if no candidates are found.
	 */
	List getPartialCandidateAddresses (String street, String zip){
		// Get candidates
		List candidates = candidateService.getCandidates(street, zip)

		List<Address> candidateAddresses = []

		// Build minimal candidate address, regardless of how many candidates are found.
		candidates.each {candidate ->
			candidateAddresses << buildPartialAddressFromCandidate(candidate)
		}

		return candidateAddresses
	}

	/**
	 * It maps from candidate raw data to Address Object and
	 * makes to other layers as needed to populate the necessary fields.
	 * Besides raw candidate data, it populates the municipality and Commission District
	 *
	 * @param candidate
	 * @param addCommissionDistrictData
	 * @return
	 */
	private Address buildPartialAddressFromCandidate(def candidate, Boolean addCommissionDistrictData = false){

		Address addr = new Address()
		addr.with{
			address = candidate.address
			score = candidate.score
			addressType = candidate.attributes.Addr_type
			locatorName = candidate.attributes.Loc_name
			parsedAddress = buildParsedAddress(candidate)
			location = candidate.location
			municipalityId = candidate.municipalityId
			municipality = gisConfig.municipalities[municipalityId]
		}

		//Get Commission District Data
		if(addCommissionDistrictData) {
			Map commissionDistrictData = featureService.featuresAttributesFromPointLayersIntersection(candidate.location.x as String, candidate.location.y as String, ['MDC.CommissionDistrict'])
			populateAddressFields(addr, commissionDistrictData)
		}

		return addr
	}

	/**
	 * given candidate data coming from a locator builds an address with all its data.
	 * That is
	 * 1. parsed Address.
	 * 2. location
	 * 3. data coming from different layers as specified by EsriFieldMappings.addressAttributes.
	 * 4. propertyInfo coming from the parcel layer.
	 *
	 * @param candidate
	 * @return
	 */
	private Address buildAddressFromCandidate(def candidate){
		
		Address addr = buildPartialAddressFromCandidate(candidate)

		List layers = []

		// Get the layers that need to be queried.
		if(addr.municipality == 'MIAMI')
			layers = gisConfig.cityOfMiamiAddressLayers
		else
			layers = gisConfig.addressLayers

		// Query layers for data.
		Map dataFromLayers = featureService.featuresAttributesFromPointLayersIntersection(candidate.location.x as String, candidate.location.y as String, layers)
		
		// Map data from layers to fields in the address
		populateAddressFields(addr, translateCodes(dataFromLayers))
		
		// Populate derived fields
		populateDerivedAddressFields(addr);

		// Get polling location
		addr.pollingLocation = commonLocationsService.getPollingLocationByPrecinct(addr.electionsPrecinctId as String)
		
		// Map property Info but only for geoAddress locators. geoStreet locators do not have a property.
		if (addr.locatorName == 'GeoAddress'){
			Map propertyInfo = propertyInfoService.getPropertyInfo(candidate.location.x, candidate.location.y)
			addr.propertyInfo = propertyInfo 
		}
		// Get the raw street maintenance and populate address with it
		_log.info("MAINTENANCE IDS are ${candidate.streetMaintenanceIds}");	
		Map dataFromStreetMaintenance = featureService.streetMaintenanceFeaturesByIds(candidate.streetMaintenanceIds)
		_log.info("DATA FROM STREET MAINTENANCE ${dataFromStreetMaintenance}");
		populateStreetMaintenance(addr, translateCodes(dataFromStreetMaintenance))

		// Get the raw light maintenance and populate address with it
		Map dataFromLightMaintenance = featureService.lightMaintenanceFeaturesByProximity(candidate.location.x, candidate.location.y, 100)
		populateLightMaintenance(addr, translateCodes(dataFromLightMaintenance))

		// Cache the data
		dataFromLayers['MDC.StreetMaint'] =  dataFromStreetMaintenance['MDC.StreetMaint']
		dataFromLayers['pwd_lights'] =  dataFromLightMaintenance['pwd_lights']
		dataFromLayers['address'] = addr
		
		Cache cache = cacheManager.getCache('layers')
		cache.put([
			candidate.location.x,
			candidate.location.y
		], dataFromLayers)

		return addr

	}


	/**
	 * This method is to be used for the case in which we want to build address object
	 * without using a Candidate. This method IS ONLY TO BE USED in cases where the x, y
	 * don't have a street associated with it. Either because it is not a property or the 
	 * property doesn't have a street associated to it  
	 * @param xCoord
	 * @param yCoord
	 * @return Adress - populated address using approx address. Returns null if no address close
	 * to the (xCoord, yCoord) exists.
	 */
	private Address buildAddressFromCoord(Double xCoord, Double yCoord){
		Address addr = new Address()

		// get approx address
		Map streetData = gisService.reverseGeoCode(xCoord, yCoord, gisConfig.locators.reverseGeoCodeGeoStreet, 100)
		if(!streetData) return null

		addr.address =  "${streetData.street}, ${streetData.zip}"
		addr.addressApproximation = true
		
		// populate x, y
		addr.location.x = org.sharegov.mdcgis.Utils.round(xCoord, 3)
		addr.location.y = org.sharegov.mdcgis.Utils.round(yCoord, 3)

		// Get Municipality and zip Data
		List layers = [
			'MDC.Municipality_poly',
			'MDC.ZipCode'
		]
		
		// Query layers for data.
		Map dataFromLayers = featureService.featuresAttributesFromPointLayersIntersection(addr.location.x as String, addr.location.y as String, layers)
		
		// Map data from layers to fields in the address
		populateAddressFields(addr, translateCodes(dataFromLayers))
		
		// Populate municipality Id
		addr.municipalityId = gisConfig.municipalities.getKey(addr.municipality)
		
		// Get polling location
		addr.pollingLocation = commonLocationsService.getPollingLocationByPrecinct(addr.electionsPrecinctId as String)
		
		// Populate zio
		addr.parsedAddress = candidateService.parseAddress(addr.address)
		//addr.parsedAddress.zip = dataFromLayers['MDC.ZipCode']['ZIPCODE']
		//addr.parsedAddress.zip = dataFromLayers['MDC.ZipCode']?.ZIPCODE
		addr.parsedAddress.zip = dataFromLayers['MDC.ZipCode']?.ZIPCODE ?: streetData.zip

		layers = []

		if(addr.municipality == 'MIAMI')
			layers = gisConfig.cityOfMiamiAddressLayers
		else
			layers = gisConfig.addressLayers

		dataFromLayers = featureService.featuresAttributesFromPointLayersIntersection(addr.location.x as String, addr.location.y as String, layers)

		// Map data from layers to fields in the address
		populateAddressFields(addr, translateCodes(dataFromLayers))

		// Populate derived fields
		populateDerivedAddressFields(addr)

		// Get property info.
		Map propertyInfo = propertyInfoService.getPropertyInfo(addr.location.x, addr.location.y)
		addr.propertyInfo = propertyInfo

		// Get street maintenance and light maintenance and populate address with it
		Map dataFromStreetMaintenance = [:]
		Map dataFromLightMaintenance = [:]
		if(addr.propertyInfo){ // This x, y is in a property may be far from a street
			dataFromStreetMaintenance = featureService.streetMaintenanceFeaturesByProximity(addr.location.x, addr.location.y, 100)
			dataFromLightMaintenance = featureService.lightMaintenanceFeaturesByProximity(addr.location.x, addr.location.y, 100)
		}
		else{ // this x, y is in a street. The street polygon should be close
			dataFromStreetMaintenance = featureService.streetMaintenanceFeaturesByProximity(addr.location.x, addr.location.y, 20)
			dataFromLightMaintenance = featureService.lightMaintenanceFeaturesByProximity(addr.location.x, addr.location.y, 20)
		}	
		populateStreetMaintenance(addr, translateCodes(dataFromStreetMaintenance))
		populateLightMaintenance(addr, translateCodes(dataFromLightMaintenance))
		
		// Cache the data
		dataFromLayers['MDC.StreetMaint'] =  dataFromStreetMaintenance['MDC.StreetMaint']
		dataFromLayers['pwd_lights'] =  dataFromLightMaintenance['pwd_lights']
		dataFromLayers['address'] = addr

		Cache cache = cacheManager.getCache('layers')
		cache.put([
			addr.location.x,
			addr.location.y
		], dataFromLayers)
			
		return addr

	}



	private Address buildCondoAddressFromCandidate(def candidate, String unit){
		candidate.attributes.unit = unit;
		Address addr = buildPartialAddressFromCandidate(candidate)

		String folio = propertyInfoService.getCondoFolio(addr.address.tokenize(",")[0].trim(), addr.address.tokenize(",")[1].trim(), unit)
		if(!folio) return null

		List layers = []

		if(addr.municipality == 'MIAMI')
			layers = gisConfig.cityOfMiamiAddressLayers
		else
			layers = gisConfig.addressLayers

		// Map data from layers to fields in the address
		Map dataFromLayers = featureService.featuresAttributesFromPointLayersIntersection(candidate.location.x as String, candidate.location.y as String, layers)
		populateAddressFields(addr, translateCodes(dataFromLayers))
		
		// Populate derived fields
		populateDerivedAddressFields(addr);

		// Get polling location
		addr.pollingLocation = commonLocationsService.getPollingLocationByPrecinct(addr.electionsPrecinctId as String)

		// Get property info for condo
		Map data = [xCoord:addr.location.x, yCoord:addr.location.y, street:addr.address.tokenize(",")[0].trim(), zip:addr.address.tokenize(",")[1].trim(),unit:unit]
		addr.propertyInfo = propertyInfoService.getCondoPropertyInfo(data)

		// Get the raw street maintenance and populate address with it
		Map dataFromStreetMaintenance = featureService.streetMaintenanceFeaturesByIds(candidate.streetMaintenanceIds)
		populateStreetMaintenance(addr, translateCodes(dataFromStreetMaintenance))

		// Get the raw light maintenance and populate address with it
		Map dataFromLightMaintenance = featureService.lightMaintenanceFeaturesByProximity(candidate.location.x, candidate.location.y, 100)
		populateLightMaintenance(addr, translateCodes(dataFromLightMaintenance))

		// Cache the data
		dataFromLayers['MDC.StreetMaint'] = dataFromStreetMaintenance['MDC.StreetMaint']
		dataFromLayers['pwd_lights'] =  dataFromLightMaintenance['pwd_lights']
		dataFromLayers['address'] = addr
		
		Cache cache = cacheManager.getCache('layers')
		cache.put([
			candidate.location.x,
			candidate.location.y
		], dataFromLayers)

		return addr
	}


	private Map buildParsedAddress(def candidate) {

		
		// build the parsedAddress
		Map attributes = candidateService.candidateAttributes
		Map parsedAddress = [:]
		attributes.each {key, value ->
			if(candidate.attributes?.containsKey(key))
				parsedAddress << [(value):candidate.attributes[key]]
		}

		// in case of a candidate coming from a GeoStreet, populate the House field
		if (candidate.attributes?.Loc_name == 'GeoStreet' && ['Address','StreetAddress'].contains(candidate.attributes?.Addr_type)){
			def tmpParsed = candidate.address.split()
			parsedAddress['House'] = tmpParsed[0]
		}
		
		// in case of intersections populate the zip with zip1 value
		if(!parsedAddress['zip'] && parsedAddress.containsKey('zip1'))
			parsedAddress['zip'] = parsedAddress['zip1']


		return parsedAddress

	}

	private void populateAddressFields(Address address, Map dataFromLayers){
		
		Map addressAttributes = [:]
		if(address.municipality == 'MIAMI')
			addressAttributes = EsriFieldMappings.cityOfMiamiAddressAttributes
		else
			addressAttributes = EsriFieldMappings.addressAttributes

		addressAttributes.each{ layer,attributes ->
			Map data = dataFromLayers[layer]
			
			attributes.each {key, value ->
				if(data?.containsKey(key))
					address[value] = data[key]
			}
		}
	}
	
	private void populateDerivedAddressFields(Address address){
		
		if(address.municipality == 'MIAMI') {
			address.recyclingCalendarUrl = gisConfig?.cityOfMiamiRecyclingCalendarUrls[address?.recyclingRoute]
		}
		else{
			def calendarId = address?.recyclingRoute?.substring(1,3)
			address.recyclingCalendarUrl = gisConfig?.countyRecyclingCalendarUrls[calendarId] 
		}
		  					
	}

	

	/**
	 * translateCodes will "translate" codes of attributes in a layer
	 * to friendlier values, lets call them aliases.
	 * It does not overwrite the old value with the new alias value. Instead it
	 * creates a new entry where the key is the old key followed by "ALIAS". 
	 * ie: [key:value] will become [keyALIAS:aliasvalue] 	
	 * @param data
	 * [layer1:[[attr1:'h', attr2:'h', attr3:'c'],[attr1:'g', attr2:'h', attr3:'d']],
	 *	layer2:[attr3:'cc', attr4:'dd'],
	 *	layer3:[attr1:'a', attr2:'b']]
	 *	
	 *	The values for layer can be a map or list of maps,
	 * @return Map - it is of the form
	 * [layer1:[[attr1:'h', attr1ALIAS:'hello', attr2:'h', attr3:'c'],
	 *  			[attr1ALIAS:'goodbye', attr1:'g', attr2:'h', attr3ALIAS:'dog', attr3:'d']],
	 *	layer2:[attr3:'cc', attr4:'dd'],
	 *  layer3:[attr1:'a', attr2:'b']]
	 *  
	 */
	private Map translateCodes(Map data) {
		Map translator = gisConfig.codeTranslator
		translator.inject(data.clone()) {acc, layer, codeTranslator ->
			if(data[layer])
				acc << [(layer):translateCodes(data[layer], codeTranslator)]
			else acc
		}

	}

	private List translateCodes(List dataToBeTranslated, Map codeTranslator) {
		dataToBeTranslated.collect {translateCodes(it, codeTranslator)}
	}
		
	private Map translateCodes(Map dataToBeTranslated, Map codeTranslator) {

		dataToBeTranslated.inject([:]) {acc, key, value ->
			if(codeTranslator[key])
			  if(codeTranslator[key][value]){
				String aliasKey = "${key}ALIAS"
				acc << [(aliasKey):codeTranslator[key][value]]
			}
			  
			acc << [(key):value]
		}
	}
	


	
	
	private void populateStreetMaintenance(Address addr, Map dataFromStreetMaintenance){

		Map attributeMapping = EsriFieldMappings.closestFeatureClassesAddressAttributes['MDC.StreetMaint']
			
		dataFromStreetMaintenance['MDC.StreetMaint'].each {rawFeature ->
			Map feature = [:]
			attributeMapping.each {key, value ->
				//feature[value] = EsriFieldMappings.streetsMaintenanceCodesTranslations[key]?.(rawFeature[key])?:rawFeature[key]
				feature[value] = rawFeature[key]
			}

			addr.streetsMaintenance << feature
		}
	}

	private void populateLightMaintenance(Address addr, Map dataFromLightMaintenance){

		Map attributeMapping = EsriFieldMappings.closestFeatureClassesAddressAttributes['pwd_lights']

		dataFromLightMaintenance['pwd_lights'].each {rawFeature ->
			Map feature = [:]
			attributeMapping.each {key, value ->
				//feature[value] = EsriFieldMappings.streetsLightMaintenanceCodesTranslations[key]?.(rawFeature[key])?:rawFeature[key]
				feature[value] = rawFeature[key]
			}

			addr.streetsLightMaintenance << feature
		}
	}


}
