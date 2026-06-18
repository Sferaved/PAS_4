package com.taxi_pas_4.utils.phone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PhoneNumberHelperTest {

    @Test
    public void fixMaskedInput_strips380PrefixDuringTyping() {
        assertEquals("+38 093 954 65",
                PhoneNumberHelper.fixMaskedInput("+38 380 939 54 65"));
    }

    @Test
    public void fixMaskedInput_strips380WhileTypingPartial() {
        assertEquals("+38 09",
                PhoneNumberHelper.fixMaskedInput("+38 380 9"));
    }

    @Test
    public void normalize_fixesDuplicate380AfterPlus38() {
        assertEquals("+38 093 123 45 67",
                PhoneNumberHelper.normalizeAndFormat("+38 380 93 123 45 67"));
    }

    @Test
    public void normalize_leavesIncompleteDuplicateInvalid() {
        assertFalse(PhoneNumberHelper.isValid(
                PhoneNumberHelper.normalizeAndFormat("+38 380 931 23 45")));
    }

    @Test
    public void normalize_keepsValidNumber() {
        assertEquals("+38 093 666 55 44",
                PhoneNumberHelper.normalizeAndFormat("+38 093 666 55 44"));
    }

    @Test
    public void normalize_fromDigitsOnly() {
        assertEquals("+38 050 123 45 67",
                PhoneNumberHelper.normalizeAndFormat("380501234567"));
    }

    @Test
    public void isValid_acceptsFormattedUaMobile() {
        assertTrue(PhoneNumberHelper.isValid("+38 093 666 55 44"));
    }

    @Test
    public void isValid_rejectsDuplicateCountryCodeFormat() {
        assertFalse(PhoneNumberHelper.isValid("+38 380 931 23 45"));
    }

    @Test
    public void isValid_rejectsEmbedded380OperatorCode() {
        assertFalse(PhoneNumberHelper.isValid(
                PhoneNumberHelper.normalizeAndFormat("+38 380 939 54 65")));
    }
}
