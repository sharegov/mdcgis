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

class Esri9CandidateServiceTests {
//
//
//	private CandidateService candidateService
//
//	@Before
//	public void setUp() throws Exception {
//		new ClassPathXmlApplicationContext("configtest.xml");
//
//		ApplicationContext ctx = AppContext.getApplicationContext();
//		candidateService = ctx.getBean("CANDIDATE_SERVICE");
//	}
//
//
//
//	@Test
//	public void testCleanCandidates_NoCandidates(){
//		def candidates = []
//		assert candidateService.cleanCandidates(candidates) == []
//
//		candidates = null
//		assert candidateService.cleanCandidates(candidates) == []
//
//	}
//
//	@Test
//	public void testCleanCandidates_DeleteDuplicatesWithSameAddress(){
//		def candidates = [
//			[
//				address: "111 NW 1ST ST, 33128",score: 75,
//				attributes: [Loc_name: "GeoStreet", Score: 75, Match_addr: "111 NW 1ST ST, 33128"]
//			],
//			[
//				address: "109 SW 2ST ST, 33128",score: 66,
//				attributes: [Loc_name: "GeoAddress", Score: 66, Match_addr: "109 SW 2ST ST, 33128"]
//			],
//			[
//				address: "111 NW 1ST ST, 33128",score: 61,
//				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "111 NW 1ST ST, 33128"]
//			],
//			[
//				address: "111 NW 1ST ST, 33128",score: 61,
//				attributes: [Loc_name: "GeoAddress", Score: 61, Match_addr: "111 NW 1ST ST, 33128"]
//			],
//			[
//				address: "11826 SW 97TH ST, 33186",score: 66,
//				attributes: [Loc_name: "GeoStreet", Score: 66, Match_addr: "11826 SW 97TH ST, 33186"]
//			],
//			[
//				address: "11826 SW 97TH ST, 33186",score: 61,
//				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "111 NW 1ST ST, 33128"]
//			]
//		]
//
//
//		def filterCandidates = candidateService.cleanCandidates(candidates)
//		assert filterCandidates.size() == 3;
//		assert filterCandidates[0].attributes.Score == 75
//
//	}
//
//
//	@Test
//	public void testCleanCandidates_100FromGeoAddressReturnOneCandidate(){
//		def candidates = [
//			[
//				address: "111 NW 1ST ST, 33128",score: 75,
//				attributes: [Loc_name: "GeoStreet", Score: 75, Match_addr: "111 NW 1ST ST, 33128"]
//			],
//			[
//				address: "109 SW 2ST ST, 33128",score: 66,
//				attributes: [Loc_name: "GeoAddress", Score: 66, Match_addr: "109 SW 2ST ST, 33128"]
//			],
//			[
//				address: "111 NW 1ST ST, 33128",score: 100,
//				attributes: [Loc_name: "GeoAddress", Score: 100, Match_addr: "111 NW 1ST ST, 33128"]
//			],
//			[
//				address: "111 NW 1ST ST, 33128",score: 100,
//				attributes: [Loc_name: "GeoAddress", Score: 100, Match_addr: "111 NW 1ST ST, 33128"]
//			],
//			[
//				address: "11826 SW 97TH ST, 33186",score: 66,
//				attributes: [Loc_name: "GeoStreet", Score: 66, Match_addr: "11826 SW 97TH ST, 33186"]
//			],
//			[
//				address: "11826 SW 97TH ST, 33186",score: 61,
//				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "111 NW 1ST ST, 33128"]
//			]
//		]
//
//
//		def filterCandidates = candidateService.cleanCandidates(candidates)
//		assert filterCandidates.size() == 1;
//		assert filterCandidates[0].attributes.Score == 100
//		assert filterCandidates[0].attributes.Loc_name == 'GeoAddress'
//
//	}
//
//
//	@Test
//	public void testCleanCandidates_100FromGeoStreet_No100FromGeoAddress(){
//		def candidates = [
//			[
//				address: "111 NW 1ST ST, 33128",score: 75,
//				attributes: [Loc_name: "GeoStreet", Score: 75, Match_addr: "111 NW 1ST ST, 33128"]
//			],
//			[
//				address: "109 SW 2ST ST, 33128",score: 66,
//				attributes: [Loc_name: "GeoAddress", Score: 66, Match_addr: "109 SW 2ST ST, 33128"]
//			],
//			[
//				address: "111 NW 1ST ST, 33128",score: 90,
//				attributes: [Loc_name: "GeoAddress", Score: 90, Match_addr: "111 NW 1ST ST, 33128"]
//			],
//			[
//				address: "111 NW 1ST ST, 33128",score: 100,
//				attributes: [Loc_name: "GeoStreet", Score: 100, Match_addr: "111 NW 1ST ST, 33128"]
//			],
//			[
//				address: "11826 SW 97TH ST, 33186",score: 66,
//				attributes: [Loc_name: "GeoStreet", Score: 66, Match_addr: "11826 SW 97TH ST, 33186"]
//			],
//			[
//				address: "11826 SW 97TH ST, 33186",score: 61,
//				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "111 NW 1ST ST, 33128"]
//			]
//		]
//
//
//		def filterCandidates = candidateService.cleanCandidates(candidates)
//		assert filterCandidates.size() == 1;
//		assert filterCandidates[0].attributes.Score == 100
//		assert filterCandidates[0].attributes.Loc_name == 'GeoStreet'
//
//	}
//
//		
}
