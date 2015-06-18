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

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Esri10CandidateService extends CandidateService {

	private static Logger _log = LoggerFactory.getLogger(Esri10CandidateService.class);

	/**
	 * Eliminate duplicates and reformat the address field. This field is coming back
	 * as Street + MunicipalityID + zip and we want Street + zip
	 * "NW 1ST ST & NW 2ND AVE,  16, 33034" -> "NW 1ST ST & NW 2ND AVE, 33034" 
	 * Store the MunicipalityID in municipalityId field for later use.
	 * location.x and location.y are rounded to 3 decimal places.
	 * @param candidates
	 * @return - an empty array if there are no candidates
	 */
	@Override
	def cleanCandidates(def candidates) {
		if (!candidates)
			return []

		addStreetMaintenanceIdsToCandidates(candidates)
		
		//Delete any duplicate candidates. Preserve Loc_name GeoAddress over GeoStreet
		candidates.sort{a,b-> b.score <=> a.score ?: a.attributes.Loc_name <=>b.attributes.Loc_name}
		candidates.unique{candidate -> candidate.address}
		
		// If there is candidates with 100 score only return those
		def candidates100 = candidates.findAll{candidate -> candidate.score == 100}
		if (candidates100){
			candidates100.each{candidate ->
				def tokenizedAddress = candidate.address.tokenize(",")
				String newAddress =  "${tokenizedAddress[0]},${tokenizedAddress[2]}"
				candidate.address = newAddress
				candidate.municipalityId = tokenizedAddress[1] as Integer
				candidate.location.x = org.sharegov.mdcgis.Utils.round(candidate.location.x, 3)
				candidate.location.y = org.sharegov.mdcgis.Utils.round(candidate.location.y, 3)
			}

			return candidates100
		}

		// No candidate with score 100 found. Process candidates with lower score than 100
		candidates.each{candidate ->
			def tokenizedAddress = candidate.address.tokenize(",")
			String newAddress =  "${tokenizedAddress[0]},${tokenizedAddress[2]}"
			candidate.address = newAddress
			candidate.municipalityId = tokenizedAddress[1] as Integer
			candidate.location.x = org.sharegov.mdcgis.Utils.round(candidate.location.x, 3)
			candidate.location.y = org.sharegov.mdcgis.Utils.round(candidate.location.y, 3)
		}

		if (candidates.size() > 5)
			return candidates[0..4]

		candidates

	}

	/**
	 * Add to all candidates the corresponding street maintenance ids which are the ids on the Street Maintenance
	 * layer. These Ids only exist on Locators of type GeoStreet under Ref_ID, so we have to populate locators of
	 * type GeoAddress also with those ids. 
	 * @param candidates
	 * @return - list of candidates with a new list field address.streetMaintenanceIds containing the ids which
	 * are references in the street maintenance layer. If candidates is null or empty returns null or empty.
	 */
	List addStreetMaintenanceIdsToCandidates(def candidates){
		if(!candidates)
			return candidates
		_log.info("THE CANDIDATES ARE ${candidates}");
		Map groups = candidates.groupBy {it.address}
		List cleanCandidates = []
		groups.each {key, groupOfCandidates ->
			def geoStreetCandidate = groupOfCandidates.find {it.attributes.Loc_name == 'GeoStreet'}
			if(geoStreetCandidate){
				geoStreetCandidate.streetMaintenanceIds = []
				if(geoStreetCandidate?.attributes?.Ref_ID || geoStreetCandidate?.attributes?.Ref_ID == 0)
					geoStreetCandidate.streetMaintenanceIds.add(geoStreetCandidate.attributes.Ref_ID)
				if(geoStreetCandidate?.attributes?.Ref_ID1 || geoStreetCandidate?.attributes?.Ref_ID1 == 0)
					geoStreetCandidate.streetMaintenanceIds.add(geoStreetCandidate.attributes.Ref_ID1)
				if(geoStreetCandidate?.attributes?.Ref_ID2 || geoStreetCandidate?.attributes?.Ref_ID2 == 0)
					geoStreetCandidate.streetMaintenanceIds.add(geoStreetCandidate.attributes.Ref_ID2)
				cleanCandidates.add(geoStreetCandidate)
				def geoAddressCandidate = groupOfCandidates.find {it.attributes.Loc_name == 'GeoAddress'}
				if(geoAddressCandidate){
					geoAddressCandidate.streetMaintenanceIds = []
					if(geoStreetCandidate?.attributes?.Ref_ID || geoStreetCandidate?.attributes?.Ref_ID == 0)
						geoAddressCandidate.streetMaintenanceIds.add(geoStreetCandidate.attributes.Ref_ID)
					if(geoStreetCandidate?.attributes?.Ref_ID1 || geoStreetCandidate?.attributes?.Ref_ID1 == 0)
						geoAddressCandidate.streetMaintenanceIds.add(geoStreetCandidate.attributes.Ref_ID1)
					if(geoStreetCandidate?.attributes?.Ref_ID2 || geoStreetCandidate?.attributes?.Ref_ID2 == 0)
						geoAddressCandidate.streetMaintenanceIds.add(geoStreetCandidate.attributes.Ref_ID2)
					cleanCandidates.add(geoAddressCandidate)
				}
			}
		}
		_log.info("THE CLEAN CANDIDATES ARE ${cleanCandidates}");
		cleanCandidates
	}


	@Override
	def getRawCandidates(String address, String zip) {
		getRawCandidates(address, zip, (Integer) null)		
	}


	@Override
	def getRawCandidates(String address, String zip,
		String municipality) {
		
		return getRawCandidates(address, zip, (Integer)gisConfig.municipalities.getKey(municipality))
		
	}
	
	@Override
	def getRawCandidates(String address, String zip,
			Integer municipalityId) {
		
		// build query string
		String outFields = '*'
		String f = 'json'
		
		def query = [Street:address, ZIP:(zip?:''), City:(municipalityId?:''),
					f:f,
					outFields:outFields]

		String locatorUrl = gisConfig.locators.findAddressCandidates

		// Make call to http client
		def candidates  = httpService.request(locatorUrl, query)

		// In case no candidates from locator returned, drop to a lower scoring candidates to get some results
		if (!candidates?.candidates){
			locatorUrl = gisConfig.locators.findAddressCandidates20
			candidates  = httpService.request(locatorUrl, query)
		}

		return candidates
	}
}
