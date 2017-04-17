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

class EsriFieldMappings {

	// candidate 'attributes'. Keys are the esri keys, values is the possible mapping into the parsedAddress.
	public static final Map<String,String> esri9CandidateAttributes =
			['HouseNum':'House',
			 'PreDir':'PreDir',
			 'PreType':'PreType',
			 'StreetName':'StreetName',
			 'StreetType':'SufType',
			 'SufDir':'SufDir',
			 'Zone':'zip',
			 'PreDir1':'PreDir1',
			 'PreType1':'PreType1',
			 'StreetName1':'StreetName1',
			 'Type1':'SufType1',
			 'SufDir1':'SufDir1',
			 'LeftZone1':'zip1',
			 'LeftZone':'zip1',
			 'PreDir2':'PreDir2',
			 'PreType2':'PreType2',
			 'StreetName2':'StreetName2',
			 'Type2':'SufType2',
			 'SufDir2':'SufDir2',
			 'LeftZone2':'zip2',
			 'unit':'unit']


	// candidate 'attributes'. Keys are the esri keys, values is the possible mapping into the parsedAddress.
	public static final Map<String,String> esri10CandidateAttributes =
			['House':'House',
			 'PreDir':'PreDir',
			 'PreType':'PreType',
			 'StreetName':'StreetName',
			 'SufType':'SufType',
			 'SufDir':'SufDir',
			 'ZIP':'zip',
			 'PreDir1':'PreDir1',
			 'PreType1':'PreType1',
			 'StreetName1':'StreetName1',
			 'SufType1':'SufType1',
			 'SufDir1':'SufDir1',
			 'ZIP1':'zip1',
			 'PreDir2':'PreDir2',
			 'PreType2':'PreType2',
			 'StreetName2':'StreetName2',
			 'SufType2':'SufType2',
			 'SufDir2':'SufDir2',
			 'ZIP2':'zip2',
			 'unit':'unit']


	public static final Map<String,String> addressAttributes =[
			'MDC.WaterServiceArea':['UTILITYNAME':'utilityName', 'UTILITYNAMEALIAS':'utilityNameAlias'],
			'MDC.FEMAFloodZone':['FZONE':'floodZone'],
			'MDC.RecyclingRoute':['ROUTE':'recyclingRoute',
								  'WEEKDAY':'recyclingWeekDay',
								  'PICKUPWEEK':'recyclingPickUpWeek',
								  'PICKUPCODE':'recyclingPickUpCode',
								  'INSERVAREA':'recyclingInServiceArea',
								  'ISMUNIC':'recyclingIsMunic',
								  'DESCRIPTIO':'recyclingDescription',
								  'ISRECYCLE':'recyclingIsRecycle'],
			'MDC.HurricaneEvacZone':['ZONEID':'hurricaneEvacZone'],
			'MDC.HouseDistrict':['ID':'houseDistrictId'],
			'MDC.SenateDistrict':['ID':'senateDistrictId'],
			'MDC.Precinct':['ID':'electionsPrecinctId'],
			'MDC.GarbagePickupRoute':['ROUTE':'garbagePickupRoute',
									  'ALIASID':'garbagePickupAlias',
									  'COLLDAY':'garbagePickupDay',
									  'COLLDAYALIAS':'garbagePickupDayAlias',
									  'TYPE':'garbagePickupType'],
			'MDC.Municipality_poly':['NAME':'municipality'],
			'MDC.CommissionDistrict':['ID':'districtNumber',
									  'COMMNAME':'commissionerName'],
			'MDC.TurkeyPointArea':['ID':'turkeyPointAreaId']]

