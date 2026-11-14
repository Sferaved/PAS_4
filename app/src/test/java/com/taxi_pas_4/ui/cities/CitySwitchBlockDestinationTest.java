package com.taxi_pas_4.ui.cities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CitySwitchBlockDestinationTest {

    @Test
    public void blocksPaymentMethodsAndActiveOrderScreens() {
        assertTrue(MainActivity.isCitySwitchBlockedDestination(R.id.nav_card));
        assertTrue(MainActivity.isCitySwitchBlockedDestination(R.id.nav_finish_separate));
        assertTrue(MainActivity.isCitySwitchBlockedDestination(R.id.nav_card_ver));
    }

    @Test
    public void allowsCitySwitchOnOrderMap() {
        assertFalse(MainActivity.isCitySwitchBlockedDestination(R.id.nav_visicom));
        assertFalse(MainActivity.isCitySwitchBlockedDestination(R.id.nav_city));
    }
}
