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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sharegov.mdcgis.model.Address
import org.sharegov.mdcgis.utils.AppContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext


class Esri10CandidateServiceTests {


	private Esri10CandidateService candidateService

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext("configtest.xml");

		ApplicationContext ctx = AppContext.getApplicationContext();
		candidateService = ctx.getBean("CANDIDATE_SERVICE");
	}



	@Test
	public void testCleanCandidates_NoCandidates(){
		def candidates = []
		assert candidateService.cleanCandidates(candidates) == []

		candidates = null
		assert candidateService.cleanCandidates(candidates) == []

	}

	@Test
	public void testCleanCandidates_MunicipalityIdPopulated(){
		def candidates = [
			[
				address: "11826 SW 97TH ST, 30, 33186",score: 61,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "111 NW 1ST ST, 30, 33128"]
			]
		]
		
		def cleanCandidates = candidateService.cleanCandidates(candidates)
		cleanCandidates[0].municipalityId = 30		
	}
	
	@Test
	public void testCleanCandidates_AddressField_AddressZip(){
		def candidates = [
			[
				address: "11826 SW 97TH ST, 30, 33186",score: 61,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "111 NW 1ST ST, 30, 33128"]
			]
		]
		
		def cleanCandidates = candidateService.cleanCandidates(candidates)
		cleanCandidates[0].address = "11826 SW 97TH ST, 33186"
	}
	
	@Test
	public void testCleanCandidates_DeleteDuplicatesWithSameAddress(){
		def candidates = [
			[
				address: "111 NW 1ST ST, 1, 33128",score: 75,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 75, Match_addr: "111 NW 1ST ST, 1, 33128"]
			],
			[
				address: "109 SW 2nd ST, 1, 33128",score: 66,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoAddress", Score: 66, Match_addr: "109 SW 2ST ST, 1, 33128"]
			],
			[
				address: "111 NW 1ST ST, 1, 33128",score: 61,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "111 NW 1ST ST, 1, 33128"]
			],
			[
				address: "111 NW 1ST ST, 1, 33128",score: 61,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoAddress", Score: 61, Match_addr: "111 NW 1ST ST, 1, 33128"]
			],
			[
				address: "11826 SW 97TH ST, 30, 33186",score: 66,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 66, Match_addr: "11826 SW 97TH ST, 30, 33186"]
			],
			[
				address: "11826 SW 97TH ST, 30, 33186",score: 61,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "111 NW 1ST ST, 30, 33128"]
			]
		]


		def filterCandidates = candidateService.cleanCandidates(candidates)
		assert filterCandidates.size() == 3;
		assert filterCandidates[0].attributes.Score == 75

	}


	@Test
	public void testCleanCandidates_100FromGeoAddressReturnOneCandidate(){
		def candidates = [
			[
				address: "111 NW 1ST ST, 1, 33128",score: 75,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 75, Match_addr: "111 NW 1ST ST, 1, 33128"]
			],
			[
				address: "109 SW 2nd ST, 3, 33128",score: 66,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoAddress", Score: 66, Match_addr: "109 SW 2ST ST, 3, 33128"]
			],
			[
				address: "111 NW 1ST ST, 1, 33128",score: 100,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoAddress", Score: 100, Match_addr: "111 NW 1ST ST, 1, 33128"]
			],
			[
				address: "111 NW 1ST ST, 1, 33128",score: 100,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoAddress", Score: 100, Match_addr: "111 NW 1ST ST, 1, 33128"]
			],
			[
				address: "11826 SW 97TH ST, 30, 33186",score: 66,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 66, Match_addr: "11826 SW 97TH ST, 30, 33186"]
			],
			[
				address: "11826 SW 97TH ST, 30, 33186",score: 61,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "111 NW 1ST ST, 30, 33128"]
			]
		]


		def filterCandidates = candidateService.cleanCandidates(candidates)
		assert filterCandidates.size() == 1;
		assert filterCandidates[0].attributes.Score == 100
		assert filterCandidates[0].attributes.Loc_name == 'GeoAddress'
		assert filterCandidates[0].municipalityId == 1

	}


	@Test
	public void testCleanCandidates_100FromGeoStreet_No100FromGeoAddress(){
		def candidates = [
			[
				address: "111 NW 1ST ST, 1, 33128",score: 75,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 75, Match_addr: "111 NW 1ST ST, 1, 33128"]
			],
			[
				address: "109 SW 2ST ST, 3, 33128",score: 66,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoAddress", Score: 66, Match_addr: "109 SW 2ST ST, 3, 33128"]
			],
			[
				address: "111 NW 1ST ST, 1, 33128",score: 90,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoAddress", Score: 90, Match_addr: "111 NW 1ST ST, 1, 33128"]
			],
			[
				address: "111 NW 1ST ST, 22, 33128",score: 100,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 100, Match_addr: "111 NW 1ST ST, 22, 33128"]
			],
			[
				address: "11826 SW 97TH ST, 30, 33186",score: 66,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 66, Match_addr: "11826 SW 97TH ST, 30, 33186"]
			],
			[
				address: "11826 SW 97TH ST, 30, 33186",score: 61,
				location:[x:12345.2345, y:23456.4565],
				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "111 NW 1ST ST, 30, 33128"]
			]
		]


		def filterCandidates = candidateService.cleanCandidates(candidates)
		assert filterCandidates.size() == 1;
		assert filterCandidates[0].attributes.Score == 100
		assert filterCandidates[0].attributes.Loc_name == 'GeoStreet'
		assert filterCandidates[0].municipalityId == 22
		

	}

	@Test
	public void testCleanCandidates_IntersectionsBelongToDifferentMunicipalities(){
		List candidates = candidateService.getCandidates("SW 57th AVE & SW 72nd ST", "")
		assert candidates.size() == 3
		
		candidates = candidateService.getCandidates("SW 48 St and SW 67 Ave", "")
		assert candidates.size() == 2
	}
	
	
	@Test
	public void  testAddStreetMaintenanceIdsToCandidates(){
		def candidates = 
		[
			[
				address: "111 NW 1ST ST, 1, 33128",score: 75,
				attributes: [Loc_name: "GeoStreet", Score: 75, Match_addr: "111 NW 1ST ST, 1, 33128", Ref_ID:1]
			],
			[
				address: "109 SW 2ST ST, 3, 33128",score: 66,
				attributes: [Loc_name: "GeoStreet", Score: 66, Match_addr: "109 SW 2ST ST, 3, 33128", Ref_ID:2]
			],
			[
				address: "111 NW 1ST ST, 1, 33128",score: 90,
				attributes: [Loc_name: "GeoAddress", Score: 90, Match_addr: "111 NW 1ST ST, 1, 33128"]
			],
			[
				address: "11826 SW 97TH ST, 30, 33186",score: 66,
				attributes: [Loc_name: "GeoAddress", Score: 66, Match_addr: "11826 SW 97TH ST, 30, 33186"]
			],
			[
				address: "11826 SW 97TH ST, 30, 33186",score: 61,
				attributes: [Loc_name: "GeoStreet", Score: 61, Match_addr: "11826 SW 97TH ST, 30, 33186", Ref_ID:3]
			]
		]
		
		
		
		List cleanCandidates = candidateService.addStreetMaintenanceIdsToCandidates(candidates)
		 def candidate = cleanCandidates.find {it.address == "111 NW 1ST ST, 1, 33128" && it.attributes.Loc_name == "GeoStreet"}
		 assert candidate.streetMaintenanceIds == [1]
 
		 candidate = cleanCandidates.find {it.address == "111 NW 1ST ST, 1, 33128" && it.attributes.Loc_name == "GeoAddress"}
		 assert candidate.streetMaintenanceIds == [1]
		 
		 candidate = cleanCandidates.find {it.address == "109 SW 2ST ST, 3, 33128" && it.attributes.Loc_name == "GeoStreet"}
		 assert candidate.streetMaintenanceIds == [2]
		 
		 candidate = cleanCandidates.find {it.address == "11826 SW 97TH ST, 30, 33186" && it.attributes.Loc_name == "GeoStreet"}
		 assert candidate.streetMaintenanceIds == [3]
		 
		 candidate = cleanCandidates.find {it.address == "11826 SW 97TH ST, 30, 33186" && it.attributes.Loc_name == "GeoAddress"}
		 assert candidate.streetMaintenanceIds == [3]
		
	}
	
	@Test
	public void testAddStreetMaintenanceIdsToCandidates_Intersection(){
		
		def candidates = [
		["address" : "NW 1ST ST & NW 2ND AVE,  16, 33034",
		  "location" : ["x" : 827864.18415590376,"y" : 405714.87784098834],
		  "score" : 100,
		  "attributes" : ["Loc_name" : "GeoStreet","Score" : 100,
			"Match_addr" : "NW 1ST ST & NW 2ND AVE,  16, 33034",
			"X" : 827864.18417000002,"Y" : 405714.87783499999,
			"Addr_type" : "Intersection",
			"Ref_ID" : 0,"Ref_ID1" : 99514,"Ref_ID2" : 102188]],
		["address" : "NW 1ST ST & NW 2ND AVE,  1, 33128",
		  "location" : ["x" : 920168.93598240241,"y" : 524708.81157910079],
		  "score" : 100,
		  "attributes" : [
			"Loc_name" : "GeoStreet","Score" : 100,
			"Match_addr" : "NW 1ST ST & NW 2ND AVE,  1, 33128",
			"X" : 920168.93596399995,"Y" : 524708.81155999994,
			"Addr_type" : "Intersection",
			"Ref_ID" : 0,"Ref_ID1" : 61629,"Ref_ID2" : 63527]]]
					
		List cleanCandidates = candidateService.addStreetMaintenanceIdsToCandidates(candidates)
		def candidate = cleanCandidates.find {it.address == "NW 1ST ST & NW 2ND AVE,  1, 33128"}
		assert candidate.streetMaintenanceIds == [0, 61629, 63527]
	
	}
	
	@Test
	public void  testAddStreetMaintenanceIdsToCandidates_nulLCandidates(){
		def candidates = null
		List cleanCandidates = candidateService.addStreetMaintenanceIdsToCandidates(candidates)
		assert cleanCandidates == null
	}
	
	
	@Test
	public void testGetRawCandidates_PassIntegerMunicipality(){

		def candidates = candidateService.getRawCandidates("SW 57th AVE & SW 72nd ST", "33143", 30)
		assert candidates.candidates.size() == 2
		
		candidates = candidateService.getRawCandidates("SW 57th AVE & SW 72nd ST", null, 30)
		assert candidates.candidates.size() == 1

		candidates = candidateService.getRawCandidates("SW 57th AVE & SW 72nd ST", "33143", (Integer) null)
		assert candidates.candidates.size() == 3
		
		candidates = candidateService.getRawCandidates("SW 57th AVE & SW 72nd ST", null, (Integer) null)
		assert candidates.candidates.size() == 4
	}
	
	@Test
	public void testGetRawCandidates_PassStringMunicipality(){

		def candidates = candidateService.getRawCandidates("SW 57th AVE & SW 72nd ST", "33143", "UNINCORPORATED MIAMI-DADE")
		assert candidates.candidates.size() == 2
		
		candidates = candidateService.getRawCandidates("SW 57th AVE & SW 72nd ST", null, "UNINCORPORATED MIAMI-DADE")
		assert candidates.candidates.size() == 1

		candidates = candidateService.getRawCandidates("SW 57th AVE & SW 72nd ST", "33143", null)
		assert candidates.candidates.size() == 3
		
		candidates = candidateService.getRawCandidates("SW 57th AVE & SW 72nd ST", null, null)
		assert candidates.candidates.size() == 4
	}
	
	@Test
	public void testGetRawCandidates_NoMunicipality(){
		
		def candidates = candidateService.getRawCandidates("11826 sw 97th street", "33186")
		assert candidates.candidates.size() == 2
	
		candidates = candidateService.getRawCandidates("11826 sw 97th street", null)
		assert candidates.candidates.size() == 2
		
		candidates = candidateService.getRawCandidates("11826 sw 97th street", "")
		assert candidates.candidates.size() == 2
	}

}
