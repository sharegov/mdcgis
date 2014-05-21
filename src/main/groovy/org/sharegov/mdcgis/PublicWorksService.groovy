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

import static groovyx.net.http.ContentType.*

class PublicWorksService {

	HTTPService httpService
	GisConfig gisConfig
	
	public Map intersection(String street1, String street2) {
		
		// Make the request for data
		def url = gisConfig.gisServices['publicWorksGeoCoder']
		String mode = 'CSR'
		String cmd = 'INTERSECTION'

		String addZipCode= ''
		String submit1='Get GIS Information'

		def query = [mode:mode,
					 Cmd:cmd,
					 add1:street1,
					 add2:street2,
					 AddZipCode:addZipCode,
					 submit1:submit1]
		
		def result = httpService.request(url, query, HTML)
		
		// throw exception 
		if(result.toString().contains("ERROR"))
			throw new RetrievalOfDataException(result.toString())
		
		// prepare the data
		List data = parseData(result.toString())
		
		// create a map
		List keys = ['intersection',
					 'location',
					 'anchor',
					 'municipalityId',
					 'municipality',
					 'districtNumber',
					 'commissionerName',
					 'communityCouncilId',
					 'communityCouncilName',
					 'zip',
					 'ttrrss',
					 'rifDistrictNumber']
		
		 
		return [keys, data].transpose().inject([:]) { a, b -> a[b[0]] = b[1]; a }
	}
	
	public Map corridor(street1, street2, street3) {
		
		// Make the request for data
		def url = gisConfig.gisServices['publicWorksGeoCoder']
		String mode = 'CSR'
		String cmd = 'CORRIDOR'

		String addZipCode= ''
		String submit1='Get GIS Information'

		def query = [mode:mode,
					 Cmd:cmd,
					 add1:street1,
					 add2:street2,
					 add3:street3,
					 AddZipCode:addZipCode,
					 submit1:submit1]
		
		def result = httpService.request(url, query, HTML)
		
		// throw exception
		if(result.toString().contains("ERROR"))
			throw new RetrievalOfDataException(result.toString())
		
		// prepare the data
		List data = parseData(result.toString())
		
		// create a map
		List keys = ['intersection',
					 'location',
					 'anchor',
					 'municipalityId',
					 'municipality',
					 'districtNumber',
					 'commissionerName',
					 'communityCouncilId',
					 'communityCouncilName',
					 'zip',
					 'ttrrss',
					 'rifDistrictNumber']
		
		 
		return [keys, data].transpose().inject([:]) { a, b -> a[b[0]] = b[1]; a }
	}
	

	public Map area(String street1, String street2, String street3, String street4) {
		
		
		// Make the request for data
		def url = gisConfig.gisServices['publicWorksGeoCoder']
		String mode = 'CSR'
		String cmd = 'AREA'

		String addZipCode= ''
		String submit1='Get GIS Information'

		def query = [mode:mode,
					 Cmd:cmd,
					 add1:street1,
					 add2:street2,
					 add3:street3,
					 add4:street4,
					 AddZipCode:addZipCode,
					 submit1:submit1]
		
		def result = httpService.request(url, query, HTML)
		
		// throw exception
		if(result.toString().contains("ERROR"))
			throw new RetrievalOfDataException(result.toString())
		
		// prepare the data
		List data = parseData(result.toString())
		
		// create a map
		List keys = ['intersection',
			         'location',
			         'anchor',
					 'municipalityId',
					 'municipality',
					 'districtNumber',
					 'commissionerName',
					 'communityCouncilId',
					 'communityCouncilName',
					 'zip',
					 'ttrrss',
					 'rifDistrictNumber']
		
		 
		return [keys, data].transpose().inject([:]) { a, b -> a[b[0]] = b[1]; a }
		
		
	}
	
	
	
	
	
	private List parseData(String example){

			List tokens = [",",":", "*"]
			List result = []
			def cloj
			
			cloj = { value, level ->
				List acc = []
				List values = value.tokenize(tokens[level])
				if (values.size == 1) {
					if(level < 2)
						result.add([values[0]])
					return
				}
		
				values.each{
					acc.add(it)
					cloj(it,level+1)
				}
	
				if(level > 0)
				  result.add(acc)
			}
			
			cloj(example, 0)
			
			return result

	}
	
	private standarizeStreet (street) {
		//gisConfig.gisServices['standardizeAddress']
	}
	
	
}
