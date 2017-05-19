/*******************************************************************************
 * Copyright 2015 Miami-Dade County
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

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * SSL util for situations where a certificate's CA is not in the current trust store.
 * 
 * @author Thomas Hilpold
 *
 */
public class SslUtil
{	

	/**
	 * Installs a default SSL context for TLS that does not check certificate path.
	 * Certificate is still expected to provide a matching host name.
	 * 
	 * DO NOT USE IN PRODUCTION.
	 */
	public static void disableCertificateValidation()
	{
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
		{
			public X509Certificate[] getAcceptedIssuers()
			{
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType)
			{}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType)
			{}
		} };

		try
		{
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0], trustAllCerts, new SecureRandom());
			SSLContext.setDefault(ctx);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
