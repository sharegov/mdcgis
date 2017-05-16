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

import org.junit.Test

class UtilsTests {
	
	@Test
	public void testRound(){
		assert 34343.235 ==  org.sharegov.mdcgis.Utils.round(34343.234634, 3)
		assert 34343.234 == org.sharegov.mdcgis.Utils.round(34343.234434, 3)
		assert 34343.234 == org.sharegov.mdcgis.Utils.round(34343.234134, 3)
		assert 903004.812 == org.sharegov.mdcgis.Utils.round(903004.8117238171, 3)
		
		assert 34343.9 ==  org.sharegov.mdcgis.Utils.round(34343.9, 3)
		assert 34343.92 ==  org.sharegov.mdcgis.Utils.round(34343.92, 3)
		assert 34343.928 ==  org.sharegov.mdcgis.Utils.round(34343.928, 3)
		assert 34343.928 ==  org.sharegov.mdcgis.Utils.round(34343.9282, 3)
		assert 34343.929 ==  org.sharegov.mdcgis.Utils.round(34343.9287, 3)
		
		assert 345 == org.sharegov.mdcgis.Utils.round(345, 3)
		assert 345.00 == org.sharegov.mdcgis.Utils.round(345, 3)
		
	}

}
