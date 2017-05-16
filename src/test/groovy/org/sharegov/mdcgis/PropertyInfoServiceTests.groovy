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

import groovy.json.JsonBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.sharegov.mdcgis.utils.AppContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

class PropertyInfoServiceTests {

	private PropertyInfoService propertyInfoService
	private AddressController addressController

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext("configtest.xml");

		ApplicationContext ctx = AppContext.getApplicationContext();
		propertyInfoService = (PropertyInfoService) ctx
				.getBean("PROPERTYINFO_SERVICE");

		addressController = (AddressController) ctx
				.getBean("ADDRESS_CONTROLLER");
	}
	
	@After
	public void destroy() throws Exception{
		((ClassPathXmlApplicationContext) AppContext.getApplicationContext()).close();
	}

	@Test
	public void testGetPropertyInformation_with_WrongFolio() {
		Map propertyInfo = propertyInfoService.getPropertyInfoByFolio('0000000000000')
		assert propertyInfo.isEmpty() == true
	}

	@Test
	public void testGetPropertyInformationByFolioNumber() {
		Map propertyInfo = propertyInfoService.getPropertyInfoByFolio('3022060602360')
		propertyInfo.with {
			assert parcelFolioNumber == '3022060602360'
		}
	}

	@Test
	public void testGetPropertyInformationByFolioNumber_Condo(){
		Map propertyInfo = propertyInfoService.getPropertyInfoByFolio('0232341690630')
		propertyInfo.with {
			assert parcelFolioNumber == '0232341690630'
			assert propertyType == 'CONDO'
		}
	}

	@Test
	public void testGetLayerInformation_With_CorrectData(){
		Map data = [street:"11826 SW 97th Street", zip:33186]
		JsonBuilder result =addressController.getLayerInformation("MDC.RecyclingRoute", data);
		assert result.getProperties().get("content").getAt("ok") == true
	}

	@Test
	public void testGetLayerInformation_With_WrongZip(){
		Map data = [street:"11826 SW 97th Street", zip:33133]
		JsonBuilder result =addressController.getLayerInformation("MDC.RecyclingRoute", data);
		assert result.getProperties().get("content").getAt("ok") == false
	}

	@Test
	public void testGetLayerInformation_With_NoData(){
		Map data = [street:"", zip:""]
		JsonBuilder result =addressController.getLayerInformation("MDC.RecyclingRoute", data);
		assert result.getProperties().get("content").getAt("ok") == false
	}

	@Test
	public void testGetLayerInformation_With_WrongParam(){
		Map data = [streettt:"", zippp:""]
		JsonBuilder result =addressController.getLayerInformation("MDC.RecyclingRoute", data);
		assert result.getProperties().get("content").getAt("ok") == false
	}

	@Test
	public void testGetPropertyInfo_NoProperty(){
		assert propertyInfoService.getPropertyInfo(1, 1) == null
	}

	@Test
	public void testGetPropertyInfo_House(){

		// 11826 sw 97th street {x: 857869.749956198,y: 488913.9375316277}
		Map propertyInfo = propertyInfoService.getPropertyInfo(857869.749956198, 488913.9375316277)
		propertyInfo.with{
			assert parcelFolioNumber == '3059010240130'
			assert parcelInfoLegal == "GLEN COVE WEST SEC 1 PB 118-30 LOT 13 BKL 1 LOT SIZE 8800 SQ FT OR 17101-5181 0296 1"
			assert parcelInfoPtxAddress == "11826 SW 97 ST"
			assert parcelInfoAddress == "11826 SW 97 ST"
			assert propertyType == 'UNDEFINED'
		}
	}

	@Test
	public void testGetPropertyInfo_Building(){

		// 2001 Meridian Ave x: 940512.0610576011, y:532760.3259282038
		Map propertyInfo = propertyInfoService.getPropertyInfo(940512.0610576011, 532760.3259282038)
		propertyInfo.with {
			assert parcelInfoLegal == "THE MERIDIAN CONDO MID-GOLF RE-SUB PB 30-19 LOTS 4 THRU 13 INC AS DESC IN DEC OR 23419-1883 LOT SIZE 71579 SQ FT FAU 02-3234-023-0030"
			assert parcelInfoPtxAddress == "2001 MERIDIAN AVE"
			assert parcelInfoAddress == "2001 MERIDIAN AVE"
			assert parcelFolioNumber == "0232341690001"
			assert propertyType == "MULTI"
			assert units.size() == 113
		}
	}

	@Test
	public void testGetPropertyInfo_ParcelButNoStreet(){

		Map propertyInfo = propertyInfoService.getPropertyInfo(937870, 594529)
		propertyInfo.with {
			assert parcelInfoPtxAddress == null
			assert parcelInfoAddress == null
			assert parcelInfoLegal == "HALLANDALE PARK NO 8 PB 20-49 LOT 15 BLK 18 & S1/2 OF ALLEY & N1/2 OF NE 206 ST LYG N & S & ADJ CLOSED PER R-2006-61 LOT SIZE 3966 SQ FT FAU 30 1234 006 4600"
			assert parcelFolioNumber == "2812340064600"
			assert propertyType == "UNDEFINED"
		}
	}


	@Test
	public void testGetCondoPropertyInfo(){
		Map data = [xCoord:940512.0610576011, yCoord:532760.3259282038, street:"2001 Meridian Ave", zip:'33139', unit:'317']

		data['ADDRESS'] = "hello"
		
		Map propertyInfo = propertyInfoService.getCondoPropertyInfo(data)
		propertyInfo.with {
			assert parcelFolioNumber == '0232341690630'
			assert propertyType == 'CONDO'
			assert parcelInfoPtxAddress == '2001 MERIDIAN AVE 317'
		}
	}

	@Test
	public void testGetCondoPropertyInfo_NoData(){
		try {
			Map data = [:]
			def result = propertyInfoService.getCondoPropertyInfo(data)
			assert false
		}catch(RetrievalOfDataException e){
			String message = "Unexpected error for uri http://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/26/query | message: [error:[code:400, message:Failed to execute query., details:[]]]"
			assert e.message == message
		}
	}


	@Test
	public void testGetCondoPropertyInfo_NoPropertyInXY(){
		Map data = [xCoord:1, yCoord:1, street:"nostreet", zip:'11111', unit:'nounit']
		assert propertyInfoService.getCondoPropertyInfo(data) == null
	}

	@Test
	public void testGetCondoPropertyInfo_NoUnit(){
		Map data = [xCoord:940512.0610576011, yCoord:532760.3259282038, street:"2001 Meridian Ave", zip:'33139', unit:'nounit']
		assert propertyInfoService.getCondoPropertyInfo(data) == null
	}


	@Test
	public void testGetCleanPrpertyInfoByFolio_ConvertXYtoDoubleWith3Decimals(){

		propertyInfoService.getMetaClass().getRawPropertyInfoByFolio = {String folioNumber ->
			[FOLIO:folioNumber,ZIP:33139,PTXADDR:'8215 SW 152ND AVE', X_COORD:' 840977.3247', Y_COORD:' 493244.9996']
		}

		Map data = propertyInfoService.getCleanPropertyInfoByFolio("3049331130001")

		assert data.X_COORD ==  840977.325
		assert data.Y_COORD ==  493245.0

	}

	@Test
	public void testGetRawPropertyInfoByFolio(){
		Map data = propertyInfoService.getRawPropertyInfoByFolio("3059010240130")
		assert data.FOLIO == '3059010240130'
		assert data.TRUE_SITE_ZIP_CODE == '33186-2722'
		assert data.TRUE_SITE_ADDR_NO_UNIT == '11826 SW 97 ST'
		assert data.TRUE_SITE_UNIT?:"" == ""
	}

	@Test
	public void testGetRawPropertyInfoByFolio_Condo(){
		Map data = propertyInfoService.getRawPropertyInfoByFolio("0232341690630")
		assert data.FOLIO == '0232341690630'
		assert data.TRUE_SITE_ZIP_CODE == '33139-1503'
		assert data.TRUE_SITE_ADDR_NO_UNIT == '2001 MERIDIAN AVE'
		assert data.TRUE_SITE_UNIT == '317'
	}

	@Test
	public void testGetRawPropertyInfoByFolio_Building(){


		List folios = [
			'0132310650001',
			'0132310640001',
			'0132310630001'

		]
		def results = [
			[ FOLIO:'0132310650001',TRUE_SITE_ADDR_NO_UNIT: '1750 N BAYSHORE DR', TRUE_SITE_ZIP_CODE:'33132-0000', TRUE_SITE_UNIT:''],
			[ FOLIO:'0132310640001',TRUE_SITE_ADDR_NO_UNIT:'1900 N BAYSHORE DR', TRUE_SITE_ZIP_CODE:'33132-0000', TRUE_SITE_UNIT:''],
			[ FOLIO:'0132310630001',TRUE_SITE_ADDR_NO_UNIT: '1800 N BAYSHORE DR', TRUE_SITE_ZIP_CODE:'33132-0000', TRUE_SITE_UNIT:'']

		]


		folios.eachWithIndex {folio, counter	->
			Map data = propertyInfoService.getRawPropertyInfoByFolio(folio)

			assert data.FOLIO == results[counter].FOLIO
			assert data.TRUE_SITE_ZIP_CODE == results[counter].TRUE_SITE_ZIP_CODE
			assert data.TRUE_SITE_ADDR_NO_UNIT == results[counter].TRUE_SITE_ADDR_NO_UNIT
			assert data.TRUE_SITE_UNIT?:'' == results[counter].TRUE_SITE_UNIT
		}

	}

	@Test
	public void testGetRawPropertyInfoByFolio_BuildingMultiAddress(){
		Map data = propertyInfoService.getRawPropertyInfoByFolio("3049331130001")
		assert data.TRUE_SITE_ADDR_NO_UNIT == '8215 SW 152ND AVE'
		assert data.TRUE_SITE_ZIP_CODE == '33193-0000'
		assert data.X_COORD == 840931.2
		assert data.Y_COORD == 493159.9
	}

	@Test
	public void testGetRawPropertyInfoByFolio_FolioNotExists(){
		Map data = propertyInfoService.getRawPropertyInfoByFolio("4444444444")
		assert data == null
	}

	@Test
	public void testGetStreetZipUnitByFolio(){
		Map data = propertyInfoService.getStreetZipUnitByFolio("3059010240130")
		assert data.zip == '33186-2722'
		assert data.street == '11826 SW 97 ST'
		assert data.unit?:'' == ''
		assert data.x == 857869.9
		assert data.y == 488907.0
	}


	@Test
	public void testGetStreetZipUnitByFolio_Condo(){
		Map data = propertyInfoService.getStreetZipUnitByFolio("0232341690630")
		assert data.zip == '33139-1503'
		assert data.street == '2001 MERIDIAN AVE'
		assert data.unit == "317"
		assert data.x == 940512.0
		assert data.y == 532760.3
		
	}

	@Test
	public void testGetStreetZipUnitByFolio_FolioNotExists(){
		List data = propertyInfoService.getStreetZipUnitByFolio("4444444444")
		assert data == null
	}

	@Test void testGetCondoFolio(){
		String folioNumber = propertyInfoService.getCondoFolio("2001 Meridian Ave", "33139", "317")
		assert folioNumber == '0232341690630'
	}

	@Test void testGetCondoFolio_NoUnitGivenReturnBuildingFolio(){
		String folioNumber = propertyInfoService.getCondoFolio("2001 Meridian Ave", "33139", "")
		assert folioNumber == '0232341690001'

		folioNumber = propertyInfoService.getCondoFolio("2001 Meridian Ave", "33139", null)
		assert folioNumber == '0232341690001'
	}

	@Test void testGetCondoFolio_House(){
		String folioNumber = propertyInfoService.getCondoFolio("11826 sw 97th street", "", "")
		assert folioNumber == null

		folioNumber = propertyInfoService.getCondoFolio("11826 sw 97th street", null, null)
		assert folioNumber == null

	}

	@Test void testGetCondoFolio_AddressNotExists(){
		String folioNumber = propertyInfoService.getCondoFolio("no address", "", "")
		assert folioNumber == null

		folioNumber = propertyInfoService.getCondoFolio(null, null, null)
		assert folioNumber == null
	}

	@Test void testFolioExists_True(){
		propertyInfoService.getMetaClass().getCleanPropertyInfoByFolio = {String folioNumber ->
			[FOLIO:'0232341690630',ZIP:33139,PTXADDR:'2001 MERIDIAN AVE']
		}

		assert propertyInfoService.folioExists("0232341690630")
	}

	@Test void testFolioExists_False(){
		propertyInfoService.getMetaClass().getRawPropertyInfoByFolio = {String folioNumber -> null }

		assert !propertyInfoService.folioExists("4444444444444")
	}

	@Test
	void testIsCoordInProperty_True(){

		// this is a regular property, single family home 11826 sw 97th street
		assert propertyInfoService.isCoordInProperty(857869.749956198,488913.9375316277) == true

		// and this one is a parcel with no address attached to it but with folio number
		assert propertyInfoService.isCoordInProperty(898029, 512098.4) == true
	}

	@Test
	void testIsCoordInProperty_False(){
		assert propertyInfoService.isCoordInProperty(1,1) == false
	}

	@Test
	void testGetPropertyType_PAGIS_Building(){
		Map dataFromLayers = ['MDC.PaGIS':[CONDO_FLAG:'N',PARENT_FOLIO:null,FOLIO:'0232341690001',REFERENCE_ONLY_FLAG:'Y'],'MDC.PaParcel':[CONDO:'']]
		assert propertyInfoService.findPropertyType(dataFromLayers) == PropertyInfoService.PropertyType.MULTI
	}

	@Test
	void testGetPropertyType_PAGIS_Condo(){
		Map dataFromLayers = ['MDC.PaGIS':[CONDO_FLAG:'Y'],'MDC.PaParcel':[CONDO_FLAG:'Y']]
		assert propertyInfoService.findPropertyType(dataFromLayers) == PropertyInfoService.PropertyType.CONDO
	}

	@Test
	void testGetPropertyType_PAGIS_Undefined(){
		Map dataFromLayers = ['MDC.PaGIS':[CONDO_FLAG:'N'],'MDC.PaParcel':[CONDO_FLAG:'Y']]
		assert propertyInfoService.findPropertyType(dataFromLayers) == PropertyInfoService.PropertyType.UNDEFINED
	}

	@Test
	void testGetPropertyType_Parcels_Building(){
		Map dataFromLayers = ['MDC.PaParcel':[CONDO_FLAG:'Y']]
		assert propertyInfoService.findPropertyType(dataFromLayers) == PropertyInfoService.PropertyType.CONDO
	}

	@Test
	void testGetPropertyType_Parcels_Undefined(){
		Map dataFromLayers = ['MDC.PaParcel':[CONDO_FLAG:'']]
		assert propertyInfoService.findPropertyType(dataFromLayers) == PropertyInfoService.PropertyType.UNDEFINED
	}

	@Test
	void testGetPropertyType_NoLayers_Undefined(){
		Map dataFromLayers = [:]
		assert propertyInfoService.findPropertyType(dataFromLayers) == PropertyInfoService.PropertyType.UNDEFINED
	}

	@Test
	void testGetPropertyType_NullNoLayers_Undefined(){
		Map dataFromLayers = null
		assert propertyInfoService.findPropertyType(dataFromLayers) == PropertyInfoService.PropertyType.UNDEFINED
	}


	@Test
	void testGetUnitsInBuildingByFolio(){
		List units = propertyInfoService.getUnitsInBuildingByFolio("0232341690001")
		assert units.size() == 113
	}

	@Test
	void testGetUnitsInBuildingByCoords(){
		List units = propertyInfoService.getUnitsInBuildingByCoords("940512.0610576011", "532760.3259282038")
		assert units.size() == 113

	}

	@Test
	void testGetAddressForMultiAddressBuilding(){
		String address = propertyInfoService.getAddressForMultiAddressBuilding("3049331130001") 
		assert  address == "8215 SW 152ND AVE"
	}

	@Test
	void testGetAddressForMultiAddressBuilding_NoBuilding(){
		String address = propertyInfoService.getAddressForMultiAddressBuilding("0000")
		assert  address == ""
	}


