package com.taxi_pas_4.ui.finish;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddCostBottomUpdateResponseTest {

    @Test
    public void resolveDisplayCost_prefersClientCost() {
        AddCostBottomUpdateResponse response = new AddCostBottomUpdateResponse();
        setField(response, "uid", "new-uid");
        setField(response, "clientCost", "21");
        setField(response, "webCost", "20");

        assertTrue(response.hasRecreatedOrder());
        assertEquals("21", response.resolveDisplayCostGrivna());
    }

    @Test
    public void hasRecreatedOrder_falseWhenUidMissing() {
        AddCostBottomUpdateResponse response = new AddCostBottomUpdateResponse();
        setField(response, "response", "401");

        assertFalse(response.hasRecreatedOrder());
    }

    private static void setField(AddCostBottomUpdateResponse target, String field, String value) {
        try {
            java.lang.reflect.Field f = AddCostBottomUpdateResponse.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
