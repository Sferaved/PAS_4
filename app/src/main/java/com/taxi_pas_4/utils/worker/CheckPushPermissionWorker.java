package com.taxi_pas_4.utils.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.taxi_pas_4.androidx.startup.MyApplication;
import com.taxi_pas_4.utils.log.Logger;
import com.taxi_pas_4.utils.worker.utils.PushPermissionChecker;

public class CheckPushPermissionWorker extends Worker {
    private static final String TAG = "CheckPushPermissionWorker";

    public CheckPushPermissionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            PushPermissionChecker.checkAndRequestPushPermission(getApplicationContext());
            return Result.success();
        } catch (Exception e) {
            Logger.e(MyApplication.getContext(), TAG, "Ошибка в проверке разрешений: " + e.getMessage());
            return Result.failure();
        }
    }
}
