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

import java.util.Map;

import org.apache.commons.collections.BidiMap
import org.apache.commons.collections.bidimap.DualHashBidiMap
import org.springframework.beans.factory.config.AbstractFactoryBean
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GisConfigFactoryBean extends AbstractFactoryBean<GisConfig>{

	private static Logger _log = LoggerFactory.getLogger(GisConfigFactoryBean.class);

	HTTPService httpService
	Map connections
		
	@Override
	public Class<GisConfig> getObjectType() {
		return GisConfig.class;
	}

	@Override
	protected GisConfig createInstance() throws Exception {

		GisConfig config = new GisConfig()		
		config.layersUrls = populateLayersUrls()
		config.layers = populateLayers(config.layersUrls)
		config.locators = populateLocators()
		config.gisServices = populateGisServices()
		config.gisGeometryServices = populateGisGeometryServices()
		config.serviceLayers = populateServiceLayers()
		config.commonLocationsLayerNames = populateCommonLocationsLayerNames()
		config.addressLayers = populateAddresslayers()
		config.cityOfMiamiAddressLayers = populatecityOfMiamiAddresslayers()
		config.municipalities = populateMunicipalities(config.layers)
		config.countyRecyclingCalendarUrls = populateCountyRecyclingCalendarUrls()
		config.cityOfMiamiRecyclingCalendarUrls = populateCityOfMiamiRecyclingCalendarUrls()
		config.cityOfMiamiCommissioners = populateCityOfMiamiCommissioners()
		config.codeTranslator = populateCodeTranslator(config.layersUrls)
		
		
		return config
				
	}
	
	
	private BidiMap populateLayersUrls(){
		
		String url = connections.layer.url
		Map query = connections.layer.query

		_log.info("populateLayersUrls(): about to populate layers from ${url}")
				
		def result = httpService.request(url, query)
		
		Map layers = [:]
		   
		result.each{def layer ->
			_log.info("${layer.hasName}: ${layer.hasUrl}")
			String builtUrl = "${layer['hasUrl']}"
			layers << [(layer.hasName):builtUrl]
		}
		
		return new DualHashBidiMap(layers)
	}
	
	private BidiMap populateLayers(BidiMap layersUrls){

		_log.info("populateLayers(): about to build query layers urls")
		
		Map layers = [:]
	   	
		layersUrls.each{layer, url ->
			String builtUrl = "${url}/query"
			layers << [(layer):builtUrl]
		}
		
		return new DualHashBidiMap(layers)
	}
	
	private Map populateLocators(){
		String url = connections.locator.url
		Map query = connections.locator.query
		
		_log.info("populateLocators(): about to populate locators from ${url}")
		
		def result = httpService.request(url, query)
		
		Map locators = [:]
		result.each{def locator ->
			_log.info("${locator.hasName}: ${locator.hasUrl}")
			locators << [(locator.hasName):locator['hasUrl']]
		}
		
		// Fix in change from MD_Locator-20 to MD_Locator_75
		//TODO: Change ontology configuration
		locators.findAddressCandidates20 = "http://gisws.miamidade.gov/ArcGIS/rest/services/MDC_Locators/MD_Locator_75/GeocodeServer/findAddressCandidates"
		
		return locators
	}
	
	private Map populateGisServices(){
		String url = connections.gisService.url
		Map query = connections.gisService.query
		
		_log.info("populateGisServices(): about to populate gis services from ${url}")
		
		def result = httpService.request(url, query)
		Map gisServices = [:]
		result.each{def gisService ->
			_log.info("${gisService.hasName}: ${gisService.hasUrl}")
			gisServices << [(gisService.hasName):gisService['hasUrl']]
		}
		
		return gisServices
	}
	
	private Map populateGisGeometryServices(){
		String url = connections.gisGeometryService.url
		Map query = connections.gisGeometryService.query
		
		_log.info("populateGisGeometryServices(): about to populate gis geometry services from ${url}")
		
		def result = httpService.request(url, query)
		Map gisGeometryServices = [:]
		result.each{def gisGeometryService ->
			_log.info("${gisGeometryService.hasName}: ${gisGeometryService.hasUrl}")
			gisGeometryServices << [(gisGeometryService.hasName):gisGeometryService['hasUrl']]
		}
		
		return gisGeometryServices
	}
	
	private BidiMap populateMunicipalities(Map layers){
		String url = layers['MDC.Municipality_poly']
		
		// create the query attributes
		String where = "OBJECTID > 0"
		String outFields = '*'
		String f = 'json'

		def query = [where:where,
					f:f,
					outFields:outFields,
					returnGeometry:false]
		
		_log.info("populateMunicipalities(): about to populate municipalities from ${url}")
		
		def data = httpService.request(url, query)
		
		// Extract just the attributes of each feature
		List features = []
		data.features.each {feature->
			features << feature.attributes
		}

		
		Map municipalities = [:]
		features.each{def municipality ->
			_log.info("${municipality.MUNICID}: ${municipality.NAME}")
			municipalities << [(municipality.MUNICID as Integer):municipality['NAME'].trim()]
		}
		
		return new DualHashBidiMap(municipalities)	
	}
	
	private List populateServiceLayers(){
		['MDC.WaterServiceArea',
			'MDC.RecyclingRoute',
			'MDC.HurricaneEvacZone',
			'MDC.Precinct',
			'MDC.Municipality_poly',
			'MDC.CommissionDistrict',
			'MDC.StreetMaint',
			'MDC.MinimumHousing',
			'MDC.NeighborhoodCodeOfficer',
			'MDC.PoliceGrid_poly',
			'MDC.SewerServiceArea',
			'MDC.TMBoundary',
			'Code_Enforcement_Zones',
			'Commission_Districts',
			'GarbageRoutes',
			'NET_Areas',
			'NewGarbRoutes',
			'Recycle_Routes',
			'Trash_Route',
			'pwd_lights',
			'EnforcementMunicipalities',
			'MDC.DSWMServiceArea',
			'MDC.SWMEnforcementZone',
			'MDC.WCSBulkyBook',
			'MDC.RoadImpactFeeDistricts',
			'MDC.CommunityCouncil']

	}
	
	private List populateAddresslayers(){
		['MDC.WaterServiceArea',
			'MDC.FEMAFloodZone',
			'MDC.RecyclingRoute',
			'MDC.HurricaneEvacZone',
			'MDC.HouseDistrict',
			'MDC.SenateDistrict',
			'MDC.Precinct',
			'MDC.GarbagePickupRoute',
			'MDC.CommissionDistrict', 
			'MDC.TurkeyPointArea']
	}
	
	private List populatecityOfMiamiAddresslayers(){
		['MDC.WaterServiceArea',
			'MDC.FEMAFloodZone',
			'Recycle_Routes',
			'MDC.HurricaneEvacZone',
			'MDC.HouseDistrict',
			'MDC.SenateDistrict',
			'MDC.Precinct',
			'GarbageRoutes',
			'NewGarbRoutes',
			'Trash_Route',	
			'MDC.CommissionDistrict',
			'Commission_Districts',
			'MDC.TurkeyPointArea',
			'NET_Areas',
			'Code_Enforcement_Zones',
			'PW_Maint_Zones']
		
	}
	
	private List populateCommonLocationsLayerNames(){
		
		['COLLEGE',
			'COURT',
			'DAYCARE',
			'FIRE STATION - COUNTY',
			'FIRE STATION - MUNICIPAL',
			'HOSPITAL',
			'HURRICANE SHELTER',
			'LANDMARK',
			'LIBRARY - COUNTY',
			'LIBRARY - MUNICIPAL',
			'METRORAIL STATION',
			'PARK - COUNTY',
			'PARK - MUNICIPAL',
			'POLICE STATION - COUNTY',
			'POLICE STATION - MUNICIPAL',
			'PORT',
			'POST OFFICE',
			'SCHOOL - CHARTER',
			'SCHOOL - PRIVATE',
			'SCHOOL - PUBLIC']
	}
	
	
	private Map populateCountyRecyclingCalendarUrls(){
		
		String url = connections.miamiDadeCounty.url

		_log.info("populateCountyRecyclingCalendarUrls(): about to populate MDC recycling calendars from ${url}")
				
		def result = httpService.request(url, [:])
		
		Map calendars = [:]
		   
		result.hasRecyclingRoute.each{def calendar ->
			_log.info("${calendar.hasRecyclingRouteID}: ${calendar.hasUrl}")
			String builtUrl = "${calendar['hasUrl']}"
			calendars << [(calendar.hasRecyclingRouteID):builtUrl]
		}
		
		return calendars
	}
	
	
	private Map populateCityOfMiamiRecyclingCalendarUrls(){
		
		String url = connections.cityOfMiami.url

		_log.info("populateCityOfMiamyRecyclingCalendarUrls(): about to populate COM recycling calendars from ${url}")
				
		def result = httpService.request(url, [:])
		
		Map calendars = [:]
		   
		result.hasRecyclingRoute.each{def calendar ->
			_log.info("${calendar.label}: ${calendar.hasUrl}")
			String builtUrl = "${calendar['hasUrl']}"
			calendars << [(calendar.hasRecyclingRouteID):builtUrl]
		}
		
		return calendars
	}

	private Map populateCityOfMiamiCommissioners(){
		
		String url = connections.cityOfMiami.url

		_log.info("populateCityOfMiamiCommissioners(): about to populate COM commissioners from ${url}")
				
		def result = httpService.request(url, [:])
		
		Map commissioners = [:]
		   
		result.hasDistrict.each{def commissioner ->
			_log.info("${commissioner.hasDistrictID}: ${commissioners.Commissioner_Name}")
			String commissionerName = "${commissioner['Commissioner_Name']}"
			Integer districtId = commissioner.hasDistrictID.toInteger()
			commissioners << [(districtId):commissionerName]
		}
		
		return commissioners
	}
	
	
	/**
	 * Builds a Map of codes and their friendly tranlation or alias if you may
	 * for different layer attributes. Some aliases exist in the layer services
	 * others we are going to hardcode them within this method
	 * @param layersUrls - urls of the layers in use so that it can be checked if 
	 * the layer has already aliases for certain attributes
	 * @return - A map where the keys are name of layers and the value is a map itself. 
	 * The second map the key is the attribute of hte layer and the value is a map of translations.
	 * [MDC.SewerServiceArea:[UTILITYNAME:[MDWS:Miami Dade Water and Sewer, 
	 *                                     NMB:North Miami Beach]],
     *  MDC.StreetMaint:[ONEWAY:[TF:To direction to the From direction, 
     *  						 FT:From direction to the To direction, 
     *                           B:Both Directions]]]
	 
	private Map populateCodeTranslator(BidiMap layersUrls) {
		
		// Get the Data from the layers url.
		String f = 'json'
		def query = [f:f]
		Map data = httpService.request(layersUrls.values() as List, query)

		// Process the data fields.domain.codeValues
		Map data2 = [:]
		data.each {key, value -> 
			List domains = value.fields.findAll {it.domain}
			if(domains)
				data2 << [(key):domains]
		}

		def data3 = data2.inject([:]){acc, key, value ->

			List names =  value.name
			List values = []
			value.domain.codedValues.each {codes ->
				 values << codes.inject([:]) {result, entry ->
						result << [(entry.code):entry.name]
					}
			}

			// Combine in one map two lists. names of attributes as keys
			// and translations as values
			def x = [names, values].transpose().inject([:]) { a, b -> a[b[0]] = b[1]; a }
			acc << [(layersUrls.getKey(key)):x]

		}
		
		// Add hardcoded data
		data3 << ['MDC.GarbagePickupRoute':['COLLDAY':[1:'MON/THU', 2:'TUE/FRI']]]
		data3 << ['pwd_lights':['ST_LIGHT':["LIGHT":'FPL - (800) 468-8243', 'CML': 'County Maintained Light']]]
		data3 << ['Trash_Route':['TRASHDAY':['M':'MONDAY',
											 'T':'TUESDAY',
											 'W':'WEDNESDAY',
											 'R':'THURSDAY',
											 'F':'FRIDAY']]]
	}
	
	*/
	private Map populateCodeTranslator(BidiMap layersUrls) {
		
		// Get the Data from the layers url.
		String f = 'json'
		def query = [f:f]
		Map data = httpService.request(layersUrls.values() as List, query)

		// Process the data fields.domain.codeValues
		Map data2 = [:]
		data.each {key, value ->
			List domains = value.fields.findAll {it.domain && it.domain != "null"}
			if(domains)
				data2 << [(key):domains]
		}
		
		println "the data2 is ${data2}"

		def data3 = data2.inject([:]){acc, key, value ->

			List names =  value.name
			List values = []
			value.domain.codedValues.each {codes ->
				 values << codes.inject([:]) {result, entry ->
						result << [(entry.code):entry.name]
					}
			}

			// Combine in one map two lists. names of attributes as keys
			// and translations as values
			def x = [names, values].transpose().inject([:]) { a, b -> a[b[0]] = b[1]; a }
			acc << [(layersUrls.getKey(key)):x]

		}
		
		// Add hardcoded data
		data3 << ['MDC.GarbagePickupRoute':['COLLDAY':[1:'MON/THU', 2:'TUE/FRI']]]
		data3 << ['pwd_lights':['ST_LIGHT':["LIGHT":'FPL - (800) 468-8243', 'CML': 'County Maintained Light']]]
		data3 << ['Trash_Route':['TRASHDAY':['M':'MONDAY',
											 'T':'TUESDAY',
											 'W':'WEDNESDAY',
											 'R':'THURSDAY',
											 'F':'FRIDAY']]]
	}
	
}
