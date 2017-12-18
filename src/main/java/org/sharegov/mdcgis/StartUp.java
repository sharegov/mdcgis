/*******************************************************************************
 * Copyright 2016 Miami-Dade County
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

import org.restlet.*;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.resource.Directory;
import org.sharegov.mdcgis.utils.AppContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URL;
import java.util.logging.Level;

/**
 * Startup class with main method to start up MDCGIS application.<br>
 * <br>
 * Waits for remote configuration server at RemoteConfigCheckUrl up to RemoteConfigWaitSecs and exits VM 
 * before starting its own server if not available.<br>
 * 
 * @author fiallega, others, T. Hilpold
 */
public class StartUp
{
	public static void main(String []argv) {
		//Wait for remote configuration server to become available
		runStartupApplication();
		//Availability asserted, now run main application
		initMainApplication();
	}
	
	/**
	 * Uses startupConfig.xml to configure and execute wait for remote.
	 */
	private static void runStartupApplication() {
		ClassPathXmlApplicationContext cpctx = new ClassPathXmlApplicationContext("startupConfig.xml");
		ApplicationContext ctx = AppContext.getApplicationContext();
		StartupApplicationConfig startupConfig  = (StartupApplicationConfig) ctx.getBean("STARTUP_APPLICATION_CONFIG");
		//StartupUtils.printSSLCipherSuites();
		SslUtil.disableCertificateValidation();
		waitForRemoteConfigurationService(startupConfig);
		cpctx.close();
		AppContext.setApplicationContext(null);
	}
	
	@SuppressWarnings("resource")
	private static void initMainApplication() {
		new ClassPathXmlApplicationContext("config.xml");
		ApplicationContext ctx = AppContext.getApplicationContext();
		final ApplicationConfig config  = (ApplicationConfig) ctx.getBean("APPLICATION_CONFIG");
		Component server = new Component();
	    server.getServers().add(Protocol.HTTP, 9193);

		final Server httpsServer = server.getServers().add(Protocol.HTTPS, selfUrl().getPort());
		httpsServer.getContext().getParameters().add("hostname", selfUrl().getHost());
		httpsServer.getContext().getParameters().add("keystorePath", config.getKeyStorePath());
		httpsServer.getContext().getParameters().add("keystorePassword", config.getKeyStorePassword());
		httpsServer.getContext().getParameters().add("keyPassword", "password");
		httpsServer.getContext().getParameters().add("disabledCipherSuites", "TLS_DHE_RSA_WITH_DES_CBC_SHA TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA TLS_RSA_WITH_DES_CBC_SHA TLS_RSA_EXPORT_WITH_RC4_40_MD5 TLS_RSA_EXPORT_WITH_DES40_CBC_SHA");
		httpsServer.getContext().getParameters().add("sslContextFactory", "org.restlet.ext.ssl.DefaultSslContextFactory");
		
	    //server.getContext().getParameters().add("maxThreads", "20");
	    //server.getClients().add(Protocol.HTTP);
	    server.getClients().add(Protocol.FILE);

	    final JaxRsApplication app = new JaxRsApplication(server.getContext().createChildContext());
	    app.getLogger().setLevel(Level.ALL);
	    
		//Add filter
		PaddedJSONFilter jsonpFilter = new PaddedJSONFilter(app.getContext());
        HttpMethodOverrideFilter methodFilter = new HttpMethodOverrideFilter(app.getContext());
        jsonpFilter.setNext(methodFilter);
        methodFilter.setNext(app);
	   
	    app.add(new GisRestApplication());
	    server.getDefaultHost().attach("/mdc",jsonpFilter);
	    server.getLogger().setLevel(Level.ALL);
	    
	    // add app to manage serving static files
	    final Context childCtx = server.getContext().createChildContext();
	    Application application = new Application(childCtx) {  
	        @Override  
	        public Restlet createInboundRoot() {  
	                return new Directory(childCtx.createChildContext(), config.getRootUri());  
	        }  
	    };
	    server.getDefaultHost().attach("", application);	    

	    try	{	        
	    	server.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static URL selfUrl() {
		try {
			String hostname = java.net.InetAddress.getLocalHost().getHostName();
			boolean ssl = true;//config.is("ssl", true); 
			int port = 9196;//ssl ? config.at("ssl-port").asInteger() : config.at("port").asInteger();
			return new URL( ( ssl ? "https://" : "http://") + hostname + ":" + port);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Waits for the  configured remote configuration server to become available.<br>
	 * Exits VM if service at RemoteConfigCheckUrl is not available after RemoteConfigWaitSecs.<br>
	 *
	 * @param startupConfig
	 */
	public static void waitForRemoteConfigurationService(StartupApplicationConfig startupConfig) {
		URL remoteUrl;
		int maxWaitSecs;
		long startTime = System.currentTimeMillis();
		boolean available;
		boolean timeout;
		double curWaitSecs;
		try {
			remoteUrl = new URL(startupConfig.getRemoteConfigCheckUrl());
			maxWaitSecs = Integer.parseInt(startupConfig.getRemoteConfigWaitSecs());
		} catch (Exception e) {
			throw new RuntimeException("Error parsing RemoteConfigCheckUrl or ApplicationConfig from configuration" , e);
		}
		System.out.println("Startup: waiting for remote configuration server at " + remoteUrl 
				+ " for up to " + maxWaitSecs / 60 + " minutes...");
		do {
			try {
				//Attempt to connect and retrieve response
				Object o = remoteUrl.getContent();
				available = (o != null);
			} catch (Exception e) {
				available = false;
				System.err.println(e);
				//e.printStackTrace();
			}
			if (!available) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ie) {}		
			}
			curWaitSecs = (System.currentTimeMillis() - startTime) / 1000.0;
			timeout = (curWaitSecs > maxWaitSecs);
			if (!timeout) {
				System.err.println("Startup: still waiting after..." + curWaitSecs + " secs");
			}

		} while (!available && !timeout);
		if (!available) {
			System.err.println("Startup: remote configuration server not available after " + maxWaitSecs / 60 + " minutes. Exiting.");
			System.err.println("Startup: EXITING VM.");
			exitVM(-1);
		} else {
			System.out.println("Startup: remote configuration server became available after " + ((int)curWaitSecs * 10 / 60)/10.0 + " minutes");
		}
	}
	
	/**
	 * Exit VM method to make it clear this class may exit the vm on failed startup explicitly.
	 * @param code
	 */
	private static void exitVM(int code) {
		System.exit(code);
	}
}
