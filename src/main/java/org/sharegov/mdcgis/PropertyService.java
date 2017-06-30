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
package org.sharegov.mdcgis;

import com.newrelic.api.agent.Trace;
import groovy.json.JsonBuilder;
import mjson.Json;
import org.restlet.Request;
import org.restlet.data.Form;
import org.sharegov.mdcgis.utils.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

import static mjson.Json.read;

/**
 * The PropertyService handles the requests from clients interested in GIS
 * information. This service is not trying to duplicate the existing GIS
 * Services. It will add functionality where the GIS group has not provided it
 * or is very specific to our needs.
 * 
 * @author fiallega
 * 
 */

@Path("mdcgis-0.1")
@Produces("application/json")
public class PropertyService {

	private static Logger _log = LoggerFactory.getLogger(PropertyService.class);


	/**
	 * Get address for latitude and longitude.
	 * It expects
	 * 	Query Param - lat and lng
	 *
	 * @return
     */
	@Trace(dispatcher = true)
	@GET
	@Path("/addressFromLatAndLng")
	@Produces("application/json")
	public Json getAddressFromLatitudeAndLongitude() {

		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();

		ApplicationContext ctx = AppContext.getApplicationContext();
		AddressController addressController = (AddressController) ctx.getBean("ADDRESS_CONTROLLER");
		JsonBuilder answer = addressController.getAddressByLatAndLng(queryParams.getValuesMap());

		return read(answer.toString());
	}

	/**
	 * Get Property Information by Folio.
	 * It expects
	 * 		Path Param - folio
	 *
	 * @param folio
	 * @return
     */
	@Trace(dispatcher= true)
	@GET
	@Path("/properties/{folio}")
	@Produces("application/json")
	public Json getPropertyInformation(@PathParam("folio") String folio){

		ApplicationContext applicationContext = AppContext.getApplicationContext();
		PropertyController propertyController = (PropertyController) applicationContext.getBean("PROPERTY_CONTROLLER");
		JsonBuilder propertyInfo = propertyController.propertyByFolio(folio);

		return read(propertyInfo.toString());
	}

	/**
	 * Get Layer Information for given layer.
	 * It expects
	 * 		Path Param - Layer Name
	 * 		Query Params - street, zip, municipality and municipalityId
	 *
	 * @param layername
	 * @return
     */
	@Trace(dispatcher= true)
	@GET
	@Path("/servicelayers/{layerName}")
	@Produces("application/json")
	public Json getLayerInformation(@PathParam("layerName") String layername){

		Form queryParams = Request.getCurrent().getResourceRef().getQueryAsForm();
		ApplicationContext applicationContext = AppContext.getApplicationContext();
		AddressController addressController = (AddressController) applicationContext.getBean("ADDRESS_CONTROLLER");
		JsonBuilder answer = addressController.getLayerInformation(layername, queryParams.getValuesMap());

		return read(answer.toString());
	}

	/**
	 * Gets list of possible address candidates for the given address parameters
	 * It expects
	 * 		queryParams - street, zip, municipality, municipalityId.
	 *
	 * @return
     */
	@Trace(dispatcher = true)
	@GET
	@Path("/candidates")
	@Produces("application/json")
	public Json getCandidates() {

		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();
	
		ApplicationContext ctx = AppContext.getApplicationContext();
		AddressController addressController = (AddressController) ctx.getBean("ADDRESS_CONTROLLER");
		JsonBuilder answer = addressController.getCandidates(queryParams.getValuesMap()); 

		return read(answer.toString());

	}

	@Trace(dispatcher = true)
	@GET
	@Path("/commonlocationcandidates")
	@Produces("application/json")
	public Json getCommonLocationCandidates() {

		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();
		
		Map params = queryParams.getValuesMap();
		params.put("layer", queryParams.getValuesArray("layer"));
		ApplicationContext ctx = AppContext.getApplicationContext();
		AddressController addressController = (AddressController) ctx.getBean("ADDRESS_CONTROLLER");
		JsonBuilder answer = addressController.getCommonLocationCandidates(params); 

		return read(answer.toString());
		

	}

	/**
	 * Get the address on the x,y.
	 * It expects
	 * 		QueryParams - x, y
	 *
	 * @return
     */
	@Trace(dispatcher = true)
	@GET
	@Path("/addressFromCoords")
	@Produces("application/json")
	public Json getAddressFromCoords() {

		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();
		
		ApplicationContext ctx = AppContext.getApplicationContext();
		AddressController addressController = (AddressController) ctx.getBean("ADDRESS_CONTROLLER");
		JsonBuilder answer = addressController.getAddressFromCoords(queryParams.getValuesMap()); 

		return read(answer.toString());

	}

