package com.taxi_pas_4.ui.visicom;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CostRequestGateTest {

    @Test
    public void tryEnter_secondCallWhileHeld_isRejected() {
        CostRequestGate gate = new CostRequestGate();

        assertTrue(gate.tryEnter());
        assertTrue(gate.isEntered());
        assertFalse(gate.tryEnter());
    }

    @Test
    public void exit_allowsNextEnter() {
        CostRequestGate gate = new CostRequestGate();

        assertTrue(gate.tryEnter());
        gate.exit();
        assertFalse(gate.isEntered());
        assertTrue(gate.tryEnter());
    }
}
