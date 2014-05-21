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

class Esri9CandidateService extends CandidateService {


	
	/**
	 *
	 * @param candidates
	 * @return - an empty array if there are no candidates
	 */
	@Override
	def cleanCandidates(def candidates) {
		if (!candidates)
			return []

		//Delete any duplicate candidates. Preserve Loc_name GeoAddress over GeoStreet
		candidates.sort{a,b-> b.score <=> a.score ?: a.attributes.Loc_name <=>b.attributes.Loc_name}
		candidates.unique{candidate -> candidate.address}

		if (candidates[0].score == 100 && candidates[0].attributes.Loc_name == 'GeoAddress')
			return [candidates[0]]

		if (candidates[0].score == 100 && candidates[0].attributes.Loc_name == 'GeoStreet')
			return [candidates[0]]

		if (candidates.size() > 5)
			return candidates[0..4]
		candidates

	}


	@Override
	def getRawCandidates(String address, String zip) {

		// build query string
		String outFields = '*'
		String f = 'json'

		def query = [Street:address, Zone:zip,
					f:f,
					outFields:outFields]

		String locatorUrl = gisConfig.locators['findAddressCandidates']
		
		// Make call to http client
		def candidates  = httpService.request(locatorUrl, query)

		// In case no candidates from locator returned, drop to a lower scoring candidates to get some results
		if (candidates.candidates.size() == 0){
			locatorUrl = gisConfig.locators['findAddressCandidates20']
			candidates  = httpService.request(locatorUrl, query)
		}
		
		return candidates
	}


	@Override
	public Object getRawCandidates(String address, String zip,
			Integer municipalityId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object getRawCandidates(String address, String zip,
			String municipality) {
		// TODO Auto-generated method stub
		return null;
	}
}