//	@Test
//	void testSlurper(){
//		// read data
//		File file = new File('/Users/fiallega/Projects/Work/mdcgis/src/test/groovy/org/sharegov/mdcgis/units.txt')
//		String json = file.getText()
//		def slurper = new JsonSlurper()
//		def startTime = new Date().time
//		def result = slurper.parseText(json)
//		def endTime = new Date().time
//		println "The time is ${endTime-startTime}"
//		
//
//	}
//	
//	@Test
//	void testInternalSlurper(){
//		// read data
//		File file = new File('/Users/fiallega/Projects/Work/mdcgis/src/test/groovy/org/sharegov/mdcgis/units.txt')
//		String json = file.getText()
//		def startTime = new Date().time
//		def result = Json.read(json)
//		def endTime = new Date().time
//		println "The time is ${endTime-startTime}"
//		
//
//	}
//
//	@Test
//	void testGsonSlurper(){
//		// read data
//		File file = new File('/Users/fiallega/Projects/Work/mdcgis/src/test/groovy/org/sharegov/mdcgis/units.txt')
//		String filePath = '/Users/fiallega/Projects/Work/mdcgis/src/test/groovy/org/sharegov/mdcgis/units.txt'
//		String json = file.getText()
//		Reader reader = new InputStreamReader(new FileInputStream (filePath))
//		Gson mygson = new Gson()
//		//JsonParser parser = new JsonParser();
//		def startTime = new Date().time
//		def result = mygson.fromJson(reader, Object.class)
//
//		//JsonObject result = (JsonObject)parser.parse(json);
//		def endTime = new Date().time
//		println result.toString()
//		println result.getClass()
//
//		println "The time is ${endTime-startTime}"
//	}
		
	@Test
	void testBuildPropertyInfo_OnlyMDCParcelsExists(){

	}

	@Test
	void testBuildPropertyInfo_OnlyMDCPTXGISExists(){

	}

	@Test
	void testBuildPropertyInfo_BothMDCParcelsAndPTXGISExist(){

	}

}
