package com.taxi_pas_4.utils.blacklist;

import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientBlackList {
//    private static final String BASE_URL = "https://m.easy-order-taxi.site/";
    private static final String BASE_URL = sharedPreferencesHelperMain.getValue("baseUrl", "https://m.easy-order-taxi.site") + "/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
