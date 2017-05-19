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

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.sharegov.mdcgis.utils.AppContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

class CandidateServiceTests {


	private CandidateService candidateService

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext("configtest.xml");

		ApplicationContext ctx = AppContext.getApplicationContext();
		candidateService = ctx.getBean("CANDIDATE_SERVICE");
		
		
	}

	@After
	public void destroy() throws Exception{
		((ClassPathXmlApplicationContext) AppContext.getApplicationContext()).close();
	}
	

	@Test
	public void testGetCandidates_NoStreetPassedIn() {
		assert [] == candidateService.getCandidates("", "33139", 1)
		assert [] == candidateService.getCandidates(null, "33139", 1)
	}
	
	
	@Test
	public void testStandarizeAddress(){
		assert candidateService.standarizeAddress("11826 sw 97th st") == '11826 SW 97TH ST'
	}

	@Test
	public void testStandarizeAddress_NoAddress(){
		assert candidateService.standarizeAddress("1182") == ''
	}

	@Test
	public void testStandarizeAddress_Intersection(){
		assert candidateService.standarizeAddress("nw 1st st and nw 2nd ave") == 'NW 1ST ST & NW 2ND AVE'
	}

	@Test
	public void testStandarizeAddress_NoIntersection(){
		assert candidateService.standarizeAddress("nw2nd") == ''
	}
	
	@Test
	public void testParseAddress(){
		Map parsedAddress = [House:'11826', 
					PreDir:'SW', StreetName:'97TH', SufType:'ST', SufDir:'']
		assert candidateService.parseAddress("11826 sw 97th steet") == parsedAddress 
	}

	@Test
	public void testParseAddress_NoAddress(){
		assert candidateService.parseAddress("guacaguaca") == [:]
	}
	
	@Test
	public void testParseAddress_2Suffixes(){
		Map parsedAddress = [House:'25', 
					PreDir:'SW', StreetName:'100TH STREET', SufType:'CIR', SufDir:'']
		assert candidateService.parseAddress("25 SW 100th Street Circle") == parsedAddress
	}
	
	@Test
	public void testParseAddress_Intersection(){
		Map parsedAddress = [PreDir1:'NW', StreetName1:'1ST', SufType1:'ST', SufDir1:'', 
			PreDir2:'NW', StreetName2:'2ND', SufType2:'AVE', SufDir2:'']
		assert candidateService.parseAddress("nw 1st st and nw 2nd ave") == parsedAddress
	}

//	@Test
//	public void testGetClosestFeatures_StreetMaintenance(){
//		// x y for address 3123 NW 29TH ST, 33142
//		Map data = addressService.getClosestFeature(904028.0626120828, 534703.3748988137, 150, "MDC.STREETMAINT")
//		assert data.SNAME == "NW 29TH ST"
//		assert data.MAINTCODE == "CO"
//	}
	
//	@Test
//	public void testPopulateClosestFeatures(){
//		def start = new Date().time
//		Map candidate =[address:"3123 NW 29TH ST, 33142", location:[x:904028.0626120828, y:534703.3748988137], score:100,
//			attributes:[Loc_name:"GeoAddress", Score:100, X:904028.0626124851, Y:534703.3748989105, HouseNum:3123, PreDir:"NW", PreType:"", StreetName:"29TH", StreetType:"ST",
//				 SufDir:"", Zone:"33142", Match_addr:"3123 NW 29TH ST, 33142", Side:"", LeftFrom:"", LeftTo:"", RightFrom:"", RightTo:"", LeftZone:"", RightZone:""]]
//		
//		Address address = [address:"3123 NW 29TH ST, 33142", location:[x:904028.0626120828, y:534703.3748988137]]
//		candidateService.populateClosestFeatures(address)
//		
//		def end = new Date().time
//		println "object build time ${end-start}"
//		assert address.streetsMaintenance == [[streetName:"NW 31ST AVE", maintenanceCode:"CO"], [streetName:"NW 29TH ST", maintenanceCode:"CO"]]
//		assert address.streetsLightMaintenance == [[streetName:'NW 31ST AVE', maintenanceCode:'LIGHT'], [streetName:'NW 29TH ST', maintenanceCode:'LIGHT']]
//	}
		
}
