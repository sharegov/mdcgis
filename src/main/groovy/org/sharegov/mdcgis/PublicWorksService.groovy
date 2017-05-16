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

import static groovyx.net.http.ContentType.XML;

class PublicWorksService {

	HTTPService httpService
	GisConfig gisConfig
	
	
	public Map intersection(String street1, String street2) {
		
		// Make the request for data
		def url = gisConfig.gisServices['publicWorksIntersection']

		def query = [
					 addr1:street1,
					 addr2:street2]
		
		def result = httpService.request(url, query, XML)
		def intersection = result.AddrD.children()[0]
		
		// throw exception when no intersection found
		if(intersection.Anchor_Address.text() == "")
			throw new RetrievalOfDataException("No Intersection Available")
			
		// Map the data
		return [ 'intersection'         :[intersection.Address1.text()],
				 'location'             :["${intersection.X_Coord1.text()}/${intersection.Y_Coord1.text()}"],
				 'anchor'               :[intersection.Anchor_Address.text()],
				 'municipalityId'       :intersection.Munic_Code.text().tokenize(":"),
				 'municipality'         :intersection.Munic_Name.text().tokenize(":"),
				 'districtNumber'       :intersection.Comm_District.text().tokenize(":"),
				 'commissionerName'     :intersection.Commissioner_Name.text().tokenize(":"),
				 'communityCouncilId'   :intersection.Council.text().tokenize(":"),
				 'communityCouncilName' :intersection.Council_Name.text().tokenize(":"),
				 'zip'                  :intersection.ZipCode.text().tokenize(":"),
				 'ttrrss'               :intersection.TTRRSS.text().tokenize(":"),
				 'rifDistrictNumber'    :intersection.RoadImpact.text().tokenize(":")
			]
	}
	
	public Map corridor(street1, street2, street3) {
		
		// Make the request for data
		def url = gisConfig.gisServices['publicWorksCorridor']

		def query = [
					 addr1:street1,
					 addr2:street2,
					 addr3:street3]
		
		def result = httpService.request(url, query, XML)
		def corridor = result.Segm.children()[0]
		
		// throw exception when no intersection found
		if(corridor.Anchor_Address.text() == "")
			throw new RetrievalOfDataException("No Corridor Available")

		
		// Map the data
		return [ 'intersection'         :[corridor.Address1.text(), corridor.Address2.text()],
				 'location'             :["${corridor.X_Coord1.text()}/${corridor.Y_Coord1.text()}",
					 					  "${corridor.X_Coord2.text()}/${corridor.Y_Coord2.text()}"],
				 'anchor'               :[corridor.Anchor_Address.text()],
				 'municipalityId'       :corridor.Munic_Code.text().tokenize(":"),
				 'municipality'         :corridor.Munic_Name.text().tokenize(":"),
				 'districtNumber'       :corridor.Comm_District.text().tokenize(":"),
				 'commissionerName'     :corridor.Commissioner_Name.text().tokenize(":"),
				 'communityCouncilId'   :corridor.Council.text().tokenize(":"),
				 'communityCouncilName' :corridor.Council_Name.text().tokenize(":"),
				 'zip'                  :corridor.ZipCode.text().tokenize(":"),
				 'ttrrss'               :corridor.TTRRSS.text().tokenize(":"),
				 'rifDistrictNumber'    :corridor.RoadImpact.text().tokenize(":")
			]

	}
	

	public Map area(street1, street2, street3, String street4) {
		
		// Make the request for data
		def url = gisConfig.gisServices['publicWorksArea']

		def query = [
					 addr1:street1,
					 addr2:street2,
					 addr3:street3,
					 addr4:street4]
		
		def result = httpService.request(url, query, XML)
		def area = result.mArea.children()[0]
		
		// throw exception when no intersection found
		if(area.Anchor_Address.text() == "")
			throw new RetrievalOfDataException("No Area Available")

	
		// Map the data
		return [ 'intersection'         :[area.Address1.text(),
											area.Address2.text(),
											area.Address3.text(),
											area.Address4.text()],
				 'location'             :["${area.X_Coord1.text()}/${area.Y_Coord1.text()}",
					 						"${area.X_Coord2.text()}/${area.Y_Coord2.text()}",
											"${area.X_Coord3.text()}/${area.Y_Coord3.text()}",
											"${area.X_Coord4.text()}/${area.Y_Coord4.text()}"],
				 'anchor'               :[area.Anchor_Address.text()],
				 'municipalityId'       :area.Munic_Code.text().tokenize(":"),
				 'municipality'         :area.Munic_Name.text().tokenize(":"),
				 'districtNumber'       :area.Comm_District.text().tokenize(":"),
				 'commissionerName'     :area.Commissioner_Name.text().tokenize(":"),
				 'communityCouncilId'   :area.Council.text().tokenize(":"),
				 'communityCouncilName' :area.Council_Name.text().tokenize(":"),
				 'zip'                  :area.ZipCode.text().tokenize(":"),
				 'ttrrss'               :area.TTRRSS.text().tokenize(":"),
				 'rifDistrictNumber'    :area.RoadImpact.text().tokenize(":")
			]

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
