package com.taxi_pas_4.ui.weather.finish;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;

import com.taxi_pas_4.MainActivity;

import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CityInfoHelper {
    private static final String BASE_URL = "https://City-Info.utax.top/api/data/";

    private final OkHttpClient client;
    private final Context context;

    public CityInfoHelper(Context context) {
        this.context = context.getApplicationContext();
        client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .cache(null)
                .build();
    }

    public interface CityInfoCallback {
        void onSuccess(CityInfo info);
        void onError(String error);
    }

    public void getCityInfo(String city, CityInfoCallback callback) {
        new Thread(() -> {
            String locale = getCurrentLocale();
            String token = MainActivity.utaxKey;
            String url = BASE_URL + city + "?lang=" + locale + "&_=" + System.currentTimeMillis();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Accept-Language", locale)
                    .addHeader("Cache-Control", "no-cache, no-store")
                    .addHeader("Pragma", "no-cache")
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    CityInfo info = parseJson(json);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(info));
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onError("HTTP " + response.code()));
                }
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError(e.getMessage()));
            }
        }).start();
    }

    /**
     * Получает текущую локаль приложения
     */
    private String getCurrentLocale() {
        if (context == null) {
            return "en"; // Значение по умолчанию
        }

        Configuration config = context.getResources().getConfiguration();
        Locale locale = config.getLocales().get(0);
        String language = locale.getLanguage();

        // Поддерживаемые языки
        switch (language) {
            case "uk":
                return "uk";
            case "ru":
                return "ru";
            case "en":
            default:
                return "en";
        }
    }

    /**
     * Альтернативный метод с явной передачей локали
     */
    public void getCityInfo(String city, String locale, CityInfoCallback callback) {
        new Thread(() -> {
            String token = MainActivity.utaxKey;
            String url = BASE_URL + city + "?lang=" + locale + "&_=" + System.currentTimeMillis();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Accept-Language", locale)
                    .addHeader("Cache-Control", "no-cache, no-store")
                    .addHeader("Pragma", "no-cache")
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    CityInfo info = parseJson(json);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(info));
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onError("HTTP " + response.code()));
                }
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError(e.getMessage()));
            }
        }).start();
    }

    private CityInfo parseJson(String json) throws Exception {
        JSONObject obj = new JSONObject(json);
        CityInfo info = new CityInfo();
        info.setWeather(obj.optString("weather", ""));
        info.setTemperature(obj.optDouble("temperature", 0));
        info.setAirAlarm(obj.optBoolean("air_alarm", false));
        info.setRebActive(obj.optBoolean("reb_active", false));
        info.setTimeStamp(obj.optString("time_stamp", ""));
        return info;
    }
}