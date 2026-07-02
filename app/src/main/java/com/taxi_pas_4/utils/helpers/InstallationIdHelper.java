package com.taxi_pas_4.utils.helpers;

import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import java.util.UUID;

public class InstallationIdHelper {

    private static final String KEY_INSTALLATION_ID = "installation_id";

    public static String getOrCreateInstallationId() {
        String existing = (String) sharedPreferencesHelperMain.getValue(KEY_INSTALLATION_ID, "");
        if (existing != null && !existing.isEmpty()) {
            return existing;
        }
        String id = UUID.randomUUID().toString();
        sharedPreferencesHelperMain.saveValue(KEY_INSTALLATION_ID, id);
        return id;
    }
}

