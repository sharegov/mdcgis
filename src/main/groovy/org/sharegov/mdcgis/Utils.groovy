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

import groovyx.net.http.HTTPBuilder
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.X509Certificate

class Utils {

	public static double round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * Builds HttpBuilder with Self Signed Certificates
	 *
	 * @param http
	 * @return
     */
	public static HTTPBuilder buildHttpWithSelfSignedCertificate(HTTPBuilder http){

		def sslContext = SSLContext.getInstance("SSL")
		sslContext.init(null, [ new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {null }
			public void checkClientTrusted(X509Certificate[] certs, String authType) { }
			public void checkServerTrusted(X509Certificate[] certs, String authType) { }
		} ] as TrustManager[], new SecureRandom())

		def sf = new SSLSocketFactory(sslContext)
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
		def httpsScheme = new Scheme("https", sf, 443)
		http.client.connectionManager.schemeRegistry.register( httpsScheme )
		return http;
	}
}
