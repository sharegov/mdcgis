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

import static mjson.Json.make;
import static mjson.Json.read;
import groovy.json.JsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.PathParam;

import mjson.Json;

import org.restlet.Request;
import org.restlet.data.Form;
import org.sharegov.mdcgis.model.Address;
import org.sharegov.mdcgis.model.CommonLocation;
import org.sharegov.mdcgis.utils.AppContext;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AddressController {

	private static Logger _log = LoggerFactory.getLogger(CandidateService.class);

	GisConfig gisConfig
	AddressService addressService
	CandidateService candidateService
	CommonLocationsService commonLocationsService
	FeatureService featureService
	CacheManager cacheManager
	MailSender mailSender

	/**
	 * It will find a list of possible address candidates for the given parameters
	 * @param queryParams - street, zip, municipality, municipalityId.
	 * The parameter municipality takes precedence over 
	 * municipalityId. There is a mapping between municipality and municipalityId.
	 * @return JsonBuilder - On success json of the form 
	 * [ok:true, message:"some message", candidates:[{},{},...]]
	 * On failure json of the form 
	 * [ok:false, message:"bad message", candidates:[]]
	 * It is possible to have a successful retrieve of candidates where no candidates 
	 * were found. The return would be 
	 * [ok:true, message:"some message", candidates:[]]
	 */
	public JsonBuilder getCandidates(Map<String, String> queryParams) {

		// get Parameters
		String address = queryParams["street"]
		String zip = queryParams["zip"]
		String municipalityId = queryParams["municipalityId"]
		String municipality = queryParams["municipality"]
		Integer intMunicipalityId

		// clean municipalityId
		if (municipalityId == null)
			intMunicipalityId = null
		else
			try {
				intMunicipalityId = Integer.valueOf(municipalityId)
			} catch (NumberFormatException nfe) {
				intMunicipalityId = null
			}

		// municipality name takes precedence municipality id
		if (municipality != null)
			intMunicipalityId = (Integer) gisConfig.municipalities.getKey(
					municipality.trim().toUpperCase());

		_log.info("getCandidates - Start - address: " + address + " zip: " +
				zip + " municipalityId: " + intMunicipalityId);

		// Get candidates
		Map answer = [:]
		try{
			List candidates = addressService.getCandidateAddresses(address,
					zip, intMunicipalityId);
			answer = [ok:true, candidates:candidates]

		}catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message, candidates:[]]
			sendMail(rode.message)
		}


		_log.info("getCandidates - About to Finish - address: " + address +
				" zip: " + zip + " with data " + answer)
		_log.info("getCandidates - Finish - address: " + address +
				" zip: " + zip + " with code " + answer.ok)

		// Convert map to json object
		JsonBuilder json = new JsonBuilder()
		json.call(answer)
	}


	/**
	 * Get the address on the x,y.
	 * @param queryParams - x, y
	 * @return JsonBuilder - On success json of the form 
	 * [ok:true, message:"some message", address:{...}]
	 * On failure json of the form 
	 * [ok:false, message:"bad message", address:{}]
	 * It is possible to have a successful retrieve of address where no address 
	 * was found for the given x, y. The return would be 
	 * [ok:true, message:"some message", address:{}]
	 */
	public JsonBuilder getAddressFromCoords(Map<String, String> queryParams) {

		String x = queryParams["x"]
		String y = queryParams["y"]

		_log.info("getAddressFromCoords - Start - x,y: " + x + "," + y);

		// Get Address
		Map answer = [:]
		try{
			Address address = addressService.getAddressFromCoord(
					org.sharegov.mdcgis.Utils.round(Double.valueOf(x), 3),
					org.sharegov.mdcgis.Utils.round(Double.valueOf(y), 3));

			if(!address)
				answer = [ok:true, address:null]
			else
				answer = [ok:true, address:address]


		}catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message, address:[:]]
			sendMail(rode.message)
		}

		_log.info("getAddressFromCoords - Finish - x,y: " + x + "," + y + " with code " + answer.ok);

		// Convert map to json object
		JsonBuilder json = new JsonBuilder()
		json.call(answer)
	}

	/**
	 * Get an address for a condo
	 * @param queryParams - street, zip, unit, municipalityId
	 * @return JsonBuilder - On success json of the form
	 * [ok:true, message:"some message", address:{...}]
	 * On failure json of the form
	 * [ok:false, message:"bad message", address:{}]
	 * It is possible to have a successful retrieve of address where no address
	 * was found for the given x, y. The return would be
	 * [ok:true, message:"some message", address:{}]
	 */
	public JsonBuilder getCondoAddress(Map<String, String> queryParams) {

		String street = queryParams["street"]
		String zip = queryParams["zip"]
		String unit = queryParams["unit"]
		String municipalityId = queryParams["municipalityId"]
		Integer intMunicipalityId;
		if (municipalityId == null)
			intMunicipalityId = null;
		else
			intMunicipalityId = Integer.valueOf(municipalityId);

		_log.info("getCondoAddress - Start - street: " + street + " zip: "
				+ zip + " unit: " + unit);

		// Get Address
		Map answer = [:]

		try{
			Address address = addressService.getCondoAddress(street, zip, unit,
					intMunicipalityId);

			if(!address)
				answer = [ok:true, address:null]
			else
				answer = [ok:true, address:address]

		}catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message, address:[:]]
			sendMail(rode.message)
		}

		_log.info("getCondoAddress - Finish - street: " + street + " zip: "
				+ zip + " unit: " + unit + " with code " + answer.ok);

		// Convert map to json object
		JsonBuilder json = new JsonBuilder()
		json.call(answer)

	}

	/**
	 * Get an address for a folio
	 * @param queryParams - folio
	 * @return JsonBuilder - On success json of the form
	 * [ok:true, message:"some message", address:{...}]
	 * On failure json of the form
	 * [ok:false, message:"bad message", address:{}]
	 * It is possible to have a successful retrieve of address where no address
	 * was found for the given x, y. The return would be
	 * [ok:true, message:"some message", address:{}]
	 */
	public JsonBuilder getAddress(Map<String, String> queryParams) {

		String folio = queryParams["folio"]

		_log.info("getAddress - Start - folio: " + folio)

		// Get Address
		Map answer = [:]
		try{
			Address address = addressService.getAddressByFolio(folio);
			if(!address)
				answer = [ok:true, address:null]
			else
				answer = [ok:true, address:address]


		}catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message, address:[:]]
			sendMail(rode.message)
		}

		_log.info("getAddress - Finish - folio: " + folio + " with code " + answer.ok)

		// Convert map to json object
		JsonBuilder json = new JsonBuilder()
		json.call(answer)

	}

	/**
	 * It will find a list of possible common location candidates for the given parameters
	 * @param queryParams - name, layers
	 * name: the name of the common location to search for ie: "Jackson Hostpital"
	 * layers: List of layers where to search. Find the list at 
	 * gisConfig.commonLocationsLayerNames. ie: "COLLEGE", "DAYCARE", ... 
	 * @return JsonBuilder - On success json of the form 
	 * [ok:true, message:"some message", candidates:[{},{},...]]
	 * On failure json of the form 
	 * [ok:false, message:"bad message", candidates:[]]
	 * It is possible to have a successful retrieve of candidates where no candidates 
	 * were found. The return would be 
	 * [ok:true, message:"some message", candidates:[]]
	 */
	public JsonBuilder getCommonLocationCandidates(Map<String, String> queryParams) {

		String name = queryParams["name"]

		_log.info("getCommonLocationCandidates - Start - name: " + name);

		List<String> layers = new ArrayList<String>();
		Collections.addAll(layers, queryParams["layer"]);

		if (layers.isEmpty())
			layers.addAll(gisConfig.commonLocationsLayerNames)

		Map answer = [:]
		try{
			List candidates = commonLocationsService.getCommonLocationCandidates(name, layers);
			answer = [ok:true, candidates:candidates]

		}catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message, candidates:[]]
			sendMail(rode.message)
		}

		_log.info("getCommonLocationCandidates - Finish - name: " + name + " with code " + answer.ok);

		JsonBuilder json = new JsonBuilder();
		json.call(answer);
	}


	/**
	 * Get a common location by id
	 * @param id - the id of the common location
	 * @return JsonBuilder - On success json of the form
	 * [ok:true, message:"some message", commonlocation:{...}]
	 * On failure json of the form
	 * [ok:false, message:"bad message", commonlocation:{}]
	 * It is possible to have a successful retrieve of address where no address
	 * was found for the given x, y. The return would be
	 * [ok:true, message:"some message", commonlocation:{}]
	 */
	public JsonBuilder getCommonLocations(Long id) {

		_log.info("getCommonLocations - Start - id: " + id);

		Map answer = [:]
		try{
			CommonLocation commonLocation = commonLocationsService.getCommonLocation(id);
			if(!commonLocation)
				answer = [ok:true, commonlocation:null]
			else
				answer = [ok:true, commonlocation:commonLocation]

		}catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message, commonlocation:[:]]
			sendMail(rode.message)
		}

		_log.info("getCommonLocations - Finish - id: " + id + " with code " + answer.ok);

		JsonBuilder json = new JsonBuilder();
		json.call(answer);

	}

	/**
	 * Obtain attributes from the layers where a point interserct that layer
	 * @param queryParams - 
	 * layer: is a list of possible layers to intersect. If
	 * no layer specified use all at gisConfig.serviceLayers
	 * x: x-coord
	 * y: y-coord
	 * @return JsonBuilider -
	 * On success json of the form
	 * [ok:true, data:['layername1':[attr1:value1,...],'layername2':[attr1:value1,...], ....]
	 * On failure json of the form
	 * [ok:false, message:"bad message"]
	 */
	public JsonBuilder getServiceLayers(Map<String, String> queryParams) {

		String x = queryParams["x"]
		String y = queryParams["y"]
		List layers = queryParams["layer"]

		_log.info("getServiceLayers - Start - x,y: " + x + "," + y);

		// If no layers are given, use all of them.
		if (!layers)
			layers = gisConfig.serviceLayers

		List<Double> coords = Arrays.asList(
				org.sharegov.mdcgis.Utils.round(Double.valueOf(x), 3),
				org.sharegov.mdcgis.Utils.round(Double.valueOf(y), 3));

		// Get the data in the layers. If errors occur retry.
		Map answer = [:]
		int counter = 0
		while(!answer.ok && counter < 4){
			answer = getData(coords, layers)
			if(!answer.ok) {
				_log.error "getServiceLayer: error on try # ${counter}. ${answer.message}"
				Thread.sleep(2000)
			}
			counter ++;
		}

		if(!answer.ok)
			_log.error "getServiceLayers. Still error out after ${counter} tries. Give up ... sorry"


		_log.info("getServiceLayers - About to Finish - x,y: " + x + "," + y + " with data " + answer);
		_log.info("getServiceLayers - Finish - x,y: " + x + "," + y + " with code " + answer.ok);
		JsonBuilder json = new JsonBuilder();
		return json.call(answer);

	}


	public JsonBuilder standardizeStreet(Map<String, String> queryParams) {

		String street = queryParams["street"]

		_log.info("standardizeStreet - Start - street: " + street)

		// Get standardized street
		Map answer = [:]
		try{
			String standardizeStreet = candidateService.standardizeStreet(street)
			answer = [ok:true, street:standardizeStreet]


		}catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message]
			sendMail(rode.message)
		}

		_log.info("standardizeStreet - Finish - street: " + street + " with code " + answer.ok)

		// Convert map to json object
		JsonBuilder json = new JsonBuilder()
		json.call(answer)

	}

	/**
	 * get The data from the layers that correspond to 
	 * the coords. If there is already data in cache it is 
	 * because an address with those coords was already used so, we
	 * just have to get the data of the layers not in cache. If no data is
	 * in cache lets first try to see if there is an address associated with those
	 * coords. The reason is not obvious. The only way to get road maitenance data is
	 * from an address. It is not just enough to to intersect the layer with the coord.
	 * After that we can get the rest of the data not capture by the address.
	 * @param coords
	 * @param layers
	 * @return Map - key is the layer name, value is the attributes associated
	 */
	private Map getData(List coords, List layers){
		Map answer = [:]
		try {
			Map data
			// Some data in cache, get the rest
			if (cacheManager.getCache("layers").get(coords) != null) {
				data = getDataFromLayers(coords, layers)

			} else { // No data in cache, the process is a bit different

				// Use address service to retrive address
				Address address = addressService.getAddressFromCoord(coords[0], coords[1]);

				// Now get the data from the layers
				// use address.location as coord because that is where the cache is at
				if(address?.location)
					data = getDataFromLayers([
						address.location.x,
						address.location.y
					], layers)
				// use coords if there was no address
				else
					data = getDataFromLayers(coords, layers)
			}

			answer["data"] = data
			answer["ok"] = true
		}
		catch(RetrievalOfDataException rode) {
			answer = [ok:false, message:rode.message]
			sendMail(rode.message)
		}

		return answer
	}


	/**
	 * Obtain the data from layers by intersecting the coords with layers
	 * Check for any data that is already in the cache.
	 * @param coords - coordinates of point
	 * @param layers - layers to be intersected
	 * @return Map - key is the layer name, value is the attributes associated
	 * with the layer
	 */
	private Map getDataFromLayers(List coords, List layers) {

		// Get data already in cache
		Map<String, Object> cacheData = (Map) cacheManager.getCache("layers").get(coords)?.get() ?: [:]

		// Get layers that were not in cache and intersect against those
		List layersToQuery = layers - cacheData.keySet()

		// Combined cached and non cached results.
		Map data = (Map) featureService.featuresAttributesFromPointLayersIntersection(coords[0].toString(), coords[1].toString(), layersToQuery);
		data.putAll(cacheData);

		return data
	}

	private void sendMail(String body){

		try{
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setFrom("cirmadmin@miamidade.gov")
			helper.setTo([
				'jorgefi@miamidade.gov',
				'dvwerne@miamidade.gov',
				'assia@miamidade.gov'] as String[])
			helper.setSubject("GIS ERROR")
			helper.setText(body);

			Date startTime = new Date()
			mailSender.send(message);
			Date endTime = new Date()
			_log.info "mail sent - ${endTime.time - startTime.time}ms"

		} catch(Exception e){
			_log.error "AddressController.sendMail(): Could not send mail. ${e.message}"
		}

	}

}
