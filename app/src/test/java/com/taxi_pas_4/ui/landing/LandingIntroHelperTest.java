package com.taxi_pas_4.ui.landing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LandingIntroHelperTest {

    @Test
    public void showsWhenNeverShown() {
        assertTrue(LandingIntroHelper.shouldShowIntroAfterUpdate(0, 1147));
    }

    @Test
    public void showsWhenAppUpdated() {
        assertTrue(LandingIntroHelper.shouldShowIntroAfterUpdate(1146, 1147));
    }

    @Test
    public void skipsWhenAlreadyShownForCurrentVersion() {
        assertFalse(LandingIntroHelper.shouldShowIntroAfterUpdate(1147, 1147));
    }

    @Test
    public void skipsWhenStoredAheadOfCurrent() {
        assertFalse(LandingIntroHelper.shouldShowIntroAfterUpdate(1200, 1147));
    }

    @Test
    public void opensLandingOnColdStart() {
        assertTrue(LandingIntroHelper.shouldOpenLandingOnColdStart(true));
        assertFalse(LandingIntroHelper.shouldOpenLandingOnColdStart(false));
    }
}
