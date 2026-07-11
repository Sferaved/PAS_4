package com.taxi_pas_4.ui.landing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LandingIntroHelperTest {

    @Test
    public void showsWhenNeverShown_guestOnly() {
        assertTrue(LandingIntroHelper.shouldShowIntroAfterUpdate(0, 1147, true));
        assertFalse(LandingIntroHelper.shouldShowIntroAfterUpdate(0, 1147, false));
    }

    @Test
    public void showsWhenAppUpdated_guestOnly() {
        assertTrue(LandingIntroHelper.shouldShowIntroAfterUpdate(1146, 1147, true));
        assertFalse(LandingIntroHelper.shouldShowIntroAfterUpdate(1146, 1147, false));
    }

    @Test
    public void skipsWhenAlreadyShownForCurrentVersion() {
        assertFalse(LandingIntroHelper.shouldShowIntroAfterUpdate(1147, 1147, true));
    }

    @Test
    public void skipsWhenStoredAheadOfCurrent() {
        assertFalse(LandingIntroHelper.shouldShowIntroAfterUpdate(1200, 1147, true));
    }

    @Test
    public void opensLandingOnColdStart_guestOnly() {
        assertTrue(LandingIntroHelper.shouldOpenLandingOnColdStart(true, true));
        assertFalse(LandingIntroHelper.shouldOpenLandingOnColdStart(true, false));
        assertFalse(LandingIntroHelper.shouldOpenLandingOnColdStart(false, true));
        assertTrue(LandingIntroHelper.shouldEnterOrderOnColdStart(true, false));
        assertFalse(LandingIntroHelper.shouldEnterOrderOnColdStart(true, true));
    }
}
