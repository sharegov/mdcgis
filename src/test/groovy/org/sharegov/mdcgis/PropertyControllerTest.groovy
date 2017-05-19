package org.sharegov.mdcgis

import groovy.json.JsonBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.sharegov.mdcgis.utils.AppContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Created by ram on 5/17/2017.
 */
class PropertyControllerTest {

    private PropertyController propertyController


    @Before
    public void setUp()throws Exception{
        new ClassPathXmlApplicationContext("configtest.xml")

        ApplicationContext applicationContext = AppContext.getApplicationContext();
        propertyController = (PropertyController) applicationContext.getBean("PROPERTY_CONTROLLER")
    }

    @After
    public void destory() throws Exception{
        ((ClassPathXmlApplicationContext) AppContext.getApplicationContext()).close();
    }

    @Test
    public void testPropertyByFolio_With_HouseFolio(){
        JsonBuilder result = propertyController.propertyByFolio("3059010240130");
        assert result.getProperties().get("content").getAt("ok") == true
        assert result.getProperties().get("content").getAt("data").propertyInfo.parcelFolioNumber == "3059010240130"
    }

    @Test
    public void testPropertyByFolio_With_BuildingFolio(){
        JsonBuilder result = propertyController.propertyByFolio("0232341690001");
        assert result.getProperties().get("content").getAt("ok") == true
        assert result.getProperties().get("content").getAt("data").propertyInfo.parcelFolioNumber == "0232341690001"
    }

    @Test
    public void testPropertyByFolio_With_CondoFolioUnit(){
        JsonBuilder result = propertyController.propertyByFolio("0232341690630");
        assert result.getProperties().get("content").getAt("ok") == true
        assert result.getProperties().get("content").getAt("data").propertyInfo.parcelFolioNumber == "0232341690630"
        assert result.getProperties().get("content").getAt("data").propertyInfo.propertyType == "CONDO"
        assert result.getProperties().get("content").getAt("data").propertyInfo.parcelInfoCondoUnit == "317"
    }

    @Test
    public void testPropertyByFolio_With_MultipleAddress(){
        JsonBuilder result = propertyController.propertyByFolio("3049331130001")
        assert result.getProperties().get("content").getAt("ok") == true
    }

    @Test
    public void testPropertyByFolio_With_WrongData(){
        JsonBuilder result = propertyController.propertyByFolio("000");
        assert result.getProperties().get("content").getAt("ok") == false
    }

    @Test
    public void testPropertyByFolio_With_NoData(){
        JsonBuilder result = propertyController.propertyByFolio("");
        assert result.getProperties().get("content").getAt("ok") == false
    }

}
