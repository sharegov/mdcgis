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
package org.sharegov.mdcgis.model

import groovy.transform.ToString

@ToString(includeNames=true)
class Address {

	
	String address
	// true if address comes from a reverse geocoding where the x, y was not associated with an address
	// either because it was not a property or the property had no address.
	Boolean addressApproximation = false
	Integer score
	Map location = [:]
	String municipality
	Integer municipalityId
	Map parsedAddress = [:]
	Map propertyInfo = [:]

	List streetsMaintenance = []
	List streetsLightMaintenance = []


	Integer districtNumber
	Integer cityDistrictNumber
	String commissionerName
	String cityCommissionerName
	String recyclingRoute
	String recyclingWeekDay
	String recyclingPickUpWeek
	String recyclingPickUpCode
	String recyclingInServiceArea
	String recyclingIsMunic
	String recyclingDescription
	String recyclingIsRecycle
	String recyclingCalendarUrl
	String garbagePickupRoute
	String garbagePickupAlias
	String garbagePickupDay
	String garbagePickupDayAlias
	String bulkyTrashPickupDay
	String bulkyTrashPickupDayAlias
	String garbagePickupType
	String utilityName
	String utilityNameAlias
	String floodZone
	String hurricaneEvacZone
	String netOfficeName
	Integer netAreaId
	Integer houseDistrictId
	Integer senateDistrictId
	Integer electionsPrecinctId
	String  pollingLocation
	Integer turkeyPointAreaId
	String  codeEnforcementZone
	Integer publicWorksGridNumber
	String  miamiNeighborhood
	Integer firePreventionBureauDistrict
	
	//"StreetAddress",  "Intersection". Interesting to flag something as intersection
	String addressType
	
	// Type of locator data came from. GeoStreet, GeoAddress
	String locatorName
	
}
