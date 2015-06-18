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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Test
import org.junit.Before

import groovy.mock.interceptor.MockFor
import groovyx.net.http.ContentType

import org.sharegov.mdcgis.utils.AppContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext


class EsriGisServiceTests {
	
	private GisService gisService
	private GisConfig gisConfig
	
	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext("configtest.xml");

		ApplicationContext ctx = AppContext.getApplicationContext();
		gisService = (GisService) ctx
				.getBean("GIS_SERVICE");
				
		gisConfig = (GisConfig) ctx
				.getBean("GIS_CONFIG");						
	}
	
	@After
	public void destroy() throws Exception{
		((ClassPathXmlApplicationContext) AppContext.getApplicationContext()).close();
	}

	
	@Test
	public void testFeaturesFromPointLayersIntersection(){
		
		def requestResult = 
		["http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/42/query":
		 [
		  displayFieldName:'ROUTE',
		  fieldAliases:[OBJECTID:'OBJECTID'],
		  fields:[[name:'OBJECTID']],
		  features:[[attributes:[OBJECTID:251, ROUTE:'32A380']]]
		 ]
		]
		
		def cleanRequestResult =
		["http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/42/query":[attributes:[OBJECTID:251, ROUTE:'32A380']]]
		gisService.getHttpService().getMetaClass().request = {List cleanUrls, Map query -> requestResult}
		
		assert gisService.featuresFromPointLayersIntersection("0", "0", [], false) == cleanRequestResult
	}

	
	@Test
	public void testFeaturesFromPointLayersIntersection_ErrorInRequest(){		
		def requestResult = ["http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/43/query":
							[error:[code:"400", message:"bad error", details:[""]]]]
		
		gisService.getHttpService().getMetaClass().request = {List cleanUrls, Map query -> requestResult}
		try{
			gisService.featuresFromPointLayersIntersection("0", "0", [], false)
			assert false
			
		}catch(RetrievalOfDataException rode){
			assert true
		}
	}
	
	@Test
	public void testFeaturesFromPointLayersIntersection_SeveralUrlsInRequestWithOneError(){
		def requestResult =
		["http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/42/query":
		 [
		  displayFieldName:'ROUTE',
		  fieldAliases:[OBJECTID:'OBJECTID'],
		  fields:[[name:'OBJECTID']],
		  features:[[attributes:[OBJECTID:251, ROUTE:'32A380']]]
		 ],
		 "http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/43/query":
		 [
		   error:[code:"400", message:"bad error", details:[""]]
		 ]
		]
		
		def cleanRequestResult =
		[
		 "http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/42/query":
		 [
			 attributes:[OBJECTID:251, ROUTE:'32A380']
		 ],
		 "http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/43/query":
		 [
			 error:[code:"400", message:"bad error", details:[""]]
		 ]
		]
		
		gisService.getHttpService().getMetaClass().request = {List cleanUrls, Map query -> requestResult}
		
		try{
			assert gisService.featuresFromPointLayersIntersection("0", "0", [], false) == cleanRequestResult
			assert false
		}catch(RetrievalOfDataException rode){
			String message = "Unexpected error for uri http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/43/query | message: [error:[code:400, message:bad error, details:[]]]"  
			assert rode.message == message
		}
		
	}
	
	@Test
	public void testFeaturesInCircle(){
		String urlStreetMaint = gisConfig.layers["MDC.StreetMaint"]
		List features = gisService.featuresInCircle(904028.0626120828, 534703.3748988137, 300, urlStreetMaint)[urlStreetMaint]
		assert features.size() == 2
	}
	
	@Test
	public void testFeaturesInCircle_NoFeature(){
		String urlStreetMaint = gisConfig.layers["MDC.StreetMaint"]
		List features = gisService.featuresInCircle(904028.0626120828, 534703.3748988137, 50, urlStreetMaint)[urlStreetMaint]
		assert features.size() == 0
	}
	
	@Test
	public void testFeaturesInCircle_ErrorInStreetMaintenance(){
		
		String urlStreetMaint = gisConfig.layers["MDC.StreetMaint"]
		def requestResult = [error:[code:"400", message:"bad error", details:[""]]]
		
		def cleanRequestResult =
		[(urlStreetMaint):
		 [error:[code:"400", message:"bad error", details:[""]]]
		]

		//featureService.getGisService().getMetaClass().getCircleBuffer = {Double x, Double y, Integer r -> [2, 3]}
		gisService.getHttpService().getMetaClass().requestPost = {String url, Map query, ContentType contentType -> requestResult}

		try {
			assert gisService.featuresInCircle(2.1, 3.1, 5, urlStreetMaint) == cleanRequestResult
			assert false
		} catch (RetrievalOfDataException rode){
			String message = "Unexpected error for uri $urlStreetMaint | message: [error:[code:400, message:bad error, details:[]]]"
			assert rode.getMessage() == message
		}	
	}
	
	@Test
	public void testFeaturesInPolygon(){
		String urlPTXGIS = gisConfig.layers["MDC.PTXGIS"]
		Map geometry = [rings:[[[857827.405,488961.478],[857908.642,488964.622],[857912.028,488852.544],[857832.004,488848.964],[857827.405,488961.478]]]]
		Map features = gisService.featuresInPolygon(geometry, urlPTXGIS)
		assert features[urlPTXGIS].size() == 1
		assert features[urlPTXGIS][0].attributes.FOLIO == '3059010240130'
		assert features[urlPTXGIS][0].attributes.PTXADDR == '11826 SW 97 ST'
	}

	@Test
	public void testFeaturesInPolygon_emptyGeometry(){
		Map geometry = [rings:[]]
		String urlPTXGIS = gisConfig.layers["MDC.PTXGIS"]
		Map features = gisService.featuresInPolygon(geometry, urlPTXGIS)
		assert features[urlPTXGIS] == []

		geometry = null
		features = gisService.featuresInPolygon(geometry, urlPTXGIS)
		assert features[urlPTXGIS] == []
	}
		
	@Test
	public void testFeaturesInPolygon_QueryForGeometry() {
		// x y for 2001 Meridian Ave
		String urlParcels = gisConfig.layers["MDC.Parcels"]
		String urlPTXGIS = gisConfig.layers["MDC.PTXGIS"]
		def geometry = gisService.featuresGeometryFromPointLayersIntersection("940512.061", "532760.323", [urlParcels])
		Map features = gisService.featuresInPolygon(geometry[urlParcels], urlPTXGIS)
		assert features[urlPTXGIS].size() == 113
	}
	
	@Test
	public void testFeaturesInPolygon_QueryForLargeGeometry() {
		// x y
		String urlParcels = gisConfig.layers["MDC.Parcels"]
		String urlPTXGIS = gisConfig.layers["MDC.PTXGIS"]
		def geometry = gisService.featuresGeometryFromPointLayersIntersection("879912.9752018265", "582436.1347285807", [urlParcels])
		Map features = gisService.featuresInPolygon(geometry[urlParcels], urlPTXGIS)
		assert features[urlPTXGIS].size() == 117
	}

	
	@Test
	public void testFeaturesByProximity_LightMaintenance() {
		
		String url = gisConfig.layers["pwd_lights"]
		Map results = gisService.featuresByProximity(904028.0626120828, 534703.3748988137, url, "STNAME1", 200)
		List features = results[url]

		assert features.size() == 2
		assert features[0].attributes.STNAME1 == "NW 31ST AVE"
		assert features[0].attributes.ST_LIGHT == 'LIGHT'
		assert features[1].attributes.STNAME1 == "NW 29TH ST"
		assert features[1].attributes.ST_LIGHT == 'LIGHT'
	}
	
	
	@Test
	public void testFeaturesByProximity_StreetMaintenance(){
		
		String url = gisConfig.layers["MDC.StreetMaint"]
		Map results = gisService.featuresByProximity(904028.0626120828, 534703.3748988137, url, "SNAME", 200)
		List features = results[url]

		assert features.size() == 2
		assert features[0].attributes.SNAME == "NW 31ST AVE"
		assert features[0].attributes.MAINTCODE == 'CO'
		assert features[1].attributes.SNAME == "NW 29TH ST"
		assert features[1].attributes.MAINTCODE == 'CO'
	}
	
	@Test
	public void testFeaturesByProximity(){
		String url = gisConfig.layers["MDC.StreetMaint"]
		Map featuresInCircle =
		[(url):[
			[attributes:[OBJECTID:46004, STREETID:36735, CLASS:5, SNAME:'NW 31ST AVE', ONEWAY:'']],
			[attributes:[OBJECTID:19529, STREETID:36732, CLASS:5, SNAME:'NW 30TH ST', ONEWAY:'']]
			]
		]
		
		//Cannot override methods via metaclass that are part of an interface implementation
		//http://jira.codehaus.org/browse/GROOVY-3493
		Esri10GisService.metaClass.featuresInCircle = {Double x, Double y, Integer radius, String layerName -> featuresInCircle}
		GisService myService = new Esri10GisService()
		myService.metaClass.featuresInCircle = {Double x, Double y, Integer radius, String layerName -> featuresInCircle}
		
		List features = myService.featuresByProximity(0, 0, url, "SNAME", 20)[url]
				
		assert features[0].attributes['SNAME'] == 'NW 31ST AVE'
		assert features[0].attributes['STREETID'] == 36735
		assert features[1].attributes['SNAME'] == 'NW 30TH ST'
		assert features[1].attributes['STREETID'] == 36732
	}
	
	@Test
	public void testFeaturesByProximity_EliminateDuplicates(){
		
		String urlStreetMaint = gisConfig.layers["MDC.StreetMaint"]
		Map featuresInCircle =
		[(urlStreetMaint):[
			[attributes:[OBJECTID:46004, STREETID:36735, CLASS:5, SNAME:'NW 31ST AVE', ONEWAY:'']], 
			[attributes:[OBJECTID:19529, STREETID:36732, CLASS:5, SNAME:'NW 30TH ST', ONEWAY:'']],
			[attributes:[OBJECTID:64067, STREETID:36733, CLASS:5, SNAME:'NW 31ST AVE', ONEWAY:'']]
			]
		]
		
		//Cannot override methods via metaclass that are part of an interface implementation
		//http://jira.codehaus.org/browse/GROOVY-3493		
		Esri10GisService.metaClass.featuresInCircle = {Double x, Double y, Integer radius, String layerName -> featuresInCircle}
		GisService myService = new Esri10GisService()
		myService.metaClass.featuresInCircle = {Double x, Double y, Integer radius, String layerName -> featuresInCircle}
		
		assert myService.featuresByProximity(0, 0, urlStreetMaint, "SNAME", 20)[urlStreetMaint].size() == 2
					
	}
	
	@Test
	public void testFeaturesByProximity_NoFeaturesFound(){
			
		String urlStreetMaint = gisConfig.layers["MDC.StreetMaint"]
		Map featuresInCircle = [(urlStreetMaint):[]]
		
		//Cannot override methods via metaclass that are part of an interface implementation
		//http://jira.codehaus.org/browse/GROOVY-3493
		Esri10GisService.metaClass.featuresInCircle = {Double x, Double y, Integer radius, String layerName -> featuresInCircle}
		GisService myService = new Esri10GisService()
		myService.metaClass.featuresInCircle = {Double x, Double y, Integer radius, String layerName -> featuresInCircle}
		
		assert myService.featuresByProximity(0, 0, urlStreetMaint, "SNAME", 20)[urlStreetMaint] == []
					
	}
	
	@Test
	public void testFeaturesByProximity_DistanceMoreThan500(){
		String urlStreetMaint = gisConfig.layers["MDC.StreetMaint"]
		assert gisService.featuresByProximity(2.1, 3.1, urlStreetMaint, 'SNAME', 501)[urlStreetMaint] == []

	}
	
	@Test
	public void testFeaturesByProximity_NonExistantUniqueAttribute(){
		
		String urlStreetMaint = gisConfig.layers["MDC.StreetMaint"]
		Map featuresInCircle =
		[(urlStreetMaint):[
			[OBJECTID:46004, STREETID:36735, CLASS:5, SNAME:'NW 31ST AVE', ONEWAY:''],
			[OBJECTID:19529, STREETID:36732, CLASS:5, SNAME:'NW 30TH ST', ONEWAY:''],
			[OBJECTID:64067, STREETID:36733, CLASS:5, SNAME:'NW 31ST AVE', ONEWAY:'']
			]
		]
		
		//Cannot override methods via metaclass that are part of an interface implementation
		//http://jira.codehaus.org/browse/GROOVY-3493
		Esri10GisService.metaClass.featuresInCircle = {Double x, Double y, Integer radius, String layerName -> featuresInCircle}
		GisService myService = new Esri10GisService()
		myService.metaClass.featuresInCircle = {Double x, Double y, Integer radius, String layerName -> featuresInCircle}
		
		String uniqueAttribute = ""
		assert myService.featuresByProximity(0, 0, urlStreetMaint, uniqueAttribute, 20)[urlStreetMaint].size() == 3

		uniqueAttribute = null
		assert myService.featuresByProximity(0, 0, urlStreetMaint, uniqueAttribute, 20)[urlStreetMaint].size() == 3

	}
	
	
	@Test
	public void testFeaturesByProximity_ErrorWhenGettingCircle(){
		

		//Cannot override methods via metaclass that are part of an interface implementation
		//http://jira.codehaus.org/browse/GROOVY-3493
		Esri10GisService.metaClass.featuresInCircle = {Double x, Double y, Integer radius, String urlValue -> throw new RetrievalOfDataException()}
		GisService myService = new Esri10GisService()
		myService.metaClass.featuresInCircle = {Double x, Double y, Integer radius, String urlValue -> throw new RetrievalOfDataException()}
		
		try{
		  myService.featuresByProximity(2.1, 3.1, "http://someurl", "SNAME", 200)
		  assert false
		}catch(RetrievalOfDataException rode){
		  assert true  	
		}
	}

	
	
	
	
	
	@Test
	public void testFeaturesByIds(){
		
		String urlStreetMaint = gisConfig.layers["MDC.StreetMaint"]
		// we could ask to get street maintenance for one id
		Map results = gisService.featuresByIds(urlStreetMaint, "STREETID", [99514])
		List features = results[urlStreetMaint]
		assert features.size() == 1
		assert features[0].attributes.MAINTCODE == 'CI'
		assert features[0].attributes.SNAME == 'NW 1ST ST'
		
		// or we could ask for more than one id
		results = gisService.featuresByIds(urlStreetMaint, "STREETID", [99514, 72795])
		features = results[urlStreetMaint]
		assert features.size() == 2
		assert features[0].attributes.MAINTCODE == 'CO'
		assert features[0].attributes.SNAME == 'SW 97TH ST'
		assert features[1].attributes.MAINTCODE == 'CI'
		assert features[1].attributes.SNAME == 'NW 1ST ST'
		
		// or we could pass a non existent id
		results = gisService.featuresByIds(urlStreetMaint, "STREETID", [0])
		features = results[urlStreetMaint]
		assert features == []
		assert features.size() == 0
		
		// or we could pass an empty list of ids
		results = gisService.featuresByIds(urlStreetMaint, "STREETID", [])
		features = results[urlStreetMaint]
		assert features == []
		assert features.size() == 0
		
		// or we could pass a null list of ids
		results = gisService.featuresByIds(urlStreetMaint, "STREETID", null)
		features = results[urlStreetMaint]
		assert features == []
		assert features.size() == 0
	}
	
	@Test
	public void testFeaturesByIds_Error(){
		
		def requestResult = [error:[code:"400", message:"bad error", details:[""]]]
		gisService.getHttpService().getMetaClass().request = {String url, Map query -> requestResult}
		
		try{
			gisService.featuresByIds("somelayer", "someattribid", [99514])
			assert false
		} catch(RetrievalOfDataException rode){
			String message = "Unexpected error for uri somelayer | message: [error:[code:400, message:bad error, details:[]]]"
			assert rode.message == message
		}
		
	}
	
	
}
