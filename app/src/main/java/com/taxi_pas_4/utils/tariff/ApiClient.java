package com.taxi_pas_4.utils.tariff;

import com.taxi_pas_4.utils.city.BaseUrlHelper;

import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = BaseUrlHelper.fromPrefs(sharedPreferencesHelperMain) + "/apiTest/android/";

    public static Retrofit getRetrofitClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

