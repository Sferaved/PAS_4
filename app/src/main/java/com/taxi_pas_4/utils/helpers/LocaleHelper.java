package com.taxi_pas_4.utils.helpers;

import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.taxi_pas_4.utils.preferences.SharedPreferencesHelper;

import java.util.Locale;

public final class LocaleHelper {

    public static final String PREF_LOCALE = "locale";
    private static final String[] LOCALE_CODES = {"en", "ru", "uk"};

    private LocaleHelper() {
    }

    public static String normalizeLocaleCode(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "uk";
        }
        String code = raw.split("_")[0].split("-")[0].toLowerCase(Locale.ROOT);
        for (String supported : LOCALE_CODES) {
            if (supported.equals(code)) {
                return code;
            }
        }
        return "uk";
    }

    /** Для API/FCM — код языка без региона (en, ru, uk). */
    public static String getLocale() {
        if (sharedPreferencesHelperMain == null) {
            return "uk";
        }
        Object stored = sharedPreferencesHelperMain.getValue(PREF_LOCALE, "uk");
        return normalizeLocaleCode(stored != null ? stored.toString() : "uk");
    }

    /**
     * Контекст для prefs: в attachBaseContext getApplicationContext() ещё null — берём переданный context.
     */
    private static Context prefsStorageContext(Context context) {
        if (context == null) {
            return null;
        }
        Context app = context.getApplicationContext();
        return app != null ? app : context;
    }

    public static String getSavedLocaleCode(Context context) {
        Context storage = prefsStorageContext(context);
        if (storage == null) {
            return "uk";
        }
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(storage);
        Object stored = prefs.getValue(PREF_LOCALE, "uk");
        return normalizeLocaleCode(stored != null ? stored.toString() : "uk");
    }

    public static int localeCodeToSpinnerIndex(String localeCode) {
        return switch (normalizeLocaleCode(localeCode)) {
            case "en" -> 0;
            case "ru" -> 1;
            default -> 2;
        };
    }

    public static String spinnerIndexToLocaleCode(int index) {
        if (index < 0 || index >= LOCALE_CODES.length) {
            return "uk";
        }
        return LOCALE_CODES[index];
    }

    public static void persistLocale(Context context, String localeCode) {
        Context storage = prefsStorageContext(context);
        if (storage == null) {
            return;
        }
        new SharedPreferencesHelper(storage)
                .saveValue(PREF_LOCALE, normalizeLocaleCode(localeCode));
    }

    /**
     * Применяет сохранённую локаль ко всему приложению (API 24+ через AppCompat).
     */
    public static void applyAppLocale(Context context) {
        String localeCode = getSavedLocaleCode(context);
        Locale locale = Locale.forLanguageTag(localeCode);
        Locale.setDefault(locale);
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeCode));
    }

    public static Context wrapContext(Context context) {
        if (context == null) {
            return null;
        }
        String localeCode = getSavedLocaleCode(context);
        Locale locale = Locale.forLanguageTag(localeCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }
}
