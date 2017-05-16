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

class Esri9GisServiceTests {
//
//	private GisService gisService
//	
//		@Before
//		public void setUp() throws Exception {
//			new ClassPathXmlApplicationContext("configtest.xml");
//	
//			ApplicationContext ctx = AppContext.getApplicationContext();
//			gisService = ctx.getBean("GIS_SERVICE");
//		}
//		
//		
//		@Test
//		public void testGetPointLayersIntersectionAttributes() {
//	
//			def servicePaths = [
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/10/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_Map/MapServer/11/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/14/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/15/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/18/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/20/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/21/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/25/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/26/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/27/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/28/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/29/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/30/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/31/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/32/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/33/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/34/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/35/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/36/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/37/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/38/query'
//			]
//	
//			def results = gisService.getPointLayersIntersectionAttributes("940719.3750563636", "525354.1876266897", servicePaths)
//			assert results.size() == 21
//		}
//
//		@Test
//		public void testGetPointLayersIntersectionAttributes_DummyLayer() {
//			def servicePaths = ['http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/101/query']
//			def results = gisService.getPointLayersIntersectionAttributes("940719.3750563636", "525354.1876266897", servicePaths)
//			assert results.size() == 1
//			assert results[servicePaths[0]] == null
//		}
//		
//		@Test
//		public void testGetPointLayersIntersectionAttributes_NoIntersection() {
//			def servicePaths = ['http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/25/query']
//			def results = gisService.getPointLayersIntersectionAttributes("940719.3750563636", "525354.1876266897", servicePaths)
//			assert results.size() == 1
//			assert results[servicePaths[0]] == [:]
//		}
//			
//		@Test
//		public void testGetPointLayersIntersectionGeometry() {
//			def servicePaths = [
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/10/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_Map/MapServer/11/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/14/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/15/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/18/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/20/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/21/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/25/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/26/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/27/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/28/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/29/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/30/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/31/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/32/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/33/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/34/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/35/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/36/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/37/query',
//				'http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/38/query'
//			]
//			
//			def results = gisService.getPointLayersIntersectionGeometry("940719.3750563636", "525354.1876266897", servicePaths)
//			assert results.size() == 21
//
//		}	
//
//		
//		@Test
//		public void testGetPointLayersIntersectionGeometry_DummyLayer() {
//			def servicePaths = ['http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/101/query']
//			def results = gisService.getPointLayersIntersectionGeometry("940719.3750563636", "525354.1876266897", servicePaths)
//			assert results.size() == 1
//			assert results[servicePaths[0]] == null
//		}
//		
//		@Test
//		public void testGetPointLayersIntersectionGeometry_NoIntersection() {
//			def servicePaths = ['http://s0142357.miamidade.gov/ArcGIS/rest/services/MD_GIC/MapServer/25/query']
//			def results = gisService.getPointLayersIntersectionGeometry("940719.3750563636", "525354.1876266897", servicePaths)
//			assert results.size() == 1
//			assert results[servicePaths[0]] == [:]
//		}
//		
//		@Test
//		public void testReverseGeoCode(){
//			
//			// Test for 11826 sw 97th street
//			def result = gisService.reverseGeoCode(857869.749956198, 488913.9375316277, 0)
//			assert result.street == "11826 SW 97TH ST"
//			assert result.zip == "33186"
//			
//			// What if a try without specifying the distance from the point
//			result = gisService.reverseGeoCode(857869.749956198, 488913.9375316277, null)
//			assert result.street == "11826 SW 97TH ST"
//			assert result.zip == "33186"
//			
//			// Let me try 111 nw 1st street, 33030 but with a null distance returns an empty result.
//			result = gisService.reverseGeoCode(827830.292622894, 413683.60797343403, null)
//			assert result == null
//		}
//		
//		
}
