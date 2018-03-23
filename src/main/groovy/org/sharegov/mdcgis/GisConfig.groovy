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

import org.apache.commons.collections.BidiMap
import org.apache.commons.collections.bidimap.DualHashBidiMap

class GisConfig {
	Map<String, String> locators = [:]
	Map<String, String> gisServices = [:]
	Map<String, String> gisGeometryServices = [:]
	Map<String, String>	countyRecyclingCalendarUrls = [:]
	Map<String, String>	cityOfMiamiRecyclingCalendarUrls = [:]
	List<String> serviceLayers = []
	List<String> commonLocationsLayerNames = []
	List<String> addressLayers = []
	List<String> cityOfMiamiAddressLayers = []
	BidiMap layers = new DualHashBidiMap();
	BidiMap layersUrls = new DualHashBidiMap();
	BidiMap municipalities = new DualHashBidiMap();
	Map codeTranslator = [:]
}
