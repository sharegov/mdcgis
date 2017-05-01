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

import org.apache.commons.collections.bidimap.DualHashBidiMap
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.sharegov.mdcgis.utils.AppContext;


class GisConfigFactoryBeanTests {

	private GisConfig config
	

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext("configtest.xml");

		ApplicationContext ctx = AppContext.getApplicationContext();
		config = (GisConfig) ctx.getBean("GIS_CONFIG");
	}
	
	
	@After
	public void destroy() throws Exception{
		((ClassPathXmlApplicationContext) AppContext.getApplicationContext()).close();
	}
	
	@Test
	public void testLoadLayers(){
		assert config.layers.size() == 44
		assert config.locators.size() == 5
		assert config.gisServices.size() == 9
		assert config.municipalities.size() ==  35
	}
	
	@Test
	public void testMunicipalities_GetValueFromKey(){
		assert config.municipalities[30] == "UNINCORPORATED MIAMI-DADE"
	}
	
	@Test
	public void testMunicipalities_GetKeyFromValue(){
		assert config.municipalities.getKey("UNINCORPORATED MIAMI-DADE") == 30 
	}
	
	@Test
	public void testMunicipalities_GetKeyFromNullValue(){
		assert config.municipalities.getKey(null) == null
	}
	
	@Test
	public void testMunicipalities_GetKeyFromNonExistentValue(){
		assert config.municipalities.getKey("NO MUNICIPALITY") == null
	}
	
	@Test
	public void testLayers_getValueFromKey(){
		assert config.layers["pwd_lights"] == "http://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/41/query"
	}
	
	@Test
	public void testLayers_GetKeyFromValue(){
		assert config.layers.getKey("http://s0142354.miamidade.gov/ArcGIS/rest/services/Gic/MapServer/41/query") == "pwd_lights"
	}
	
	@Test
	public void testLayers_GetKeyFromNullValue(){
		assert config.layers.getKey(null) == null
	}
	
	@Test
	public void testLayers_GetKeyFromNonExistentValue(){
		assert config.layers.getKey("NO LAYER") == null
	}
}
