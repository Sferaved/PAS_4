package com.taxi_pas_4.utils.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.messaging.FirebaseMessaging;
import com.taxi_pas_4.utils.worker.utils.TokenUtils;

public class SendTokenWorker extends ListenableWorker {
    private static final String TAG = "SendTokenWorker";
    private final Context context;

    public SendTokenWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(completer -> {
            String userEmail = getInputData().getString("userEmail");
            Log.d(TAG, "startWork: " + userEmail);

            com.google.firebase.installations.FirebaseInstallations.getInstance().delete()
                    .addOnCompleteListener(deleteTask -> {
                        if (!deleteTask.isSuccessful()) {
                            Log.e(TAG, "Failed to delete Firebase installation");
                            completer.set(Result.failure());
                            return;
                        }

                        FirebaseMessaging.getInstance().getToken()
                                .addOnSuccessListener(token -> {
                                    Log.d(TAG, "FCM Token: " + token);
                                    TokenUtils.sendToken(context, userEmail, token);
                                    completer.set(Result.success());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to get FCM token", e);
                                    completer.set(Result.failure());
                                });
                    });

            return "SendTokenWorker-getToken";
        });
    }

}
