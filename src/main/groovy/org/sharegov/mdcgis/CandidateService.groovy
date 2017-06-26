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

abstract class CandidateService {

	private static Logger _log = LoggerFactory.getLogger(CandidateService.class);
	HTTPService httpService
	FeatureService featureService
	GisConfig gisConfig
	
	Map candidateAttributes

	/**
	 * get latitude/longitude for given x/y
	 * @param x
	 * @param y
	 * @param url
     * @return lat and lng
     */
	Map getLatAndLng(double x, double y, String url ){
		_log.info("CandidateService - getLatAndLng x: "+ x + " y: " + y)

		def xml = httpService.request(url, [X:x, Y:y], groovyx.net.http.ContentType.XML)
		if(!xml) return [:]

		return ["lng":xml.double[0]?.text(), "lat":xml.double[1]?.text()]
	}

	/**
	 * get X/Y for given latitude/longitude
	 * @param latitude
	 * @param longitude
	 * @param url
     * @return converted x and y data or empty data
     */
	Map getXandY(double latitude, double longitude, String url){
		_log.info("CandidateService - getXandY  latitude: "+ latitude + " longitude: " + longitude)

		def xml = httpService.request(url, [LNG:longitude, LAT:latitude], groovyx.net.http.ContentType.XML)
		if(!xml) return [:]
		String x = xml.double[0]?.text()
		String y = xml.double[1]?.text()

		//Invalid data... return null.
		if(x.equalsIgnoreCase("NaN") || y.equalsIgnoreCase("NaN")){
			return [:]
		}
		return [x:x, y: y]
	}


	/**
	* Produces a list of address candidates with address, zip. If only one
	* candidate is match a fully populated address object is returned. If
	* several candidates are found, the address objecs are only populated partially.
	* @param address
	* @param zip
	* @return List<Address> - List of candidates. Empty list if no candidates are found.
	*/
	List getCandidates(String street, String zip){
	
		getCandidates(street, zip, (Integer) null)	

	}

	/**
	 * Produces a list of address candidates with address, zip, municipalityId. 
	 * There are cases like intersections, where same address and same zip will belong
	 * to 2 or more differet municipalities. If only one
	 * candidate is match a fully populated address object is returned. If
	 * several candidates are found, the address objecs are only populated partially.
	 * @param street
	 * @param zip
	 * @param municipalityId
	 * @return
	 */
	List getCandidates(String street, String zip, Integer municipalityId){
		// No street -> No candidates. Done
		if(!street)
			return []
			
		// standarize the street
		String standarizeStreet = standarizeAddress(street)

		// No street -> No candidates, otherwise try it.
		if(!standarizeStreet)
			[]
		else{
			// get a list of candidates		
			List candidates = []
			candidates = cleanCandidates(getRawCandidates(standarizeStreet, zip, municipalityId).candidates)
		}
	}
	
	/**
	 * Get raw data from the GIS service in question. 
	 * @param address
	 * @param zip
	 * @return 
	 */
	abstract def getRawCandidates(String address, String zip);
	
	/**
	 * Get raw data from the GIS service in question.
	 * Important in the case of intersections that are in the boundary lines of 
	 * municipalities.
	 * @param address
	 * @param zip
	 * @param municipalityId
	 * @return
	 */
	abstract def getRawCandidates(String address, String zip, Integer municipalityId);
	
	/**
	 * Get raw data from the GIS service in question.
	 * Important in the case of intersections that are in the boundary lines of 
	 * municipalities.
	 * @param address
	 * @param zip
	 * @param municipality
	 * 
	 * @return
	 */
	abstract def getRawCandidates(String address, String zip, String municipality);

	/**
	 * Do to the scoring mechanism of candidates in different 
	 * versions, different implementations are needed.
	 * @param candidates
	 * @return an empty array if there are no candidates
	 */
	abstract def cleanCandidates(def candidates);

