package com.taxi_pas_4.utils.web;

import android.content.Context;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.navigation.NavController;

import com.taxi_pas_4.R;

public class MyWebViewClient extends WebViewClient {
    private final String TAG = "MyWebViewClient";
    private final Context context;
    private final NavController navController;

    public MyWebViewClient(Context context, NavController navController) {
        this.context = context;
        this.navController = navController;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        Log.d(TAG, "Loading URL: " + url);

        // Добавьте свою логику обработки URL здесь
        if (url.contains("https://secure.wayforpay.com/closing")) {
            // Карта привязана, показываем Toast сообщение
            Toast.makeText(context, context.getString(R.string.aplay_card_message), Toast.LENGTH_LONG).show();

            // Программно нажимаем кнопку "Назад"
            navController.navigate(R.id.nav_visicom);

            return true; // Указываем, что мы обработали событие загрузки URL
        }

        // Возвращаем false, чтобы URL обрабатывался стандартным способом
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.d(TAG, "Finished loading URL: " + url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.e(TAG, "Error loading URL: " + request.getUrl().toString() + " Error: " + error.getDescription());
    }
}

