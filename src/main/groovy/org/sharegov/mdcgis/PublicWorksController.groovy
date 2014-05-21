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

import groovy.json.JsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class PublicWorksController {

	PublicWorksService publicWorksService
	
	private static Logger _log = LoggerFactory.getLogger(PublicWorksController.class)

	
	public JsonBuilder getIntersectionData(Map<String, String> queryParams) {
		
		// get Parameters
		String street1 = queryParams["street1"]
		String street2 = queryParams["street2"]

		// Get data from the area delimited by the above streets
		Map answer = [:]
		try{
			Map data = publicWorksService.intersection(street1, street2)
			answer = [ok:true, data:data]

		}catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message, data:[:]]
			//sendMail(rode.message)
		}

		
		_log.info("getAreaData - About to Finish - streets: " +
				  street1 + " " + street2 + " " + 
				  " with data " + answer)

		// Convert map to json object
		JsonBuilder json = new JsonBuilder()
		json.call(answer)
		
	}
	
	
	public JsonBuilder getCorridorData(Map<String, String> queryParams) {
		
		// get Parameters
		String street1 = queryParams["street1"]
		String street2 = queryParams["street2"]
		String street3 = queryParams["street3"]

		// Get data from the area delimited by the above streets
		Map answer = [:]
		try{
			Map data = publicWorksService.corridor(street1, street2, street3)
			answer = [ok:true, data:data]

		}catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message, data:[:]]
			//sendMail(rode.message)
		}

		
		_log.info("getAreaData - About to Finish - streets: " +
				  street1 + " " + street2 + " " + street3 + 
				  " with data " + answer)

		// Convert map to json object
		JsonBuilder json = new JsonBuilder()
		json.call(answer)
		
	}
	
	
		
	public JsonBuilder getAreaData(Map<String, String> queryParams) {
		
		// get Parameters
		String street1 = queryParams["street1"]
		String street2 = queryParams["street2"]
		String street3 = queryParams["street3"]
		String street4 = queryParams["street4"]

		// Get data from the area delimited by the above streets
		Map answer = [:]
		try{
			Map data = publicWorksService.area(street1, street2, street3, street4)
			answer = [ok:true, data:data]

		}catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message, data:[:]]
			//sendMail(rode.message)
		}

		
		_log.info("getAreaData - About to Finish - streets: " + 
			      street1 + " " + street2 + " " + street3 + " " + street4 +  
			      " with data " + answer)

		// Convert map to json object
		JsonBuilder json = new JsonBuilder()
		json.call(answer)
		
	}
	
}
