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
		'MDC.Parcels':["FOLIO" :"parcelFolioNumber",
		"ADDRESS" :"parcelInfoAddress", //check
		"FROM_NUM" :"parcelInfoFromNumber", //check
		"TO_NUM" :"parcelInfoToNumber", //check
		"PRE_DIR" :"parcelInfoPreDir", // check
		"ST_NAME" :"parcelInfoStreetName", // check
		"ST_TYP" :"parcelInfoStreetType", // check
		"ST_TYP2" :"parcelInfoStreetType2", // check
		"SUF_DIR" :"parcelInfoSufDir", // check
		"ZIP" :"parcelInfoZip", // check
		"CONDO_FLAG" :"parcelInfoCondo", // CONDO
		"MOD_DATE" :"parcelInfoModDate", // check
		"TRUE_SITE_ADDR" :"parcelInfoPtxAddress", //PTXADDRESS
		"MAILING_BLOCK_LINE1" :"parcelInfoMailAddress1", // MAIL_ADD1
		"MAILING_BLOCK_LINE2" :"parcelInfoMailAddress2", // MAIL_ADD2
		"MAILING_BLOCK_LINE3" :"parcelInfoMailAddress3", // MAIL_ADD3
		"MAILING_BLOCK_LINE4" :"parcelInfoMailAddress4", // MAIL_ADD4
		"TRUE_MAILING_ZIP_CODE " :"parcelInfoMailZip", //MAIL_ZIP
		"OWNER_L" :"parcelInfoOwnerLastName", // no corresponding TRUE_OWNER1, TRUE_OWNER2, TRUE_OWNER1
		"OWNER_F" :"parcelInfoOwnerFirstName", // no corresponding TRUE_OWNER1, TRUE_OWNER2, TRUE_OWNER1
		"CLUC_CODE_CUR" :"parcelInfoCluc", // CLUC
		"SLUC_CODE_CUR" :"parcelInfoSluc", // SLUC
		"PRIMARY_ZONE" :"parcelInfoPriZone", //PRI_ZONE
		"SECONDARY_ZONE" :"parcelInfoSecZone", // SEC_ZONE
		"BEDROOM_COUNT" :"parcelInfoNumberOfBeds", // BED
		"BATHROOM_COUNT" :"parcelInfoNumberOfBaths", // BATH
		"HALF_BATHROOM_COUNT" :"parcelInfoNumberOfHalfBaths", // HALF_BATH
		"FLOOR_COUNT" :"parcelInfoNumberOfFloors", // FLOORS
		"UNIT_COUNT" :"parcelInfoNumberOfUnits", // UNITS
		"BUILDING_EFFECTIVE_AREA" :"parcelInfoBldgSqft", // BLDG_SQFT
		"LOT_SIZE" :"parcelInfoLotSize",
		//"LOT_SIZ_UNIT" :"parcelInfoLotSizUnit", // gone -> anything available ?
		"YEAR_BUILT" :"parcelInfoYearBuilt", //YR_BUILT
		"SITE_FLAG" :"parcelInfoSiteFlag", // gone -> anything available ?
		"STATE_EX_CODE_CUR" :"parcelInfoStateEx1", // STATE_EX1
		"STATE_EX_CODE_2_CUR" :"parcelInfoStateEx2", // STATE_EX2
		"CANCEL_FLAG" :"parcelInfoCancel", // CANCEL
		"ASSESSMENT_YEAR_PRI" :"parcelInfoAssmtYr1", // ASSMT_YR1
		"WIDOW_EX_VAL_PRI" :"parcelInfoWidowAmt1", // WIDOW_AMT1
		"CNTY_SR_EX_VAL_PRI" :"parcelInfoSeniorAmt1", // SENIOR_AMT1
		"VETERAN_EX_VAL_PRI" :"parcelInfoVetAmt1", // VET_AMT1
		"DISABLED_EX_VAL_PRI" :"parcelInfoDisablAmt1",  // DISABL_AMT1
		"HSTEAD_EX_VAL_PRI" :"parcelInfoHexVal1",  // HEX_VAL1
		"AG_DIFFERENTIAL_VAL_PRI" :"parcelInfoAgriVal1",// AGRI_VAL1
		"STATE_EX_VAL_PRI" :"parcelInfoExVal1", // EX_VAL1
		"LAND_VAL_PRI" :"parcelInfoLandValue1",  // LAND_VAL1
		"BLDG_VAL1" :"parcelInfoBuildingValue1",
		"TOTAL_VAL1" :"parcelInfoTotalValue1",
		"SITE_VALUE1" :"parcelInfoSiteValue1",
		"BLDG_SITE_VALUE1" :"parcelInfoBuildingSiteValue1",
		"ASSMT_VAL1" :"parcelInfoAssmtValue1",
		"ASSMT_YR2" :"parcelInfoAssmtYr2",
		"WIDOW_AMT2" :"parcelInfoWidowAmt2",
		"SENIOR_AMT2" :"parcelInfoSeniorAmt2",
		"VET_AMT2" :"parcelInfoVetAmt2",
		"DISABL_AMT2" :"parcelInfoDisablAmt2",
		"HEX_VAL2" :"parcelInfoHexVal2",
		"AGRI_VAL2" :"parcelInfoAgriVal2",
		"EX_VAL2" :"parcelInfoExVal2",
		"LAND_VAL2" :"parcelInfoLandValue2",
		"BLDG_VAL2" :"parcelInfoBuildingValue2",
		"TOTAL_VAL2" :"parcelInfoTotalValue2",
		"SITE_VALUE2" :"parcelInfoSiteValue2",
		"BLDG_SITE_VALUE2" :"parcelInfoBuildingSiteValue2",
		"ASSMT_VAL2" :"parcelInfoAssmtVal2",
		"SPTAX" :"parcelInfoSptax",
		"LEGAL1" :"parcelInfoLegal1",
		"LEGAL2" :"parcelInfoLegal2",
		"LEGAL3" :"parcelInfoLegal3",
		"LEGAL4" :"parcelInfoLegal4",
		"LEGAL5" :"parcelInfoLegal5",
		"LEGAL6" :"parcelInfoLegal6",
		"SALES_QUAL_CODE1" :"parcelInfoSalesQualCode1",
		"AMT1" :"parcelInfoAmt1",
		"DATE_MM1" :"parcelInfoDateMm1",
		"DATE_YR1" :"parcelInfoDateYr1",
		"SALES_OR1" :"parcelInfoSalesOr1",
		"SALES_VAC1" :"parcelInfoSalesVac1",
		"SALES_QUAL_CODE2" :"parcelInfoSalesQualCode2",
		"AMT2" :"parcelInfoAmt2",
		"DATE_MM2" :"parcelInfoDateMm2",
		"DATE_YR2" :"parcelInfoDateYr2",
		"SALES_OR2" :"parcelInfoSalesOr2",
		"SALES_VAC2" :"parcelInfoSalesVac2",
		"SALES_QUAL_CODE3" :"parcelInfoSalesQualCode3",
		"AMT3" :"parcelInfoAmt3",
		"DATE_MM3" :"parcelInfoDateMm3",
		"DATE_YR3" :"parcelInfoDateYr3",
		"SALES_OR3" :"parcelInfoSalesOr3",
		"SALES_VAC3" :"parcelInfoSalesVac3",
		"LAST_ACTIV" :"parcelInfoLastActiv",
		"CONF" :"parcelInfoConf",
		"CONDO_UNIT" :"parcelInfoCondoUnit",
		"HEX2_REGIONAL_VALUE1" :"parcelInfoHex2RegionalValue1",
		"HEX2_COUNTY_VALUE1" :"parcelInfoHex2CountyValue1",
		"HEX2_CITY_VALUE1" :"parcelInfoHex2CityValue1",
		"HEX2_REGIONAL_VALUE2" :"parcelInfoHex2RegionalValue2",
		"HEX2_COUNTY_VALUE2" :"parcelInfoHex2CountyValue2",
		"HEX2_CITY_VALUE2" :"parcelInfoHex2CityValue2",
		"AR_EFFECTIVE_AMOUNT" :"parcelInfoArEffectiveAmount",
		"PORTTO_AMOUNT" :"parcelInfoPorttoAmount",
		"SCHOOL_BOARD_EXE1" :"parcelInfoSchoolBoardExe1",
		"SCHOOL_BOARD_TAX1" :"parcelInfoSchoolBoardTax1",
		"REGIONAL_EXE1" :"parcelInfoRegionalExe1",
		"REGIONAL_TAX1" :"parcelInfoRegionalTax1",
		"COUNTY_EXE1" :"parcelInfoCountyExe1",
		"COUNTY_TAX1" :"parcelInfoCountyTax1",
		"CITY_EXE1" :"parcelInfoCityExe1",
		"CITY_TAX1" :"parcelInfoCityTax1",
		"VABATE_E_BOARD1" :"parcelInfoVabateEBoard1",
		"VABATE_REGIONAL1" :"parcelInfoVabateRegional1",
		"VABATE_COUNTY1" :"parcelInfoVabateCounty1",
		"VABATE_CITY1" :"parcelInfoVabateCity1",
		"SCHOOL_BOARD_EXE2" :"parcelInfoSchoolBoardExe2",
		"SCHOOL_BOARD_TAX2" :"parcelInfoSchoolBoardTax2",
		"REGIONAL_EXE2" :"parcelInfoRegionalExe2",
		"REGIONAL_TAX2" :"parcelInfoRegionalTax2",
		"COUNTY_EXE2" :"parcelInfoCountyExe2",
		"COUNTY_TAX2" :"parcelInfoCountyTax2",
		"CITY_EXE2" :"parcelInfoCityExe2",
		"CITY_TAX2" :"parcelInfoCityTax2",
		"VABATE_E_BOARD2" :"parcelInfoVabateEBoard2",
		"VABATE_REGIONAL2" :"parcelInfoVabateRegional2",
		"VABATE_COUNTY2" :"parcelInfoVabateCounty2",
		"VABATE_CITY2" :"parcelInfoVabateCity2"]]
	
	public static final Map<String,Map<String,String>> closestFeatureClassesAddressAttributes = [
		"MDC.StreetMaint":['SNAME':'streetName','MAINTCODE':'maintenanceCode', 'MAINTCODEALIAS':'maintenanceCodeAlias'],
		"pwd_lights":['STNAME1':'streetName','ST_LIGHT':'maintenanceCode', 'ST_LIGHTALIAS':'maintenanceCodeAlias']
	]
		
}
