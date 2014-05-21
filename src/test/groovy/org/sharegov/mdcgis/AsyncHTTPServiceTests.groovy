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
package org.sharegov.mdcgis;

import static org.junit.Assert.*;

import groovyx.net.http.*;
import net.sf.json.JSONObject
import net.sf.json.JSONArray
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.sharegov.mdcgis.utils.AppContext;
import groovyx.net.http.Method
import java.util.concurrent.FutureTask
import java.net.UnknownHostException
import org.sharegov.mdcgis.AsyncHTTPService


import javax.net.ssl.X509TrustManager
import javax.net.ssl.SSLContext
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManager
import java.security.SecureRandom
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.scheme.SchemeRegistry

class AsyncHTTPServiceTests {
	
	private AsyncHTTPService httpService
	
	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext("configtest.xml");

		ApplicationContext ctx = AppContext.getApplicationContext();
		httpService = (AsyncHTTPService) ctx
				.getBean("ASYNC_HTTP_SERVICE");
	}
	
	
	@Test
	public void testRequest(){
		def url = 'http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/14/query'
		String outFields = '*'
		String f = 'json'
		String where = "UPPER(NAME) LIKE '%SOUTH MIAMI HOSPITAL%'"
		def query = [where:where, f:f, outFields:outFields]
		
		def result = httpService.request(url, query)
		assert result.features.size() == 4
	}
	
	@Test
	public void testRequest_ListUrls(){
		def url = ['http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/14/query']
		String outFields = '*'
		String f = 'json'
		String where = "UPPER(NAME) LIKE '%SOUTH MIAMI HOSPITAL%'"
		def query = [where:where, f:f, outFields:outFields]
		
		def result = httpService.request(url, query)
		assert result[url[0]].features.size() == 4 
	}
	
	
	@Test
	public void testRequest_ThrowRetrievalOfDataExpception(){
		try {
			String outFields = '*'
			String f = 'json'
			String where = "UPPER(NAME) LIKE '%SOUTH MIAMI HOSPITAL%'"
			def query = [where:where, f:f, outFields:outFields]
			
			def result = httpService.request(['http://arcgis.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/14/query',
											'http://s0141668:9193/crossdomain2.xml',
											'http://s0141668:9193/crossdomain3.xml'], query)
			assert false
		} catch (RetrievalOfDataException rode) {
			assert true
		}
	}
	
	@Test
	public void testRequest2_NullQuery(){
		def url = 'http://s0020269/individuals/predefined/configset'
		def query = null
		def result = httpService.request(url, query)
		assert result.size() == 20
	}
		
	@Test
	public void testRequest_404(){
		try{	
		def result = httpService.request('http://s0141668:9193/crossdomain2.xml', null)
			assert false
		} catch (RetrievalOfDataException rode) {
			assert true
			assert rode.message == " ++ request() - Exception at the thread level for url http://s0141668:9193/crossdomain2.xml | message: org.sharegov.mdcgis.RetrievalOfDataException: Unexpected error for uri http://s0141668:9193/crossdomain2.xml | message: 404 : Not Found | query: null | query: null ++ "
		}
	}
		
	@Test
	public void testRequest_ConnectionRefused(){
		try{
			def result = httpService.request('http://ols00046:9193/crossdomain2.xml', null)
			assert false
		}catch(RetrievalOfDataException rode) {
			String message = " ++ request() - Exception at the thread level for url http://ols00046:9193/crossdomain2.xml | message: org.apache.http.conn.HttpHostConnectException: Connection to http://ols00046:9193 refused | query: null ++ "
			assert rode.message == message
		}
	}
	
	
	@Test
	public void testRequestUrlList_404(){
		try {
			def result = httpService.request(['http://s0141668:9193/crossdomain2.xml'], null)
			assert false
		}catch (RetrievalOfDataException rode) {
			String message = " ++ request() - Exception at the thread level for url http://s0141668:9193/crossdomain2.xml | message: org.sharegov.mdcgis.RetrievalOfDataException: Unexpected error for uri http://s0141668:9193/crossdomain2.xml | message: 404 : Not Found | query: null | query: null ++ "
			assert rode.message == message
		}
	}
		
	@Test
	public void testRequestUrlList_IllegalStateException(){
		try {
			def result = httpService.request(['null/query'], null)
			assert false
		} catch(RetrievalOfDataException rode){
			String message = " ++ request() - Exception at the thread level for url null/query | message: java.lang.IllegalStateException: Target host must not be null, or set in parameters. | query: null ++ "
			assert rode.message == message
		}
	}
	
	
	@Test
	public void testPostRequest_404(){
		try {
			def result = httpService.request('http://s0141668:9193/crossdomain2.xml', null)
			assert false
		} catch(RetrievalOfDataException rode){
			String message = " ++ request() - Exception at the thread level for url http://s0141668:9193/crossdomain2.xml | message: org.sharegov.mdcgis.RetrievalOfDataException: Unexpected error for uri http://s0141668:9193/crossdomain2.xml | message: 404 : Not Found | query: null | query: null ++ "
			assert rode.message == message
		}
	}
	
	@Test
	public void testPostRequest_ConnectionRefused(){
		try{
			def result = httpService.requestPost('http://ols00046:9193/crossdomain2.xml', null)
			assert false
		}catch(RetrievalOfDataException rode) {
			String message = "requestPost() - Exception at the thread level for url http://ols00046:9193/crossdomain2.xml | message: org.apache.http.conn.HttpHostConnectException: Connection to http://ols00046:9193 refused | query: null"
			assert rode.message == message
		}
	}
	
	@Test
	public void testRequest_CA_https(){

		//def url = 'https://github.com'
		//String f = 'html'
		//def query = [f:f]		
		//def result = httpService.request(url, query, groovyx.net.http.ContentType.HTML)

		def url = 'https://gisweb.miamidade.gov/ArcGIS/rest/services/MD_Map/MapServer/29'
		String f = 'json'
		def query = [f:f]
		def result = httpService.request(url, query, groovyx.net.http.ContentType.JSON)
		
		assert result.currentVersion == 10.05
	}
	
	@Test
	public void testRequest_URLEncoded(){
		def url = 'http://gisweb.miamidade.gov/ArcGIS/rest/services/MD_Map/MapServer/29'
		String f = 'json'
		def query = [f:f]
		def result = httpService.request(url, query, groovyx.net.http.ContentType.JSON)
		
		assert result.currentVersion == 10.05
	}
	
	
