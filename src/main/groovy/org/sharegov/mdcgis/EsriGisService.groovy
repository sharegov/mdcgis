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

import java.util.List;
import java.util.Map

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONNull

abstract class EsriGisService implements GisService {
	
	private static Logger _log = LoggerFactory.getLogger(EsriGisService.class);

	HTTPService httpService
	GisConfig gisConfig

	def geoCode(Map params) {
		// TODO Auto-generated method stub
		return null;
	}

	abstract Map reverseGeoCode(Double x, Double y, Integer distance);
	
	abstract Map reverseGeoCode(Double x, Double y, String url, Integer distance);

	abstract def getCircleBuffer(Double x, Double y, Integer radius);


	public Map featuresFromPointLayersIntersection(String xCoord, String yCoord,
	List urls, Boolean returnGeometry=true) {

		// clean urls from empty values
		List cleanUrls = urls.findAll{it}
	
		// create a geometry point.
		def geometry = new JsonBuilder()
		geometry {x xCoord; y yCoord}

		String geometryType = 'esriGeometryPoint'
		String spatialRel = 'esriSpatialRelIntersects'
		String outFields = '*'
		String f = 'json'

		// build the query parameters.
		def query = [geometry:geometry.toString(),
					geometryType:geometryType,
					spatialRel:spatialRel,
					f:f,
					outFields:outFields,
					returnGeometry:returnGeometry]

		// get the data for the given urls.
		def results = httpService.request(cleanUrls, query)

		// clean data
		cleanData(results, returnGeometry).collectEntries {key, value ->
			[key, value[0]]}		
	}


	public Map featuresAttributesFromPointLayersIntersection(String xCoord, String yCoord, List urls) {

		Map data = featuresFromPointLayersIntersection(xCoord, yCoord, urls, false)
		data.collectEntries {key, value -> [key, value?.attributes]}
	}


	public Map featuresGeometryFromPointLayersIntersection(String xCoord, String yCoord, List urls) {

		Map data = featuresFromPointLayersIntersection(xCoord, yCoord, urls, true)
		data.collectEntries {key, value -> [key, value?.geometry]}

	}
	
	
	public Map featuresInCircle(Double x, Double y, Integer radius, String url, Boolean returnGeometry=true){

		// Get circle buffer to intersect with the layer.
		def geometryCircle = getCircleBuffer(x, y, radius)

		// Build the query parameters.
		Map query = [geometry:geometryCircle, 
				geometryType:'esriGeometryPolygon',
				f:'json',
				outFields:'*',
				returnGeometry:returnGeometry]
		
		// Make http call to layer to get the desired features.
		def data = httpService.requestPost(url, query, groovyx.net.http.ContentType.JSON)

		// Clean data
		cleanData([(url):data], true)

	}
	
	public Map featuresAttributesInCircle(Double x, Double y, Integer radius, String url){		
		Map data = featuresInCircle(x, y, url, false)
		data.collectEntries {key, value -> [key, value?.attributes]}
	}
	
	public Map featuresGeometryInCircle(Double x, Double y, Integer radius, String url){
		Map data = featuresInCircle(x, y, url, true)
		data.collectEntries {key, value -> [key, value?.geometry]}
	}
	
	public Map featuresInPolygon(Map geometry, String url, List outFields = ['*'], Boolean returnGeometry=true){

		// Create an empty geometry if null or empty geometry is passed in.
		if(!geometry || geometry == [rings:[]])
		  return [(url):[]]
		
		// Wrap the geomtry in a json object.
		def jsonGeometry = new JsonBuilder(geometry)

		// Make http call to layer to get the desired features.spatialRel:esriSpatialRelIntersects
		Map query = [geometry:jsonGeometry, 
			geometryType:'esriGeometryPolygon', 
			spatialRel:'esriSpatialRelIntersects',
			f:'json',
			outFields:outFields.join(','),
			returnGeometry:returnGeometry]
		
		def data = httpService.requestPost(url, query, groovyx.net.http.ContentType.JSON)

		// Clean the data
		cleanData([(url):data], true)
		
	}
	
