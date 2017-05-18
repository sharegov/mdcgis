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
    public void testGetFolioInformation_With_CorrectData(){
        Map data = [folio:"3022060602360"]
        JsonBuilder result = propertyController.getPropertyInformation(data);
        assert result.getProperties().get("content").getAt("ok") == true
    }

    @Test
    public void testGetFolioInformation_With_CondoFolio(){
        Map data = [folio:"0232341690001"]
        JsonBuilder result = propertyController.getPropertyInformation(data);
        assert result.getProperties().get("content").getAt("ok") == true
    }

    @Test
    public void testGetFolioInformation_With_WrongData(){
        Map data = [folio:"000"]
        JsonBuilder result = propertyController.getPropertyInformation(data);
        assert result.getProperties().get("content").getAt("ok") == false
    }

    @Test
    public void testGetFolioInformation_With_NoData(){
        Map data = [folio:""]
        JsonBuilder result = propertyController.getPropertyInformation(data);
        assert result.getProperties().get("content").getAt("ok") == false
    }

    @Test
    public void testGetFolioInformation_With_WrongParam(){
        Map data = [foliooo:"3022060602360"]
        JsonBuilder result = propertyController.getPropertyInformation(data);
        assert result.getProperties().get("content").getAt("ok") == false
    }
}