	/**
	 * Given an address, with NO zip, ie:111 nw 1st street,
	 * it will try address standarization. If no standarization for
	 * the given 'address', it will try to use intersection
	 * standarization. Standarization does not find real addresseses, it 
	 * is just formatting given their internal rules. 
	 * @param address - address to be standarize wether it is an address or an intersection.
	 * @return String - the new standarized address string.
	 */
	String standarizeAddress(String address){

		//try address standarization
		def query = [myaddress:address]
		String standarizeUrl = gisConfig.gisServices['standardizeAddress']
		// Make call to http client
		def xml = httpService.request(standarizeUrl, query, groovyx.net.http.ContentType.XML)
		String standarizeAddress = xml.StdAddress?.text()
		standarizeAddress = (standarizeAddress =~ / +/).replaceAll(" ").trim()
		if (standarizeAddress)
			return standarizeAddress

		//if no address standarization try intersection standarization
		query = [myintersection:address]
		standarizeUrl = gisConfig.gisServices['standardizeIntersection']
		// Make call to http client
		xml = httpService.request(standarizeUrl, query, groovyx.net.http.ContentType.XML)
		standarizeAddress = xml.StdIntersection?.text()
		standarizeAddress = (standarizeAddress =~ / +/).replaceAll(" ").trim()
		return standarizeAddress

	}
	
	/**
	 * 
	 * Attention: this is a hacky standarazition of address, while another method comes.
	 * Currently there is no service to do standarization of streets ie:NW 1 StReet, but there
	 * is support of standarizaiton of address ie: 111 NW 1 StReet.
	 * A fake number will be created to prefix the street, standarize as an address and drop the
	 * fake number after standarization.
	 * @param street
	 * @return
	 */
	String standardizeStreet(String street){
		
		// Add a dummy number to the street.
		String dummyAddress = "999999 " + street
		
		//try address standarization
		def query = [myaddress:dummyAddress]
		String standarizeUrl = gisConfig.gisServices['standardizeAddress']
		// Make call to http client
		def xml = httpService.request(standarizeUrl, query, groovyx.net.http.ContentType.XML)
		String standardizeAddress = xml.StdAddress?.text()
		standardizeAddress = (standardizeAddress =~ / +/).replaceAll(" ").trim()
		if (!standardizeAddress)
			return street
		else
			return standardizeAddress.minus("999999")
	}
	
	Map parseAddress(String address){
		
		//try address standarization
		def query = [myaddress:address]
		String standarizeUrl = gisConfig.gisServices['standardizeAddress']
		// Make call to http client
		def xml = httpService.request(standarizeUrl, query, groovyx.net.http.ContentType.XML)
		String standarizeAddress = xml.StdAddress.text()
		standarizeAddress = (standarizeAddress =~ / +/).replaceAll(" ").trim()

		// build the parsed address
		Map parsedAddress = [:]
		if (standarizeAddress){
			parsedAddress.House = xml.House_Num.text().trim()
			parsedAddress.PreDir = xml.Pre_Dir.text().trim()
			parsedAddress.StreetName = "${xml.St_Name.text()} ${xml.St_Type.text()}".trim()
			parsedAddress.SufType = xml.St_Type2.text().trim()
			parsedAddress.SufDir = xml.Suf_Dir.text().trim()
		
			return parsedAddress	
		}
		
		
		//if no address standarization try intersection standarization
		query = [myintersection:address]
		standarizeUrl = gisConfig.gisServices['standardizeIntersection']
		// Make call to http client
		xml = httpService.request(standarizeUrl, query, groovyx.net.http.ContentType.XML)
		standarizeAddress = xml.StdIntersection.text()
		standarizeAddress = (standarizeAddress =~ / +/).replaceAll(" ").trim()
		
		// build parsed address
		if (standarizeAddress){
			parsedAddress.PreDir1 = xml.Pre_Dir.text().trim()
			parsedAddress.StreetName1 = "${xml.St_Name.text()} ${xml.St_Type.text()}".trim()
			parsedAddress.SufType1 = xml.St_Type2.text().trim()
			parsedAddress.SufDir1 = xml.Suf_Dir.text().trim()
	
			parsedAddress.PreDir2 = xml.Pre_Dir_2.text().trim()
			parsedAddress.StreetName2 = "${xml.St_Name_2.text()} ${xml.St_Type_2.text()}".trim()
			parsedAddress.SufType2 = xml.St_Type2_2.text().trim()
			parsedAddress.SufDir2 = xml.Suf_Dir_2.text().trim()

		}
		
		return parsedAddress
	}


}
