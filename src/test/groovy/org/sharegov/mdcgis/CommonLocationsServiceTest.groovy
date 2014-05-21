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
import org.junit.*;
import org.sharegov.mdcgis.model.Address
import org.sharegov.mdcgis.model.CommonLocation
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.sharegov.mdcgis.utils.AppContext;


class CommonLocationsServiceTest {

	private CommonLocationsService commonLocationsService

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext("configtest.xml");

		ApplicationContext ctx = AppContext.getApplicationContext();
		commonLocationsService = (CommonLocationsService) ctx
				.getBean("COMMON_LOCATIONS_SERVICE");
	}

	@Test
	public void testGetRawCommonLocations_ByNameAndLayer(){
		// lets check a simple call
		def locations =  commonLocationsService.getRawCommonLocations("SOUTH MIAMI HOSPITAL", ['HOSPITAL', 'LANDMARK'])
		assert locations.size() == 2

		// and it should not matter if the name is lower case
		locations =  commonLocationsService.getRawCommonLocations("south miami hospital", ['HOSPITAL', 'LANDMARK'])
		assert locations.size() == 2

		// passing a non existent name should return zero results
		locations =  commonLocationsService.getRawCommonLocations("noname", ['HOSPITAL', 'LANDMARK'])
		assert locations.size() == 0

		// passing a null name should return zero results 
		locations =  commonLocationsService.getRawCommonLocations(null, ['HOSPITAL', 'LANDMARK'])
		assert locations.size() == 0
		
		// and passing no filters will use gisConfig.commonLocationsLayerNames as filters
		locations =  commonLocationsService.getRawCommonLocations("SOUTH MIAMI HOSPITAL", [])
		assert locations.size() == 4

		// and passing no filters will use gisConfig.commonLocationsLayerNames as filters
		locations =  commonLocationsService.getRawCommonLocations("SOUTH MIAMI HOSPITAL", null)
		assert locations.size() == 4

	}
	
	@Test
	public void testGetRawCommonLocations_ById(){
		// lets check a simple call
		def locations =  commonLocationsService.getRawCommonLocations(1780)
		assert locations.size() == 1
		def location = locations[0]
		location.with {
			location.OBJECTID == 1780
			location.LAYER == 'HOSPITAL'
			location.NAME == 'South Miami Hospital'
		}
	}
		
	@Test
	public void testGetCommonLocationCandidates(){
		def rawLocations =
				[
					[NAME:'South Miami Hospital', ADDRESS:'7400 SW 62ND AV', CITY:'SOUTH MIAMI', ZIP:33143, LAYER:'LANDMARK',OBJECTID:2035],
					[NAME:'SOUTH MIAMI HOSPITAL CCC', ADDRESS:'6200 SW 73RD ST', CITY:'SOUTH MIAMI', ZIP:33143, LAYER:'DAYCARE',OBJECTID:2073],
					[NAME:'South Miami Hospital', ADDRESS:'6200 SW 73 Street', CITY:'Miami', ZIP:33143, LAYER:'HOSPITAL',,OBJECTID:2074],
					[NAME:'SOUTH MIAMI HOSPITAL', ADDRESS:'6200 SW 73RD STREET', CITY:'SOUTH MIAMI', ZIP:33143, LAYER:'HURRICANE SHELTER',,OBJECTID:2075]
				]

		commonLocationsService.getMetaClass().getRawCommonLocations = {String name, List layerNames -> 
			rawLocations                         
		}
		
		assert commonLocationsService.getCommonLocationCandidates("SOUTH MIAMI HOSPITAL", ['HOSPITAL', 'LANDMARK']).size() == 4
	}
	
	
	@Test
	public void testGetCommonLocationCandidates_NoName(){
		assert [] == commonLocationsService.getCommonLocationCandidates("", null)
		assert [] == commonLocationsService.getCommonLocationCandidates(null, null)
	}
	
	@Test
	public void testGetCommonLocationCandidates_OneCandidate_PopulateAddressField(){
		def rawLocations =
		  [[NAME:'Miami Design District', ADDRESS:'4001 NE 2ND AVE', CITY:'MIAMI', ZIP:33137, LAYER:'LANDMARK',OBJECTID:5346]]
		def commonLocation = ["id":5346,"name":"Miami Design District","layer":"LANDMARK", "address":["address":"4001 NE 2ND AVE, 33137"]]  
		  commonLocationsService.getMetaClass().getRawCommonLocations = {String name, List layerNames -> rawLocations}
		  commonLocationsService.getMetaClass().getCommonLocation = {Integer objectId -> commonLocation}

		  assert commonLocationsService.getCommonLocationCandidates("Design District", ['LANDMARK'])[0].address.address == "4001 NE 2ND AVE, 33137"		  
	}
	
	@Test
	public void testGetCommonLocationByNameAndLayer(){
		def rawLocations =
				[
					[NAME:'South Miami Hospital', ADDRESS:'6200 SW 73 Street', CITY:'Miami', ZIP:33143, LAYER:'HOSPITAL',,OBJECTID:2035],
				]

		commonLocationsService.getMetaClass().getRawCommonLocations = {String name, List layerNames ->
			rawLocations
		}
		
		CommonLocation commonLocation =  commonLocationsService.getCommonLocation("SOUTH MIAMI HOSPITAL", 'HOSPITAL')
		assert commonLocation != null
		commonLocation.with{
			assert name == 'South Miami Hospital'
			assert layer == 'HOSPITAL' 
			assert id == 2035
			assert address.address == '6200 SW 73RD ST, 33143'
			assert address.score == 100
			assert address.location.x == 888722.074
			assert address.municipality == 'SOUTH MIAMI'
			assert address.parsedAddress.House == '6200'
			assert address.propertyInfo.parcelFolioNumber == '0940360400130'
			assert address.houseDistrictId == 114
			assert address.senateDistrictId == 40
		}
		
	}

	@Test
	public void testGetCommonLocationByNameAndLayer_NoMatch(){
		def rawLocations = []

		commonLocationsService.getMetaClass().getRawCommonLocations = {String name, List layerNames ->
			rawLocations
		}
		
		CommonLocation cl =  commonLocationsService.getCommonLocation("nolocation", 'HOSPITAL')
		assert cl == null
		
	}

	
	@Test
	public void testGetCommonLocationByNameAndLayer_TooManyMatches(){
		def rawLocations = [
			[NAME:'South Miami Hospital', ADDRESS:'7400 SW 62ND AV', CITY:'SOUTH MIAMI', ZIP:33143, LAYER:'LANDMARK'],
			[NAME:'SOUTH MIAMI HOSPITAL CCC', ADDRESS:'6200 SW 73RD ST', CITY:'SOUTH MIAMI', ZIP:33143, LAYER:'DAYCARE'],
			[NAME:'South Miami Hospital', ADDRESS:'6200 SW 73 Street', CITY:'Miami', ZIP:33143, LAYER:'HOSPITAL'],
			[NAME:'SOUTH MIAMI HOSPITAL', ADDRESS:'6200 SW 73RD STREET', CITY:'SOUTH MIAMI', ZIP:33143, LAYER:'HURRICANE SHELTER']
			]

		commonLocationsService.getMetaClass().getRawCommonLocations = {String name, List layerNames ->
			rawLocations
		}
		
		CommonLocation cl =  commonLocationsService.getCommonLocation("SOUTH MIAMI HOSPITAL", 'HOSPITAL')
		assert cl == null
		
	}
	
	
	
	@Test
	public void testGetCommonLocationById(){
		def rawLocations =
				[
					[NAME:'South Miami Hospital', ADDRESS:'6200 SW 73 Street', CITY:'Miami', ZIP:33143, LAYER:'HOSPITAL',,OBJECTID:2035],
				]

		commonLocationsService.getMetaClass().getRawCommonLocations = {Long id ->
			rawLocations
		}
		
		CommonLocation commonLocation =  commonLocationsService.getCommonLocation(2035)
		assert commonLocation != null
		commonLocation.with{
			assert name == 'South Miami Hospital'
			assert layer == 'HOSPITAL'
			assert id == 2035
			assert address.address == '6200 SW 73RD ST, 33143'
			assert address.score == 100
			assert address.location.x == 888722.074
			assert address.municipality == 'SOUTH MIAMI'
			assert address.parsedAddress.House == '6200'
			assert address.propertyInfo.parcelFolioNumber == '0940360400130'
			assert address.houseDistrictId == 114
			assert address.senateDistrictId == 40
		}
		
	}

	@Test
	public void testGetCommonLocationById_NoMatch(){
		def rawLocations = []

		commonLocationsService.getMetaClass().getRawCommonLocations = {Long id ->
			rawLocations
		}
		
		CommonLocation cl =  commonLocationsService.getCommonLocation(1)
		assert cl == null
		
	}

	@Test
	public void testGetCommonLocationById_Match(){
		
		CommonLocation cl =  commonLocationsService.getCommonLocation(2221)
		assert cl.id == 2221
		assert cl.layer == 'SCHOOL - PUBLIC'
		assert cl.name == "Kelley's ALF"

	}
	
	@Test
	public void testGetRawPollingLocationByPrecinct(){
		Map pollingPlace = commonLocationsService.getRawPollingLocationByPrecinct("156.0")
		assert pollingPlace.name == "Catholic Community Svc. Sr. Center"
		assert pollingPlace.address == "9900 NE 2 Ave"
		assert pollingPlace.municipality == "Miami Shores 33138"
		

	}
	
	@Test
	public void testGetRawPollingLocationByPrecinct_NullPrecinct(){
		Map pollingPlace = commonLocationsService.getRawPollingLocationByPrecinct(null)
		assert pollingPlace == null
		
		pollingPlace = commonLocationsService.getRawPollingLocationByPrecinct("")
		assert pollingPlace == null
	}
	
	@Test
	public void testGetRawPollingLocationByPrecinct_NoPrecinctExists(){
		Map pollingPlace = commonLocationsService.getRawPollingLocationByPrecinct("1111")
		assert pollingPlace == null
	}
	
	@Test
	public void testGetPollingLocationByPrecinct(){
		String pollingPlace = commonLocationsService.getPollingLocationByPrecinct("156.0")
		assert pollingPlace == "9900 NE 2 Ave, Miami Shores 33138 (Catholic Community Svc. Sr. Center)"
		
		pollingPlace = commonLocationsService.getPollingLocationByPrecinct("156")
		assert pollingPlace == "9900 NE 2 Ave, Miami Shores 33138 (Catholic Community Svc. Sr. Center)"
		
		pollingPlace = commonLocationsService.getPollingLocationByPrecinct("asdfasd")
		assert pollingPlace == ""
		
		commonLocationsService.getPollingLocationByPrecinct("2345")
		assert pollingPlace == ""
	}
	
	@Test
	public void testGetPollingLocationByPrecinct_156(){
		String pollingPlace = commonLocationsService.getPollingLocationByPrecinct("156")
		assert pollingPlace == "9900 NE 2 Ave, Miami Shores 33138 (Catholic Community Svc. Sr. Center)"
	}
}
