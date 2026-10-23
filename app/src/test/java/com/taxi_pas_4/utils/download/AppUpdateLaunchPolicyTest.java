package com.taxi_pas_4.utils.download;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AppUpdateLaunchPolicyTest {

    @Test
    public void skipsAutoStartOnAndroid10AndNewer() {
        assertFalse(AppUpdateLaunchPolicy.shouldAutoStartMainActivity(29));
        assertFalse(AppUpdateLaunchPolicy.shouldAutoStartMainActivity(33));
    }

    @Test
    public void allowsAutoStartBeforeAndroid10() {
        assertTrue(AppUpdateLaunchPolicy.shouldAutoStartMainActivity(28));
    }
}
