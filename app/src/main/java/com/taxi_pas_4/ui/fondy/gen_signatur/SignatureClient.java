package com.taxi_pas_4.ui.fondy.gen_signatur;

import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignatureClient {

//    private static final String BASE_URL = "https://m.easy-order-taxi.site/";
    private static final String BASE_URL = sharedPreferencesHelperMain.getValue("baseUrl", "https://m.easy-order-taxi.site") + "/";
    private static final String TAG = "SignatureClient";

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
            public void onFailure(@NonNull Call<SignatureResponse> call, @NonNull Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
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
