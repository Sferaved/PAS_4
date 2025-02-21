package com.taxi_pas_4.utils.pusher;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.ui.finish.fragm.FinishSeparateFragment;
import com.taxi_pas_4.ui.visicom.VisicomFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PusherManager {
    private static final String PUSHER_APP_KEY = "a10fb0bff91153d35f36"; // Ваш ключ
    private static final String PUSHER_CLUSTER = "mt1"; // Ваш кластер
    private static final String CHANNEL_NAME = "teal-towel-48"; // Канал

    private final String eventUid;
    private final String eventOrder;
    private final String eventStatus;
    private final String eventCanceled;
    private final Pusher pusher;
    private boolean isSubscribed = false;
    public PusherManager(String eventSuffix, String userEmail) {
        this.eventUid = "order-status-updated-" + eventSuffix + "-" + userEmail;
        this.eventOrder = "order-" + eventSuffix + "-" + userEmail;
        this.eventStatus = "transactionStatus-" + eventSuffix + "-" + userEmail;
        this.eventCanceled = "eventCanceled-" + eventSuffix + "-" + userEmail;

        PusherOptions options = new PusherOptions();
        options.setCluster(PUSHER_CLUSTER);

        pusher = new Pusher(PUSHER_APP_KEY, options);
    }

    // Подключение к Pusher
    public void connect() {
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i("Pusher", "State changed from " + change.getPreviousState() + " to " + change.getCurrentState());
                if (change.getCurrentState() == ConnectionState.CONNECTED) {
                    Log.i("Pusher", "Successfully connected to Pusher");
                }
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.e("Pusher", "Error connecting: " + message + " (Code: " + code + ")", e);
            }
        }, ConnectionState.ALL);
    }

    // Подписка на канал и событие
    public void subscribeToChannel() {
        if (isSubscribed) return; // Предотвращает повторную подписку
        isSubscribed = true;

        Channel channel = pusher.subscribe(CHANNEL_NAME);
        Log.i("Pusher", "Subscribing to channel: " + CHANNEL_NAME);
        Log.i("Pusher", "Subscribing to event: " + eventUid);
        Log.i("Pusher", "Subscribing to eventOrder: " + eventOrder);
        Log.i("Pusher", "Subscribing to eventStatus: " + eventStatus);
        Log.i("Pusher", "Subscribing to eventCanceled: " + eventCanceled);

        // Обработка события
        channel.bind(eventUid, event -> {
            Log.i("Pusher", "Received event: " + event.toString());
            try {
                JSONObject eventData = new JSONObject(event.getData());
                String orderUid = eventData.getString("order_uid");

                Log.i("Pusher", "Order UID: " + orderUid);
                // Переключаемся на главный поток для обновления UI и переменной
                new Handler(Looper.getMainLooper()).post(() -> {
                    MainActivity.uid = orderUid;

                    if (FinishSeparateFragment.btn_cancel_order != null) {
                        FinishSeparateFragment.btn_cancel_order.setEnabled(true);
                        FinishSeparateFragment.btn_cancel_order.setClickable(true);
                    } else {
                        Log.e("Pusher", "btn_cancel_order is null!");
                    }
                });

            } catch (JSONException e) {
                Log.e("Pusher", "JSON Parsing error", e);
            }
        });

        // Привязка к Pusher каналу
        channel.bind(eventStatus, event -> {
            Log.i("Pusher", "Received event: " + event.toString());

            try {
                JSONObject eventData = new JSONObject(event.getData());
                String transactionStatus = eventData.getString("transactionStatus");
                Log.i("Pusher", "Parsed transactionStatus: " + transactionStatus);

                // Проверка на null перед переключением на главный поток
                new Handler(Looper.getMainLooper()).post(() -> {
                    Log.d("Pusher", "Updating UI with status: " + transactionStatus);

                    // Установка начального статуса транзакции
                    FinishSeparateFragment.viewModel.setTransactionStatus(transactionStatus);
                    Log.i("Transaction", "Initial transaction status set: " + transactionStatus);

                    // Проверка UI элемента перед взаимодействием
                    if (FinishSeparateFragment.btn_cancel_order != null) {
                        FinishSeparateFragment.btn_cancel_order.setEnabled(true);
                        FinishSeparateFragment.btn_cancel_order.setClickable(true);
                        Log.d("Pusher", "Cancel button enabled successfully");
                    } else {
                        Log.e("Pusher", "btn_cancel_order is null when updating status: " + transactionStatus);
                    }
                });

            } catch (JSONException e) {
                Log.e("Pusher", "JSON Parsing error for event: " + event.getData(), e);
            } catch (Exception e) {
                Log.e("Pusher", "Unexpected error processing Pusher event", e);
            }
        });
 // Привязка к Pusher каналу
        channel.bind(eventCanceled, event -> {
            Log.i("Pusher", "Received event: " + event.toString());

            try {
                JSONObject eventData = new JSONObject(event.getData());
                String canceled = eventData.getString("canceled");
                Log.i("Pusher", "Parsed eventCanceled: " + canceled);

                // Проверка на null перед переключением на главный поток
                new Handler(Looper.getMainLooper()).post(() -> {
                    Log.d("Pusher", "Updating UI with eventCanceled: " + canceled);

                    // Установка начального статуса транзакции
                    FinishSeparateFragment.viewModel.setCanceledStatus(canceled);
                    Log.i("Transaction", "Initial canceled set: " + canceled);

                    // Проверка UI элемента перед взаимодействием
                    if (FinishSeparateFragment.btn_cancel_order != null) {
                        FinishSeparateFragment.btn_cancel_order.setVisibility(View.GONE);
                        FinishSeparateFragment.btn_again.setVisibility(View.VISIBLE);
                        Log.d("Pusher", "Cancel button enabled successfully");
                    } else {
                        Log.e("Pusher", "btn_cancel_order is null when updating  canceled status: " + canceled);
                    }
                });

            } catch (JSONException e) {
                Log.e("Pusher", "JSON Parsing error for event: " + event.getData(), e);
            } catch (Exception e) {
                Log.e("Pusher", "Unexpected error processing Pusher event", e);
            }
        });

        // Обработка события
        channel.bind(eventOrder, event -> {
            Log.i("Pusher", "Received eventOrder: " + event.toString());
            try {
                // Преобразуем данные события в JSONObject
                JSONObject eventData = new JSONObject(event.getData());

                // Создаём Map для хранения данных
                Map<String, String> eventValues = new HashMap<>();
                // Добавляем данные в Map
                eventValues.put("from_lat", eventData.optString("from_lat", "null"));
                eventValues.put("from_lng", eventData.optString("from_lng", "null"));
                eventValues.put("lat", eventData.optString("lat", "null"));
                eventValues.put("lng", eventData.optString("lng", "null"));
                eventValues.put("dispatching_order_uid", eventData.optString("dispatching_order_uid", "null"));
                eventValues.put("order_cost", eventData.optString("order_cost", "0"));
                eventValues.put("currency", eventData.optString("currency", "null"));
                eventValues.put("routefrom", eventData.optString("routefrom", "null"));
                eventValues.put("routefromnumber", eventData.optString("routefromnumber", "null"));
                eventValues.put("routeto", eventData.optString("routeto", "null"));
                eventValues.put("to_number", eventData.optString("to_number", "null"));
                eventValues.put("required_time", eventData.optString("required_time", ""));
                eventValues.put("flexible_tariff_name", eventData.optString("flexible_tariff_name", "null"));
                eventValues.put("comment_info", eventData.optString("comment_info", ""));
                eventValues.put("extra_charge_codes", eventData.optString("extra_charge_codes", ""));

                // Добавляем дополнительные поля, если они существуют

                String dispatchingOrderUidDouble = eventData.optString("dispatching_order_uid_Double", " ");
                eventValues.put("dispatching_order_uid_Double", dispatchingOrderUidDouble.equals(" ") ? " " : dispatchingOrderUidDouble);


                VisicomFragment.sendUrlMap = eventValues;
                // Логируем успешный случай
                    Log.i("Pusher", "Event Values: " + eventValues.toString());

            } catch (JSONException e) {
                // Логируем ошибку при парсинге JSON
                Log.e("Pusher", "JSON Parsing error", e);

                // Добавляем ошибку в Map
                Map<String, String> errorValues = new HashMap<>();
                errorValues.put("order_cost", "0");
                errorValues.put("message", "JSON Parsing error");
                Log.e("Pusher", "Error Values: " + errorValues.toString());
            }
        });

    }


    // Отключение
    public void disconnect() {
        if (pusher != null) {
            pusher.disconnect();
        }
    }

}
