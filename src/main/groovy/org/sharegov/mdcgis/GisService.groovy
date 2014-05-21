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

import java.util.List;
import java.util.Map


interface GisService {
	
	def geoCode(Map params)
	
	/**
	 * Find the closest address given (x, y) coords. Interesting things may happen like if the x,y that belongs
	 * to a property because it is in the polygon of that property is closer to the x,y of another property
	 * (the property has a canonical x,y), the geocode will return by closest, so it will return the second
	 * property, which in most usecases will be wrong. Use a default url.
	 * @param x
	 * @param y
	 * @param distance
	 * @return Map - returns a map with two keys, street and zip. 
	 * [street:"11826 SW 97TH ST",zip:"33186"]. If nothing found it will return null
	 */

	
	public Map reverseGeoCode(Double x, Double y, Integer distance)

   /**
	* Find the closest address given (x, y) coords. Interesting things may happen like if the x,y that belongs
	* to a property because it is in the polygon of that property is closer to the x,y of another property
	* (the property has a canonical x,y), the geocode will return by closest, so it will return the second
	* property, which in most usecases will be wrong.
	* @param x
	* @param y
	* @param url - url of the reverse geocoding - it could be a url for a generic one or
	*  			   a GeoStreet or a GeoAddress
	* @param distance
	* @return Map - returns a map with two keys, street and zip.
	* [street:"11826 SW 97TH ST",zip:"33186"]. If nothing found it will return null
	*/
	public Map reverseGeoCode(Double x, Double y, String url, Integer distance)
	
	
	/**
	 * Obtains a circle buffer from a geometry service. The circle is centered at (x,y) and radius 'radius' 
	 * @param x - x Coord of the center of the circle.
	 * @param y - y Coord of the center of the circle.
	 * @param radius - radius of the circle.
	 * @return - Obejct with property rings:[[[][][]]]
	 */
	public def getCircleBuffer(Double x, Double y, Integer radius)
	
	
	/**
	* it intersects the point (xCoord, yCoord) with the layers in the urls. It returns the
	* attributes and the geometry (if returnGeometry true) of the feature containing the point (xCoord, yCoord)
	* @param xCoord
	* @param yCoord
	* @param urls - List or urls pointing to the layers
	* @param returnGeometry - when true it also returns geometry information. If false, no geomtry data
	* is returned.
	* @return Map - Key: the url of the layer as expressed in urls.
	*               Value (returnGeometry=false): [attributes:[property1:value1, property2:value2, ...]] 
	*               Value (returnGeometry=true): [geometry:[rings:[[[][][]]]],attributes:[property1:value1, property2:value2, ...]] 
	*/
	public Map featuresFromPointLayersIntersection(String xCoord, String yCoord,List urls, Boolean returnGeometry)
	
	public Map featuresAttributesFromPointLayersIntersection(String xCoord, String yCoord, List urls)
	
	public Map featuresGeometryFromPointLayersIntersection(String xCoord, String yCoord, List urls)
	
	/**
	 * returns the features do to an intersection of a circle centered at (x,y,radius) with the layer.
	 * @param x - x coord of the circle
	 * @param y - y coord of the circle
	 * @param radius - radius of the circle
	 * @param layerName - name of the layer to intersect with the circle where the features are at.
	 * @return Map - Map with one single key, value. The key is the layerName, the value is a list where
	 * each entry in the list represents a feature. The keys representing
	 * the attributes of the features will vary from layer to layer. It will return an empty list if
	 * no features are found in the specified circle. ie: 
	 * ["MDC.StreetMaint":[[OBJECTID:46004, STREETID:36735],[OBJECTID:19529, STREETID:36732W]]]
	 */
	public Map featuresInCircle(Double x, Double y, Integer radius, String url, Boolean returnGeometry)
	
	public Map featuresAttributesInCircle(Double x, Double y, Integer radius, String url)
	
	public Map featuresGeometryInCircle(Double x, Double y, Integer radius, String url)

		
	public Map featuresInPolygon(Map geometry, String url, List outFields, Boolean returnGeometry)

	public Map featuresAttributesInPolygon(Map geometry, String url, List outFields)
	
	public Map featuresGeometryInPolygon(Map geometry, String url, List outFields)
	
	/**
	 * Get a set of features in layer layer, from a point x,y. Tries to get all features a distance 
	 * radius away from (x,y). If none are found double the radius and try again. If nothing is found
	 * at radius 500, then an empty my is returned [(layer):[]]
	 * @param x - x-coord of the center of the circle
	 * @param y - y-coord of the center of the circle
	 * @param layer - name of layer to look for features
	 * @param uniqueAttribute - attribute in layer to distinguish duplicate features. For example in the
	 * case of street maintenance, the same street may be returned several times. If uniqueAttribute is null 
	 * no duplicate check is done and no duplicates are eliminated.
	 * @param radius - radius of the circle. Max radius is hardcoded to 500
	 * @return Map - contains one key,value pair. Key is the layer, value is an array of features 
	 * 	["MDC.StreetMaint":[[OBJECTID:46004, STREETID:36735],[OBJECTID:19529, STREETID:36732W]]]. It returns
	 * an empty List of features if none are found at a max radius of 500.
	 */
	public Map featuresByProximity(Double x, Double y, String url, String uniqueAttribute, Integer radius)

	public Map featuresAttributesByProximity(Double x, Double y, String url, String uniqueAttribute, Integer radius)
	
	public Map featuresGeometryByProximity(Double x, Double y, String url, String uniqueAttribute, Integer radius)
	
	/**
	 * Get a set of features corresponding to the list of ids
	 * @param layer - name of layer where to find the features.
	 * @param idAttribute - The attribute that contains the id
	 * @param ids - List of ids
	 * @return Map - Contains one key, value pair. Key is the layer name, value is an array of features
	 * each one having an id in ids list.
	 * ["MDC.StreetMaint":[[OBJECTID:46004, STREETID:36735],[OBJECTID:19529, STREETID:36732W]]]
	 */
	public Map featuresByIds(String url, String idAttribute,  List ids, Boolean returnGeometry)
	
	public Map featuresAttributesByIds(String url, String idAttribute,  List ids)
	
	public Map featuresGeometryByIds(String url, String idAttribute,  List ids)

	

}
