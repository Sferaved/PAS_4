package com.taxi_pas_4.utils.connect;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.taxi_pas_4.R;
import com.taxi_pas_4.utils.log.Logger;

public class NetworkMonitor {
    private static final String TAG = "NetworkMonitor";
    private static final long STABILIZATION_DELAY_MS = 2000; // Ждём 2 секунды для стабилизации
    private static final long MIN_TOAST_INTERVAL_MS = 3000; // Минимум 3 секунды между тостами

    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isRegistered = false;
    private boolean isCurrentlyConnected = true;
    private long lastToastTime = 0;

    // Для debounce
    private Runnable pendingNetworkAction = null;
    private String lastProcessedState = "";

    public NetworkMonitor(Context context) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Инициализируем начальное состояние
        this.isCurrentlyConnected = checkInternetSync();
        Logger.d(context, TAG, "NetworkMonitor initialized. Initial state: " + isCurrentlyConnected);
    }

    public void startMonitoring(Activity activity) {
        if (connectivityManager == null) {
            Logger.e(context, TAG, "ConnectivityManager is null, cannot start monitoring");
            return;
        }

        if (isRegistered) {
            Logger.d(context, TAG, "Network monitoring already active");
            return;
        }

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                Logger.d(context, TAG, "Network available: " + network);
                scheduleNetworkCheck(activity, "onAvailable");
            }

            @Override
            public void onLost(@NonNull Network network) {
                Logger.w(context, TAG, "Network lost: " + network);
                scheduleNetworkCheck(activity, "onLost");
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
                Logger.d(context, TAG, "Capabilities changed for: " + network);
                scheduleNetworkCheck(activity, "onCapabilitiesChanged");
            }

            @Override
            public void onUnavailable() {
                Logger.w(context, TAG, "No networks available");
                scheduleNetworkCheck(activity, "onUnavailable");
            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        isRegistered = true;
        Logger.i(context, TAG, "Network monitoring started");
    }

    public void stopMonitoring() {
        if (!isRegistered || connectivityManager == null || networkCallback == null) {
            Logger.d(context, TAG, "Cannot stop monitoring - not active");
            return;
        }

        // Отменяем отложенные задачи
        if (pendingNetworkAction != null) {
            mainHandler.removeCallbacks(pendingNetworkAction);
            pendingNetworkAction = null;
        }

        try {
            connectivityManager.unregisterNetworkCallback(networkCallback);
            isRegistered = false;
            Logger.i(context, TAG, "Network monitoring stopped");
        } catch (IllegalArgumentException e) {
            Logger.e(context, TAG, "Error unregistering callback: " + e.getMessage());
        }
    }

    /**
     * Откладывает проверку сети для стабилизации состояния
     */
    private void scheduleNetworkCheck(Activity activity, String trigger) {
        // Отменяем предыдущую отложенную проверку
        if (pendingNetworkAction != null) {
            mainHandler.removeCallbacks(pendingNetworkAction);
            Logger.d(context, TAG, "Cancelled previous scheduled check (trigger: " + trigger + ")");
        }

        // Создаём новую отложенную проверку
        pendingNetworkAction = () -> {
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                Logger.e(context, TAG, "Activity is invalid, skipping network check");
                pendingNetworkAction = null;
                return;
            }

            checkAndNotifyNetworkChange(activity);
            pendingNetworkAction = null;
        };

        mainHandler.postDelayed(pendingNetworkAction, STABILIZATION_DELAY_MS);
        Logger.d(context, TAG, "Scheduled network check in " + STABILIZATION_DELAY_MS + "ms (trigger: " + trigger + ")");
    }

    /**
     * Проверяет реальное состояние сети и уведомляет об изменениях
     */
    private void checkAndNotifyNetworkChange(Activity activity) {
        boolean currentState = checkInternetSync();
        String stateKey = currentState ? "connected" : "disconnected";

        Logger.d(context, TAG, String.format(
                "Network check - Current: %b, Previous: %b, StateKey: %s, LastProcessed: %s",
                currentState, isCurrentlyConnected, stateKey, lastProcessedState));

        // Проверяем, изменилось ли состояние и не обрабатывали ли мы уже это состояние
        if (currentState == isCurrentlyConnected && stateKey.equals(lastProcessedState)) {
            Logger.d(context, TAG, "Network state stable and already processed - no action needed");
            return;
        }

        // Состояние изменилось - обновляем и уведомляем
        boolean previousState = isCurrentlyConnected;
        isCurrentlyConnected = currentState;
        lastProcessedState = stateKey;

        Logger.i(context, TAG, String.format("Network state changed: %b -> %b", previousState, currentState));

        // Показываем уведомление только при реальном изменении
        notifyUser(activity, previousState, currentState);
    }

    /**
     * Уведомляет пользователя об изменении состояния сети
     */
    private void notifyUser(Activity activity, boolean wasConnected, boolean isConnected) {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastToast = currentTime - lastToastTime;

        // Определяем сообщение
        int messageResId = -1;
        boolean shouldShowToast = false;

        if (!isConnected) {
            // Потеря интернета
            if (timeSinceLastToast >= MIN_TOAST_INTERVAL_MS) {
                messageResId = R.string.network_no_internet;
                shouldShowToast = true;
                Logger.w(context, TAG, "Internet LOST - showing notification");
            } else {
                Logger.d(context, TAG, "Internet LOST - notification suppressed (too frequent)");
            }
        }
        else if (!wasConnected && isConnected) {
            // Восстановление интернета
            if (timeSinceLastToast >= MIN_TOAST_INTERVAL_MS) {
                messageResId = R.string.network_restored;
                shouldShowToast = true;
                Logger.i(context, TAG, "Internet RESTORED - showing notification");
            } else {
                Logger.d(context, TAG, "Internet RESTORED - notification suppressed (too frequent)");
            }
        }
        else if (wasConnected && isConnected) {
            // Интернет был и остаётся
            Logger.d(context, TAG, "Internet remains connected - no notification");
        }

        // Показываем тост если нужно
        if (shouldShowToast && messageResId != -1) {
            lastToastTime = currentTime;
            showToastOnUiThread(activity, messageResId);
        }
    }

    /**
     * Синхронная проверка наличия интернета
     */
    private boolean checkInternetSync() {
        if (connectivityManager == null) {
            Logger.e(context, TAG, "ConnectivityManager is null");
            return false;
        }

        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            Logger.d(context, TAG, "No active network");
            return false;
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (capabilities == null) {
            Logger.d(context, TAG, "No network capabilities");
            return false;
        }

        boolean hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        boolean isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

        boolean result = hasInternet && isValidated;
        Logger.d(context, TAG, String.format(
                "Internet check - HasInternet: %b, Validated: %b, Result: %b",
                hasInternet, isValidated, result));

        return result;
    }

    /**
     * Показывает Toast в UI потоке
     */
    private void showToastOnUiThread(Activity activity, int messageResId) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            Logger.e(context, TAG, "Cannot show toast - activity invalid");
            return;
        }

        activity.runOnUiThread(() -> {
            try {
                String message = activity.getString(messageResId);
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                Logger.d(context, TAG, "Toast shown: " + message);
            } catch (Exception e) {
                Logger.e(context, TAG, "Failed to show toast: " + e.getMessage());
            }
        });
    }

    /**
     * Возвращает текущее состояние интернета
     */
    public boolean isInternetConnected() {
        return checkInternetSync();
    }

    /**
     * Быстрая проверка для внешнего использования
     */
    public boolean hasInternet() {
        return isCurrentlyConnected;
    }
}