package com.taxi_pas_4.utils.orders;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrderChannelResponseHelperTest {

    @Test
    public void routeSnapshotWithoutUid_isNotReady() {
        Map<String, String> snapshot = new HashMap<>();
        snapshot.put("routefrom", "вул. Успенська");
        snapshot.put("order_cost", "16");
        assertFalse(OrderChannelResponseHelper.hasDispatchingOrderUid(snapshot));
    }

    @Test
    public void mapWithUid_isReady() {
        Map<String, String> map = new HashMap<>();
        map.put("dispatching_order_uid", "abc-123");
        assertTrue(OrderChannelResponseHelper.hasDispatchingOrderUid(map));
    }

    @Test
    public void nullUidLiteral_isNotReady() {
        Map<String, String> map = new HashMap<>();
        map.put("dispatching_order_uid", "null");
        assertFalse(OrderChannelResponseHelper.hasDispatchingOrderUid(map));
    }
}
