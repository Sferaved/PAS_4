package com.taxi_pas_4.utils.helpers;

import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import java.util.Locale;

public class LocaleHelper {

    public static String getLocale() {
        String localeCode = (String) sharedPreferencesHelperMain.getValue("locale", Locale.getDefault().toString());
        return localeCode.split("_")[0];
    }
}
