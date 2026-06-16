package com.taxi_pas_4.utils.helpers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WfpGooglePayHelperTest {

    @Test
    public void formatTotalPriceForGooglePay_usesTwoDecimalPlaces() {
        assertEquals("60.00", WfpGooglePayHelper.formatTotalPriceForGooglePay("60"));
        assertEquals("5.00", WfpGooglePayHelper.formatTotalPriceForGooglePay("5"));
        assertEquals("22.50", WfpGooglePayHelper.formatTotalPriceForGooglePay("22,5"));
    }
}
