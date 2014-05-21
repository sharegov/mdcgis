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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext

import org.sharegov.mdcgis.utils.AppContext;
import org.sharegov.mdcgis.model.Address
import org.sharegov.mdcgis.model.CommonLocation

class CommonLocationsService {

	private static Logger _log = LoggerFactory.getLogger(CommonLocationsService.class);
	AddressService addressService
	HTTPService httpService
	GisConfig gisConfig


	/**
	 * Get a list of commonLocations with addresses partially filled matching the name of a 
	 * common location. address.name and address.layer are populated.
	 * @param name - common location to match
	 * @param filterLayers - layer to look for matches
	 * @return List - list of CommonLocation type matching the name in the filterLayers.
	 * It returns an empty list if no match is found.
	 */
	List getCommonLocationCandidates(String name, List filterLayers){
        
		// No name -> No Common Location. Return right away.
		if(!name)
			return []
			
		// Clean the filter layers
		// No filter specify take all in gisConfig.commonLocationsLayerNames
		List layerNames = []
		if(!filterLayers)
			layerNames = gisConfig.commonLocationsLayerNames
		else
			layerNames = gisConfig.commonLocationsLayerNames.intersect(filterLayers)

		// get data from common locations layer
		List commonLocations = []
		def rawCommonLocations = getRawCommonLocations(name, layerNames)

		// when only one location found build the whole object
		if(rawCommonLocations?.size() == 1){
		  commonLocations << getCommonLocation(rawCommonLocations[0]?.OBJECTID)       	
		}
		else{
			rawCommonLocations.each {location ->
				// pick relevant data in the common locations layer
				CommonLocation commonLocation = new CommonLocation(name:location?.NAME, layer:location?.LAYER, id:location?.OBJECTID)
				commonLocations << commonLocation
			}
		}

		return commonLocations

	}


	/**
	 * returns a commonLocation matching the name and the layer with address fully populated.
	 * @param name - name of the common location
	 * @param layer - layer where the common location is at.
	 * @return CommonLocation - returns a CommonLocation with fully populated address object for the common
	 * place that match name and layer. If no match or more than one match exists it returns null
	 */
	CommonLocation getCommonLocation(String name, String layer){

		List filterLayers = [layer]
		List layerNames = gisConfig.commonLocationsLayerNames.intersect(filterLayers)

		// get data from common locations layer
		def rawCommonLocations = getRawCommonLocations(name, layerNames)

		CommonLocation commonLocation
		if(rawCommonLocations.size() == 1) {

			// call address service to get an address object

			Address candidate = addressService.getAddress(rawCommonLocations[0]?.ADDRESS, rawCommonLocations[0]?.ZIP as String)
			commonLocation = new CommonLocation(name:rawCommonLocations[0]?.NAME, layer:rawCommonLocations[0]?.LAYER, id:rawCommonLocations[0]?.OBJECTID)
			commonLocation.address = candidate

		} else
			commonLocation = null

		return commonLocation

	}


	/**
	 * returns a commonLocation matching the name and the layer with address fully populated.
	 * @param id - the id in the MDC.CommonLocations layer
	 * @return CommonLocation - returns a CommonLocation with fully populated address object for the common
	 * place with OBJECTID id in the MDC.CommonLocations layer. If no match or more than one match exists it returns null
	 */
	CommonLocation getCommonLocation(Long id){

		// get data from common locations layer
		def rawCommonLocations = getRawCommonLocations(id)

		CommonLocation commonLocation
		if(rawCommonLocations.size() == 1) {

			// call address service to get an address object

			Address candidate = addressService.getAddress(rawCommonLocations[0]?.ADDRESS, rawCommonLocations[0]?.ZIP as String)
			commonLocation = new CommonLocation(name:rawCommonLocations[0]?.NAME, layer:rawCommonLocations[0]?.LAYER, id:rawCommonLocations[0]?.OBJECTID)
			commonLocation.address = candidate

		} else
			commonLocation = null

		return commonLocation

	}




