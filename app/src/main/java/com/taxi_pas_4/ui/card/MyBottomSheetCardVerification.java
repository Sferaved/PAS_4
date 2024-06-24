package com.taxi_pas_4.ui.card;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;
import com.taxi_pas_4.ui.fondy.callback.CallbackResponse;
import com.taxi_pas_4.ui.fondy.callback.CallbackService;
import com.taxi_pas_4.ui.fondy.gen_signatur.SignatureClient;
import com.taxi_pas_4.ui.fondy.gen_signatur.SignatureResponse;
import com.taxi_pas_4.ui.fondy.revers.ApiResponseRev;
import com.taxi_pas_4.ui.fondy.revers.ReversApi;
import com.taxi_pas_4.ui.fondy.revers.ReversRequestData;
import com.taxi_pas_4.ui.fondy.revers.ReversRequestSent;
import com.taxi_pas_4.ui.fondy.revers.SuccessResponseDataRevers;
import com.taxi_pas_4.ui.fondy.status.ApiResponse;
import com.taxi_pas_4.ui.fondy.status.FondyApiService;
import com.taxi_pas_4.ui.fondy.status.StatusRequest;
import com.taxi_pas_4.ui.fondy.status.StatusRequestBody;
import com.taxi_pas_4.ui.fondy.status.SuccessfulResponseData;
import com.taxi_pas_4.ui.home.MyBottomSheetErrorFragment;
import com.taxi_pas_4.ui.mono.MonoApi;
import com.taxi_pas_4.ui.mono.cancel.RequestCancelMono;
import com.taxi_pas_4.ui.mono.cancel.ResponseCancelMono;
import com.taxi_pas_4.ui.mono.status.ResponseStatusMono;
import com.taxi_pas_4.ui.payment_system.PayApi;
import com.taxi_pas_4.ui.payment_system.ResponsePaySystem;
import com.taxi_pas_4.ui.wfp.checkStatus.StatusResponse;
import com.taxi_pas_4.ui.wfp.checkStatus.StatusService;
import com.taxi_pas_4.ui.wfp.revers.ReversResponse;
import com.taxi_pas_4.ui.wfp.revers.ReversService;
import com.taxi_pas_4.ui.wfp.token.CallbackResponseWfp;
import com.taxi_pas_4.ui.wfp.token.CallbackServiceWfp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyBottomSheetCardVerification extends BottomSheetDialogFragment {
    private WebView webView;
    private String TAG = "MyBottomSheetCardVerification";
    private String checkoutUrl;
    private String amount;
    private AppCompatButton btnOk;
    String email;
    String pay_method;
    static SQLiteDatabase database;
    private final String baseUrl = "https://m.easy-order-taxi.site";
    Activity context;
    private NavController navController;
    String city;
    FragmentManager fragmentManager;
    public MyBottomSheetCardVerification(String checkoutUrl, String amount) {
        this.checkoutUrl = checkoutUrl;
        this.amount = amount;
    }

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fondy_payment, container, false);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        fragmentManager = getParentFragmentManager();
        webView = view.findViewById(R.id.webView);
        email = logCursor(MainActivity.TABLE_USER_INFO).get(3);
        context = requireActivity();
        database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        List<String> stringList = logCursor(MainActivity.CITY_INFO);
        city = stringList.get(1);
        // Настройка WebView
        webView.getSettings().setJavaScriptEnabled(true);
        // Добавляем прослушиватель событий клавиатуры
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int screenHeight = view.getRootView().getHeight();

                // Вычисляем размер видимой области экрана
                int heightDifference = screenHeight - (r.bottom - r.top);

                // Если высота разницы больше 200dp (можете подстроить под свои нужды)
                if (heightDifference > dpToPx(200)) {
                    // Поднимаем WebView
                    view.setTranslationY(-heightDifference);
                } else {
                    // Сбрасываем перевод, если клавиатура закрыта
                    view.setTranslationY(0);
                }
            }
        });

        pay_system();

        return view;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * Wfp
     */

    private void getStatusWfp() {
        Log.d(TAG, "getStatusWfp: ");
        List<String> stringList = logCursor(MainActivity.CITY_INFO);
        String city = stringList.get(1);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://m.easy-order-taxi.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        StatusService service = retrofit.create(StatusService.class);

        Call<StatusResponse> call = service.checkStatus(
                context.getString(R.string.application),
                city,
                MainActivity.order_id
        );

        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {

                if (response.isSuccessful()) {
                    StatusResponse statusResponse = response.body();
                    if (statusResponse != null) {
                        String orderStatus = statusResponse.getTransactionStatus();
                        Log.d(TAG, "Transaction Status: " + orderStatus);
                        switch (orderStatus) {
                            case "Approved":
                            case "WaitingAuthComplete":
                                getCardTokenWfp(city);
                                break;
                            default:
                                getReversWfp(city);
                                if (isAdded()) { // Проверка, что фрагмент привязан к активности перед отображением диалогового окна
                                    MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.pending));
                                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                                }
                        }


                        // Другие данные можно также получить из statusResponse
                    } else {
                        Log.d(TAG, "Response body is null");
                        getReversWfp(city);
                        if (isAdded()) { //
                            MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                        }

                    }
                } else {
                    getReversWfp(city);
                    if (isAdded()) { //
                        MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }
                    Log.d(TAG, "Request failed:");
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                getReversWfp(city);
                if (isAdded()) { //
                    MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                }
                Log.d(TAG, "Request failed:"+ t.getMessage());
            }
        });

    }

    private void getCardTokenWfp(String city) {
        Log.d(TAG, "getCardTokenWfp: ");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://m.easy-order-taxi.site") // Замените на фактический URL вашего сервера
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        // Создайте сервис
        CallbackServiceWfp service = retrofit.create(CallbackServiceWfp.class);
        Log.d(TAG, "getCardTokenWfp: ");
        // Выполните запрос
        Call<CallbackResponseWfp> call = service.handleCallbackWfp(
                context.getString(R.string.application),
                city,
                email,
                "wfp"
        );
        call.enqueue(new Callback<CallbackResponseWfp>() {
            @Override
            public void onResponse(@NonNull Call<CallbackResponseWfp> call, @NonNull Response<CallbackResponseWfp> response) {

                Log.d(TAG, "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    CallbackResponseWfp callbackResponse = response.body();
                    if (callbackResponse != null) {
                        List<CardInfo> cards = callbackResponse.getCards();
                        Log.d(TAG, "onResponse: cards" + cards);
                        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                        database.delete(MainActivity.TABLE_WFP_CARDS, "1", null);
                        if (cards != null && !cards.isEmpty()) {
                            for (CardInfo cardInfo : cards) {
                                String masked_card = cardInfo.getMasked_card(); // Маска карты
                                String card_type = cardInfo.getCard_type(); // Тип карты
                                String bank_name = cardInfo.getBank_name(); // Название банка
                                String rectoken = cardInfo.getRectoken(); // Токен карты
                                String merchant = cardInfo.getMerchant(); // Токен карты

                                Log.d(TAG, "onResponse: card_token: " + rectoken);
                                ContentValues cv = new ContentValues();
                                cv.put("masked_card", masked_card);
                                cv.put("card_type", card_type);
                                cv.put("bank_name", bank_name);
                                cv.put("rectoken", rectoken);
                                cv.put("merchant", merchant);
                                cv.put("rectoken_check", "0");
                                database.insert(MainActivity.TABLE_WFP_CARDS, null, cv);
                            }
                            Cursor cursor = database.rawQuery("SELECT * FROM " + MainActivity.TABLE_WFP_CARDS + " ORDER BY id DESC LIMIT 1", null);
                            if (cursor != null && cursor.moveToFirst()) {
                                // Получаем значение ID последней записи
                                @SuppressLint("Range") int lastId = cursor.getInt(cursor.getColumnIndex("id"));
                                cursor.close();

                                // Обновляем строку с найденным ID
                                ContentValues cv = new ContentValues();
                                cv.put("rectoken_check", "1");
                                database.update(MainActivity.TABLE_WFP_CARDS, cv, "id = ?", new String[] { String.valueOf(lastId) });
                            }

                            database.close();
                        }
                    }
                    navController.navigate(R.id.nav_card);
                } else {
                    if (isAdded()) {
                        MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }


                }
                getReversWfp(city);
            }

            @Override
            public void onFailure(@NonNull Call<CallbackResponseWfp> call, @NonNull Throwable t) {
                // Обработка ошибки запроса
                getReversWfp(city);
                MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                Log.d(TAG, "onResponse: failure " + t.toString());
            }
        });
    }

    private void getReversWfp(String city) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://m.easy-order-taxi.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ReversService service = retrofit.create(ReversService.class);

        Call<ReversResponse> call = service.checkStatus(
                context.getString(R.string.application),
                city,
                MainActivity.order_id,
                "1"
        );
        call.enqueue(new Callback<ReversResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReversResponse> call, @NonNull Response<ReversResponse> response) {
                if (response.isSuccessful()) {
                    ReversResponse statusResponse = response.body();
                    if (statusResponse != null) {
                        Log.d(TAG, "Transaction Status: " + statusResponse.getTransactionStatus());
                        // Другие данные можно также получить из statusResponse
                    } else {
                        Log.d(TAG, "Response body is null");
                        MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                    }
                } else {
                    Log.d(TAG, "Request failed: " + response.code());
                    if (isAdded()) { //
                        MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }

                }
                navController.popBackStack();
                navController.navigate(R.id.nav_bonus);
            }

            @Override
            public void onFailure(@NonNull Call<ReversResponse> call, @NonNull Throwable t) {
                if (isAdded()) { //
                    MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    dismiss();
                }
                Log.d(TAG, "Request failed: " + t.getMessage());
            }
        });

    }


    /**
     * Fondy section
     */
    private void getStatusFondy(String signature) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pay.fondy.eu/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FondyApiService apiService = retrofit.create(FondyApiService.class);
        List<String>  arrayList = logCursor(MainActivity.CITY_INFO);
        String MERCHANT_ID = arrayList.get(6);


        StatusRequestBody requestBody = new StatusRequestBody(
                MainActivity.order_id,
                MERCHANT_ID,
                signature
        );


        StatusRequest statusRequest = new StatusRequest(requestBody);
        Log.d("TAG1", "getUrlToPayment: " + statusRequest.toString());

        Call<ApiResponse<SuccessfulResponseData>> call = apiService.checkOrderStatus(statusRequest);

        call.enqueue(new Callback<ApiResponse<SuccessfulResponseData>>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<ApiResponse<SuccessfulResponseData>> call, @NonNull Response<ApiResponse<SuccessfulResponseData>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<SuccessfulResponseData> apiResponse = response.body();
                    Log.d(TAG, "JSON Response: " + new Gson().toJson(apiResponse));
                    if (apiResponse != null) {
                        SuccessfulResponseData responseData = apiResponse.getResponse();
                        Log.d(TAG, "onResponse: " + responseData.toString());
                        // Обработка успешного ответа
                        Log.d("TAG", "getMerchantId: " + responseData.getMerchantId());
                        Log.d("TAG", "getOrderStatus: " + responseData.getOrderStatus());
                        Log.d("TAG", "getResponse_description: " + responseData.getResponseDescription());
                        String orderStatus = responseData.getOrderStatus();
                        if(orderStatus.equals("approved")){
                            getCardTokenFondy(MERCHANT_ID);
                            getReversFondy(MainActivity.order_id,context.getString(R.string.return_pay), amount);
                        };
                    }
                } else {
                    // Обработка ошибки запроса
                    Log.d("TAG", "onResponse: Ошибка запроса, код " + response.code());

                    if (isAdded()) { //
                        MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<SuccessfulResponseData>> call, @NonNull Throwable t) {
                // Обработка ошибки сети или другие ошибки
                Log.d("TAG", "onFailure: Ошибка сети: " + t.getMessage());

                MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
            }
        });

    }
    private void getCardTokenFondy(String MERCHANT_ID) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://m.easy-order-taxi.site") // Замените на фактический URL вашего сервера
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Создайте сервис
        CallbackService service = retrofit.create(CallbackService.class);
        Log.d(TAG, "getCardTokenFondy: ");
        // Выполните запрос
        Call<CallbackResponse> call = service.handleCallback(email, "fondy", MERCHANT_ID);
        call.enqueue(new Callback<CallbackResponse>() {
            @Override
            public void onResponse(@NonNull Call<CallbackResponse> call, @NonNull Response<CallbackResponse> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    CallbackResponse callbackResponse = response.body();
                    if (callbackResponse != null) {
                        List<CardInfo> cards = callbackResponse.getCards();
                        Log.d(TAG, "onResponse: cards" + cards);
                        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                        database.delete(MainActivity.TABLE_FONDY_CARDS, "1", null);
                        if (cards != null && !cards.isEmpty()) {
                            for (CardInfo cardInfo : cards) {
                                String masked_card = cardInfo.getMasked_card(); // Маска карты
                                String card_type = cardInfo.getCard_type(); // Тип карты
                                String bank_name = cardInfo.getBank_name(); // Название банка
                                String rectoken = cardInfo.getRectoken(); // Токен карты

                                Log.d(TAG, "onResponse: card_token: " + rectoken);
                                ContentValues cv = new ContentValues();
                                cv.put("masked_card", masked_card);
                                cv.put("card_type", card_type);
                                cv.put("bank_name", bank_name);
                                cv.put("rectoken", rectoken);
                                cv.put("merchant", MERCHANT_ID);
                                cv.put("rectoken_check", "0");
                                database.insert(MainActivity.TABLE_FONDY_CARDS, null, cv);
                            }
                            Cursor cursor = database.rawQuery("SELECT * FROM " + MainActivity.TABLE_FONDY_CARDS + " ORDER BY id DESC LIMIT 1", null);
                            if (cursor != null && cursor.moveToFirst()) {
                                // Получаем значение ID последней записи
                                @SuppressLint("Range") int lastId = cursor.getInt(cursor.getColumnIndex("id"));
                                cursor.close();

                                // Обновляем строку с найденным ID
                                ContentValues cv = new ContentValues();
                                cv.put("rectoken_check", "1");
                                database.update(MainActivity.TABLE_FONDY_CARDS, cv, "id = ?", new String[] { String.valueOf(lastId) });
                            }

                            database.close();
                        }
                    }

                } else {
                    // Обработка случаев, когда ответ не 200 OK
                }
            }

            @Override
            public void onFailure(@NonNull Call<CallbackResponse> call, @NonNull Throwable t) {
                // Обработка ошибки запроса
                Log.d(TAG, "onResponse: failure " + t.toString());
            }
        });
    }
    private void getReversFondy(String orderId, String comment, String amount) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pay.fondy.eu/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ReversApi apiService = retrofit.create(ReversApi.class);
        List<String>  arrayList = logCursor(MainActivity.CITY_INFO);
        String MERCHANT_ID = arrayList.get(6);
        String merchantPassword = arrayList.get(7);

        ReversRequestData reversRequestData = new ReversRequestData(
                orderId,
                comment,
                amount,
                MERCHANT_ID,
                merchantPassword
        );
        Log.d("TAG1", "getRevers: " + reversRequestData.toString());
        ReversRequestSent reversRequestSent = new ReversRequestSent(reversRequestData);


        Call<ApiResponseRev<SuccessResponseDataRevers>> call = apiService.makeRevers(reversRequestSent);

        call.enqueue(new Callback<ApiResponseRev<SuccessResponseDataRevers>>() {
            @Override
            public void onResponse(Call<ApiResponseRev<SuccessResponseDataRevers>> call, Response<ApiResponseRev<SuccessResponseDataRevers>> response) {

                if (response.isSuccessful()) {
                    ApiResponseRev<SuccessResponseDataRevers> apiResponse = response.body();
                    Log.d("TAG1", "JSON Response: " + new Gson().toJson(apiResponse));
                    if (apiResponse != null) {
                        SuccessResponseDataRevers responseData = apiResponse.getResponse();
                        Log.d("TAG1", "onResponse: " + responseData.toString());
                        if (responseData != null) {
                            // Обработка успешного ответа
                            Log.d("TAG1", "onResponse: " + responseData.toString());
//                            if (isAdded()) { // Проверяем, что фрагмент присоединен к активности
                                if (response.isSuccessful()) {
                                    if(isAdded()) {
                                        Toast.makeText(context, context.getString(R.string.link_card_succesfuly), Toast.LENGTH_SHORT).show();
                                    }


                                    ArrayList<Map<String, String>> cardMaps = getCardMapsFromDatabase();
                                    Log.d("TAG", "onResume: cardMaps" + cardMaps);
                                    if (!cardMaps.isEmpty()) {
                                        // Если массив пустой, отобразите текст "no_routs" вместо списка
                                        CardFragment.textCard.setVisibility(View.GONE);

                                        CustomCardAdapter listAdapter = new CustomCardAdapter(context, getCardMapsFromDatabase(), CardFragment.table, pay_method);
                                        CardFragment.listView.setAdapter(listAdapter);
                                        CardFragment.listView.setVisibility(View.VISIBLE);
                                        updateCardFragment();
                                    }
                                    dismiss();
//                                }
                            }
                        }
                    }
                } else {
                    // Обработка ошибки запроса
                    Log.d("TAG", "onResponse: Ошибка запроса, код " + response.code());

                    try {
                        String errorBody = response.errorBody().string();
                        Log.d("TAG", "onResponse: Тело ошибки: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponseRev<SuccessResponseDataRevers>> call, Throwable t) {
                // Обработка ошибки сети или другие ошибки
                Log.d("TAG", "onFailure: Ошибка сети: " + t.getMessage());

            }
        });

    }

    private void updateCardFragment() {
        // Проверяем, привязан ли фрагмент к активности
        if (isAdded()) {
            // Создаем новый экземпляр фрагмента
            CardFragment newFragment = new CardFragment();

            // Получаем менеджер фрагментов
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

            // Начинаем транзакцию фрагментов
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Заменяем текущий фрагмент новым
            transaction.replace(R.id.fragment_card, newFragment);

            // Добавляем транзакцию в стек возврата
            transaction.addToBackStack(null);

            // Применяем транзакцию
            transaction.commit();
        }
    }


    @SuppressLint("Range")
    private ArrayList<Map<String, String>> getCardMapsFromDatabase() {
        ArrayList<Map<String, String>> cardMaps = new ArrayList<>();
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        // Выполните запрос к таблице TABLE_FONDY_CARDS и получите данные
        Cursor cursor = database.query(MainActivity.TABLE_FONDY_CARDS, null, null, null, null, null, null);
        Log.d("TAG", "getCardMapsFromDatabase: card count: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> cardMap = new HashMap<>();
                cardMap.put("card_type", cursor.getString(cursor.getColumnIndex("card_type")));
                cardMap.put("bank_name", cursor.getString(cursor.getColumnIndex("bank_name")));
                cardMap.put("masked_card", cursor.getString(cursor.getColumnIndex("masked_card")));
                cardMap.put("rectoken", cursor.getString(cursor.getColumnIndex("rectoken")));

                cardMaps.add(cardMap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return cardMaps;
    }
    /**
     * Mono section
     */
    private void getStatusMono() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.monobank.ua/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MonoApi monoApi = retrofit.create(MonoApi.class);

        String token = context.getString(R.string.mono_key_storage); // Получение токена из ресурсов
        Call<ResponseStatusMono> call = monoApi.getInvoiceStatus(token, MainActivity.invoiceId);

        call.enqueue(new Callback<ResponseStatusMono>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<ResponseStatusMono> call, @NonNull Response<ResponseStatusMono> response) {
                if (response.isSuccessful()) {
                    ResponseStatusMono apiResponse = response.body();
                    Log.d(TAG, "JSON Response: " + new Gson().toJson(apiResponse));
                    if (apiResponse != null) {
                        String status = apiResponse.getStatus();
                        Log.d(TAG, "onResponse: " + status);
                        // Обработка успешного ответа

//                            dismiss();
//                            getCardToken();

                    }
                } else {
                    // Обработка ошибки запроса
                    Log.d("TAG", "onResponse: Ошибка запроса, код " + response.code());

                    if (isAdded()) { //
                        MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseStatusMono> call, @NonNull Throwable t) {
                // Обработка ошибки сети или другие ошибки
                Log.d("TAG", "onFailure: Ошибка сети: " + t.getMessage());

                MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
            }
        });

    }
    private void getReversMono(
            String invoiceId,
            String extRef,
            int amount
    ) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.monobank.ua/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MonoApi monoApi = retrofit.create(MonoApi.class);

        RequestCancelMono paymentRequest = new RequestCancelMono(
                invoiceId,
                extRef,
                amount
        );
        Log.d("TAG1", "getRevers: " + paymentRequest.toString());

        String token = context.getString(R.string.mono_key_storage); // Получение токена из ресурсов
        Call<ResponseCancelMono> call = monoApi.invoiceCancel(token, paymentRequest);

        call.enqueue(new Callback<ResponseCancelMono>() {
            @Override
            public void onResponse(@NonNull Call<ResponseCancelMono> call, @NonNull Response<ResponseCancelMono> response) {

                if (response.isSuccessful()) {
                    ResponseCancelMono apiResponse = response.body();
                    Log.d("TAG2", "JSON Response: " + new Gson().toJson(apiResponse));
                    if (apiResponse != null) {
                        String responseData = apiResponse.getStatus();
                        Log.d("TAG2", "onResponse: " + responseData.toString());
                        if (responseData != null) {
                            // Обработка успешного ответа

                            switch (responseData) {
                                case "processing":
                                    Log.d("TAG2", "onResponse: " + "заява на скасування знаходиться в обробці");
                                    break;
                                case "success":
                                    Log.d("TAG2", "onResponse: " + "заяву на скасування виконано успішно");
                                    break;
                                case "failure":
                                    Log.d("TAG2", "onResponse: " + "неуспішне скасування");
                                    Log.d("TAG2", "onResponse: ErrCode: " + apiResponse.getErrCode());
                                    Log.d("TAG2", "onResponse: ErrText: " + apiResponse.getErrText());
                                    break;
                            }

                        }
                    }
                } else {
                    // Обработка ошибки запроса
                    Log.d("TAG2", "onResponse: Ошибка запроса, код " + response.code());
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d("TAG2", "onResponse: Тело ошибки: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseCancelMono> call, Throwable t) {
                // Обработка ошибки сети или другие ошибки
                Log.d("TAG2", "onFailure: Ошибка сети: " + t.getMessage());
            }
        });

    }

    /**
     *
     * @param dialog the dialog that was dismissed will be passed into the
     *               method
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        CardFragment.progressBar.setVisibility(View.GONE);
        switch (pay_method) {
            case "fondy_payment":
                getReversFondy(MainActivity.order_id, context.getString(R.string.return_pay), amount);
                break;
            case "wfp_payment":
                getReversWfp(city);;
                break;
        }
    }

    private void pay_system() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PayApi apiService = retrofit.create(PayApi.class);
        Call<ResponsePaySystem> call = apiService.getPaySystem();
        call.enqueue(new Callback<ResponsePaySystem>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePaySystem> call, @NonNull Response<ResponsePaySystem> response) {
                if (response.isSuccessful()) {
                    // Обработка успешного ответа
                    ResponsePaySystem responsePaySystem = response.body();
                    assert responsePaySystem != null;
                    String paymentCode = responsePaySystem.getPay_system();

                    switch (paymentCode) {
                        case "wfp":
                            pay_method = "wfp_payment";
                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                                    Log.d("WebView", "Загружен URL: " + url);
                                    if(url.contains("https://secure.wayforpay.com/invoice")){
                                        return false;
                                    }
                                    if(url.contains("https://secure.wayforpay.com/closing")
                                    ) {
                                        getStatusWfp();
                                        return true;
                                    }
                                    return false;
                                    // Возвращаем false, чтобы разрешить WebView загрузить страницу.
                                }
                            });
                            // Ensure checkoutUrl is not null and valid before loading it
                            if (checkoutUrl != null && URLUtil.isValidUrl(checkoutUrl)) {
                                webView.loadUrl(checkoutUrl);
                            } else {
                                Log.e("MyBottomSheetCardVerification", "Checkout URL is null or invalid");
                                // Handle the error appropriately, e.g., show an error message to the user
                            }
                            break;
                        case "fondy":
                            pay_method = "fondy_payment";
                            List<String>  arrayList = logCursor(MainActivity.CITY_INFO);
                            String MERCHANT_ID = arrayList.get(6);


                            Map<String, String> params = new TreeMap<>();
                            params.put("order_id", MainActivity.order_id);
                            params.put("merchant_id", MERCHANT_ID);
                            SignatureClient signatureClient = new SignatureClient();
                            signatureClient.generateSignature(params.toString(), new SignatureClient.SignatureCallback() {
                                @Override
                                public void onSuccess(SignatureResponse response) {
                                    // Обработка успешного ответа
                                    String digest = response.getDigest();
                                    Log.d(TAG, "Received signature digest: " + digest);

                                    webView.setWebViewClient(new WebViewClient() {
                                        @Override
                                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                            Log.d("WebView", "Загружен URL: " + url);
                                            if(url.equals("https://m.easy-order-taxi.site/mono/redirectUrl")) {
                                                Log.d(TAG, "shouldOverrideUrlLoading: " + pay_method);
                                                switch (pay_method) {
                                                    case "fondy_payment":
                                                        getStatusFondy(digest);
                                                        break;
                                                    case "mono_payment":
                                                        getStatusMono();
                                                        break;
                                                }
                                                return true;
                                            } else {
                                                // Возвращаем false, чтобы разрешить WebView загрузить страницу.
                                                return false;
                                            }
                                        }
                                    });
                                    webView.loadUrl(Objects.requireNonNull(checkoutUrl));
                                }


                                @Override
                                public void onError(String error) {
                                    // Обработка ошибки

                                    Log.d(TAG, "Received signature error: " + error);
                                }
                            });
                            break;
                        case "mono":
                            pay_method = "mono_payment";
                            break;
                    }
                    if(isAdded()){
                        ContentValues cv = new ContentValues();
                        cv.put("payment_type", pay_method);
                        // обновляем по id
                        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                        database.update(MainActivity.TABLE_SETTINGS_INFO, cv, "id = ?",
                                new String[] { "1" });
                        database.close();

                    }


                } else {
                    if (isAdded()) { //
                        MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponsePaySystem> call, @NonNull Throwable t) {
                if (isAdded()) { //
                    MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(context.getString(R.string.verify_internet));
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                }
            }
        });
    }
    @SuppressLint("Range")
    public List<String> logCursor(String table) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase database = requireActivity().openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        Cursor c = database.query(table, null, null, null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                        list.add(c.getString(c.getColumnIndex(cn)));

                    }

                } while (c.moveToNext());
            }
        }
        database.close();
        return list;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if (database != null && database.isOpen()) {
            database.close();
        }
        CardFragment.progressBar.setVisibility(View.GONE);
        switch (pay_method) {
            case "fondy_payment":
                getReversFondy(MainActivity.order_id, context.getString(R.string.return_pay), amount);
                break;
            case "wfp_payment":
                getReversWfp(city);
                break;
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (database != null && database.isOpen()) {
            database.close();
        }
        CardFragment.progressBar.setVisibility(View.GONE);
        switch (pay_method) {
            case "fondy_payment":
                getReversFondy(MainActivity.order_id, context.getString(R.string.return_pay), amount);
                break;
            case "wfp_payment":
                getReversWfp(city);
                break;
        }
    }
}

