package org.sharegov.mdcgis

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.sharegov.mdcgis.utils.AppContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Created by ram on 6/15/2017.
 */
class AddressSearchTest {

    private AddressSearch addressByLatLng

    @Before
    public void setUp() throws Exception{

        new ClassPathXmlApplicationContext("configtest.xml")
        ApplicationContext applicationContext = AppContext.getApplicationContext()
        addressByLatLng = (AddressSearch) applicationContext.getBean("ADDRESS_SEARCH")
    }

    @After
    public void destroy() throws Exception{
        ((ClassPathXmlApplicationContext) AppContext.getApplicationContext()).close()
    }

    @Test
    public void testGetAddressByLatAndLng_House(){
        Map address = addressByLatLng.getAddressByLatAndLng(25.833127, -80.21369)
        address.with {
            assert address.parsedAddress.House == "1051"
            assert address.parsedAddress.StreetName == "63RD"
            assert address.parsedAddress.zip == "33150-4222"
            assert address.location.x == 914797.5
            assert address.location.y == 545818.3
            assert address.location.lat == 25.83313973224484
            assert address.location.lng == -80.21369512024741
        }
    }

    @Test
    public void testGetAddressByLatAndLng_Street(){
        Map address = addressByLatLng.getAddressByLatAndLng(25.668134345060032, -80.40935435903381)
        address.with {
            assert address.parsedAddress.House == "13270"
            assert address.parsedAddress.StreetName == "108TH STREET"
            assert address.parsedAddress.zip == "33186"
            assert address.location.x == 850695.528
            assert address.location.y == 485502.747
            assert address.location.lat == 25.668116639160523
            assert address.location.lng == -80.40939337232128
        }
    }

    @Test
    public void testGetAddressByLatLng_Condo(){
        Map address = addressByLatLng.getAddressByLatAndLng(25.796771504664829, -80.135783695616027)
        address.with {
            assert address.parsedAddress.House == "2001"
            assert address.parsedAddress.StreetName == "MERIDIAN"
            assert address.parsedAddress.zip == "33139-0000"
            assert address.location.x == 940512.0
            assert address.location.y == 532760.3
            assert address.location.lat == 25.796771442490467
            assert address.location.lng == -80.13578388145655
        }
    }

    @Test
    public void testGetAddressByLatLng_InParcelButNoStreetNumber(){

        Map address = addressByLatLng.getAddressByLatAndLng(25.96672958066738, -80.142585350900276)
        address.with {
            assert address.parsedAddress.House == null
            assert address.parsedAddress.StreetName == null
            assert address.parsedAddress.zip == "33180-0000"
            assert address.location.x == 937870.5
            assert address.location.y == 594521.2
            assert address.location.lat == 25.96672958066738
            assert address.location.lng == -80.14258535090028
        }
    }

    @Test
    public void testGetAddressByLatLng_InParcelButNoStreetNumberPlaceXYToPropertyXY() {
        Map address = addressByLatLng.getAddressByLatAndLng(25.714050457884539, -80.335819756448529)
        address.with {
            assert address.parsedAddress.House == null
            assert address.parsedAddress.StreetName == null
            assert address.parsedAddress.zip == "33173-0000"
            assert address.location.x == 874841.0
            assert address.location.y == 502306.3
            assert address.location.lat == 25.71403320815113
            assert address.location.lng == -80.33583293251267
        }
    }

    @Test
    public void getAddressByLatAndLng_WrongData(){
        Map address = addressByLatLng.getAddressByLatAndLng(-0, -1)
        assert address.isEmpty() == true
    }

    @Test
    public void getAddressByPaLayers_HomeAddress(){
        Map address = addressByLatLng.getAddressByPaLayers(914797.5, 545818.3)
        address.with {
            assert address.parsedAddress.House == "1051"
            assert address.parsedAddress.StreetName == "63RD"
            assert address.parsedAddress.zip == "33150-4222"
            assert address.location.x == 914797.5
            assert address.location.y == 545818.3
            assert address.addressApproximation == false
        }
    }

    @Test
    public void getAddressByPaLayers_InvalidAddress(){
        Map address = addressByLatLng.getAddressByPaLayers(0, 545818.3)
        assert address == null
    }

    @Test
    public void getAddressByStreetLocator_StreetAddress(){
        Map address = addressByLatLng.getAddressByStreetLocator(899633.126, 521089.4)
        address.with {
            assert address.parsedAddress.House == "3947"
            assert address.parsedAddress.StreetName == "7TH"
            assert address.parsedAddress.zip == "33134"
            assert address.location.x == 899633.126
            assert address.location.y == 521089.4
            assert address.addressApproximation == true
        }
    }

    @Test
    public void getAddressByStreetLocator_InvalidAddress(){
        Map address = addressByLatLng.getAddressByPaLayers(0, 1.0)
        assert address == null
    }
}