	/**
	 * Searches on the common locations layer based on the following where clause:
	 * UPPER(NAME) LIKE '%location.toUpperCase%' and (layer='a' or layer='b' ...)
	 * 
	 * @param name - name of the common location 
	 * @param filterLayerNames - layers to look on for 'name'
	 * @return List - Common locations found. Empty list if name is empty string or null.
	 */
	List getRawCommonLocations(String name, List filterLayerNames){

		if(!name)
			return []

		// no filter use the most generic set of filters
		if(!filterLayerNames)
			filterLayerNames = gisConfig.commonLocationsLayerNames

		// build the where clause
		String orWhere = filterLayerNames.collect{"layer='${it}'"}.join(' or ')
		String where = "UPPER(NAME) LIKE '%${name.toUpperCase()}%' "  + (orWhere? "and ( ${orWhere} )" :"")
		String outFields = '*'
		String f = 'json'

		// create the query attributes
		def query = [where:where,
					f:f,
					outFields:outFields,
					returnGeometry:false]

		//url of commons location layer
		String url = gisConfig.layers['MDC.CommonLocations']

		// Make call to http client
		def result = httpService.request(url, query)

		result.features.collect{feature->feature.attributes as Map}

	}



	/**
	 * 
	 * Searches on the common locations layer by id
	 *
	 * @param id - id of the entry in the MDC.CommonLocations layer
	 * @return - entry found in the MDC.CommonLocations layer 
	 */
	List getRawCommonLocations(Long id){

		// create the query attributes
		String where = "OBJECTID = '${id}'"
		String outFields = '*'
		String f = 'json'

		def query = [where:where,
					f:f,
					outFields:outFields,
					returnGeometry:false]

		//url of commons location layer
		String url = gisConfig.layers['MDC.CommonLocations']

		// Make call to http client
		def result = httpService.request(url, query)

		result.features.collect{feature->feature.attributes as Map}

	}

	/**
	 * Get a String representation of the Polling location
	 * @param precinctId - id of the precinct in the form xxx.0 like 156.0. If only 156 is 
	 * passed the .0 will be added.
	 * @return String - address, municipality (name) - 
	 * ie: 9900 NE 2 Ave, Miami Shores (Catholic Community Svc. Sr. Center)
	 */
	String getPollingLocationByPrecinct(String precinctId){
		
		// Append .0 to precinctId if only digits are passed.
		if (precinctId ==~ /^\d+$/)
			precinctId = "${precinctId}.0"

		// precinctId needs to be of the form xxx.x	
		if (!(precinctId ==~/^\d+\.0$/))
			return ""
					
		// Get the Raw data
		Map pollingLocation = getRawPollingLocationByPrecinct(precinctId)
		if (!pollingLocation)
			return "" 
			
		// Build a simple string for now
		"${pollingLocation['address']}, ${pollingLocation['municipality']} (${pollingLocation['name']})"	

	}
	
	/**
	 * finds the polling place for a given precinct.
	 * @param precinctId - the id of the precinct for which we want to know the polling place.
	 * @return Map - ['name':'Catholic Community Svc. Sr. Center','address':'9900 NE 2 Ave','municipality':'Miami Shores']
	 * It will return null if either no precinctId is passed or the precinctId is not found.
	 */
	Map getRawPollingLocationByPrecinct(String precinctId) {

		// returns null if no precinct is passed
		if(!precinctId) return null
		
		// Make call to http client to get xml
		def query = [Prec:precinctId]
		String url = gisConfig.gisServices['pollingPlaceByPrecinct']
		def xmlRawPollingLocation = httpService.request(url, query, groovyx.net.http.ContentType.XML)
		
		// break data and store it in a map	
		def fields = xmlRawPollingLocation?.text()?.tokenize(",").collect{it.trim()}
		if (!fields)
			return null
			
		['name':fields[0],'address':fields[1],'municipality':fields[2]]
	}

}
