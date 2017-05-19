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
import org.sharegov.mdcgis.model.Address
import org.sharegov.mdcgis.utils.AppContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

class Esri10AddressServiceTest {

	private AddressService addressService

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext("configtest.xml");

		ApplicationContext ctx = AppContext.getApplicationContext();
		addressService = (AddressService) ctx
				.getBean("ADDRESS_SERVICE");
	}
	
	@After
	public void destroy() throws Exception{
		((ClassPathXmlApplicationContext) AppContext.getApplicationContext()).close();
	}

	@Test
	public void testGetAddress_House(){
		Address addr = addressService.getAddress("11826 sw 97th street", "33186")
		assert addr != null
		addr.with{
			assert address == '11826 SW 97TH ST, 33186'
			assert addressApproximation == false
			assert score == 100
			assert location.x == 857869.753
			assert municipality == 'UNINCORPORATED MIAMI-DADE'
			assert municipalityId == 30
			assert parsedAddress.House == '11826'
			assert propertyInfo.parcelFolioNumber == '3059010240130'
			assert propertyInfo.propertyType == 'UNDEFINED'
			assert houseDistrictId == 116
			assert senateDistrictId == 40
		}
	}


	
	@Test
	public void testGetAddress_Building(){
		def addressInput = [["2001 Meridian Ave", "33139"], ['7200 NW 177TH ST','33015']]
		def output = [
					[address:'2001 MERIDIAN AVE, 33139',x:940512.061,
							municipality:'MIAMI BEACH',parsedAddressHouse:'2001',
							parcelFolioNumber:'0232341690001',
							propertyType:'MULTI',numberOfUnits:113,
							houseDistrictId:113,senateDistrictId:38],
					  [address: "7200 NW 177TH ST, 33015", x:879912.975,
						  municipality: "UNINCORPORATED MIAMI-DADE",parsedAddressHouse:'7200',
						  parcelFolioNumber:'3020110620001',
						  propertyType:"MULTI",numberOfUnits:117,
						  houseDistrictId:110,senateDistrictId:36]]
		
		addressInput.eachWithIndex{input, counter  ->
			Address addr = addressService.getAddress(input[0], input[1])
			assert addr != null
			addr.with{
				assert address == output[counter]['address']
				assert location.x == output[counter]['x']
				assert municipality == output[counter]['municipality']
				assert parsedAddress.House == output[counter]['parsedAddressHouse']
				assert propertyInfo.parcelFolioNumber == output[counter]['parcelFolioNumber']
				assert propertyInfo.propertyType == output[counter]['propertyType']
				assert propertyInfo.units.size() == output[counter]['numberOfUnits']
				assert houseDistrictId == output[counter]['houseDistrictId']
				assert senateDistrictId == output[counter]['senateDistrictId']
			}
			
		}
	}
	
	@Test
	public void testGetCondoAddress(){
		Address addr = addressService.getCondoAddress("2001 Meridian Ave", "33139", "317")
		assert addr != null
		addr.with{
			assert address == '2001 MERIDIAN AVE, 33139'
			assert addressApproximation == false
			assert score == 100
			assert location.x == 940512.061
			assert municipality == 'MIAMI BEACH'
			assert municipalityId == 2
			assert parsedAddress.House == '2001'
			assert propertyInfo.parcelFolioNumber == '0232341690630'
			assert propertyInfo.propertyType == 'CONDO'
			assert houseDistrictId == 113
			assert senateDistrictId == 38
		}
	}

	
	@Test
	public void testGetCondoAddress_NullMunicipalityId(){
		Address addr = addressService.getCondoAddress("2001 Meridian Ave", "33139", "317", null)
		assert addr != null
		addr.with{
			assert address == '2001 MERIDIAN AVE, 33139'
			assert addressApproximation == false
			assert score == 100
			assert location.x == 940512.061
			assert municipality == 'MIAMI BEACH'
			assert municipalityId == 2
			assert parsedAddress.House == '2001'
			assert propertyInfo.parcelFolioNumber == '0232341690630'
			assert propertyInfo.propertyType == 'CONDO'
			assert houseDistrictId == 113
			assert senateDistrictId == 38
		}
	}

	@Test
	public void testGetCondoAddress_WithMunicipalityId(){
		Address addr = addressService.getCondoAddress("2001 Meridian Ave", "33139", "317", 2)
		assert addr != null
		addr.with{
			assert address == '2001 MERIDIAN AVE, 33139'
			assert addressApproximation == false
			assert score == 100
			assert location.x == 940512.061
			assert municipality == 'MIAMI BEACH'
			assert municipalityId == 2
			assert parsedAddress.House == '2001'
			assert propertyInfo.parcelFolioNumber == '0232341690630'
			assert propertyInfo.propertyType == 'CONDO'
			assert houseDistrictId == 113
			assert senateDistrictId == 38
		}
	}

	
	@Test
	public void testGetAddress_TooManyCandidates(){
		Address addr = addressService.getAddress("111 nw 1st street", "")
		assert addr == null
	}

	@Test
	public void testGetAddress_NoCandidates(){
		Address addr = addressService.getAddress("thereisnoaddress", "")
		assert addr == null
	}

	@Test
	public void testGetPartialAddress(){
		Address addr = addressService.getPartialAddress("11826 sw 97th street", "33186")
		assert addr != null
		addr.with{
			assert address == '11826 SW 97TH ST, 33186'
			assert addressApproximation == false
			assert score == 100
			assert location.x == 857869.753
			assert municipality == 'UNINCORPORATED MIAMI-DADE'
			assert municipalityId == 30
			assert parsedAddress.House == '11826'
			assert propertyInfo == [:]
			assert houseDistrictId == null
			assert senateDistrictId == null
		}
	}

	@Test
	public void testGetPartialAddress_TooManyCandidates(){
		Address addr = addressService.getPartialAddress("111 nw 1st street", "")
		assert addr == null
	}

	@Test
	public void testGetPartialAddress_NoCandidates(){
		Address addr = addressService.getPartialAddress("thereisnoaddress", "")
		assert addr == null
	}

	@Test
	public void testGetAddressFromCoord(){
		Address addr = addressService.getAddressFromCoord(857869.749956198, 488913.9375316277)

		assert addr != null
		addr.with{
			assert address == '11826 SW 97TH ST, 33186'
			assert addressApproximation == false
			assert score == 100
			assert location.x == 857869.753
			assert municipality == 'UNINCORPORATED MIAMI-DADE'
			assert municipalityId == 30
			assert parsedAddress.House == '11826'
			assert propertyInfo.parcelFolioNumber == '3059010240130'
			assert propertyInfo.propertyType == 'UNDEFINED'
			assert houseDistrictId == 116
			assert senateDistrictId == 40
		}
	}



	@Test
	public void testGetAddressFromCoord_InParcelButNoStreetNumber(){

		Address address = addressService.getAddressFromCoord(937870, 594529)
		assert address != null

		assert address.address == "2975 NE 206TH ST, 33180"
		assert address.municipality == 'AVENTURA'
		assert address.municipalityId == 28
		assert address.location.x == 937870.5
		assert address.location.y ==  594521.2
		assert address.addressApproximation == true
		assert address.propertyInfo.parcelFolioNumber == '2812340064600'
		assert address.propertyInfo.propertyType == 'UNDEFINED'
		assert address.streetsMaintenance ==  [
			[streetName:'NE 206TH ST', maintenanceCode:'CI', maintenanceCodeAlias:'City maintained']
		]
		assert address.streetsLightMaintenance == [
			[streetName:'NE 206TH ST', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243']
		]
	}


	@Test
	public void testGetdAddressFromCoord_InParcelButNoStreetNumber2(){

		Address address = addressService.getAddressFromCoord(898029, 512098.4)
		assert address != null

		assert address.address == "405 UNIVERSITY DR, 33134"
		assert address.municipality == 'CORAL GABLES'
		assert address.location.x == 898469.263
		assert address.location.y ==  512484.987
		assert address.addressApproximation == false
		assert address.propertyInfo.parcelFolioNumber == '0341170040220'
		assert address.propertyInfo.propertyType == 'UNDEFINED'
		assert address.streetsMaintenance ==  [
			[streetName:'UNIVERSITY DR', maintenanceCode:'CI', maintenanceCodeAlias:'City maintained']
		]
		assert address.streetsLightMaintenance == [
				[streetName:'ANASTASIA AVE', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243'], [streetName:'UNIVERSITY DR', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243']
		]
	}


	@Test
	public void testGetdAddressFromCoord_InParcelButNoStreetNumberPlaceXYToPropertyXY(){

		Address address = addressService.getAddressFromCoord(874845.306753479, 502312.591349639)
		assert address != null

		assert address.address == "5860 SW 87TH AVE, 33173"
		assert address.municipality == 'UNINCORPORATED MIAMI-DADE'
		assert address.municipalityId == 30
		assert address.location.x == 874841.0
		assert address.location.y == 502306.3
		assert address.addressApproximation == true
		assert address.propertyInfo.parcelFolioNumber == '3040280000050'
		assert address.propertyInfo.propertyType == 'UNDEFINED'
		assert address.streetsMaintenance ==  [
			[streetName:'SW 87TH AVE', maintenanceCode:'SR', maintenanceCodeAlias:'State maintained']
		]
		assert address.streetsLightMaintenance == [
			[streetName:'SW 87TH AVE', maintenanceCode:'CML', maintenanceCodeAlias:'County Maintained Light']
		]
	}
	
	
	

	@Test
	public void testGetAddressFromCoord_InStreet(){
		Address address = addressService.getAddressFromCoord(858465.453081198,489016.4765941277)
		assert address != null

		assert address.address == "11787 SW 97TH ST, 33186"
		assert address.municipality == 'UNINCORPORATED MIAMI-DADE'
		assert address.municipalityId == 30
		assert address.location.x == 858465.453
		assert address.location.y ==  489016.477
		assert address.addressApproximation == true
		assert address.propertyInfo == null
		assert address.streetsMaintenance ==  [
			[streetName:'SW 97TH ST', maintenanceCode:'CO', maintenanceCodeAlias:'County maintained']]
		assert address.streetsLightMaintenance == [
			[streetName:'SW 97TH ST', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243']]
	}

	@Test
	public void testGetAddressFromCoord_NothingClose(){
		Address address = addressService.getAddressFromCoord(1, 1)
		assert address == null
	}



	
	@Test
	public void testGetAddressByFolio_House(){
		Address addr = addressService.getAddressByFolio("3059010240130")
		assert addr != null
		addr.with{
			assert address == '11826 SW 97TH ST, 33186'
			assert addressApproximation == false
			assert score == 100
			assert location.x == 857869.753
			assert municipality == 'UNINCORPORATED MIAMI-DADE'
			assert municipalityId == 30
			assert parsedAddress.House == '11826'
			assert propertyInfo.parcelFolioNumber == '3059010240130'
			assert propertyInfo.propertyType == 'UNDEFINED'
			assert houseDistrictId == 116
			assert senateDistrictId == 40
		}
	}

	@Test
	public void testGetAddressByFolio_House2(){
		Address addr = addressService.getAddressByFolio("3040270050250")
		assert addr != null
		addr.with{
			assert address == '8220 SW 56TH ST, 33155'
			assert addressApproximation == false
			assert score == 100
			assert location.x == 877651.372
			assert municipality == 'UNINCORPORATED MIAMI-DADE'
			assert municipalityId == 30
			assert parsedAddress.House == '8220'
			assert propertyInfo.parcelFolioNumber == '3040270050250'
			assert propertyInfo.propertyType == 'UNDEFINED'
			assert houseDistrictId == 115
			assert senateDistrictId == 40
		}
	}
	
	@Test
	public void testGetAddressByFolio_Condo(){
		Address addr = addressService.getAddressByFolio("0232341690630")
		assert addr != null
		addr.with{
			assert address == '2001 MERIDIAN AVE, 33139'
			assert addressApproximation == false
			assert score == 100
			assert municipality == 'MIAMI BEACH'
			assert municipalityId == 2
			assert parsedAddress.House == '2001'
			assert propertyInfo.parcelFolioNumber == '0232341690630'
			assert propertyInfo.propertyType == 'CONDO'
			assert houseDistrictId == 113
			assert senateDistrictId == 38
		}
	}


	@Test void testGetAddressByFolio_FolioWithNoStreet(){
		Address addr = addressService.getAddressByFolio("0341170040650")

		addr.with{
			assert address == "405 UNIVERSITY DR, 33134"
			assert municipality == 'CORAL GABLES'
			assert municipalityId == 03
			assert location.x == 898469.263
			assert location.y ==  512484.987
			assert addressApproximation == false
			assert ( propertyInfo.parcelFolioNumber == '0341170040220')
			assert propertyInfo.propertyType == 'UNDEFINED'
			assert streetsMaintenance ==  [
				[streetName:'UNIVERSITY DR', maintenanceCode:'CI', maintenanceCodeAlias:'City maintained']
			]
			assert streetsLightMaintenance == [
					[streetName:'ANASTASIA AVE', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243'], [streetName:'UNIVERSITY DR', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243']
			]
		}
	}


	@Test
	public void testGetAddressByFolio_FolioNotExists(){
		Address address = addressService.getAddressByFolio("4")
		assert address == null
	}

	@Test
	public void testGetAddressByFolio_FolioNull(){
		Address address = addressService.getAddressByFolio(null)
		assert address == null
	}

	@Test
	public void testGetAddressByFolio_Building(){
		Address address = addressService.getAddressByFolio('3020110620001')
		assert address != null
	}
	
	@Test
	public void testBuildCondoAddressFromCandidate(){
		Map candidate = [address:'2001 MERIDIAN AVE, 33139', location:[x:940512.0610576011,y:532760.3259282038],
					streetMaintenanceIds : [42460],
					score:100,
					attributes:[Loc_name:'GeoAddress',Score:100,X:940512.0610580035,Y:532760.3259283006,
						HouseNum:2001,PreDir:'',PreType:'',StreetName:'MERIDIAN',StreetType:'AVE',SufDir:'',Zone:'33139',
						Match_addr:'2001 MERIDIAN AVE,33139',Side:'',LeftFrom:'',LeftTo:'',RightFrom:'',RightTo:'',LeftZone:'',
						RightZone:'']]
		Address address = addressService.buildCondoAddressFromCandidate(candidate, '317')
		assert address.address == '2001 MERIDIAN AVE, 33139'
		assert address.propertyInfo.parcelFolioNumber == '0232341690630'
		assert address.propertyInfo.propertyType == 'CONDO'
		assert address.streetsMaintenance == [[streetName:'N MERIDIAN AVE', maintenanceCode:'CI', maintenanceCodeAlias:'City maintained']]
		
	}


	@Test
	public void testBuildCondoAddressFromCandidate_BuildingExistsUnitNotExists(){
		Map candidate = [address:'2001 MERIDIAN AVE, 33139', location:[x:940512.0610576011,y:532760.3259282038],
					score:100,
					attributes:[Loc_name:'GeoAddress',Score:100,X:940512.0610580035,Y:532760.3259283006,
						HouseNum:2001,PreDir:'',PreType:'',StreetName:'MERIDIAN',StreetType:'AVE',SufDir:'',Zone:'33139',
						Match_addr:'2001 MERIDIAN AVE,33139',Side:'',LeftFrom:'',LeftTo:'',RightFrom:'',RightTo:'',LeftZone:'',
						RightZone:'']]
		Address address = addressService.buildCondoAddressFromCandidate(candidate, '5555')
		assert address == null
	}


	@Test
	public void testGetCandidateAddresses_distinguishByZip(){
		def candidateAddress = addressService.getCandidateAddresses("111 nw 1st street", "33128")

		assert candidateAddress.size()  == 1
		assert candidateAddress[0].address == "111 NW 1ST ST, 33128"
		assert candidateAddress[0].propertyInfo.parcelFolioNumber == "0141370230020"
		assert candidateAddress[0].propertyInfo.propertyType == 'UNDEFINED'
	}

	@Test
	public void testGetCandidateAddresses_NoPropertyInfoForGeoStreet(){
		def candidateAddress = addressService.getCandidateAddresses("111 nw 1st street", "33030")
		assert candidateAddress.size()  == 1
		assert candidateAddress[0].address == "111 NW 1ST ST, 33030"
		assert candidateAddress[0].propertyInfo == [:]
	}

	@Test
	public void testGetCandidateAddresses_ParsedAddressHasZipInGeoStreet(){
		def candidateAddress = addressService.getCandidateAddresses("111 nw 1st street", "33030")
		assert candidateAddress.size()  == 1
		assert candidateAddress[0].address == "111 NW 1ST ST, 33030"
		assert candidateAddress[0].propertyInfo == [:]
		assert candidateAddress[0].parsedAddress.zip == "33030"
	}


	@Test
	public void testGetCandidateAddresses_Singles(){
		def addressInput = [
			'140 W Flagler St',
			'626 Euclid Ave',
			'126 Calabria Ave',
			'1650 W 68 St',
			'450 Eldron Dr',
			'520 NW 124 St',
			'501 NE 180 Dr',
			'3250 NW 132 Ter',
			'6510 SW 57 Pl',
			'972 NE 42 Ave',
			'9880 NE 9 Ave',
			'127 Bal Bay Dr',
			'1075 101 St',
			'9580 Byron Ave',
			'5770 SW 10 St',
			'11830 NE 6 Ave',
			'461 NE 88 St',
			'88 Terracina Ave',
			'12304 SW 71 Ct',
			'35 Indian Creek Dr',
			'7400 NW 79 Ave',
			'7540 Mutiny Ave',
			'414 Fernwood Rd',
			'11502 NW 4 Way',
			'3800 NW 65 Ave',
			'9085 NW 117 St',
			'20913 NE 30 Ct',
			'7655 NW 179 Ter',
			'16900 N  Bay Rd',
			'15143 NW 89 Pl',
			'9630 SW 182 St',
			'4320 NW 187 St',
			'4633 NW 94 Pl',
			'9862 SW 196 St'
		]
		def addressOutput = [
			'140 W FLAGLER ST, 33130',
			'626 EUCLID AVE, 33139',
			'126 CALABRIA AVE, 33134',
			'1650 W 68TH ST, 33014',
			'450 ELDRON DR, 33166',
			'520 NW 124TH ST, 33168',
			'501 NE 180TH DR, 33162',
			'3250 NW 132ND TER, 33054',
			'6510 SW 57TH PL, 33143',
			'972 NE 42ND AVE, 33033',
			'9880 NE 9TH AVE, 33138',
			'127 BAL BAY DR, 33154',
			'1075 101ST ST, 33154',
			'9580 BYRON AVE, 33154',
			'5770 SW 10TH ST, 33144',
			'11830 NE 6TH AVE, 33161',
			'461 NE 88TH ST, 33138',
			'88 TERRACINA AVE, 33160',
			'12304 SW 71ST CT, 33156',
			'35 INDIAN CREEK DR, 33154',
			'7400 NW 79TH AVE, 33166',
			'7540 MUTINY AVE, 33141',
			'414 FERNWOOD RD, 33149',
			'11502 NW 4TH WAY, 33172',
			'3800 NW 65TH AVE, 33166',
			'9085 NW 117TH ST, 33018',
			'20913 NE 30TH CT, 33180',
			'7655 NW 179TH TER, 33015',
			'16900 N BAY RD, 33160',
			'15143 NW 89TH PL, 33018',
			'9630 SW 182ND ST, 33157',
			'4320 NW 187TH ST, 33055',
			'4633 NW 94TH PL, 33178',
			'9862 SW 196TH ST, 33157'
		]

		addressInput.eachWithIndex{address, counter ->
			def candidateAddresses = addressService.getCandidateAddresses(address, "")
			assert candidateAddresses.size() == 1
			assert candidateAddresses[0].address == addressOutput[counter]
		}
	}

	@Test
	public void testGetCandidateAddresses_Duplicates(){
		def addressInput = [
			'409 W 60th ST',
			'310 NW 1st ST',
			'360 NW 3rd ST',
			'111 NW 1st ST',
			'300 W 44th ST',
			'120 SE 1st AVE',
			'1300 Coral Way',
			'357 W 41st ST',
			'160 NW 7th ST',
			'1809 Washington AVE'
		]

		def addressOutput = [
			[
				'409 W 60TH ST, 33012',
				'409 W 60TH ST, 33140'
			],
			[
				'310 NW 1ST ST, 33034',
				'310 NW 1ST ST, 33128'
			],
			[	
				'360 NW 3RD ST, 33034',
				'360 NW 3RD ST, 33128'
			],
			[
				'111 NW 1ST ST, 33128',
				'111 NW 1ST ST, 33030'
			],
			[
				'300 W 44TH ST, 33012',
				'300 W 44TH ST, 33140'
			],
			[
				'120 SE 1ST AVE, 33034',
				'120 SE 1ST AVE, 33131'
			],
			[
				'1300 CORAL WAY, 33134',
				'1300 CORAL WAY, 33145'
			],
			[
				'357 W 41ST ST, 33012',
				'357 W 41ST ST, 33140'
			],
			[
				'160 NW 7TH ST, 33136',
				'160 NW 7TH ST, 33030'
			],
			[
				'1809 WASHINGTON AVE, 33139',
				'1809 WASHINGTON AVE, 33054'
			]
		]


		addressInput.eachWithIndex{address, counter ->
			def candidateAddresses = addressService.getCandidateAddresses(address, "")
			assert candidateAddresses.size() == 2
			assert candidateAddresses[0].address == addressOutput[counter][0]
			assert candidateAddresses[1].address == addressOutput[counter][1]
		}
	}




	@Test
	public void testGetCandidateAddresses_Triples(){
		def addressInput = [
			'327 NW 2 St',
			'144 NW 2ND AVE',
			'3661 S Le Jeune Rd'
		]
		def addressOutput = [
			[
				'327 NW 2ND ST, 33034',
				'327 NW 2ND ST, 33030',
				'327 NW 2ND ST, 33128'
			],
			[
				'144 NW 2ND AVE, 33128',
				'144 NW 2ND AVE, 33030',
				'144 NW 2ND AVE, 33034'
			],
			[
				'3661 S LE JEUNE RD, 33146',
				'3661 S LE JEUNE RD, 33134',
				'3661 S LE JEUNE RD, 33146'
			],
		]

		addressInput.eachWithIndex{address, counter ->
			def candidateAddresses = addressService.getCandidateAddresses(address, "")
			println(candidateAddresses[0].address);
			println(candidateAddresses[1].address);
			println(candidateAddresses[2].address);
			assert candidateAddresses.size() == 3
			assert candidateAddresses[0].address == addressOutput[counter][0]
			assert candidateAddresses[1].address == addressOutput[counter][1]
			assert candidateAddresses[2].address == addressOutput[counter][2]
		}
	}


	@Test
	public void testGetCandidateAddresses_Intersections(){
		def addressInput = 	[
			'nw 1st st & nw 2nd ave',
			'nw 1st st and nw 2nd ave',
			'SW 56th ST & SW 87th AVE',
			'137th & 42nd'
		]
		def addressOutput = [
			[
				'NW 1ST ST & NW 2ND AVE, 33030',
				'NW 1ST ST & NW 2ND AVE, 33034',
				'NW 1ST ST & NW 2ND AVE, 33128'
			],
			[
				'NW 1ST ST & NW 2ND AVE, 33030',
				'NW 1ST ST & NW 2ND AVE, 33034',
				'NW 1ST ST & NW 2ND AVE, 33128'
			],
			[
				'SW 56TH ST & SW 87TH AVE, 33143',
				'SW 56TH ST & SW 87TH AVE, 33155',
				'SW 56TH ST & SW 87TH AVE, 33165',
				'SW 56TH ST & SW 87TH AVE, 33173'
			],
			[
				'SW 137TH PL & SW 42ND TER, 33175',
				'SW 137TH CT & SW 42ND ST, 33175',
				'SW 137TH CT & SW 42ND TER, 33175',
				'SW 137TH AVE & SW 42ND ST, 33175',
				'SW 137TH AVE & NE 42ND AVE, 33033'
			]
		]

		addressInput.eachWithIndex{address, counter ->
			def candidateAddresses = addressService.getCandidateAddresses(address, "")
			assert candidateAddresses.size() == addressOutput[counter].size()

			candidateAddresses.eachWithIndex{candidateAddress, index ->
				assert candidateAddress.address == addressOutput[counter][index]
			}
		}
	}


	@Test
	public void testGetCandidateAddresses_IntersectionsWithAlias(){
		def addressInput = 	[
			'sw 87th avenue and bird road',
			'sw 87th ave and bird road',
			'sw 87th and bird road',
			'Hibiscus and shipping',
			'Galloway and Coral Way',
			'137th & Bird Road'
		]
		def addressOutput = [
			[
				'SW 87TH AVE & BIRD RD, 33165'
			],
			[
				'SW 87TH AVE & BIRD RD, 33165'
			],
			[
				'SW 87TH PL & BIRD RD, 33165',
				'SW 87TH CT & BIRD RD, 33165',
				'SW 87TH AVE & BIRD RD, 33165'
			],
			[
				'HIBISCUS ST & SHIPPING AVE, 33133'
			],
			[
				'GALLOWAY RD & CORAL WAY, 33155',
				'GALLOWAY RD & CORAL WAY, 33165'
			],
			[
				'SW 137TH CT & BIRD RD, 33175',
				'SW 137TH AVE & BIRD RD, 33175'
			]
		]

		addressInput.eachWithIndex{address, counter ->
			def candidateAddresses = addressService.getCandidateAddresses(address, "")
			assert candidateAddresses.size() == addressOutput[counter].size()

			candidateAddresses.eachWithIndex{candidateAddress, index ->
				assert candidateAddress.address == addressOutput[counter][index]
			}
		}
	}






	@Test
	public void testGetCandidateAddresses_UsingAlias(){
		def addressInput = [
			'8700 Bird Road',
			'6700 Coral Way',
			'8700 kendall drive',
			'8700 Killian'
		]

		def addressOutput =[
			['8700 BIRD RD, 33165'],
			['6700 CORAL WAY, 33155'],
			['8700 N KENDALL DR, 33176'],
			['8700 KILLIAN DR, 33176']
		]
		addressInput.eachWithIndex{address, counter ->
			def candidateAddresses = addressService.getCandidateAddresses(address, "")
			assert candidateAddresses.size() == addressOutput[counter].size()

			candidateAddresses.eachWithIndex{candidateAddress, index ->
				assert candidateAddress.address == addressOutput[counter][index]
			}
		}
	}

	@Test
	public void testGetCandidateAddresses_Miscellaneous(){
		def addressInput = 	[
			'830 almeria',
//			'8720 sw 41',
			'38th AND 137th',
//			'8720 sw 41st'
		]
		def addressOutput = [
			['830 ALMERIA AVE, 33134'],
//			['8720 SW 41ST TER, 33165',
//				'8720 SW 41ST ST, 33165',
//				'8720 NW 41ST ST, 33178',
//				'8721 SW 41ST TER, 33165',
//				'8721 SW 41ST ST, 33165'
//			],
			[
				'SW 38TH ST & SW 137TH CT, 33175',
				'SW 38TH ST & SW 137TH AVE, 33175',
				'SW 328TH ST & SW 137TH AVE, 33033',
				'SW 328TH ST & SW 137TH AVE, 33035',
				'SW 368TH ST & SW 137TH AVE, 33035']
			//,
//			['8720 SW 41ST TER, 33165',
//				'8720 SW 41ST ST, 33165',
//				'8720 NW 41ST ST, 33178',
//				'8721 SW 41ST TER, 33165',
//				'8721 SW 41ST ST, 33165']
		]
		
		addressInput.eachWithIndex{address, counter ->
			def candidateAddresses = addressService.getCandidateAddresses(address, "")
			assert candidateAddresses.size() == addressOutput[counter].size()

			candidateAddresses.eachWithIndex{candidateAddress, index ->
				assert candidateAddress.address == addressOutput[counter][index]
			}
		}
	}


	@Test
	public void testGetCadidateAddresses_ManyResults_MinimalAddress(){
		List candidateAddresses = addressService.getCandidateAddresses('310 NW 1st ST', "")
		assert candidateAddresses.size() == 2
		Address address1 = candidateAddresses[0]
		assert address1.propertyInfo == [:]
		assert address1.senateDistrictId == null
		assert address1.address == '310 NW 1ST ST, 33034'
		assert address1.municipality == 'FLORIDA CITY'
		assert address1.municipalityId == 16 
	}

	@Test
	public void testGetCadidateAddresses_OneResult_FullAddress(){

		List candidateAddresses = addressService.getCandidateAddresses('11826 sw 97th st', "")

		assert candidateAddresses.size() == 1
		Address address1 = candidateAddresses[0]
		assert address1.propertyInfo.keySet().containsAll(EsriFieldMappings.propertyInfoAttributes['MDC.PaParcel'].values())
		assert address1.senateDistrictId == 40
		assert address1.propertyInfo.propertyType == 'UNDEFINED'
		assert address1.address == '11826 SW 97TH ST, 33186'
		assert address1.addressApproximation == false
		assert address1.municipality == 'UNINCORPORATED MIAMI-DADE'
		assert address1.municipalityId == 30
		assert address1.parsedAddress.zip == '33186'
		
	}


	@Test
	public void testBuildParsedAddress_IntersectionZip(){
		Map intersectionCandidate = [address : "SW 56TH ST & SW 87TH AVE,  30, 33143",
			location : [x : 875137.9365699999, y : 503291.49812828749],
			score : 100,
			attributes : [
		        Loc_name : "GeoStreet",
		        Score : 100,
		        Match_addr : "SW 56TH ST & SW 87TH AVE,  30, 33143",
		        House : "",
		        Side : "",
		        PreDir : "",
		        PreType : "",
		        StreetName : "",
		        SufType : "",
		        SufDir : "",
		        City : "",
		        State : "",
		        ZIP : "",
		        Ref_ID : 0,
		        X : 875137.93656299997,
		        Y : 503291.49813899997,
		        Addr_type : "Intersection",
		        Side1 : "L",
		        FromAddr1 : "8400",
		        ToAddr1 : "8698",
		        PreDir1 : "SW",
		        PreType1 : "",
		        StreetName1 : "56TH",
		        SufType1 : "ST",
		        SufDir1 : "",
		        State1 : "",
		        ZIP1 : "33143",
		        Ref_ID1 : 63407,
		        Side2 : "R",
		        FromAddr2 : "5550",
		        ToAddr2 : "5598",
		        PreDir2 : "SW",
		        PreType2 : "",
		        StreetName2 : "87TH",
		        SufType2 : "AVE",
		        SufDir2 : "",
		        State2 : "",
		        ZIP2 : "33165",
		        Ref_ID2 : 244904]]
		

		Map parsedAddress = addressService.buildParsedAddress(intersectionCandidate)
		assert parsedAddress.zip == '33143'
	}



	@Test
	public void testBuildFullCandidateAddress_Miami(){

		Map candidate = [address:"2546 NW 29TH ST, 33142",
					location:[x:906589.1248736754, y:534593.687447615],
					score:100, municipalityId : 1,
					attributes:[Loc_name:"GeoAddress", Score:100, X:906589.1248740777, Y:534593.6874477118, HouseNum:2546, PreDir:"NW", PreType:"",
						StreetName:"29TH", StreetType:"ST", SufDir:"", Zone:"33142",
						Match_addr:"2546 NW 29TH ST, 33142", Side:"", LeftFrom:"", LeftTo:"", RightFrom:"", RightTo:"",
						LeftZone:"", RightZone:""]]

		Address addr = addressService.buildAddressFromCandidate(candidate)
		addr.with{
			assert address == "2546 NW 29TH ST, 33142"
			assert score == 100
			assert location.x == 906589.1248736754
			assert location.y == 534593.687447615
			assert municipality == "MIAMI"
			assert propertyInfo.parcelFolioNumber == "0131270160980"
			assert propertyInfo.propertyType == "UNDEFINED"
			assert districtNumber == 2
			assert commissionerName == "Jean Monestime"
			assert cityCommissionerName == "Wifredo (Willy) Gort"
			assert recyclingRoute == "Friday A"
			assert recyclingWeekDay == 'Friday'
			assert recyclingPickUpWeek == null
			assert recyclingPickUpCode == null
			assert recyclingInServiceArea == null
			assert recyclingIsMunic == null
			assert recyclingDescription == null
			assert recyclingIsRecycle == null
			assert garbagePickupRoute == "113"
			assert garbagePickupAlias == null
			assert garbagePickupDay == "MON/THU"
			assert garbagePickupType == null
			assert utilityName == "MDWS"
			assert floodZone == "AE"
			assert hurricaneEvacZone == "E"
			assert netOfficeName == "Allapattah"
			assert houseDistrictId == 111
			assert senateDistrictId == 37
			assert electionsPrecinctId == 594
			assert addressType == null
			assert locatorName == "GeoAddress"

		}


	}

	@Test
	public void testBuildFullCandidateAddress_NotMiami(){

		Map candidate =[address:"3123 NW 29TH ST, 33142", municipalityId:30, location:[x:904028.0626120828, y:534703.3748988137], score:100,
						streetMaintenanceIds : [36745],
						attributes:[Loc_name : "GeoAddress",Score : 100,Match_addr : "3123 NW 29TH ST, 30, 33142",
						House : "3123",Side : "",PreDir : "NW",PreType : "",StreetName : "29TH",SufType : "ST",SufDir : "",
						City : "30",State : "",ZIP : "33142",Ref_ID : 198169,X : 904028.06132400001,Y : 534703.37271100003,
						Addr_type : "StreetAddress",	FromAddr : "",ToAddr : ""]]

		Address addr = addressService.buildAddressFromCandidate(candidate)

		addr.with{
			assert address == "3123 NW 29TH ST, 33142"
			assert score == 100
			assert location.x == 904028.0626120828
			assert location.y == 534703.3748988137
			assert municipality == "UNINCORPORATED MIAMI-DADE"
			assert propertyInfo.parcelFolioNumber == "3031280112920"
			assert propertyInfo.propertyType == "UNDEFINED"
			assert districtNumber == 2
			assert commissionerName == "Jean Monestime"
			assert cityCommissionerName == null
			assert recyclingRoute == "15A12"
			assert recyclingWeekDay == "FRIDAY"
			assert recyclingPickUpWeek == "A"
			assert recyclingPickUpCode ==  "5 FRI A"
			assert recyclingInServiceArea == "Y"
			assert recyclingIsMunic == "N"
			assert recyclingDescription == null
			assert recyclingIsRecycle == "Y"
			assert garbagePickupRoute == "2218"
			assert garbagePickupAlias ==   "2218"
			assert garbagePickupDay == "2"
			assert garbagePickupType == "A"
			assert utilityName == "MDWS"
			assert floodZone == "AE"
			assert hurricaneEvacZone == "D"
			assert netOfficeName == null
			assert houseDistrictId == 111
			assert senateDistrictId == 37
			assert electionsPrecinctId == 284
			assert addressType == 'StreetAddress'
			assert locatorName == "GeoAddress"
			assert streetsMaintenance == [[streetName:'NW 29TH ST', maintenanceCode:'CO', maintenanceCodeAlias:'County maintained']]
			assert streetsLightMaintenance == [
				[streetName:'NW 31ST AVE', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243'],
				[streetName:'NW 29TH ST', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243']
				
			]
		}

	}

	
	@Test
	public void testBuildAddressFromCoord_OutsideOfZipBounderiesPickZipofApproxAddress(){

		Address address = addressService.buildAddressFromCoord(887776.438, 590660.938)
		assert address != null

		assert address.address == "5732 NW 202ND ST, 33015"
		assert address.parsedAddress.zip == '33015'
	}
	
	
	@Test
	public void testBuildAddressFromCoord_InParcelButNoStreetNumber(){

		Address address = addressService.buildAddressFromCoord(937870, 594529)
		assert address != null

		assert address.address == "2975 NE 206TH ST, 33180"
		assert address.municipality == 'AVENTURA'
		assert address.municipalityId == 28
		assert address.location.x == 937870
		assert address.location.y ==  594529
		assert address.addressApproximation == true
		assert address.propertyInfo.parcelFolioNumber == '2812340064600'
		assert address.propertyInfo.propertyType == 'UNDEFINED'

		assert address.parsedAddress.zip == '33180'
		assert address.streetsMaintenance ==  [
			[streetName:'NE 206TH ST', maintenanceCode:'CI', maintenanceCodeAlias:'City maintained']
		]
		assert address.streetsLightMaintenance == [
			[streetName:'NE 206TH ST', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243']
		]

	}


	@Test
	public void testBuildAddressFromCoord_InParcelButNoStreetNumber2(){

		Address address = addressService.buildAddressFromCoord(898029, 512098.4)
		assert address != null

		assert address.address == "3361 RIVIERA DR, 33134"
		assert address.municipality == 'CORAL GABLES'
		assert address.municipalityId == 3
		assert address.location.x == 898029
		assert address.location.y ==  512098.4
		assert address.addressApproximation == true
		assert address.propertyInfo.parcelFolioNumber == '0341170040650'
		assert address.propertyInfo.propertyType == 'UNDEFINED'
		assert address.parsedAddress.zip == '33134'
		assert address.streetsMaintenance ==  [
			[streetName:'RIVIERA DR', maintenanceCode:'CI', maintenanceCodeAlias:'City maintained']
		]
		assert address.streetsLightMaintenance == [
			[streetName:'RIVIERA DR', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243']
		]

	}

	@Test
	public void testBuildAddressFromCoord_InStreet2(){
		Address address = addressService.buildAddressFromCoord(918939.499,542836.124)
		assert true
	}
	
	@Test
	public void testBuildAddressFromCoord_InStreet(){
		Address address = addressService.buildAddressFromCoord(858465.453081198,489016.4765941277)
		assert address != null

		assert address.address == "11787 SW 97TH ST, 33186"
		assert address.municipality == 'UNINCORPORATED MIAMI-DADE'
		assert address.municipalityId == 30
		assert address.location.x == 858465.453
		assert address.location.y ==  489016.477
		assert address.addressApproximation == true
		assert address.parsedAddress.zip == '33186'
		assert address.propertyInfo == null
		assert address.streetsMaintenance ==  [
			[streetName:'SW 97TH ST', maintenanceCode:'CO', maintenanceCodeAlias:'County maintained']]
		assert address.streetsLightMaintenance == [
			[streetName:'SW 97TH ST', maintenanceCode:'LIGHT', maintenanceCodeAlias:'FPL - (800) 468-8243']]

	}

	@Test
	public void testBuildAddressFromCoord_NothingClose(){
		Address address = addressService.buildAddressFromCoord(1, 1)
		assert address == null
	}
	
	
	@Test
	public void testBuildPartialAddressFromCandidate(){
		
		Map candidate =[address:"3123 NW 29TH ST, 33142", location:[x:904028.0626120828, y:534703.3748988137], score:100,
			streetMaintenanceIds : [36745], municipalityId : 30, 
			attributes:[Loc_name : "GeoAddress",Score : 100,Match_addr : "3123 NW 29TH ST, 30, 33142",
			House : "3123",Side : "",PreDir : "NW",PreType : "",StreetName : "29TH",SufType : "ST",SufDir : "",
			City : "30",State : "",ZIP : "33142",Ref_ID : 198169,X : 904028.06132400001,Y : 534703.37271100003,
			Addr_type : "StreetAddress",	FromAddr : "",ToAddr : ""]]
		
		
		Address addr = addressService.buildPartialAddressFromCandidate(candidate)
		
		addr.with{
			assert address == "3123 NW 29TH ST, 33142"
			assert score == 100
			assert addressType == "StreetAddress"
			assert locatorName == "GeoAddress"
			assert location == [x:904028.0626120828, y:534703.3748988137]
			assert municipalityId == 30
			assert municipality == 'UNINCORPORATED MIAMI-DADE'
		}

		
		
	}
	

	
	@Test
	public void testTranslateCodes(){
		Map dataToBeTranslated = [layer1:[attr1:'h', attr2:'h', attr3:'c'], 
								  layer2:[attr3:'cc', attr4:'dd']]
		addressService.gisConfig.codeTranslator = [layer1:[attr1:['h':'hello','g':'goodbye'],attr3:['d':'dog'],attr4:['d':'dog']],
												   layer3:[:]]
		assert addressService.translateCodes(dataToBeTranslated) == [layer1:[attr1:'h', 	attr2:'h', attr3:'c',attr1ALIAS:'hello'], 
																	 layer2:[attr3:'cc', attr4:'dd']]
	}
	
	
	@Test void testTranslateCodes2(){
		Map dataToBeTranslated = [attr1:'h', attr2:'h', attr3:'c']
		Map codeTranslator = [attr1:['h':'hello','g':'goodbye'],attr3:['d':'dog'],attr4:['d':'dog']]
		
		Map value = addressService.translateCodes(dataToBeTranslated, codeTranslator)
		assert value == [attr1ALIAS:'hello', attr1:'h', attr2:'h', attr3:'c']
	}
	
	@Test void testTranslateCodes2_IntegerKeys(){
		Map dataToBeTranslated = [attr1:1, attr2:'h', attr3:'c']
		Map codeTranslator = [attr1:[1:'hello',2:'goodbye'],attr3:['d':'dog'],attr4:['d':'dog']]
		
		Map value = addressService.translateCodes(dataToBeTranslated, codeTranslator)
		assert value == [attr1ALIAS:'hello', attr1:1, attr2:'h', attr3:'c']
	}
	
	@Test void testTranslateCodes6(){
		List dataToBeTranslated = [[attr1:'h', attr2:'h', attr3:'c'], [attr1:'h', attr2:'g', attr3:'d']]
		Map codeTranslator = [attr1:['h':'hello','g':'goodbye'],attr3:['d':'dog'],attr4:['d':'dog']]
		
		List value = addressService.translateCodes(dataToBeTranslated, codeTranslator)
		assert value == [[attr1:'h',attr1ALIAS:'hello', attr2:'h', attr3:'c'], 
						 [attr1:'h', attr1ALIAS:'hello', attr2:'g', attr3:'d',attr3ALIAS:'dog']]
	}

	
		
	@Test
	public void testTranslateCodes9(){
		Map dataToBeTranslated = [layer1:[attr1:'h', attr2:'h', attr3:'c'],
								  layer2:[attr3:'cc', attr4:'dd'], 
								  layer3:[attr1:'a', attr2:'b']]
		addressService.gisConfig.codeTranslator = [layer1:[attr1:['h':'hello','g':'goodbye'],attr3:['d':'dog'],attr4:['d':'dog']]]
		assert addressService.translateCodes(dataToBeTranslated) == [layer1:[attr1:'h', attr1ALIAS:'hello', attr2:'h', attr3:'c'],
																	 layer2:[attr3:'cc', attr4:'dd'],
																	 layer3:[attr1:'a', attr2:'b']]
	}
	
	@Test
	public void testTranslateCodes11(){
		Map dataToBeTranslated = [layer1:[[attr1:'h', attr2:'h', attr3:'c'],[attr1:'g', attr2:'h', attr3:'d']],
								  layer2:[attr3:'cc', attr4:'dd'],
								  layer3:[attr1:'a', attr2:'b']]
		addressService.gisConfig.codeTranslator = [layer1:[attr1:['h':'hello','g':'goodbye'],attr3:['d':'dog'],attr4:['d':'dog']]]
		assert addressService.translateCodes(dataToBeTranslated) == [layer1:[[attr1:'h', attr1ALIAS:'hello', attr2:'h', attr3:'c'],
																			 [attr1ALIAS:'goodbye', attr1:'g', attr2:'h', attr3ALIAS:'dog', attr3:'d']],
																	 layer2:[attr3:'cc', attr4:'dd'],
																	 layer3:[attr1:'a', attr2:'b']]
	}
	
	@Test
	public void testTurkeyPointAreaId(){

		def addressInput = ['15365 SW 296th Street',
			                '14515 SW 286th Street',
							'11826 sw 97th street']
		def output = [[turkeyPointAreaId:8],
					  [turkeyPointAreaId:7],
					  [turkeyPointAreaId:null]]
		
		addressInput.eachWithIndex{input, counter  ->
			Address addr = addressService.getAddress(input, null)
			assert addr != null
			addr.with{
				assert turkeyPointAreaId == output[counter]['turkeyPointAreaId']
			}
		}
	}

	
}
