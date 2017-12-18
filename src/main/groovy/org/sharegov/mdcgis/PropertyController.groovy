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
     * @param folio
     * @return PropertyInfo or error message with empty data.
     */
    JsonBuilder propertyByFolio(String folio){

        _log.info("PropertyController - propertyByFolio - folio: " + folio)
        Map answer = [:]

        try {
            Map propertyInfo = propertyInfoService.getPropertyInfo(folio)
            if(propertyInfo == null || propertyInfo.isEmpty()){
                answer = [ok: false, message: "could not find propertyByFolio", data:[:]]
            }else{
                answer = [ok: true, data:["propertyInfo": propertyInfo ]]
            }
        }catch(RetrievalOfDataException rode){
            _log.error( "PropertyController - propertyByFolio - folio: " + folio + " message: " + rode.message )
            answer = [ok: false, message: "could not find propertyByFolio", data:[:]]
        }

        _log.info("PropertyController - propertyByFolio - About to finish with "+ answer.ok)
        JsonBuilder json = new JsonBuilder();
        json.call(answer)
    }
}
