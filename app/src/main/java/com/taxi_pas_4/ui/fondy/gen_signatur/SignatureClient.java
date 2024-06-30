package com.taxi_pas_4.ui.fondy.gen_signatur;

import android.util.Log;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignatureClient {

    private static final String BASE_URL = "https://m.easy-order-taxi.site/";

    private final ApiService apiService;

    public SignatureClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void generateSignature(String params, final SignatureCallback callback) {
        Call<SignatureResponse> call = apiService.generateSignature(params);
        String requestUrl = call.request().url().toString();
        Log.d("RequestUrl", "Request URL: " + requestUrl);

        call.enqueue(new Callback<SignatureResponse>() {
            @Override
            public void onResponse(@NonNull Call<SignatureResponse> call, @NonNull Response<SignatureResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SignatureResponse signatureResponse = response.body();
                    if (callback != null) {
                        callback.onSuccess(signatureResponse);

                    }
                } else {
                    if (callback != null) {
                        callback.onError("Error occurred");
                    }
                }
            }

            @Override
            public void onFailure(Call<SignatureResponse> call, Throwable t) {
                if (callback != null) {
                    callback.onError(t.getMessage());
                }
            }
        });
    }

    public interface SignatureCallback {
        void onSuccess(SignatureResponse response);

        void onError(String error);
    }
}
