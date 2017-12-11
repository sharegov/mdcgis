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

import org.apache.commons.collections.BidiMap
import org.apache.commons.collections.bidimap.DualHashBidiMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.AbstractFactoryBean

class GisConfigFactoryBeanLocal extends AbstractFactoryBean<GisConfig>{

	private static Logger _log = LoggerFactory.getLogger(GisConfigFactoryBeanLocal.class);
	
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
		
		_log.info("populateLayersUrls(): about to populate layers from local")
				
		Map layers = ["Fire_Prevention_Bureau_District":
				 "https://s0142354.miamidade.gov/arcgis/rest/services/Gic/MapServer/67",

"MiamiNeighborhoods":
"https://s0142354.miamidade.gov/arcgis/rest/services/Gic/MapServer/66",

"MDC.TurkeyPointArea":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/61",

"MDC.PainterTerritory":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/37",

"MDC.RecyclingRoute":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/43",

"MDC.RoadImpactFeeDistricts":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/45",

"MDC.ZipCode":
"https://s0142354.miamidade.gov/arcgis/rest/services/Gic/MapServer/68",

 "MDC.HomeOwnerAssociation":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/26",

 "Code_Enforcement_Zones":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/10",

 "GarbageRoutes":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/23",

 "MDC.DSWMServiceArea":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/17",

 "MDC.SenateDistrict":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/47",

 "MDC.StreetMaint":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/51",

 "EnforcementMunicipalities":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/19",

 "Commission_Districts":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/13",

 "MDC.PaGIS":
"https://s0142354.miamidade.gov/arcgis/rest/services/Gic/MapServer/62",

 "MDC.GarbagePickupRoute":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/22",

 "MDC.CommonLocations":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/14",

 "MDC.SWMEnforcementZone":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/52",

 "MDC.TMBoundary":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/53",

 "MDC.PTXTABS":
"https://gisweb.miamidade.gov/ArcGIS/rest/services/MD_PropertySearch/MapServer/4",

 "MDC.WaterServiceArea":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/55",

 "MDC.NeighborhoodCodeOfficer":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/32",

 "Recycle_Routes":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/42",

 "MDC.OutReach":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/36",

 "MDC.MinimumHousing":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/30",

 "NewGarbRoutes":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/34",

 "pwd_lights":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/41",

 "MDC.WCSBulkyBook":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/56",

 "MDC.HurricaneEvacZone":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/28",

 "MDC.PaParcel":
"https://s0142354.miamidade.gov/arcgis/rest/services/Gic/MapServer/60",

 "MDC.FEMAFloodZone":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/21",

 "MDC.PoliceGrid_poly":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/38",

 "NET_Areas":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/33",

 "MDC.CommissionDistrict":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/11",

 "Trash_Route":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/54",

 "MDC.CommunityCouncil":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/15",

 "MDC.SewerServiceArea":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/49",

 "MDC.HouseDistrict":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/27",

 "MDC.GeoProp":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/57",

 "MDC.Precinct":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/39",

 "MDC.GISSection":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/24",

 "MDC.Municipality_poly":
"https://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/31",

 "PW_Maint_Zones":
"https://s0142354.miamidade.gov/arcgis/rest/services/Gic/MapServer/64"

]
		
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

		_log.info("populateLocators(): about to populate locators from local")
		
		Map locators = [ "reverseGeoCodeGeoStreet":
"https://gisws.miamidade.gov/ArcGIS/rest/services/MDC_Locators/GeoStreet/GeocodeServer/reverseGeocode",

 "findAddressCandidates75":
"https://gisws.miamidade.gov/ArcGIS/rest/services/MDC_Locators/MD_Locator_75/GeocodeServer/findAddressCandidates",

 "reverseGeoCodeGeoAddress":
"https://gisws.miamidade.gov/ArcGIS/rest/services/MDC_Locators/GeoAddress/GeocodeServer/reverseGeocode",

 "reverseGeoCode":
"https://gisws.miamidade.gov/ArcGIS/rest/services/MDC_Locators/MD_Locator/GeocodeServer/reverseGeocode",

 "findAddressCandidates":
"https://gisws.miamidade.gov/ArcGIS/rest/services/MDC_Locators/MD_Locator/GeocodeServer/findAddressCandidates"
]
		
		
		return locators
	}
	
	private Map populateGisServices(){

		_log.info("populateGisServices(): about to populate gis services from local")
		
		Map gisServices = [ "pollingPlaceByPrecinct":
"https://s0144339.miamidade.gov/Pploc/GetLocation.asmx/PPLoc",

 "standardizeIntersection":
"https://gisws.miamidade.gov/GISAddress/standardize.asmx/standardizeIntersection",

 "condoAddressZip":
"https://gisws.miamidade.gov/gisaddress/condo.asmx/CondoAddressZip",

 "standardizeAddress":
"https://gisws.miamidade.gov/GISAddress/standardize.asmx/standardizeAddress",

 "parentChildFolio":
"https://gisws.miamidade.gov/gisaddress/condo.asmx/ParentChildFolio",

 "publicWorksGeoCoder":
"https://gisimsintra.miamidade.gov/website/pw_geocoder/CSR_Geocoder.asp",

  "publicWorksIntersection":"https://gisws.miamidade.gov/ITDServ/ITDService.asmx/Intersection",
  "publicWorksArea":"https://gisws.miamidade.gov/ITDServ/ITDService.asmx/Area",
  "publicWorksCorridor":"https://gisws.miamidade.gov/ITDServ/ITDService.asmx/Segment"
  
]
		
		return gisServices
	}
	
	private Map populateGisGeometryServices(){

		_log.info("populateGisGeometryServices(): about to populate gis geometry services from Local")
		
		Map gisGeometryServices = ["buffer":"https://311arcgis.miamidade.gov/arcgis/rest/services/Utilities/Geometry/GeometryServer/buffer"]
		
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
