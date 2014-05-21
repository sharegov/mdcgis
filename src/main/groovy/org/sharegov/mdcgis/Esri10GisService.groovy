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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class Esri10GisService extends EsriGisService{

	private static Logger _log = LoggerFactory.getLogger(Esri10GisService.class);

	/**
	 * @inheritDoc
	 */
	Map reverseGeoCode(Double xCoord, Double yCoord, Integer distance = 5) {
		
		String reverseGeoCodeUrl = gisConfig.locators.reverseGeoCode
		reverseGeoCode(xCoord, yCoord, reverseGeoCodeUrl, distance)
	}

	
	/**
	* @inheritDoc
	*/


   Map reverseGeoCode(Double xCoord, Double yCoord, String url, Integer distance = 5) {

	   // create a geometry point.
	   def geometry = new JsonBuilder()
	   geometry {x xCoord; y yCoord}

	   // hardcode other params
	   Integer outSR=2236
	   String f = 'json'

	   def query = [location:geometry,
				   distance:distance,
				   f:f,
				   outSR:outSR]

	   // Reverse Geocode the address x, y -> street, zip
	   def result  = httpService.request(url, query)

	   if (!result?.address)
		   return null
		   
	   return [street:result?.address?.Street?:"", zip:result?.address?.ZIP?:""];
   }
	
	/**
	 * @inheritDoc
	 */
    def getCircleBuffer(Double x, Double y, Integer radius) {
		
		// Build url and query for geometry service
		def center = "${x},${y}"
		Map query = [geometries:center,distances:radius,inSR:2236,f:'json']
		String url = gisConfig.gisGeometryServices['buffer']

		// Make http call to geometry service to get a 'circle polygon'
		def  buffer = httpService.request(url, query)

		return buffer.geometries[0]
	}

}
