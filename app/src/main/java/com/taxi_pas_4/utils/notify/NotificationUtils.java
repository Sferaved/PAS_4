package com.taxi_pas_4.utils.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.List;

public class NotificationUtils {

    private static final String TAG = "NotificationUtils";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void logNotificationChannels(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "logNotificationChannels: ");
        if (notificationManager != null) {
            List<NotificationChannel> channels = notificationManager.getNotificationChannels();
            for (NotificationChannel channel : channels) {

                Log.d(TAG, "Channel" + channel.toString());
                Log.d(TAG, "Channel ID: " + channel.getId() + ", Name: " + channel.getName());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void updateNotificationChannel(Context context, String channelId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            NotificationChannel existingChannel = notificationManager.getNotificationChannel(channelId);
            if (existingChannel != null) {
                existingChannel.setImportance(NotificationManager.IMPORTANCE_NONE);
                existingChannel.setSound(null, null);
                existingChannel.enableVibration(false);
                existingChannel.setShowBadge(false); // Отключение отображения значка уведомлений
                existingChannel.enableLights(false); // Отключение световых сигналов
                notificationManager.createNotificationChannel(existingChannel);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void resetNotificationChannel(Context context, String channelId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.deleteNotificationChannel(channelId); // Удаление существующего канала
            NotificationChannel newChannel = new NotificationChannel(channelId, "Отключите этот канал", NotificationManager.IMPORTANCE_NONE);
            newChannel.setSound(null, null);
            newChannel.enableVibration(false);
            newChannel.setShowBadge(false); // Отключение отображения значка уведомлений
            newChannel.enableLights(false); // Отключение световых сигналов
            notificationManager.createNotificationChannel(newChannel); // Создание нового канала
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void disableNotificationChannel(Context context, String channelId) {
        logNotificationChannels(context);
        NotificationUtils.resetNotificationChannel(context, "ForegroundServiceChannel");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Log.d(TAG, "disableNotificationChannel: ");
            notificationManager.deleteNotificationChannel(channelId);
        }
        logNotificationChannels(context);
    }
}

