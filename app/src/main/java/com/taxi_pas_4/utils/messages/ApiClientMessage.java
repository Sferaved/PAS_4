package com.taxi_pas_4.utils.messages;

import com.taxi_pas_4.utils.city.BaseUrlHelper;

import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class ApiClientMessage  {

    private static final String BASE_URL = BaseUrlHelper.fromPrefsWithSlash(sharedPreferencesHelperMain);
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}