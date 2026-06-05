package com.taxi_pas_4.utils.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.webkit.URLUtil;

public final class BrowserIntentHelper {

    private static final String CHROME_PACKAGE = "com.android.chrome";

    private BrowserIntentHelper() {
    }

    /**
     * Opens URL in Chrome when available, otherwise in the default browser.
     */
    public static boolean openUrl(Context context, String url) {
        if (context == null || url == null || url.isEmpty() || !URLUtil.isValidUrl(url)) {
            return false;
        }

        Uri uri = Uri.parse(url);
        PackageManager packageManager = context.getPackageManager();

        Intent chromeIntent = new Intent(Intent.ACTION_VIEW, uri);
        chromeIntent.setPackage(CHROME_PACKAGE);
        if (chromeIntent.resolveActivity(packageManager) != null) {
            context.startActivity(chromeIntent);
            return true;
        }

        Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, uri);
        if (fallbackIntent.resolveActivity(packageManager) != null) {
            context.startActivity(fallbackIntent);
            return true;
        }

        return false;
    }
}