//	@Test
//	public void testRequest_SelfSigned_https(){
//
//		def url = 'https://311hub.miamidade.gov/individuals'
//		String f = 'json'
//		def query = [f:f]
//				
//		def result = httpService.request(url, query)
//		
//		println result
//
//	}

	
		
//	@Test
//	public void testRequest(){
//		def urls = ['http://ajax.googleapis.com/ajax/services/search/web']
//		def results = []
//		results = httpService.request(urls, null)
//		assert results.size() == 1
//		def values = results['http://ajax.googleapis.com/ajax/services/search/web']
// 		assert values['responseData'] == 'null' 
//		assert values['responseDetails'] == 'invalid version'
//		assert values['responseStatus'] == 400
//	}
//	
//	@Test
//	public void testRequest_404(){
//		def urls = ['http://ajax.googleapis.com/nonexistent.html']
//		def results = []
//		results = httpService.request(urls, null)
//		assert results.size() == 1
//		def values = results['http://ajax.googleapis.com/nonexistent.html']
//		assert values['error'] == true 
//	    assert values['message'] == '404 : Not Found' 
//	}
//	
//	@Test
//	public void testRequest_BadRequest(){
//		def urls = ['http://nonexistentserver']
//		def results = []
//		try{
//		results = httpService.request(urls, null)
//		assert false
//		}catch(Exception e){
//			println "${e.class} ${e.message}"
//			assert true
//		}
//	}
	

	
}
