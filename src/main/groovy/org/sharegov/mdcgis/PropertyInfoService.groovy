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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PropertyInfoService {

	private static Logger _log = LoggerFactory.getLogger(PropertyInfoService.class);

	HTTPService httpService
	FeatureService featureService
	GisConfig gisConfig

	enum PropertyType {
		MULTI, CONDO, SINGLE, UNDEFINED
	}

	/**
	 * Get Property information by Folio.
	 *
	 * @param folio
	 * @return gets folio or returns null.
	 */
	Map getPropertyInfo(String folio){

		List layers = []

		// Get data from PaGIS
		Map folioInfoByPaGIS = getCleanPropertyInfoByFolio(folio)

		//No property, return null.
		if(folioInfoByPaGIS == null || folioInfoByPaGIS.isEmpty()){
			return null
		}

		// Get data from PaParcel and HomeOwnerAssociation by intersecting with xCoord, yCoord
		EsriFieldMappings.propertyInfoAttributes.each{key,value -> layers << key}
		Map dataFromLayers = featureService.featuresFromPointLayersIntersection(folioInfoByPaGIS?.X_COORD as String, folioInfoByPaGIS?.Y_COORD as String, layers)

		dataFromLayers["MDC.PaGIS"] = folioInfoByPaGIS
		return buildPropertyInfo(dataFromLayers)
	}

	/**
	 * 
	 * @param xCoord
	 * @param yCoord
	 * @return Map - representing the property info object. Returns null if no property is found for the
	 * given (xCoord, yCoord)
	 */
	Map getPropertyInfo(Double xCoord, Double yCoord) {		
		List layers = []
		
		// Get data from intersecting layers with xCoord, yCoord
		EsriFieldMappings.propertyInfoAttributes.each{key,value -> layers << key}
		Map dataFromLayers = featureService.featuresFromPointLayersIntersection(xCoord as String, yCoord as String, layers)

		// No property, return null
		if (!dataFromLayers['MDC.PaParcel']?.geometry || !dataFromLayers['MDC.PaParcel']?.attributes)
			return null

		// Get the property data in the case of condos from PaGIS
		dataFromLayers['MDC.PaGIS'] = getCleanPropertyInfoByFolio(dataFromLayers['MDC.PaParcel'].attributes.FOLIO)

		// Ensemble it	
		buildPropertyInfo(dataFromLayers)
	}
	
	// this is useful if you happen to have all this data. Because it is expensive to get it
	// Maybe we can use a candidate with a unit combo. Eventually we should also support 
	//x, y, unit and street, zip, unit. But it will be more expensive.
	// pass a map [street;'', zip:'', unit:'', xCoord:'', yCoord:'']
	Map getCondoPropertyInfo(data){
		
		List layers = []		

		// Get data from intersecting layers with xCoord, yCoord
		EsriFieldMappings.propertyInfoAttributes.each{key,value -> layers << key}
		Map dataFromLayers = featureService.featuresAttributesFromPointLayersIntersection(data?.xCoord as String, data?.yCoord as String, layers)
		
		// No property, return null
		if (!dataFromLayers['MDC.PaParcel'])
			return null
			
		// Get the folio for the unit. No folio -> no property -> return null	
		String folio = getCondoFolio(data?.street, data?.zip, data?.unit)
		if(!folio) 
			return null
		
		// Get the property data in the case of condos from PTXGIS	
		dataFromLayers['MDC.PaGIS'] = getCleanPropertyInfoByFolio(folio)
		
		// Ensemble it	
		buildPropertyInfo(dataFromLayers)

	}

/**
	 * Clean up of data coming from the getRawPropertyInfoByFolio should happen here.
	 * 1. convert X_COORD and Y_COORD into double rounded to 3 decimal places.
	 * @param folioNumber
	 * @return
	 */
	Map getCleanPropertyInfoByFolio(String folioNumber){
		
		Map data = getRawPropertyInfoByFolio(folioNumber)
		
		if(data){
			
			// make x, y coords doubles with 3 decimal precission.	
			data.X_COORD = org.sharegov.mdcgis.Utils.round(Double.valueOf(data.X_COORD), 3)
            data.Y_COORD = org.sharegov.mdcgis.Utils.round(Double.valueOf(data.Y_COORD), 3)
		}
		
		return data	
	}
	
	/**
	 * By searching on layer, obtain the property information for folioNumber.
	 * If no folioNumber found return null. 
	 * All folios including condos exist on PAGIS layers which is a point layer. The issue is
	 * that PAGIS does not contain address data for some buildings, so in case of building
	 * a different layer, MDC.GeoProp needs to be query to obtain the address information.
	 * @param folioNumber - The Folio Number to search by
	 * @return Map - property information data for given folio. Return null if
	 * folioNumber is not found.
	 */
	private Map getRawPropertyInfoByFolio(String folioNumber){

		Map attributes = featureService.propertyAttributesByFolio(folioNumber)

		// folio exists. Find the address field.
		if (attributes){

			// In case of a building look ADDR data in a different layer. It is nasty.
			PropertyType pt = findPropertyType(['MDC.PaGIS':attributes])
			if(pt == PropertyType.MULTI){
				Map buildingAttributes = featureService.propertyAttributesForBuildingByFolio(folioNumber)
				
				// No adddress found, it maybe a multi address building
				String address = buildingAttributes['ADDRESS']
				if(!address)
					address = getAddressForMultiAddressBuilding(folioNumber)

				attributes['TRUE_SITE_ADDR_NO_UNIT'] = address
			}
		}
		return attributes
	}

	/**
	 * It builds a PropertyInfo object as a map. PropertyInfo is just conceptual, there is no
	 * such a class representing it. It is built based on EsriFieldMappings.propertyInfoAttributes
	 * by mapping dataFromLayers.
	 * @param dataFromLayers - Map where key: Layer Name. Value: map of key,values as it comes from the gis layers
	 * @return Map - key: property as expressed by the value of the EsriFieldMappings.propertyInfoAttributes
	 *               value: value of the matching property for the given layer.  
	 */
	Map buildPropertyInfo(Map dataFromLayers) {
		Map propertyInfo = [:]

		// get the geometry if it exists
		Map geometry = dataFromLayers['MDC.PaParcel']?.geometry?:dataFromLayers['MDC.PaParcel']
		
		// If there is data coming from PaGIS, it should be used instead of Parcels data.
		// Condo data is only available in PaGIS.
		if(dataFromLayers['MDC.PaGIS'])
			dataFromLayers['MDC.PaParcel'] = dataFromLayers['MDC.PaGIS']

		// map fields in data to fields in propertyInfo
		EsriFieldMappings.propertyInfoAttributes.each{ layer,attributes ->
			if(attributes == null)
				println("the layer with null attributes" + layer)
				
			else {
				Map data = dataFromLayers[layer]?.attributes?:dataFromLayers[layer]

				//Add other composite fields for only for PaGis and PaParcel layers
				if(layer.equalsIgnoreCase("MDC.PaParcel") || layer.equalsIgnoreCase("MDC.PaGIS")){
					data = addAdditionalCompositeFields(data);
				}

				attributes.each {key, value ->
					if(data?.containsKey(key)){
						def trimValue = data[key]
						if(data[key] instanceof String)
							trimValue = (trimValue =~ / +/).replaceAll(" ").trim()
	
						propertyInfo << [(value):trimValue]
					}
				}
			}
		}

		if( dataFromLayers['MDC.PaGIS'] || dataFromLayers['MDC.PaParcel']) {

			// populate propertyType
			propertyInfo.propertyType = findPropertyType(dataFromLayers) as String

			// add units on the building
			if (propertyInfo.propertyType == "MULTI") {
				// Intersect layer with Polygon.
				List features = featureService.featuresAttributesInPolygon(geometry, 'MDC.PaGIS', ['TRUE_SITE_UNIT'])['MDC.PaGIS']
				// Process results to extract just unit numbers
				propertyInfo.units = features.collect { it.TRUE_SITE_UNIT }.sort()
			}
		}

		return propertyInfo
	}

	/**
	 * It finds out what the propertyType is given the data from layers.
	 * Specifically, either the MDC.PaGIS (point layer containing all properties including condos)
	 * or the MDC.PaParcel (polygon layer which does not contain condo data) need to be present
	 * in order to determine the property type. It checks first for PAGis data. In both layers
	 * the property to analyze is CONDO
	 * @param dataFromLayers - Map of layer names and map of values for that layer.
	 * @return PropertyType - as defined by the enum.
	 */
	private PropertyType findPropertyType(Map dataFromLayers) {

		PropertyType propertyType = PropertyType.UNDEFINED
		
		if(!dataFromLayers)
			return propertyType
		
		if (dataFromLayers['MDC.PaGIS']){
			propertyType = getPropertyType(dataFromLayers, 'MDC.PaGIS')
		}
		else if (dataFromLayers['MDC.PaParcel']) {
			propertyType = getPropertyType(dataFromLayers, 'MDC.PaParcel')
		}

		return propertyType
	}

	/**
	 * Get Property type.
	 *
	 * @param dataFromLayers
	 * @param layerName
     * @return
     */
	private PropertyType getPropertyType(Map dataFromLayers, String layerName) {

		PropertyType propertyType = PropertyType.UNDEFINED;
		String isCondo = dataFromLayers[layerName].attributes?.CONDO_FLAG ?: dataFromLayers[layerName].CONDO_FLAG
		String parentFolioNumber = dataFromLayers[layerName].attributes?.PARENT_FOLIO ?: dataFromLayers[layerName].PARENT_FOLIO
		String folioNumber = dataFromLayers[layerName].attributes?.FOLIO ?: dataFromLayers[layerName].FOLIO
		String referenceOnlyFlag = dataFromLayers[layerName].attributes?.REFERENCE_ONLY_FLAG ?: dataFromLayers[layerName].REFERENCE_ONLY_FLAG

		if (isCondo != null && isCondo.toBoolean()) {
			propertyType = PropertyType.CONDO
		} else if ( (parentFolioNumber != folioNumber) &&
					(folioNumber != null && folioNumber.endsWith("0001")) &&
					(referenceOnlyFlag != null && referenceOnlyFlag.toBoolean())) {
			propertyType = PropertyType.MULTI
		} else {
			propertyType = PropertyType.UNDEFINED
		}
		return propertyType
	}

	/**
	 * Searches on a property layer by folio number in order to get the street, zip and condo unit
	 * if one exists.
	 * This layer has been indexed on folio number. The basic information
	 * we need from here is the address, zip and unit number.
	 *
	 * @param folioNumber - the folio number
	 * @return Map - it returns [street:'2001 MERIDIAN AVE', zip:33139, unit:'317'].
	 * It returns null if no folio is found.
	 */
	Map getStreetZipUnitByFolio(String folioNumber){

		def data = getCleanPropertyInfoByFolio(folioNumber)
		data?[street:data.TRUE_SITE_ADDR_NO_UNIT, zip:data.TRUE_SITE_ZIP_CODE, unit:data.TRUE_SITE_UNIT, x:data.X_COORD, y:data.Y_COORD]:null

	}


	Map getStreetZipByCoord(Double xCoord, Double yCoord){
		List layers = ['MDC.PaParcel']
		Map dataFromLayers = featureService.featuresAttributesFromPointLayersIntersection(xCoord as String, yCoord as String, layers)
		dataFromLayers['MDC.PaParcel']?getStreetZipUnitByFolio(dataFromLayers['MDC.PaParcel']['FOLIO']):null
	}

	/**
	 * Finds out if a folio exits or not. Can be used to make a decision based on folio 
	 * if such a property may exits, weather it is a Single family home property, a condo property
	 * or a parent property (a building).
	 * @param folioNumber
	 * @return Boolean - true if folio is found, false otherwise.
	 */
	Boolean folioExists(String folioNumber){
		def data = getCleanPropertyInfoByFolio(folioNumber)
		data?true:false
	}


	/**
	 * Intersects (xCoord,yCoord) against the parcel layer MDC.PaParcel.
	 * @param xCoord
	 * @param yCoord
	 * @return - true if intersection with parcel layer succeeds, false otherwise.
	 */
	Boolean isCoordInProperty(Double xCoord, Double yCoord){
		List layers = ['MDC.PaParcel']
		Map dataFromLayers = featureService.featuresAttributesFromPointLayersIntersection(xCoord as String, yCoord as String, layers)
		dataFromLayers['MDC.PaParcel']?true:false
	}

	/**
	 * Get the folio number of the condo property given the Street, Zip and Unit.
	 * If no Unit, the parent folio is returned which is either the folio of the 'building'
	 * or the folio of a 'house'.
	 * @param street
	 * @param zip
	 * @param unit
	 * @return String - Return the folio number of a property. Return null if no property 
	 * is found.
	 */
	String getCondoFolio(String street, String zip, String unit){

		def query = [
					'myAddress':street,
					'myZipCode':zip,
					'unit':unit]

		// Make call to http client to get condo folio data
		def condoFolios = httpService.request(gisConfig.gisServices['condoAddressZip'], query, groovyx.net.http.ContentType.XML)

		// With a count of one the children folios gives the folio number
		// With count bigger than one we are talking about a building so choose the parent folio.
		String folioNumber = null
		Integer count = condoFolios.Count.text().toInteger()

		if(count == 1){
			folioNumber = condoFolios.Children.children()[0].text()
		} else if(count > 1) {
			folioNumber = condoFolios.Parent.text()
		}

	}
	
	/**
	 * 
	 * @param folioNumber
	 * @return Map - [x:123444, y:2232323]
	 */
	Map folio2Coord(String folioNumber){
		def data = getCleanPropertyInfoByFolio(folioNumber)
		data?[x:data.X_COORD, y:data.Y_COORD]:null
	}


	List getUnitsInBuildingByFolio(String folio){
		
		// Make sure it is a multi type of property (building with other properties)
		Map coords = folio2Coord(folio)

		// Get Polygons for building.
		def geometry = featureService.featuresGeometryFromPointLayersIntersection(coords.x as String, coords.y as String, ['MDC.PaParcel'])['MDC.PaParcel']

		// Intersect layer with Polygon.
		List features = featureService.featuresInPolygon(geometry, 'MDC.PaGIS', ['TRUE_SITE_UNIT'])['MDC.PaGIS']

		// Process results to extract just unit numbers
		features.collect {it.TRUE_SITE_UNIT}.sort()

	}

	/**
	 * There are some fields needs to be concatenate for simplicity.
	 *
	 * @param data
	 * @return
	 */
	Map addAdditionalCompositeFields(Map data) {
		if(data == null){
			return data;
		}

		//Parcel Sales origin1
		if(data.containsKey("OR_BK_1") && data.containsKey("OR_PG_1")){
			data.SALES_ORI1 = (data.OR_BK_1?:'') + " " + (data.OR_PG_1?:'')
		}

		//Parcel Sales origin2
		if(data.containsKey("OR_BK_2") && data.containsKey("OR_PG_2")){
			data.SALES_ORI2 = (data.OR_BK_2?:'' ) +" " + (data.OR_PG_2?:'')
		}

		//Parcel Sales origin3
		if(data.containsKey("OR_BK_3") && data.containsKey("OR_PG_3")){
			data.SALES_ORI3 = (data.OR_BK_3?:'') +" " + (data.OR_PG_3?:'')
		}

		return addMissingFiledsDeleteItLater(data);

	}

	/**
	 * Those fileds are missing... as soon as it is avilable it is going to be add into Field Mapping
	 * and then this method could be deleted.
	 *
	 * @return
     */
	Map addMissingFiledsDeleteItLater(Map data){

		data.Missing1 =""
		data.Missing2 =""
		data.Missing3 =""
		data.Missing4 =""
		data.Missing5 =""
		data.Missing6 =""
		data.Missing7 =""
		data.Missing10 =""
		data.Missing14 =""
		data.Missing15 =""
		data.Missing16 =""
		data.Missing17 =""
		data.Missing18 =""
		data.Missing19 =""
		data.Missing20 =""
		data.Missing22 =""
		data.Missing23 =""
		data.Missing24 =""
		data.Missing25 =""
		data.Missing26 =""
		data.Missing27 =""
		data.Missing28 =""
		data.Missing29 =""
		data.Missing30 =""
		data.Missing31 =""

		//temp... see the comments on filed mappings
		data.BAD_1 = ""
		data.BAD_2 = ""
		data.BAD_3 = ""

		//temp... see the comments on filed mappings
		data.LEGAL1 = ""
		data.LEGAL2 = ""
		data.LEGAL3 = ""
		data.LEGAL4 = ""
		data.LEGAL5 = ""
		data.LEGAL6 = ""

		//Delete it later
		data.DELETE_LOT_SIZE_UNIT =""
		return data;
	}
	
	List getUnitsInBuildingByCoords(String x, String y){
		
		// Get Polygons for building.
		def geometry = featureService.featuresGeometryFromPointLayersIntersection(x, y, ['MDC.PaParcel'])['MDC.PaParcel']

		// Intersect layer with Polygon.
		List features = featureService.featuresInPolygon(geometry, 'MDC.PaGIS', ['TRUE_SITE_UNIT'])['MDC.PaGIS']

		// Process results to extract just unit numbers
		features.collect {it.TRUE_SITE_UNIT}.sort()
	}
	
	
	String getAddressForMultiAddressBuilding(String folioNumber){

		def query = [folio:folioNumber]
		def url = gisConfig.gisServices['parentChildFolio']
		
		def childrenFolios = httpService.request(url, query, groovyx.net.http.ContentType.XML)
		String address = ""

		Integer count = childrenFolios.AddressCount.text().trim() ? childrenFolios.AddressCount.text().trim().toInteger():0 
		if(count > 0){
			address = childrenFolios.Addresses.children()[0].text().split(",")[0].trim()
		}
		
		return address
		
	}
	
}