	@Trace(dispatcher = true)
	@GET
	@Path("/condoaddress")
	@Produces("application/json")
	public Json getCondoAddress() {

		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();

		ApplicationContext ctx = AppContext.getApplicationContext();
		AddressController addressController = (AddressController) ctx.getBean("ADDRESS_CONTROLLER");
		JsonBuilder answer = addressController.getCondoAddress(queryParams.getValuesMap()); 

		return read(answer.toString());
		
	}

	@Trace(dispatcher = true)
	@GET
	@Path("/address")
	@Produces("application/json")
	public Json getAddress() {

		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();
		
		ApplicationContext ctx = AppContext.getApplicationContext();
		AddressController addressController = (AddressController) ctx.getBean("ADDRESS_CONTROLLER");
		JsonBuilder answer = addressController.getAddress(queryParams.getValuesMap()); 

		return read(answer.toString());

	}

	@Trace(dispatcher = true)
	@GET
	@Path("/commonlocations/{id}")
	public Json getCommonLocations(@PathParam("id") Long id) {
		
		ApplicationContext ctx = AppContext.getApplicationContext();
		AddressController addressController = (AddressController) ctx.getBean("ADDRESS_CONTROLLER");
		JsonBuilder answer = addressController.getCommonLocations(id); 

		return read(answer.toString());
		
		
	}

	@Trace(dispatcher = true)
	@GET
	@Path("/servicelayers")
	@Produces("application/json")
	public Json getDataLayers() {

		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();

		Map params = queryParams.getValuesMap();
		params.put("layer", queryParams.getValuesArray("layer"));
		ApplicationContext ctx = AppContext.getApplicationContext();
		AddressController addressController = (AddressController) ctx.getBean("ADDRESS_CONTROLLER");
		JsonBuilder answer = addressController.getServiceLayers(params); 

		return read(answer.toString());
	}
		
	@Trace(dispatcher = true)
	@GET
	@Path("/publicworksintersectiondata")
	@Produces("application/json")
	public Json getPublicWorksIntersectionData() {
		
		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();

		ApplicationContext ctx = AppContext.getApplicationContext();
		PublicWorksController publicWorksController = (PublicWorksController) ctx.getBean("PUBLICWORKS_CONTROLLER");
		JsonBuilder answer = publicWorksController.getIntersectionData(queryParams.getValuesMap()); 

		return read(answer.toString());

	}
	
	@Trace(dispatcher = true)
	@GET
	@Path("/publicworkscorridordata")
	@Produces("application/json")
	public Json getPublicWorksCorridorData() {
		
		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();

		ApplicationContext ctx = AppContext.getApplicationContext();
		PublicWorksController publicWorksController = (PublicWorksController) ctx.getBean("PUBLICWORKS_CONTROLLER");
		JsonBuilder answer = publicWorksController.getCorridorData(queryParams.getValuesMap()); 

		return read(answer.toString());

	}
	
	@Trace(dispatcher = true)
	@GET
	@Path("/standardizestreet")
	@Produces("application/json")
	public Json standardizeStreet() {
		
		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();
		
		ApplicationContext ctx = AppContext.getApplicationContext();
		AddressController addressController = (AddressController) ctx.getBean("ADDRESS_CONTROLLER");
		JsonBuilder answer = addressController.standardizeStreet(queryParams.getValuesMap()); 

		return read(answer.toString());		
		
	}
	
	
	@Trace(dispatcher = true)
	@GET
	@Path("/publicworksareadata")
	@Produces("application/json")
	public Json getPublicWorksAreaData() {
		
		Form queryParams = Request.getCurrent().getResourceRef()
				.getQueryAsForm();

		ApplicationContext ctx = AppContext.getApplicationContext();
		PublicWorksController publicWorksController = (PublicWorksController) ctx.getBean("PUBLICWORKS_CONTROLLER");
		JsonBuilder answer = publicWorksController.getAreaData(queryParams.getValuesMap()); 

		return read(answer.toString());

	}
	
	
	@Trace(dispatcher = true)
	@GET
	@Path("/testme")
	@Produces("application/json")
	public Json getTest() {
		
		Request request = Request.getCurrent();
		
		JsonBuilder json = new JsonBuilder();
		Map me = new HashMap();
		me.put("hello", "bye");
		json.call(me);
		return read(json.toString());
	}
	
	
}
