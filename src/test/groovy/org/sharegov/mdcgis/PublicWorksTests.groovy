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
import org.sharegov.mdcgis.utils.AppContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import static groovyx.net.http.ContentType.HTML

class PublicWorksTests {

	private AsyncHTTPService httpService
	private PublicWorksService publicWorksService
	
		
	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext("configtest.xml");

		ApplicationContext ctx = AppContext.getApplicationContext();
		httpService = (AsyncHTTPService) ctx
				.getBean("ASYNC_HTTP_SERVICE");
				
		publicWorksService = (PublicWorksService) ctx
				.getBean("PUBLICWORKS_SERVICE")
	}

	@After
	public void destroy() throws Exception{
		((ClassPathXmlApplicationContext) AppContext.getApplicationContext()).close();
	}

	

	@Test
	public void testRequest(){
		def url = 'https://gisimsintra.miamidade.gov/website/pw_geocoder/CSR_Geocoder.asp'
		String mode = 'CSR'
		String Cmd = 'AREA'
		String add1 = 'SW 112TH st'
		String add2 = 'SW 108TH ct'
		String add3 = 'SW 109TH ave'
		String add4 = 'SW 114TH st'
		String AddZipCode= ''
		String submit1='Get GIS Information'

		def query = [mode:mode, 
			         Cmd:Cmd, 
					 add1:add1, add2:add2, add3:add3, add4:add4, 
					 AddZipCode:AddZipCode, 
					 submit1:submit1]
		
		def result = httpService.request(url, query, HTML)
		String expected = "SW 112TH ST/SW 108TH CT,863485.3125/484357.9375,11201 SW 108TH CT 33176,30,UNINCORPORATED MIAMI-DADE,8:7:9,Daniella Levine Cava:Xavier L. Suarez:Dennis C. Moss,12:14,n/a:n/a,33157:33176,554019:554031:554018:554030:554007,5;"

		
		assert result == expected
	}
		
	
	@Test 
	public void testSplit(){
		String example = 'SW 112TH ST/SW 108TH CT,863485.3125/484357.9375,11201 SW 108TH CT 33176,30,UNINCORPORATED MIAMI-DADE,8:7:9,Lynda Bell:Xavier L. Suarez:Dennis C. Moss,12:14,n/a:n/a,33176:33157,554019:554031:554018:554030:554007,5'
		List acc = []
		
		def splitter = {String value, String token ->
			List values = value.tokenize(token)
			if (values.size == 1)
				return
			
			
			values.each{
				call(it, ',')
				println it
			}
		}
		
		splitter(example, ',')
			
	}
	

	@Test 
	void testRecursion(){
		
		String example = 'SW 112TH ST/SW 108TH CT,863485.3125/484357.9375,11201 SW 108TH CT 33176,30,UNINCORPORATED MIAMI-DADE,8:7:9,Lynda Bell:Xavier L. Suarez:Dennis C. Moss,12:14,n/a:n/a,33176:33157,554019:554031:554018:554030:554007,5'
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
		
		println result
	}
	
	@Test 
	void testArea() {
		
		println publicWorksService.area("SW 112TH st", "SW 108TH ct", "SW 109TH ave", "SW 114TH st")
	}

	
	@Test
	void testReadFile(){
		def file1 = new File('/Users/fiallega/Desktop/publicworksdatasingle.csv')
		def lines = file1.readLines()
		lines.eachWithIndex{line, index ->
			if( index == 0)
				return

				
							
				List attributes  = line.split(",")
			
			String street1 = attributes[4]
			String street2 = attributes[6]
			String street3 = attributes[8]
			String street4 = attributes[10]
			String x = attributes[19]
			String y = attributes[20]
			
			if(!street2.trim())
			return
			
			List municipalityId = []
			if(attributes[11].trim() && attributes[11] != '(null)')
				municipalityId.add(attributes[11])
			
			println municipalityId
				

			
			println "${street1} +++ ${street2} +++ ${street3} +++ ${street4}"
			
			Map data = [:]
			try {
			if(!street3.trim() || street3 == '(null)')
				data  = publicWorksService.intersection(street1, street2)
			else if(!street4.trim() || street4 == '(null)')
				data  = publicWorksService.corridor(street1, street2, street3)
			else	
				data  = publicWorksService.area(street1, street2, street3, street4)
			
			println data
			assert data.municipalityId == municipalityId
			}catch(Exception e){
				println e.message
				assert false
			}
			
			
		}
	}
	
	
	private buildTestData(){
		def file1 = new File('/Users/fiallega/Desktop/publicworksdatasingle.csv')
		def lines = file1.readLines()
		lines.eachWithIndex{line, index ->
			if( index == 0)
				return
			
			List attributes  = line.split(",")
				
			// build a key made out of streets
			String street1 = attributes[4]
			String street2 = attributes[6]
			String street3 = attributes[8] 
			String street4 = attributes[10]
		}
	}
	
}
