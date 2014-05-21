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

import static org.junit.Assert.*

import java.util.List;

import groovyx.net.http.ContentType
import org.junit.Test
import org.junit.Before

import org.sharegov.mdcgis.utils.AppContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

class FeatureServiceTests {
	
	private FeatureService featureService
	
	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext("configtest.xml");

		ApplicationContext ctx = AppContext.getApplicationContext();
		featureService = (FeatureService) ctx
				.getBean("FEATURE_SERVICE");
				
	}

	
	@Test
	public void testFeaturesFromPointLayersIntersection_SwitchKeyToLayerName(){
		Map rawResults = ["http://311arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/11/query":
			[attributes:[OBJECTID:7, ID:7, COMMNAME:'Xavier L. Suarez'], geometry:[rings:[[[931858.2328325026, 520919.6944387555]]]]]]
		
		Map results = ["MDC.CommissionDistrict":
			[attributes:[OBJECTID:7, ID:7, COMMNAME:'Xavier L. Suarez'], geometry:[rings:[[[931858.2328325026, 520919.6944387555]]]]]]
		
		featureService.gisService = [featuresFromPointLayersIntersection: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresFromPointLayersIntersection("857869.753", "488913.937", ['MDC.CommissionDistrict']) == results
	}
	
	@Test
	public void testFeaturesAttributesFromPointLayersIntersection_SwitchKeyToLayerName(){
		Map rawResults = ["http://311arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/11/query":
			[OBJECTID:7, ID:7, COMMNAME:'Xavier L. Suarez']]
		
		Map results = ["MDC.CommissionDistrict":
			[OBJECTID:7, ID:7, COMMNAME:'Xavier L. Suarez']]
		
		featureService.gisService = [featuresAttributesFromPointLayersIntersection: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresAttributesFromPointLayersIntersection("857869.753", "488913.937", ['MDC.CommissionDistrict']) == results
	}

	@Test
	public void testFeaturesAttributesFromPointLayersIntersection_NoIntersection(){
		Map result = featureService.featuresAttributesFromPointLayersIntersection("857869.753", "488913.937", ['MDC.HomeOwnerAssociation'])
		assert result == ['MDC.HomeOwnerAssociation':null]
	}
		
	@Test
	public void testFeaturesGeometryFromPointLayersIntersection_SwitchKeyToLayerName(){
		Map rawResults = ["http://311arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/11/query":
			[rings:[[[931858.2328325026, 520919.6944387555]]]]]
		
		Map results = ["MDC.CommissionDistrict":
			[rings:[[[931858.2328325026, 520919.6944387555]]]]]
		
		featureService.gisService = [featuresGeometryFromPointLayersIntersection: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresGeometryFromPointLayersIntersection("857869.753", "488913.937", ['MDC.CommissionDistrict']) == results
	}
	
	
	@Test
	public void testFeaturesInCircle_SwitchKeyToLayerName(){
		Map rawResults = ["http://311arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/41/query":
			[[attributes:[OBJECTID:68756], 
			  geometry:[paths:[[[858070.6874045022, 488840.5938830897]]]]]]]
			
		Map results = ["pwd_lights":
			[[attributes:[OBJECTID:68756],
			  geometry:[paths:[[[858070.6874045022, 488840.5938830897]]]]]]]
		
		featureService.gisService = [featuresInCircle: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresInCircle(857869.753, 488913.937, 200, 'pwd_lights') == results
	}
	
	@Test
	public void testFeaturesAttributesInCircle_SwitchKeyToLayerName(){
		
		Map rawResults = ["http://311arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/41/query":
						  [OBJECTID:68756]]
		
		Map results = ["pwd_lights":
			[OBJECTID:68756]]
		
		featureService.gisService = [featuresAttributesInCircle: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresAttributesInCircle(857869.753, 488913.937, 200, 'pwd_lights') == results
	}
	
	@Test
	public void testFeaturesGeometryInCircle_SwitchKeyToLayerName(){
		
		Map rawResults = ["http://311arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/41/query":
			[paths:[[[858070.6874045022, 488840.5938830897]]]]]
			
		Map results = ["pwd_lights":
				[paths:[[[858070.6874045022, 488840.5938830897]]]]]
		
		featureService.gisService = [featuresGeometryInCircle: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresGeometryInCircle(857869.753, 488913.937, 200, 'pwd_lights') == results
	}
	
	
	@Test
	public void testFeaturesInPolygon_SwitchKeyToLayerName(){
		Map rawResults = ["http://gisweb.miamidade.gov/ArcGIS/rest/services/MD_PropertySearch/MapServer/0/query":
			[[attributes:['MDC.PTXGIS.AREA':0], geometry:[points:[[857869.813, 488913.906]]]]]]
		
		Map results = ["MDC.PTXGIS":
			[[attributes:['MDC.PTXGIS.AREA':0], geometry:[points:[[857869.813, 488913.906]]]]]]
		
		Map geometry = [rings:[[[857827.405,488961.478],[857908.642,488964.622],[857912.028,488852.544],[857832.004,488848.964],[857827.405,488961.478]]]]

		featureService.gisService = [featuresInPolygon: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresInPolygon(geometry, 'MDC.PTXGIS') == results
	}
	
	@Test
	public void testFeaturesAttributesInPolygon_SwitchKeyToLayerName(){
		Map rawResults = ["http://gisweb.miamidade.gov/ArcGIS/rest/services/MD_PropertySearch/MapServer/0/query":
			['MDC.PTXGIS.AREA':0]]
		
		Map results = ["MDC.PTXGIS":['MDC.PTXGIS.AREA':0]]
				
		Map geometry = [rings:[[[857827.405,488961.478],[857908.642,488964.622],[857912.028,488852.544],[857832.004,488848.964],[857827.405,488961.478]]]]

		featureService.gisService = [featuresAttributesInPolygon: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresAttributesInPolygon(geometry, 'MDC.PTXGIS') == results
		
	}
	
	@Test
	public void testFeaturesGeometryInPolygon_SwitchKeyToLayerName(){
		Map rawResults = ["http://gisweb.miamidade.gov/ArcGIS/rest/services/MD_PropertySearch/MapServer/0/query":
			[points:[[857869.813, 488913.906]]]]
		
		Map results = ["MDC.PTXGIS":
			[points:[[857869.813, 488913.906]]]]
		
		Map geometry = [rings:[[[857827.405,488961.478],[857908.642,488964.622],[857912.028,488852.544],[857832.004,488848.964],[857827.405,488961.478]]]]

		featureService.gisService = [featuresGeometryInPolygon: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresGeometryInPolygon(geometry, 'MDC.PTXGIS') == results
	}
	
	
	@Test
	public void testFeaturesInPolygon_emptyGeometry(){
		
		Map geometry = [rings:[]]
		Map features = featureService.featuresInPolygon(geometry, 'MDC.PTXGIS')
		assert features == ['MDC.PTXGIS':[]]

		geometry = null
		features = featureService.featuresInPolygon(geometry, 'MDC.PTXGIS')
		assert features == ['MDC.PTXGIS':[]]
	}
	
		
	@Test
	public void testFeaturesInPolygon_QueryForGeometry() {
		// x y for 2001 Meridian Ave
		def geometry = featureService.featuresGeometryFromPointLayersIntersection("940512.0610576011", "532760.3259282038", ['MDC.Parcels'])
		Map features = featureService.featuresInPolygon(geometry['MDC.Parcels'], 'MDC.PTXGIS')
		assert features['MDC.PTXGIS'].size() == 113
	}
	
	@Test
	public void testFeaturesInPolygon_QueryForLargeGeometry() {
		// x y 
		def geometry = featureService.featuresGeometryFromPointLayersIntersection("879912.9752018265", "582436.1347285807", ['MDC.Parcels'])
		Map features = featureService.featuresInPolygon(geometry['MDC.Parcels'], 'MDC.PTXGIS')
		assert features['MDC.PTXGIS'].size() == 117
	}

	
	@Test
	public void testFeaturesByIds_SwitchKeyToLayerName(){
		Map rawResults = ["http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/51/query":
			[[attributes:[OBJECTID:42736, STREETID:99514], 
			  geometry:[paths:[[[827864.1875471696, 405714.87507389486]]]]
			 ]]]
		
		Map results = ['MDC.StreetMaint':
			[[attributes:[OBJECTID:42736, STREETID:99514],
				geometry:[paths:[[[827864.1875471696, 405714.87507389486]]]]
			   ]]]
		
		featureService.gisService = [featuresByIds: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresByIds("MDC.StreetMaint", "STREETID", [99514])
	}
	
	@Test
	public void testFeaturesAttributesByIds_SwitchKeyToLayerName(){
		Map rawResults = ["http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/51/query":
			[OBJECTID:42736, STREETID:99514]]
		
		Map results = ['MDC.StreetMaint':
			[OBJECTID:42736, STREETID:99514]]
		
		featureService.gisService = [featuresAttributesByIds: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresAttributesByIds("MDC.StreetMaint", "STREETID", [99514])
	}
	
	@Test
	public void testFeaturesGeometryByIds_SwitchKeyToLayerName(){
		Map rawResults = ["http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/51/query":
			[paths:[[[827864.1875471696, 405714.87507389486]]]]]

		
		Map results = ['MDC.StreetMaint':
			[paths:[[[827864.1875471696, 405714.87507389486]]]]]
		
		featureService.gisService = [featuresGeometryByIds: {Object[] args -> rawResults}] as GisService
		assert featureService.featuresGeometryByIds("MDC.StreetMaint", "STREETID", [99514])
	}
	
	
	@Test
	public void testPropertyAttributesByFolio(){
		Map attributes = featureService.propertyAttributesByFolio("3059010240130")
		attributes.with {
			assert FOLIO == "3059010240130"
			assert PARENT == "3059010240130"
			assert PTXADDRESS == "11826 SW 97 ST"
		}
	}
	
	@Test
	public void testPropertyAttributesByFolio_NoFolio(){
		assert featureService.propertyAttributesByFolio("33") == null
		assert featureService.propertyAttributesByFolio(null) == null
		assert featureService.propertyAttributesByFolio("") == null
	}
	

	@Test
	public void testPropertyAttributesForBuildingByFolio(){
		Map attributes = featureService.propertyAttributesForBuildingByFolio("0232341690001")
		attributes.with {
			assert FOLIO == "0232341690001"
			assert ADDRESS == "2001 MERIDIAN AVE"
		}	
	}
	
	@Test
	public void testPropertyAttributesForBuildingByFolio_NoFolio(){
		assert featureService.propertyAttributesForBuildingByFolio("33") == null
		assert featureService.propertyAttributesForBuildingByFolio(null) == null
		assert featureService.propertyAttributesForBuildingByFolio("") == null
	}
}
