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

import groovyx.net.http.ContentType;

class CouchDBImport {

	HTTPService httpService

	void exportImport() {

		def f = new File("/Users/fiallega/Downloads/PORTALDB.csv")
		int i = 0
		f.eachLine() {
			i++
			println "${i} - ${it}"
			try{

				println "about to pull"
				String urlFrom = 'http://localhost:8080/contentmanager/contentItems/' + it
				Map contentItem =  httpService.request(urlFrom, null)

				println "about to push"
				String urlTo = 'http://localhost:5984/contentitem'
				httpService.requestPost(urlTo, contentItem, ContentType.JSON)
				println "pushed"
			}catch(Exception e) {
				println "exception happened" + e.message
			}
		}
	}


	void exportImport(int id) {

		try{

			println "about to pull"
			String urlFrom = 'http://localhost:8080/contentmanager/contentItems/' + id
			Map contentItem =  httpService.request(urlFrom, null)
			//contentItem.extendedAbstract = '"<p style="text-align: justify;">All interested parties may appear at the time and place specified.</p> <p style="text-align: justify;">A person who decides to appeal any decision made by any board, agency, or commission with respect to any matter considered at its meeting or hearing, will need a record of proceedings.  Such persons may need to ensure that a verbatim record of the proceedings is made, including the testimony and evidence upon which the appeal is to be based.</p> <p style="text-align: justify;">Miami-Dade County provides equal access and equal opportunity in employment and does not discriminate on the basis of disability in its programs or services.  For material in alternate format, a sign language interpreter or other accommodations, please call 305-375-1293 or send email to: <a href="mailto:clerkbcc@miamidade.gov">clerkbcc@miamidade.gov</a></p> <p style="text-align: justify;">      HARVEY RUVIN, CLERK<br />      CHRISTOPHER AGRIPPA, DEPUTY CLERK</p> <p> </p> <p> </p>"'
			
			println contentItem.extendedAbstract.class
			 

			println contentItem.extendedAbstract
			println "about to push"
			String urlTo = 'http://localhost:5984/contentitem'
			httpService.requestPost(urlTo, contentItem, ContentType.JSON)
			println "pushed"
		}catch(Exception e) {
			println "exception happened" + e.message
		}
	}
}
