package qconsp.models.city;

import org.junit.Before;
import org.junit.Test;
import qconsp.models.state.State;
import qconsp.utils.EndpointTestCase;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static qconsp.models.order.Status.*;

public class CityTest extends EndpointTestCase {

    @Before
    public void fixtures() {
        post("/cities/sao-paulo", "{ stateId: '/states/sp' }");
    }

    @Test
    public void testCreate() {
        String json = post("/cities/sao-paulo", "{ stateId: '/states/sp' }");
        City city = from(json, City.class);

        assertEquals(id(State.class, "sp"), city.stateId);
    }

    @Test
    public void testCityPipe() {
        post("/orders", "{ cityId: '/cities/sao-paulo', status: 'CREATED' }");
        post("/orders", "{ cityId: '/cities/sao-paulo', status: 'PREPARED' }");
        awaitAsync(20, TimeUnit.SECONDS);

        String json = get("/cities/sao-paulo");
        City city = from(json, City.class);

        assertEquals((Integer) 2, city.getOrderCount());
        assertEquals((Integer) 1, city.getOrderCountByStatus(CREATED));
        assertEquals((Integer) 1, city.getOrderCountByStatus(PREPARED));
        assertEquals((Integer) 0, city.getOrderCountByStatus(DELIVERED));
    }

}
