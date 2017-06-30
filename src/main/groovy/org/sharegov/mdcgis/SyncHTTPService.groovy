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

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import net.sf.json.JSONObject
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpParams
import org.json.XML
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST

class SyncHTTPService implements HTTPService{

	private static Logger _log = LoggerFactory.getLogger(SyncHTTPService.class);

	HTTPBuilder http

	public void init(){
		
		// accept ssl self signed certificates (peer not authenticated - SSLPeerUnverifiedException
		http = Utils.buildHttpWithSelfSignedCertificate(http);
		
		// set timeouts
		HttpParams params = http.client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 2000);
		HttpConnectionParams.setSoTimeout(params, 10000);
	}

	Object request(String url, def query, ContentType contentType=JSON){
		_log.error "requestMultiple - request() - Method not implemented. It returns null."
		http.request(url, GET, contentType){
			uri.query = query

			response.success = { resp, data ->
				_log.info "syncRequest() - request for url ${url} : ${data}"
				_log.info "syncRequest() - uri ${uri}"
				_log.info "syncRequest() - response ${resp}"
				_log.info "syncRequest() - query params ${query}"
				_log.info "syncRequest() - conetentType ${contentType}"

				return data
			}

			response.'404' = { resp ->
				String message = "${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
				_log.error "syncrequest() - 404 for url ${url} : ${message}"
				_log.error "syncRequest() - uri ${uri}"
				_log.error "syncRequest() - response ${resp}"
				_log.error "syncRequest() - query params ${query}"
				_log.error "syncRequest() - conetentType ${contentType}"
				def errorResult = [error:[code:resp.statusLine.statusCode, message:resp.statusLine.reasonPhrase, details:[""]]] as JSONObject
				return errorResult
			}

			response.failure = { resp ->
				String message = "${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
				_log.error "syncRequest() - Unexpected error: request for url ${url} : ${message}"
				_log.error "syncRequest() - uri ${uri}"
				_log.error "syncRequest() - response ${resp}"
				_log.error "syncRequest() - query params ${query}"
				_log.error "syncRequest() - conetentType ${contentType}"
				def errorResult = [error:[code:resp.statusLine.statusCode, message:resp.statusLine.reasonPhrase, details:[""]]] as JSONObject
				return errorResult
			}
		}
	}

	Object requestPost(String url, def query, ContentType contentType=JSON){

		def startDate = new Date().time

		http.request(url, POST, contentType) {

			send URLENC, query

			response.success = {resp, data ->
				_log.info "syncRequestPost() - request for url ${url} : ${data}"
				_log.info "syncRequestPost() - response ${resp}"
				_log.info "syncRequestPost() - query params ${query}"
				_log.info "syncRequestPost() - conetentType ${contentType}"
				return data
			}

			response.'404' = {resp ->
				String message = "${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
				_log.error "syncRequestPost() - 404 for url ${url} : ${message}"
				_log.error "syncRequestPost() - response ${resp}"
				_log.error "syncRequestPost() - query params ${query}"
				_log.error "syncRequestPost() - conetentType ${contentType}"

				def errorResult = [error:[code:resp.statusLine.statusCode, message:resp.statusLine.reasonPhrase, details:[""]]] as JSONObject
				return errorResult
			}

			response.failure = { resp ->
				String message = "${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
				_log.error "syncRequestPost() - Unexpected error: request for url ${url} : ${message}"
				_log.error "syncRequestPost() - response ${resp}"
				_log.error "syncRequestPost() - query params ${query}"
				_log.error "syncRequestPost() - conetentType ${contentType}"

				def errorResult = [error:[code:resp.statusLine.statusCode, message:resp.statusLine.reasonPhrase, details:[""]]] as JSONObject
				return errorResult

			}
		}

		def endDate = new Date().time
		_log.info "syncRequestPost() - time for url ${url} is ${endDate-startDate}"
	}

	Map request(List urls, def query, ContentType contentType=JSON){
		def done = [:]

		// Fire each one of the requests.
		urls.each {url ->
			// make the http request to the given url.
			done[url] = request(url, query, contentType)
		}

		// Processs the request data.Convert the resuts to a map and return result.
		Boolean exceptionsFound = false
		String concatenatedExceptionsMessage = ""
		Map result = done.collectEntries {key, value ->
			try{
				[key, value]
			}catch(Exception e){
				String message = "request() - Exception at the thread level for url ${key} | message: ${e.getMessage()} | query: ${query}"
				_log.error message
				exceptionsFound = true
				concatenatedExceptionsMessage += " ++ ${message} ++ "

				[key, null]
			}
		}

		// If exceptions happens in any of the threads rethrow an exception
		if(exceptionsFound)
			throw new RetrievalOfDataException(concatenatedExceptionsMessage)

		return result
	}

}
