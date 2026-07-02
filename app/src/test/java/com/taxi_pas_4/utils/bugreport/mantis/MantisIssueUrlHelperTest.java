package com.taxi_pas_4.utils.bugreport.mantis;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MantisIssueUrlHelperTest {

    @Test
    public void buildIssueViewUrl_trimsTrailingSlash() {
        String url = MantisIssueUrlHelper.buildIssueViewUrl("https://bugs.example.com/", 42);
        assertEquals("https://bugs.example.com/view.php?id=42", url);
    }

    @Test
    public void buildIssueSummary_truncatesLongDescription() {
        String summary = MantisIssueUrlHelper.buildIssueSummary(
                "PAS_4",
                "4.1107",
                "A".repeat(200)
        );
        assertTrue(summary.length() <= 128);
        assertTrue(summary.startsWith("[PAS_4 4.1107] "));
        assertTrue(summary.endsWith("..."));
    }

    @Test
    public void buildLegacyTelegramMessage_includesLogSize() {
        String message = MantisBugReportSender.buildLegacyTelegramMessage(
                "BUG REPORT",
                "Problem",
                "App crashed",
                "Device",
                "Date",
                "02.07.2026 12:00:00",
                "Logs",
                "128 KB"
        );
        assertTrue(message.contains("128 KB"));
        assertTrue(message.contains("App crashed"));
        assertFalse(message.contains("Mantis #"));
    }

    @Test
    public void buildRetrofitBaseUrl_addsApiRestSuffix() {
        assertEquals(
                "https://bugs.example.com/api/rest/",
                MantisIssueUrlHelper.buildRetrofitBaseUrl("https://bugs.example.com")
        );
    }
}