	public static final Map<String,String> cityOfMiamiAddressAttributes =[
			'MDC.WaterServiceArea':['UTILITYNAME':'utilityName', 'UTILITYNAMEALIAS':'utilityNameAlias'],
			'MDC.FEMAFloodZone':['FZONE':'floodZone'],
			'Recycle_Routes':['RECYROUTE':'recyclingRoute',
							  'RECYDAY':'recyclingWeekDay'],
			'MDC.HurricaneEvacZone':['ZONEID':'hurricaneEvacZone'],
			'MDC.HouseDistrict':['ID':'houseDistrictId'],
			'MDC.SenateDistrict':['ID':'senateDistrictId'],
			'MDC.Precinct':['ID':'electionsPrecinctId'],
			'GarbageRoutes':['GRAPCKDAYS':'garbagePickupDay'],
			'NewGarbRoutes':['GARBROUTE':'garbagePickupRoute'],
			'Trash_Route':['TRASHDAY':'bulkyTrashPickupDay', 'TRASHDAYALIAS':'bulkyTrashPickupDayAlias'],
			'MDC.Municipality_poly':['NAME':'municipality'],
			'MDC.CommissionDistrict':['ID':'districtNumber',
									  'COMMNAME':'commissionerName'],
			'Commission_Districts':['COMNAME':'cityCommissionerName', 'COMDISTID':'cityDistrictNumber'],
			'NET_Areas':['NETNAME':'netOfficeName', 'NETID':'netAreaId'],
			'MDC.TurkeyPointArea':['ID':'turkeyPointAreaId'],
			'Code_Enforcement_Zones':['CESECTNAME':'codeEnforcementZone'],
			'PW_Maint_Zones':['GRID_NO': 'publicWorksGridNumber'],
			'MiamiNeighborhoods':['LABEL':'miamiNeighborhood'],
			'Fire_Prevention_Bureau_District':['FPBDISTID':'firePreventionBureauDistrict']]


