package com.taxi_pas_4.utils.helpers;

import java.util.Locale;

public class LocaleHelper {

    public static String getLocale() {
        return Locale.getDefault().getLanguage();
    }
}
