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

import java.net.URL;
import java.util.logging.Level;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.resource.Directory;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class StartUp
{
	//public static final String ROOT_URI = "file:///Users/fiallega/Projects/Work/mdcgis/src/main/resources/"; 
	public static final String ROOT_URI = "file:///C:/mdcgis/bin"; //test
	
	public static void main(String []argv)
	{
				
		Component server = new Component();
	    server.getServers().add(Protocol.HTTP, 9193);

	    String keyStorePath = "c:/mdcgis/conf/clientcert.jks"; //test
	    //String keyStorePath = "/Users/fiallega/Temp/clientcert.jks";
	    //String keyStorePath = "/Users/fiallega/Temp/mdcgisbad2.jks";

	    
		final Server httpsServer = server.getServers().add(Protocol.HTTPS, selfUrl().getPort());
		httpsServer.getContext().getParameters().add("hostname", selfUrl().getHost());
		httpsServer.getContext().getParameters().add("keystorePath", keyStorePath);
		//httpsServer.getContext().getParameters().add("keystorePassword", "cirmservices");
		httpsServer.getContext().getParameters().add("keystorePassword", "password"); // test
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
	                return new Directory(childCtx.createChildContext(), ROOT_URI);  
	        }  
	    };
	    server.getDefaultHost().attach("", application);	    

	    try
		{	        
	    	server.start();
			new ClassPathXmlApplicationContext("config.xml");
				        
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	static URL selfUrl()
	   {
	    		try
	    		{
	    			String hostname = java.net.InetAddress.getLocalHost().getHostName();
	    			boolean ssl = true;//config.is("ssl", true); 
	    			int port = 9196;//ssl ? config.at("ssl-port").asInteger() : config.at("port").asInteger();
	    			return new URL( ( ssl ? "https://" : "http://") + hostname + ":" + port);
	    			
	    		}
	    		catch (Exception ex)
	    		{
	    			throw new RuntimeException(ex);
	    		}
	    	}
	
}
