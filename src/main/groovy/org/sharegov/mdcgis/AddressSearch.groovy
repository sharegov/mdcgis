package org.sharegov.mdcgis

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by ram on 6/14/2017.
 */
class AddressSearch {

    private static Logger logger = LoggerFactory.getLogger(AddressSearch.class);
    CandidateService candidateService
    PropertyInfoService propertyInfoService
    GisService gisService
    GisConfig gisConfig

    /**
     * Get Address by lat and Lng
     * @param latitude
     * @param longitude
     * @return address data or empty data
     */
    Map getAddressByLatAndLng(double latitude, double longitude){

        logger.info("getAddressByLatAndLng: latitude: "+ latitude +" longitude: "+ longitude);
        long startLatLngTime = System.currentTimeMillis();

        //get x and y
        long startXY = System.currentTimeMillis();
        Map inXY = candidateService.getXandY(latitude, longitude, gisConfig.gisServices.getXYfromLatLongDec)
        System.out.println("The XY Time : " + ( System.currentTimeMillis() - startXY) );

        //No x and y, return null.
        if(!inXY) return [:]

        Double inX = inXY.x as Double; Double inY = inXY.y as Double;
        logger.info("getAddressByLatAndLng: converted x: "+ inX +" y: "+ inY);

        //Get address from PA layers.
        Map address = getAddressByPaLayers(inX, inY);
        if(!address){
            //Get Address from Street Locator
            address = getAddressByStreetLocator(inX, inY)
        }
        logger.info("getAddressByLatAndLng: got the address "+ address.toString() )
        if(!address) return [:]

        //Get address lat and lng
        long startConvertLatLng = System.currentTimeMillis();
        Map latLng = candidateService.getLatAndLng(address.location.x as double, address.location.y as double, gisConfig.gisServices.getLatLongDecFromXY )
        System.out.println("The Convert to Lat and Long Time : " + ( System.currentTimeMillis() - startConvertLatLng) );

        //Add in/out locations data
        address.location.lat = latLng.lat as Double
        address.location.lng = latLng.lng as Double
        address.location.inLat = latitude
        address.location.inLng = longitude

        System.out.println("AddressByLatLng Total Time: " + (System.currentTimeMillis() - startLatLngTime))

        logger.info("getAddressByLatAndLng: added lat and lng and done with address "+ address.toString() )
        return address;
    }

    /**
     * Get Address from PA Layers By X and Y
     * @param inX
     * @param inY
     * @return return address or null
     */
    Map getAddressByPaLayers(Double inX, Double inY) {

        //Get address by PaLayers
        long startAddrTime = System.currentTimeMillis();
        Map paAddress = propertyInfoService.getStreetZipByCoord(inX, inY)
        System.out.println("The Pa Address Time : " + (System.currentTimeMillis() - startAddrTime));

        if (!paAddress) return null

        //Parse Address
        long startparseTime = System.currentTimeMillis()
        paAddress.parsedAddress = candidateService.parseAddress("${paAddress.street}, ${paAddress.zip}");
        System.out.println("The PaAddress Parser Time : " + (System.currentTimeMillis() - startparseTime));

        paAddress = collectAddress(paAddress, inX, inY);
        if(paAddress){
            paAddress.addressApproximation = false
        }
        return paAddress
    }

    /**
     * Get Address from Street Locator
     * @param inX
     * @param inY
     * @return address or null
     */
    Map getAddressByStreetLocator(Double inX, Double inY) {
        // get approx address
        long startTime = System.currentTimeMillis()
        Map stAddress = gisService.reverseGeoCode(inX, inY, gisConfig.locators.reverseGeoCodeGeoStreet, 100)
        long endTime = System.currentTimeMillis() - startTime
        System.out.println("The address by reverse geocode Time :" + endTime)

        if (!stAddress) return null

        long startparseTime = System.currentTimeMillis()
        stAddress.parsedAddress = candidateService.parseAddress("${stAddress.street}, ${stAddress.zip}");
        System.out.println("The reverse geocode Parser Time : " + (System.currentTimeMillis() - startparseTime));

        stAddress = collectAddress(stAddress, inX, inY)
        if(stAddress){
            stAddress.addressApproximation = true
        }
        return stAddress;
    }

    /**
     * Collect address in recommended format.
     * @param address
     * @param inX
     * @param inY
     * @return address or null
     */
    Map collectAddress(Map address,Double inX, Double inY) {
        Map addressData =[:]
        if (address) {
            Map location = [:];
            location.x = address.x
            location.y = address.y
            location.inX = inX
            location.inY = inY
            addressData.address = address.street + ", " + address.zip
            addressData.municipality = address.municipality
            addressData.municipalityId = address.municipalityId
            addressData.parsedAddress = address.parsedAddress
            addressData.parsedAddress.zip = address.zip
            addressData.location = location
            return addressData;
        }
        return null
    }
}