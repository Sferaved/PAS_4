package com.taxi_pas_4.ui.cities.api;



import com.taxi_pas_4.utils.network.RetryInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CityApiTestClient {
//    private static final String BASE_URL = "https://m.easy-order-taxi.site/";
//    private static final String BASE_URL = "https://test-taxi.kyiv.ua/";
    private static final String BASE_URL = "https://t.easy-order-taxi.site/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new RetryInterceptor()) // 3 попытки
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS) // Тайм-аут на соединение
                    .readTimeout(30, TimeUnit.SECONDS)    // Тайм-аут на чтение данных
                    .writeTimeout(30, TimeUnit.SECONDS)   // Тайм-аут на запись данных
                    .build();

            // Create Retrofit instance with the OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
