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

import java.util.List;
import java.util.Map;

import net.sf.json.JSONNull
import org.sharegov.mdcgis.model.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import groovy.json.JsonBuilder


class FeatureService {

	private static Logger _log = LoggerFactory.getLogger(FeatureService.class);
	HTTPService httpService
	GisService gisService
	GisConfig gisConfig


	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param layerNames
	 * @return: 
	 * ["MDC.CommissionDistrict":
	 *		[attributes:[OBJECTID:7, ID:7, COMMNAME:'Xavier L. Suarez'], 
	 *		 geometry:[rings:[[[931858.2328325026, 520919.6944387555]]]]]]
	 *
	 */
	
	public Map featuresFromPointLayersIntersection(String x, String y, List layerNames){
		
		List urls = []
		
		// If no service layer specified, use all of them
		if(!layerNames)
			urls = gisConfig.layers.values().toList()
		else
			urls = layerNames.findResults{gisConfig.layers[it]}
			
		// Get the data
		Map results = gisService.featuresFromPointLayersIntersection(x, y, urls, true)		
		
		// Switch the key from urls back to the serviceLayerName
		results.collectEntries{url, data ->
			[gisConfig.layers.getKey(url),data]
		}	
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param layerNames
	 * @return 
	 * ["MDC.CommissionDistrict":
	 *	[OBJECTID:7, ID:7, COMMNAME:'Xavier L. Suarez']]
	 */
	public Map featuresAttributesFromPointLayersIntersection(String x, String y, List layerNames){
		
		List urls = []
		
		// If no service layer specified, use all of them
		if(!layerNames)
			urls = gisConfig.layers.values().toList()
		else
			urls = layerNames.findResults{gisConfig.layers[it]}
			
		// Get the data
		Map results = gisService.featuresAttributesFromPointLayersIntersection(x, y, urls)
		
		// Switch the key from urls back to the serviceLayerName
		results.collectEntries{url, data ->
			[gisConfig.layers.getKey(url),data]
		}
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param layerNames
	 * @return
	 * ["MDC.CommissionDistrict":
	 *  [rings:[[[931858.2328325026, 520919.6944387555]]]]]
	 */
	public Map featuresGeometryFromPointLayersIntersection(String x, String y, List layerNames){
		
		List urls = []
		
		// If no service layer specified, use all of them
		if(!layerNames)
			urls = gisConfig.layers.values().toList()
		else
			urls = layerNames.findResults{gisConfig.layers[it]}
			
		// Get the data
		Map results = gisService.featuresGeometryFromPointLayersIntersection(x, y, urls)
		
		// Switch the key from urls back to the serviceLayerName
		results.collectEntries{url, data ->
			[gisConfig.layers.getKey(url),data]
		}
	}

	/**
	 * 			
	 * @param x
	 * @param y
	 * @param radius
	 * @param layerName
	 * @return
	 * ["pwd_lights":
	 *	[[attributes:[OBJECTID:68756],
	 *	  geometry:[paths:[[[858070.6874045022, 488840.5938830897]]]]]]]
     *
	 */
	public Map featuresInCircle(Double x, Double y, Integer radius, String layerName){
		
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresInCircle(x, y, radius, url, true)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @param layerName
	 * @return 
	 * ["pwd_lights":[OBJECTID:68756]]
	 */
	public Map featuresAttributesInCircle(Double x, Double y, Integer radius, String layerName){
		
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresAttributesInCircle(x, y, radius, url)

		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @param layerName
	 * @return 
	 * ["pwd_lights":[paths:[[[858070.6874045022, 488840.5938830897]]]]]
	 */
	public Map featuresGeometryInCircle(Double x, Double y, Integer radius, String layerName){
		
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresGeometryInCircle(x, y, radius, url)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}

	/**
	 * 
	 * @param geometry
	 * @param layerName
	 * @return
	 * ["MDC.CommissionDistrict":
	 *	[attributes:[OBJECTID:7, ID:7, COMMNAME:'Xavier L. Suarez'], 
	 *   geometry:[rings:[[[931858.2328325026, 520919.6944387555]]]]]]
     *
	 */
	Map featuresInPolygon(Map geometry, String layerName, List outFields = ['*']){

		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresInPolygon(geometry, url, outFields, true)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}

	/**
	 * 
	 * @param geometry
	 * @param layerName
	 * @return
	 * ["MDC.CommissionDistrict":
	 *	[OBJECTID:7, ID:7, COMMNAME:'Xavier L. Suarez']]
	 */
	Map featuresAttributesInPolygon(Map geometry, String layerName, List outFields = ['*']){
		
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresAttributesInPolygon(geometry, url, outFields)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}
	
	/**
	 * 
	 * @param geometry
	 * @param layerName
	 * @return
	 * ["MDC.CommissionDistrict":
	 *	[rings:[[[931858.2328325026, 520919.6944387555]]]]]
	 */
	Map featuresGeometryInPolygon(Map geometry, String layerName, List outFields = ['*']){
		
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresGeometryInPolygon(geometry, url, outFields)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}
	
	public Map featuresByProximity(Double x, Double y, String layerName, String uniqueAttribute, Integer radius = 20){
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresByProximity(x, y, url, uniqueAttribute, radius)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}
	
	public Map featuresAttributesByProximity(Double x, Double y, String layerName, String uniqueAttribute, Integer radius = 20){
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresAttributesByProximity(x, y, url, uniqueAttribute, radius)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}
	
	public Map featuresGeometryByProximity(Double x, Double y, String layerName, String uniqueAttribute, Integer radius = 20){
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresGeometryByProximity(x, y, url, uniqueAttribute, radius)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}
	
	
	
	/**
	 * 
	 * @param layerName
	 * @param idAttribute
	 * @param ids
	 * @return
	 * ['MDC.StreetMaint':
	 *		[[attributes:[OBJECTID:42736, STREETID:99514],
	 *			geometry:[paths:[[[827864.1875471696, 405714.87507389486]]]]
	 *		   ]]]
	 */
	public Map featuresByIds(String layerName, String idAttribute,  List ids, Boolean returnGeometry=true) {
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresByIds(url, idAttribute, ids, returnGeometry)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}


	/**
	 * 
	 * @param layerName
	 * @param idAttribute
	 * @param ids
	 * @return
	 * ['MDC.StreetMaint':[OBJECTID:42736, STREETID:99514]]
	 */
	public Map featuresAttributesByIds(String layerName, String idAttribute,  List ids) {
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresAttributesByIds(url, idAttribute, ids)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}
	
	/**
	 * 
	 * @param layerName
	 * @param idAttribute
	 * @param ids
	 * @return
	 * ['MDC.StreetMaint':[paths:[[[827864.1875471696, 405714.87507389486]]]]]
	 */
	public Map featuresGeometryByIds(String layerName, String idAttribute,  List ids) {
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.featuresGeometryByIds(url, idAttribute, ids)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
	}
	
	public Map queryFeatures(String layerName, Map query) {
		// Get url
		String url = gisConfig.layers[layerName]
		
		// Get the data
		Map results = gisService.queryFeatures(url, query)
		
		// Switch the key from url back to the serviceLayerName
		results.collectEntries{urlValue, data ->
			[gisConfig.layers.getKey(urlValue),data]
		}
		
	}
	
	public Map streetMaintenanceFeaturesByIds(List ids){
		featuresAttributesByIds("MDC.StreetMaint", "STREETID", ids)
	}
	
	public Map streetMaintenanceFeaturesByProximity(Double x, Double y, Integer radius = 20){
		featuresAttributesByProximity(x, y, "MDC.StreetMaint", "SNAME", radius)
	}
	
	public Map lightMaintenanceFeaturesByProximity(Double x, Double y, Integer radius = 20){
		featuresAttributesByProximity(x, y, "pwd_lights", "STNAME1", radius)
	}
	
	/**
	 * 
	 * @param folioNumber
	 * @return Map - Return a map of properties for the property whose folio is folioNumber.
	 * return null if folio does not exist
	 * 
	 * [MDC.PaGIS.AREA:0, PERIMETER:0, PaGIS_:709943, PaGIS_ID:684378, FOLIO:3059010240130,
	 * PARENT:3059010240130, CONDO:N, ZIP:33186, LU:10, X_COORD:857869.8, Y_COORD:488913.9, OBJECTID:709943 ...]
	 *
	 */
	public Map propertyAttributesByFolio(String folioNumber) {
		
		// create the query attributes
		String where = "FOLIO = '${folioNumber}'"
		String outFields = '*'
		String f = 'json'
		
		def query = [where:where,
					f:f,
					outFields:outFields,
					returnGeometry:false]

		// query and extract the attributes
		List features = queryFeatures('MDC.PaGIS', query)['MDC.PaGIS']
		Map feature = features[0]
		feature?.attributes		
	}
	
	/**
	 * The reason for this method is that certain building addresses dont show up in the MDC.PaGIS
	 * layer so it can be found in the ADDRESS attribute here. 
	 * @param folioNumber
	 * @return Map - Return a map of properties for the property whose folio is folioNumber
	 * [OBJECTID:393483, TTRRSS:534234, FOLIO:0232341690001, ADDRESSID:580254, PID:513237, 
	 *  ADDRESS:2001 MERIDIAN AVE, SUBUNIT:null, HNMAX:0, ZIPCODE:331391503, CONDOFLG:C, MOD_DATE:01202006, 
	 *  MOD_TYPE:B, H_NUM:2001, H_NUM2:, PRE_DIR:null, ST_NAME:MERIDIAN, ST_TYPE:null, ST_TYPE2:AVE, 
	 *  SUF_DIR:null, ZIP_CODE:33139, SHAPE.AREA:79957.6186783283, SHAPE.LEN:1343.3396402567]
	 */
	public Map propertyAttributesForBuildingByFolio(String folioNumber) {
		// create the query attributes
		String where = "FOLIO = '${folioNumber}'"
		String outFields = '*'
		String f = 'json'
		
		def query = [where:where,
					f:f,
					outFields:outFields,
					returnGeometry:false]

		// query and extract the attributes
		List features = queryFeatures('MDC.GeoProp', query)['MDC.GeoProp']
		Map feature = features[0]
		feature?.attributes
	}
		
}
