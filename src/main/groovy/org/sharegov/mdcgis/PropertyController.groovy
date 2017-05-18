package org.sharegov.mdcgis

import groovy.json.JsonBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by ram on 5/17/2017.
 */
class PropertyController {

    private static Logger _log = LoggerFactory.getLogger(PropertyController.class);
    PropertyInfoService propertyInfoService

    /**
     * get PropertyInfo by folio.
     *
     * @param queryParam
     * @return PropertyInfo or error message with empty data.
     */
    JsonBuilder getPropertyInformation(Map queryParam ){

        String folio = queryParam["folio"]
        _log.info("AddressService - getPropertyInformation - folio: " + folio)
        Map answer = [:]

        try {
            Map propertyInfo = propertyInfoService.getPropertyInformation(folio)
            if(propertyInfo == null || propertyInfo.isEmpty()){
                answer = [ok: false, message: "could not find PropertyInformation", data:[:]]
            }else{
                answer = [ok: true, data: propertyInfo]
            }
        }catch(RetrievalOfDataException rode){
            _log.info([ok: false, message:rode.message, data:[:]])
            answer = [ok: false, message: "could not find PropertyInformation", data:[:]]
        }

        _log.info("getPropertyInformation - About to finish with "+ answer.ok)
        JsonBuilder json = new JsonBuilder();
        json.call(answer)
    }
}