	public static final Map<String,String> propertyInfoAttributes =[
			'MDC.HomeOwnerAssociation':['NAME':'homeOwnerAssociationName'],
			'MDC.PaParcel':["FOLIO":"parcelFolioNumber",
							"TRUE_SITE_ADDR_NO_UNIT":"parcelInfoAddress",
							"Missing1":"parcelInfoFromNumber",
							"Missing2":"parcelInfoToNumber",
							"Missing3":"parcelInfoPreDir",
							"Missing4":"parcelInfoStreetName",
							"Missing5":"parcelInfoStreetType",
							"Missing6":"parcelInfoStreetType2",
							"Missing7":"parcelInfoSufDir",
							"TRUE_SITE_ZIP_CODE":"parcelInfoZip",
							"CONDO_FLAG":"parcelInfoCondo",
							"Missing10":"parcelInfoModDate",
							"ADDRESS_WITH_UNIT":"parcelInfoPtxAddress",
							"MAILING_BLOCK_LINE1":"parcelInfoMailAddress1",
							"MAILING_BLOCK_LINE2":"parcelInfoMailAddress2",
							"MAILING_BLOCK_LINE3":"parcelInfoMailAddress3",
							"MAILING_BLOCK_LINE4":"parcelInfoMailAddress4",
							"TRUE_MAILING_ZIP_CODE":"parcelInfoMailZip",
							"TRUE_OWNER1":"parcelInfoOwnerLastName",
							"TRUE_OWNER2":"parcelInfoOwnerFirstName",
							"CLUC_CODE_CUR":"parcelInfoCluc",
							"SLUC_CODE_CUR":"parcelInfoSluc",		/*now only 2 digit... may be only in dev???*/
							"PRIMARY_ZONE":"parcelInfoPriZone",
							"SECONDARY_ZONE":"parcelInfoSecZone",
							"BEDROOM_COUNT":"parcelInfoNumberOfBeds",
							"BATHROOM_COUNT":"parcelInfoNumberOfBaths",
							"HALF_BATHROOM_COUNT":"parcelInfoNumberOfHalfBaths",
							"FLOOR_COUNT":"parcelInfoNumberOfFloors",
							"UNIT_COUNT":"parcelInfoNumberOfUnits",
							"BUILDING_EFFECTIVE_AREA":"parcelInfoBldgSqft",
							"LOT_SIZE":"parcelInfoLotSize",
							"DELETE_LOT_SIZE_UNIT":"parcelInfoLotSizUnit",	/*may be get deleted later */
							"YEAR_BUILT":"parcelInfoYearBuilt",
							"Missing14":"parcelInfoSiteFlag",
							"STATE_EX_CODE_CUR":"parcelInfoStateEx1",
							"STATE_EX_CODE_2_CUR":"parcelInfoStateEx2",
							"CANCEL_FLAG":"parcelInfoCancel",
							"ASSESSMENT_YEAR_PRI":"parcelInfoAssmtYr1",
							"WIDOW_EX_VAL_PRI":"parcelInfoWidowAmt1",
							"CNTY_SR_EX_VAL_PRI":"parcelInfoSeniorAmt1",
							"VETERAN_EX_VAL_PRI":"parcelInfoVetAmt1",
							"DISABLED_EX_VAL_PRI":"parcelInfoDisablAmt1",
							"HSTEAD_EX_VAL_PRI":"parcelInfoHexVal1",
							"AG_DIFFERENTIAL_VAL_PRI":"parcelInfoAgriVal1",
							"STATE_EX_VAL_PRI":"parcelInfoExVal1",
							"LAND_VAL_PRI":"parcelInfoLandValue1",
							"BUILDING_VAL_PRI":"parcelInfoBuildingValue1",
							"TOTAL_VAL_PRI":"parcelInfoTotalValue1",
							"Missing15":"parcelInfoSiteValue1",
							"Missing16":"parcelInfoBuildingSiteValue1",
							"ASSESSED_VAL_PRI":"parcelInfoAssmtValue1",
							"ASSESSMENT_YEAR_CUR":"parcelInfoAssmtYr2",
							"WIDOW_EX_VAL_CUR":"parcelInfoWidowAmt2",
							"CNTY_SR_EX_VAL_CUR":"parcelInfoSeniorAmt2",
							"VETERAN_EX_VAL_CUR":"parcelInfoVetAmt2",
							"DISABLED_EX_VAL_CUR":"parcelInfoDisablAmt2",
							"HSTEAD_EX_VAL_CUR":"parcelInfoHexVal2",
							"AG_DIFFERENTIAL_VAL_CUR":"parcelInfoAgriVal2",
							"STATE_EX_VAL_CUR":"parcelInfoExVal2",
							"LAND_VAL_CUR":"parcelInfoLandValue2",
							"BUILDING_VAL_CUR":"parcelInfoBuildingValue2",
							"TOTAL_VAL_CUR":"parcelInfoTotalValue2",
							"Missing17":"parcelInfoSiteValue2",
							"Missing18":"parcelInfoBuildingSiteValue2",
							"ASSESSED_VAL_CUR":"parcelInfoAssmtVal2",
							"Missing19":"parcelInfoSptax",
							"LEGAL":"parcelInfoLegal",	/*This has the new value and the key LEGAL1 to LEGAL6*/
							"LEGAL1":"parcelInfoLegal1",	/*May have to Delete this later*/
							"LEGAL2":"parcelInfoLegal2",	/*May have to Delete this later*/
							"LEGAL3":"parcelInfoLegal3",	/*May have to Delete this later*/
							"LEGAL4":"parcelInfoLegal4",	/*May have to Delete this later*/
							"LEGAL5":"parcelInfoLegal5",	/*May have to Delete this later*/
							"LEGAL6":"parcelInfoLegal6",	/*May have to Delete this later*/
							"QU_FLG_1":"parcelInfoSalesQualCode1",
							"PRICE_1":"parcelInfoAmt1",
							"BAD_1":"parcelInfoDateMm1", /*It takes value from DOS_1... But will have to that "Do we need this???" Duplicate element in the map?????*/
							"DOS_1":"parcelInfoDateYr1",
							"SALES_ORI1":"parcelInfoSalesOr1",
							"VI_1":"parcelInfoSalesVac1",
							"QU_FLG_2":"parcelInfoSalesQualCode2",
							"PRICE_2":"parcelInfoAmt2",
							"BAD_2":"parcelInfoDateMm2", /*It takes value from DOS_1... But will have to that "Do we need this???" Duplicate element in the map?????*/
							"DOS_2":"parcelInfoDateYr2",
							"SALES_ORI2":"parcelInfoSalesOr2",
							"VI_2":"parcelInfoSalesVac2",
							"QU_FLG_3":"parcelInfoSalesQualCode3",
							"PRICE_3":"parcelInfoAmt3",
							"BAD_3":"parcelInfoDateMm3", /*It takes value from DOS_1... But will have to that "Do we need this???" Duplicate element in the map?????*/
							"DOS_3":"parcelInfoDateYr3",
							"SALES_ORI3":"parcelInfoSalesOr3",
							"VI_3":"parcelInfoSalesVac3",
							"LAST_EDIT_DATE":"parcelInfoLastActiv",
							"Missing20":"parcelInfoConf",
							"TRUE_SITE_UNIT":"parcelInfoCondoUnit",
							"REG_2ND_HSTEAD_EX_VAL_2_PRI":"parcelInfoHex2RegionalValue1",
							"CNTY_2ND_HSTEAD_EX_VAL_2_PRI":"parcelInfoHex2CountyValue1",
							"CITY_2ND_HSTEAD_EX_VAL_2_PRI":"parcelInfoHex2CityValue1",
							"REG_2ND_HSTEAD_EX_VAL_PRI":"parcelInfoHex2RegionalValue2",
							"CNTY_2ND_HSTEAD_EX_VAL_PRI":"parcelInfoHex2CountyValue2",
							"CITY_2ND_HSTEAD_EX_VAL_PRI":"parcelInfoHex2CityValue2",
							"Missing22":"parcelInfoArEffectiveAmount",
							"Missing23":"parcelInfoPorttoAmount",
							"SCHOOL_EXEMPTION_VAL_PRI":"parcelInfoSchoolBoardExe1",
							"SCHOOL_TAXABLE_VAL_PRI":"parcelInfoSchoolBoardTax1",
							"REG_EXEMPTION_VAL_PRI":"parcelInfoRegionalExe1",
							"REG_TAXABLE_VAL_PRI":"parcelInfoRegionalTax1",
							"CNTY_EXEMPTION_VAL_PRI":"parcelInfoCountyExe1",
							"CNTY_TAXABLE_VAL_PRI":"parcelInfoCountyTax1",
							"CITY_EXEMPTION_VAL_PRI":"parcelInfoCityExe1",
							"CITY_TAXABLE_VAL_PRI":"parcelInfoCityTax1",
							"Missing24":"parcelInfoVabateEBoard1",
							"Missing25":"parcelInfoVabateRegional1",
							"Missing26":"parcelInfoVabateCounty1",
							"Missing27":"parcelInfoVabateCity1",
							"SCHOOL_EXEMPTION_VAL_CUR":"parcelInfoSchoolBoardExe2",
							"SCHOOL_TAXABLE_VAL_CUR":"parcelInfoSchoolBoardTax2",
							"REG_EXEMPTION_VAL_CUR":"parcelInfoRegionalExe2",
							"REG_TAXABLE_VAL_CUR":"parcelInfoRegionalTax2",
							"CNTY_EXEMPTION_VAL_CUR":"parcelInfoCountyExe2",
							"CNTY_TAXABLE_VAL_CUR":"parcelInfoCountyTax2",
							"CITY_EXEMPTION_VAL_CUR":"parcelInfoCityExe2",
							"CITY_TAXABLE_VAL_CUR":"parcelInfoCityTax2",
							"Missing28":"parcelInfoVabateEBoard2",
							"Missing29":"parcelInfoVabateRegional2",
							"Missing30":"parcelInfoVabateCounty2",
							"Missing31":"parcelInfoVabateCity2"]]

	public static final Map<String,Map<String,String>> closestFeatureClassesAddressAttributes = [
			"MDC.StreetMaint":['SNAME':'streetName','MAINTCODE':'maintenanceCode', 'MAINTCODEALIAS':'maintenanceCodeAlias'],
			"pwd_lights":['STNAME1':'streetName','ST_LIGHT':'maintenanceCode', 'ST_LIGHTALIAS':'maintenanceCodeAlias']
	]

}