	public Map featuresAttributesInPolygon(Map geometry, String url, List outFields = ['*']){
		Map data = featuresInPolygon(geometry, url, outFields, false)
		data.collectEntries {key, value -> [key, value?.attributes]}
	}
	
	public Map featuresGeometryInPolygon(Map geometry, String url, List outFields = ['*']){
		Map data = featuresInPolygon(geometry, url, outFields, true)
		data.collectEntries {key, value -> [key, value?.geometry]}
	}
	
	public Map featuresByProximity(Double x, Double y, String url, String uniqueAttribute, Integer radius = 20){
		
	   List features = []
	   
	   // Get features doubling radius if no features are found.
	   while (!features && radius < 500 ){
		   Map data = featuresInCircle(x, y, radius, url)
		   		   
		   features = data[url]
		   radius = 2 * radius;
	   }
	   
	   // elimininate duplicates. With no uniqueAttribute do not filter duplicates.
	   List uniqueFeatures = []
	   if(!uniqueAttribute)
	     uniqueFeatures = features
	   else
		 uniqueFeatures = features?.unique {
			 it.attributes?."$uniqueAttribute"}
	   	   
	   return [(url):uniqueFeatures]
	   
   }
	
	public Map featuresAttributesByProximity(Double x, Double y, String url, String uniqueAttribute, Integer radius = 20){
		Map data = featuresByProximity(x, y, url, uniqueAttribute, radius)
		data.collectEntries {key, value -> [key, value?.attributes]}
		
	}
	public Map featuresGeometryByProximity(Double x, Double y, String url, String uniqueAttribute, Integer radius = 20){
		Map data = featuresByProximity(x, y, url, uniqueAttribute, radius)
		data.collectEntries {key, value -> [key, value?.geometry]}
	}
	
	public Map featuresByIds(String url, String idAttribute,  List ids, Boolean returnGeometry=true) {
		if(!ids)
			return [(url):[]]
		
		// build the query parameters.
		String where = ids.collect {id -> "${idAttribute} = '${id}'"}.join(' or ')
		String outFields = '*'
		String f = 'json'

		Map query = [where:where,
					f:f,
					outFields:outFields,
					returnGeometry:returnGeometry]

		// Make http call to layer to get the desired features.
		def data = httpService.request(url, query)

		// clean data
		cleanData([(url):data], true)
	}
	
	public Map featuresAttributesByIds(String url, String idAttribute,  List ids) {
		Map data = featuresByIds(url, idAttribute, ids, false)
		data.collectEntries {key, value -> [key, value?.attributes]}
	}
	
	public Map featuresGeometryByIds(String url, String idAttribute,  List ids) {
		Map data = featuresByIds(url, idAttribute, ids, true)
		data.collectEntries {key, value -> [key, value?.geometry]}
	}
	
	
	public Map queryFeatures(String url, Map query) {
		
		// Make http call to layer to get the desired features.
		def data = httpService.request(url, query)

		// clean data
		cleanData([(url):data], true)
	}
					
	Map cleanData(Map dirtyData, Boolean returnGeometry) {

		dirtyData.collectEntries {key, value ->
			if(value.error) {
				_log.error("cleanData() - error returned by layer ${key} | ${value.error}")
				throw new RetrievalOfDataException("Unexpected error for uri ${key} | message: ${value}")
			}

			if (returnGeometry)
				[key, extractFeatures(value)]
			else
				[key, extractFeatures(value).collect{[attributes:it.attributes]}]
		}
		
	}
	
	
	List extractFeatures(Map dirtyData) {
				
		// Extract attributes and geometry for each feature.
		List features = []
		dirtyData.features.each {feature->
			Map attributes = cleanAttributes(feature?.attributes?:[:]) 
			Map geometry = feature?.geometry?:[:]
			features << [attributes:attributes, geometry:geometry]
		}
		
		return features
	}
	
	Map cleanAttributes(Map dirtyAttributes) {
		
		dirtyAttributes.collectEntries {key, value ->
			// clean attributes	by mapping JSONNull to null.
			if (value instanceof JSONNull)
				value = null
			// clean more than one space.	
			else if(value instanceof String) 
				value = (value =~ / +/).replaceAll(" ").trim()
			
			[key, value]
		}
	}

}
