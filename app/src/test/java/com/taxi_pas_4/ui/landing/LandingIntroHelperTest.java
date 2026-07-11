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
    public void blocksIntroWhenActiveOrderUidPresent() {
        assertTrue(LandingIntroHelper.shouldBlockIntroDuringActiveOrder(true, false));
    }

    @Test
    public void blocksIntroWhenOnFinishScreen() {
        assertTrue(LandingIntroHelper.shouldBlockIntroDuringActiveOrder(false, true));
    }

    @Test
    public void allowsIntroWithoutActiveOrder() {
        assertFalse(LandingIntroHelper.shouldBlockIntroDuringActiveOrder(false, false));
    }
}
