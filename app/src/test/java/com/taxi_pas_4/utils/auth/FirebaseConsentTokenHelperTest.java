package com.taxi_pas_4.utils.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.firebase.auth.GetTokenResult;

import org.junit.Test;
import org.mockito.Mockito;

public class FirebaseConsentTokenHelperTest {

    @Test
    public void isNonEmptyToken_rejectsNullBlankAndWhitespace() {
        assertFalse(FirebaseConsentTokenHelper.isNonEmptyToken(null));
        assertFalse(FirebaseConsentTokenHelper.isNonEmptyToken(""));
        assertFalse(FirebaseConsentTokenHelper.isNonEmptyToken("   "));
    }

    @Test
    public void isNonEmptyToken_acceptsRealToken() {
        assertTrue(FirebaseConsentTokenHelper.isNonEmptyToken("abc.def.ghi"));
    }

    @Test
    public void extractToken_readsTokenFromResult() {
        GetTokenResult result = Mockito.mock(GetTokenResult.class);
        Mockito.when(result.getToken()).thenReturn("token-1");

        assertEquals("token-1", FirebaseConsentTokenHelper.extractToken(result));
        assertNull(FirebaseConsentTokenHelper.extractToken(null));
    }

    @Test
    public void shouldRetryForNullUser_allowsConfiguredAttemptsOnly() {
        assertTrue(FirebaseConsentTokenHelper.shouldRetryForNullUser(0));
        assertTrue(FirebaseConsentTokenHelper.shouldRetryForNullUser(
                FirebaseConsentTokenHelper.NULL_USER_MAX_ATTEMPTS - 1));
        assertFalse(FirebaseConsentTokenHelper.shouldRetryForNullUser(
                FirebaseConsentTokenHelper.NULL_USER_MAX_ATTEMPTS));
    }
}
