package com.taxi_pas_4.ui.finish.fragm;

import static android.content.Context.MODE_PRIVATE;

import static org.chromium.base.ThreadUtils.runOnUiThread;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.taxi_pas_4.databinding.FragmentFinishBinding;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;
import com.taxi_pas_4.ui.finish.ApiClient;
import com.taxi_pas_4.ui.finish.ApiService;
import com.taxi_pas_4.ui.finish.BonusResponse;
import com.taxi_pas_4.ui.finish.FinishActivity;
import com.taxi_pas_4.ui.finish.OrderResponse;
import com.taxi_pas_4.ui.finish.Status;
import com.taxi_pas_4.ui.fondy.gen_signatur.SignatureClient;
import com.taxi_pas_4.ui.fondy.gen_signatur.SignatureResponse;
import com.taxi_pas_4.ui.fondy.payment.ApiResponsePay;
import com.taxi_pas_4.ui.fondy.payment.MyBottomSheetCardPayment;
import com.taxi_pas_4.ui.fondy.payment.PaymentApi;
import com.taxi_pas_4.ui.fondy.payment.RequestData;
import com.taxi_pas_4.ui.fondy.payment.StatusRequestPay;
import com.taxi_pas_4.ui.fondy.payment.SuccessResponseDataPay;
import com.taxi_pas_4.ui.fondy.payment.UniqueNumberGenerator;
import com.taxi_pas_4.ui.fondy.token_pay.ApiResponseToken;
import com.taxi_pas_4.ui.fondy.token_pay.PaymentApiToken;
import com.taxi_pas_4.ui.fondy.token_pay.RequestDataToken;
import com.taxi_pas_4.ui.fondy.token_pay.StatusRequestToken;
import com.taxi_pas_4.ui.fondy.token_pay.SuccessResponseDataToken;
import com.taxi_pas_4.utils.bottom_sheet.MyBottomSheetErrorFragment;
import com.taxi_pas_4.utils.bottom_sheet.MyBottomSheetErrorPaymentFragment;
import com.taxi_pas_4.utils.bottom_sheet.MyBottomSheetMessageFragment;
import com.taxi_pas_4.ui.mono.MonoApi;
import com.taxi_pas_4.ui.mono.payment.RequestPayMono;
import com.taxi_pas_4.ui.mono.payment.ResponsePayMono;
import com.taxi_pas_4.ui.wfp.checkStatus.StatusResponse;
import com.taxi_pas_4.ui.wfp.checkStatus.StatusService;
import com.taxi_pas_4.ui.wfp.invoice.InvoiceResponse;
import com.taxi_pas_4.ui.wfp.invoice.InvoiceService;
import com.taxi_pas_4.ui.wfp.purchase.PurchaseResponse;
import com.taxi_pas_4.ui.wfp.purchase.PurchaseService;
import com.taxi_pas_4.ui.wfp.revers.ReversResponse;
import com.taxi_pas_4.ui.wfp.revers.ReversService;
import com.taxi_pas_4.utils.LocaleHelper;
import com.taxi_pas_4.utils.animation.car.CarProgressBar;
import com.taxi_pas_4.utils.log.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;




public class FinishFragment extends Fragment {

    private static final String TAG = "FinishFragment";

    private FragmentFinishBinding binding;
    Activity context;
    FragmentManager fragmentManager;
    View root;
    @SuppressLint("StaticFieldLeak")
    public static TextView text_status;

    public static String baseUrl = "https://m.easy-order-taxi.site";
    Map<String, String> receivedMap;
    public static String uid;
    Thread thread;
    String pay_method;

    public static String amount;
    @SuppressLint("StaticFieldLeak")
    public static TextView text_full_message;
    String messageResult;
    public static String messageFondy;
    public static String uid_Double;
    @SuppressLint("StaticFieldLeak")
    public static AppCompatButton btn_reset_status;
    @SuppressLint("StaticFieldLeak")
    public static AppCompatButton btn_cancel_order;
    @SuppressLint("StaticFieldLeak")
    public static AppCompatButton btn_again;
    @SuppressLint("StaticFieldLeak")
    public static AppCompatButton btn_cancel;
    public static Runnable myRunnable;
    public static Runnable runnableBonusBtn;
    public static Handler handler, handlerBonusBtn,  handlerStatus;
    public static Runnable myTaskStatus;

    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;
    @SuppressLint("StaticFieldLeak")
    public static  String email;
    @SuppressLint("StaticFieldLeak")
    public static  String phoneNumber;
    private boolean cancel_btn_click = false;
    long delayMillisStatus;
    private static boolean no_pay;
    private AppCompatButton btnCallAdmin;
    private Bundle arguments;
    private CarProgressBar carProgressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFinishBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        fragmentManager = getChildFragmentManager();
        context = requireActivity();

        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        progressBar = root.findViewById(R.id.progress_bar);
        pay_method = logCursor(MainActivity.TABLE_SETTINGS_INFO).get(4);
        Logger.d(context, TAG, "onCreate: " + pay_method);

        btnCallAdmin = root.findViewById(R.id.btnCallAdmin);
        btnCallAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            List<String> stringList = logCursor(MainActivity.CITY_INFO);
            String phone = stringList.get(3);
            intent.setData(Uri.parse(phone));
            startActivity(intent);
        });
        messageFondy =  context.getString(R.string.fondy_message);
        email = logCursor(MainActivity.TABLE_USER_INFO).get(3);
        phoneNumber = logCursor(MainActivity.TABLE_USER_INFO).get(2);

        arguments = getArguments();
        assert arguments != null;
        messageResult = arguments.getString("messageResult_key");
        String no_pay_key = arguments.getString("card_payment_key");
        no_pay = no_pay_key != null && no_pay_key.equals("no");

        receivedMap = (HashMap<String, String>) arguments.getSerializable("sendUrlMap");

        assert receivedMap != null;
        amount = receivedMap.get("order_cost") + "00";

        Logger.d(context, TAG, "onCreate: receivedMap" + receivedMap.toString());
        text_full_message = root.findViewById(R.id.text_full_message);
        text_full_message.setText(messageResult);

        uid = arguments.getString("UID_key");
        uid_Double = receivedMap.get("dispatching_order_uid_Double");

        text_status = root.findViewById(R.id.text_status);

        text_status.setText( context.getString(R.string.status_checkout_message));
        btn_reset_status = root.findViewById(R.id.btn_reset_status);
        btn_reset_status.setOnClickListener(v -> {
            if(connected()){
                statusOrderWithDifferentValue(uid);
            } else {
                MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment( context.getString(R.string.verify_internet));
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
            }
        });
        statusOrderWithDifferentValue(uid);
        btn_cancel_order = root.findViewById(R.id.btn_cancel_order);
        long delayMillis = 5 * 60 * 1000;

        if (pay_method.equals("wfp_payment")) {
            amount = receivedMap.get("order_cost");
        }


        if (pay_method.equals("bonus_payment") && !no_pay) {
            handlerBonusBtn = new Handler();
            fetchBonus();
        }

        handler = new Handler();

        if (pay_method.equals("bonus_payment") || pay_method.equals("wfp_payment") || pay_method.equals("fondy_payment") || pay_method.equals("mono_payment") ) {
            handlerBonusBtn = new Handler();

            runnableBonusBtn = () -> {
                MainActivity.order_id = null;
                String newStatus = text_status.getText().toString();
                if(!newStatus.contains( context.getString(R.string.time_out_text))
                        || !newStatus.contains( context.getString(R.string.error_payment_card))
                        || !newStatus.contains( context.getString(R.string.double_order_error))
                        || !newStatus.contains( context.getString(R.string.call_btn_cancel)) ) {
                    String cancelText = context.getString(R.string.status_checkout_message);
                    text_status.setText(cancelText);

                } else {
                    text_status.setText(newStatus);
                }
                btn_cancel_order.setOnClickListener(v -> {
                    cancel_btn_click = true;

                    carProgressBar.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(Intent.ACTION_DIAL);

                    List<String> stringList = logCursor(MainActivity.CITY_INFO);
                    String phone = stringList.get(3);
                    intent.setData(Uri.parse(phone));
                    startActivity(intent);
                });
                btn_cancel_order.setText( context.getString(R.string.help_button));
               
                
                
                
                progressBar.setVisibility(View.GONE);

            };
            handlerBonusBtn.postDelayed(runnableBonusBtn, delayMillis);
        }

        handlerStatus = new Handler();
        delayMillisStatus = 5 * 1000;
        myTaskStatus = new Runnable() {
            @Override
            public void run() {
                // Ваша логика
                statusOrderWithDifferentValue(uid);
                // Запланировать повторное выполнение
                handlerStatus.postDelayed(this, delayMillisStatus);
            }
        };

        // Запускаем цикл
        startCycle();

        // Запланируйте выполнение задачи

        if (pay_method.equals("fondy_payment") || pay_method.equals("mono_payment")|| pay_method.equals("wfp_payment")) {
            MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
            callOrderIdMemory(MainActivity.order_id, uid, pay_method);
            myRunnable = () -> {
                MainActivity.order_id = null;
                String newStatus = text_status.getText().toString();
                if(!newStatus.contains( context.getString(R.string.time_out_text))
                        || !newStatus.contains( context.getString(R.string.error_payment_card))
                        || !newStatus.contains( context.getString(R.string.double_order_error))
                        || !newStatus.contains( context.getString(R.string.call_btn_cancel)) ) {
                    String cancelText = context.getString(R.string.status_checkout_message);
                    text_status.setText(cancelText);

                } else {
                    text_status.setText(newStatus);
                }
                
                btn_cancel_order.setText( context.getString(R.string.help_button));
               
                
                
                
                progressBar.setVisibility(View.GONE);
                btn_cancel_order.setOnClickListener(v -> {
                    cancel_btn_click = true;
                    btn_cancel_order.setVisibility(View.GONE);
                    carProgressBar.setVisibility(View.INVISIBLE);
                    
                    progressBar.setVisibility(View.GONE);

                    handlerBonusBtn.removeCallbacks(runnableBonusBtn);

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    List<String> stringList = logCursor(MainActivity.CITY_INFO);
                    String phone = stringList.get(3);
                    intent.setData(Uri.parse(phone));
                    startActivity(intent);
                });
            };
            handler.postDelayed(myRunnable, delayMillis);
        }
        btn_cancel_order.setOnClickListener(v -> {
            cancel_btn_click = true;

            progressBar.setVisibility(View.VISIBLE);

            handler.removeCallbacks(myRunnable);
            if(connected()){

                if(!uid_Double.equals(" ")) {
                    cancelOrderDouble();
                } else{
                    cancelOrder(uid);
                }
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                text_status.setText(R.string.verify_internet);

            }
           
            
            
            

        });

        btn_again = root.findViewById(R.id.btn_again);
        btn_again.setOnClickListener(v -> {
            MainActivity.order_id = null;
            updateAddCost(String.valueOf(0));
            if(connected()){
                startActivity(new Intent(context, MainActivity.class));
            } else {
                MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment( context.getString(R.string.verify_internet));
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
            }
        });

        


        if(!no_pay) {
            switch (pay_method) {
                case "wfp_payment":
                    try {
                        payWfp();
                    } catch (UnsupportedEncodingException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        throw new RuntimeException(e);
                    }
                    break;
                case "fondy_payment":
                    try {
                        payFondy();
                    } catch (UnsupportedEncodingException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        throw new RuntimeException(e);
                    }
                    break;
                case "mono_payment":
                    String reference = MainActivity.order_id;
                    String comment =  context.getString(R.string.fondy_message);

                    getUrlToPaymentMono(amount, reference, comment);
                    break;

            }
        }
        ImageButton btn_no = root.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(view -> startActivity(new Intent(context, MainActivity.class)));

        carProgressBar = root.findViewById(R.id.carProgressBar);

        // Запустить анимацию
        carProgressBar.resumeAnimation();

        return root;
    }
    private void startCycle() {
        handlerStatus.post(myTaskStatus);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        super.onPause();

        if (handler != null) {
            handler.removeCallbacks(myRunnable);
        }
        if (handlerBonusBtn != null) {
            handlerBonusBtn.removeCallbacks(runnableBonusBtn);
        }
        if (handlerStatus != null) {
            handlerStatus.removeCallbacks(myTaskStatus);
        }
    }

    /**
     * Wfp
     */
    @SuppressLint("Range")
    private void payWfp() throws UnsupportedEncodingException {
        String rectoken = getCheckRectoken(MainActivity.TABLE_WFP_CARDS);
        Logger.d(context, TAG, "payWfp: rectoken " + rectoken);
        amount = receivedMap.get("order_cost");
        if (rectoken.isEmpty()) {
            getUrlToPaymentWfp();
        } else {
            paymentByTokenWfp(messageFondy, amount, rectoken);
        }

    }
    //"transactionStatus":"InProcessing"
    private void getUrlToPaymentWfp() {
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

        InvoiceService service = retrofit.create(InvoiceService.class);
        List<String> stringList = logCursor(MainActivity.CITY_INFO);
        String city = stringList.get(1);

        stringList = logCursor(MainActivity.TABLE_USER_INFO);
        String userEmail = stringList.get(3);
        String phone_number = stringList.get(2);

        Call<InvoiceResponse> call = service.createInvoice(
                 context.getString(R.string.application),
                city,
                MainActivity.order_id,
                Integer.parseInt(amount),
                LocaleHelper.getLocale(),
                messageFondy,
                userEmail,
                phone_number
        );

        call.enqueue(new Callback<InvoiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<InvoiceResponse> call, @NonNull Response<InvoiceResponse> response) {
                Logger.d(context, TAG, "onResponse: 1111" + response.code());

                if (response.isSuccessful()) {
                    InvoiceResponse invoiceResponse = response.body();

                    if (invoiceResponse != null) {
                        String checkoutUrl = invoiceResponse.getInvoiceUrl();
                        Logger.d(context, TAG, "onResponse: Invoice URL: " + checkoutUrl);
                        if(checkoutUrl != null) {
                            MyBottomSheetCardPayment bottomSheetDialogFragment = new MyBottomSheetCardPayment(
                                    checkoutUrl,
                                    amount,
                                    uid,
                                    uid_Double,
                                    context
                            );
                            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                        } else {
                            Logger.d(context, TAG,"Response body is null");
                            MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                            callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                            MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("wfp_payment", messageFondy, amount, context);
                            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                        }
                    } else {
                        Logger.d(context, TAG,"Response body is null");
                        MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                        callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                        MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("wfp_payment", messageFondy, amount, context);
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }
                } else {
                    Logger.d(context, TAG, "Request failed: " + response.code());
                    MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                    callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                    MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("wfp_payment", messageFondy, amount, context);
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                }
            }

            @Override
            public void onFailure(@NonNull Call<InvoiceResponse> call, @NonNull Throwable t) {
                Logger.d(context, TAG, "Request failed: " + t.getMessage());
                MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("wfp_payment", messageFondy, amount, context);
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
            }
        });
        
        
        
        
        progressBar.setVisibility(View.GONE);
    }

    private void paymentByTokenWfp(
            String orderDescription,
            String amount,
            String rectoken
    ) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        PurchaseService service = retrofit.create(PurchaseService.class);
        List<String> stringList = logCursor(MainActivity.CITY_INFO);
        String city = stringList.get(1);

        Call<PurchaseResponse> call = service.purchase(
                context.getString(R.string.application),
                city,
                MainActivity.order_id,
                amount,
                orderDescription,
                email,
                phoneNumber,
                rectoken
        );
        call.enqueue(new Callback<PurchaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<PurchaseResponse> call, @NonNull Response<PurchaseResponse> response) {
                if (response.isSuccessful()) {
                    PurchaseResponse purchaseResponse = response.body();
                    if (purchaseResponse != null) {
                        // Обработка ответа
                        Logger.d(context, TAG, "onResponse:purchaseResponse " + purchaseResponse);
                        getStatusWfp();
                    } else {
                        // Ошибка при парсинге ответа
                        Logger.d(context, TAG, "Ошибка при парсинге ответа");
                        MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                        callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                        MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("wfp_payment", messageFondy, amount, context);
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }
                } else {
                    // Ошибка запроса
                    Logger.d(context, TAG, "Ошибка запроса");
                    MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                    callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                    MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("wfp_payment", messageFondy, amount, context);
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PurchaseResponse> call, @NonNull Throwable t) {
                // Ошибка при выполнении запроса
                Logger.d(context, TAG, "Ошибка при выполнении запроса");
                MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("wfp_payment", messageFondy, amount, context);
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
            }
        });

    }

    private void getStatusWfp() {
        Logger.d(context, TAG, "getStatusWfp: ");
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
                    assert statusResponse != null;
                    String orderStatus = statusResponse.getTransactionStatus();
                    Logger.d(context, TAG, "Transaction Status: " + orderStatus);

                    switch (orderStatus) {
                        case "Approved":
                        case "WaitingAuthComplete":
                            break;
                        default:
                            MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                            callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                            MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("wfp_payment", FinishActivity.messageFondy, amount, context);
                            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                    }
                } else {
                    getReversWfp(city);
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                getReversWfp(city);
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
                amount
        );
        call.enqueue(new Callback<ReversResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReversResponse> call, @NonNull Response<ReversResponse> response) {
                if (response.isSuccessful()) {
                    ReversResponse statusResponse = response.body();
                    assert statusResponse != null;
                    if (statusResponse.getReasonCode() == 1100) {
                        Logger.d(context, TAG, "Transaction Status: " + statusResponse.getTransactionStatus());
                        // Другие данные можно также получить из statusResponse
                    } else {
                        Logger.d(context, TAG, "Response body is null");
                        Logger.d(context, TAG,"Response body is null");
                        MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                        callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                        MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("wfp_payment", messageFondy, amount, context);
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());


                    }
                } else {
                    Logger.d(context, TAG, "Request failed: " + response.code());
                    Logger.d(context, TAG,"Response body is null");
                    MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                    callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                    MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("wfp_payment", messageFondy, amount, context);
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                }

            }

            @Override
            public void onFailure(@NonNull Call<ReversResponse> call, @NonNull Throwable t) {
//                dismiss();
                Logger.d(context, TAG, "Request failed: " + t.getMessage());
            }
        });

    }
    /**
     * payFondy
     * @throws UnsupportedEncodingException
     */

    @SuppressLint("Range")
    private void payFondy() throws UnsupportedEncodingException {


        String rectoken = getCheckRectoken(MainActivity.TABLE_FONDY_CARDS);
        Logger.d(context, TAG, "payFondy: rectoken " + rectoken);
        if (rectoken.isEmpty()) {
            getUrlToPaymentFondy(messageFondy, amount);
        } else {
            paymentByTokenFondy(messageFondy, amount, rectoken);
        }

    }
    private void paymentByTokenFondy(
            String orderDescription,
            String amount,
            String rectoken
    ) throws UnsupportedEncodingException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pay.fondy.eu/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
        callOrderIdMemory(MainActivity.order_id, uid, pay_method);
        PaymentApiToken paymentApi = retrofit.create(PaymentApiToken.class);
        List<String>  arrayList = logCursor(MainActivity.CITY_INFO);
        String MERCHANT_ID = arrayList.get(6);

//        String merchantPassword = arrayList.get(7);
        List<String> stringList = logCursor(MainActivity.TABLE_USER_INFO);
        String email = stringList.get(3);

        String order_id =  MainActivity.order_id;

        Map<String, String> params = new TreeMap<>();
        params.put("order_id", order_id);
        params.put("order_desc", orderDescription);
        params.put("currency", "UAH");
        params.put("amount", amount);
        params.put("rectoken", rectoken);
        params.put("merchant_id", MERCHANT_ID);
        params.put("preauth", "Y");
        params.put("sender_email", email);

        StringBuilder paramsBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (paramsBuilder.length() > 0) {
                paramsBuilder.append("&");
            }
            paramsBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        String queryString = paramsBuilder.toString();




        Logger.d(context, TAG, "paymentByTokenFondy: " + rectoken);

        Logger.d(context, TAG, "getStatusFondy: " + params);
        SignatureClient signatureClient = new SignatureClient();
// Передаем экземпляр SignatureCallback в метод generateSignature
        signatureClient.generateSignature(queryString, new SignatureClient.SignatureCallback() {
            @Override
            public void onSuccess(SignatureResponse response) {
                // Обработка успешного ответа
                String digest = response.getDigest();
                Logger.d(context, TAG, "Received signature digest: " + digest);

                RequestDataToken paymentRequest = new RequestDataToken(
                        order_id,
                        orderDescription,
                        amount,
                        MERCHANT_ID,
                        digest,
                        rectoken,
                        email
                );


                StatusRequestToken statusRequest = new StatusRequestToken(paymentRequest);
                Logger.d(context, TAG, "getUrlToPayment: " + statusRequest);

                Call<ApiResponseToken<SuccessResponseDataToken>> call = paymentApi.makePayment(statusRequest);


                call.enqueue(new Callback<ApiResponseToken<SuccessResponseDataToken>>() {

                    @Override
                    public void onResponse(@NonNull Call<ApiResponseToken<SuccessResponseDataToken>> call, Response<ApiResponseToken<SuccessResponseDataToken>> response) {
                        Logger.d(context, TAG, "onResponse: 1111" + response.code());
                        if (response.isSuccessful()) {
                            ApiResponseToken<SuccessResponseDataToken> apiResponse = response.body();

                            Logger.d(context, TAG, "onResponse: " +  new Gson().toJson(apiResponse));
                            try {
                                SuccessResponseDataToken responseBody = response.body().getResponse();

                                // Теперь у вас есть объект ResponseBodyRev для обработки
                                if (responseBody != null) {
                                    Logger.d(context, TAG, "JSON Response: " + new Gson().toJson(apiResponse));
                                    String orderStatus = responseBody.getOrderStatus();
                                    if (!"approved".equals(orderStatus)) {
                                        // Обработка ответа об ошибке
                                        String errorResponseMessage = responseBody.getErrorMessage();
                                        String errorResponseCode = responseBody.getErrorCode();
                                        Logger.d(context, TAG, "onResponse: errorResponseMessage " + errorResponseMessage);
                                        Logger.d(context, TAG, "onResponse: errorResponseCode" + errorResponseCode);

//                                Toast.makeText(context, R.string.pay_failure_mes, Toast.LENGTH_SHORT).show();
                                        MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                                        callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                                        MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("fondy_payment", messageFondy, amount, context);
                                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                                    }
                                } else {
//                            Toast.makeText(context, R.string.pay_failure_mes, Toast.LENGTH_SHORT).show();
                                    MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                                    callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                                    MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("fondy_payment", messageFondy, amount, context);
                                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

//                            getUrlToPaymentFondy(messageFondy, amount);
                                }
                            } catch (JsonSyntaxException e) {
                                // Возникла ошибка при разборе JSON, возможно, сервер вернул неправильный формат ответа
                                FirebaseCrashlytics.getInstance().recordException(e);
                                Logger.d(context, TAG, "Error parsing JSON response: " + e.getMessage());
//                        Toast.makeText(context, R.string.pay_failure_mes, Toast.LENGTH_SHORT).show();
                                MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                                callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                                MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("fondy_payment", messageFondy, amount, context);
                                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
//                        getUrlToPaymentFondy(messageFondy, amount);
                            }
                        } else {
                            // Обработка ошибки
                            Logger.d(context, TAG, "onFailure: " + response.code());
//                    Toast.makeText(context, R.string.pay_failure_mes, Toast.LENGTH_SHORT).show();
                            MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                            callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                            MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("fondy_payment", messageFondy, amount, context);
                            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
//                    getUrlToPaymentFondy(messageFondy, amount);
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponseToken<SuccessResponseDataToken>> call, @NonNull Throwable t) {
                        Logger.d(context, TAG, "onFailure1111: " + t);
//                Toast.makeText(context, R.string.pay_failure_mes, Toast.LENGTH_SHORT).show();

                        MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                        callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                        MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("fondy_payment", messageFondy, amount, context);
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
//                getUrlToPaymentFondy(messageFondy, amount);
                    }
                });
            }


            @Override
            public void onError(String error) {
                // Обработка ошибки

                Logger.d(context, TAG, "Received signature error: " + error);
            }
        });





    }
    @SuppressLint("Range")
    private String getCheckRectoken(String table) {
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);

        String[] columns = {"rectoken"}; // Указываем нужное поле
        String selection = "rectoken_check = ?";
        String[] selectionArgs = {"1"};
        String result = "";

        Cursor cursor = database.query(table, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    result = cursor.getString(cursor.getColumnIndex("rectoken"));
                    Logger.d(context, TAG, "Found rectoken with rectoken_check = 1" + ": " + result);
                    return result;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        database.close();

        logTableContent(table);

        return result;
    }
    private void logTableContent(String table) {
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);

        String[] columns = {"rectoken_check", "merchant", "rectoken"}; // Укажите все необходимые поля
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = database.query(table, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String rectokenCheck = cursor.getString(cursor.getColumnIndex("rectoken_check"));
                    @SuppressLint("Range") String merchant = cursor.getString(cursor.getColumnIndex("merchant"));
                    @SuppressLint("Range") String rectoken = cursor.getString(cursor.getColumnIndex("rectoken"));

                    Logger.d(context, TAG, "rectoken_check: " + rectokenCheck + ", merchant: " + merchant + ", rectoken: " + rectoken);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        database.close();
    }


    private void getUrlToPaymentFondy(String orderDescription, String amount) throws UnsupportedEncodingException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pay.fondy.eu/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        List<String>  arrayList = logCursor(MainActivity.CITY_INFO);
        String MERCHANT_ID = arrayList.get(6);

        String email = logCursor(MainActivity.TABLE_USER_INFO).get(3);

        String order_id = MainActivity.order_id;

        Map<String, String> params = new TreeMap<>();
        params.put("order_id", order_id);
        params.put("order_desc", orderDescription);
        params.put("currency", "UAH");
        params.put("amount", amount);
        params.put("preauth", "Y");
        params.put("required_rectoken", "Y");
        params.put("merchant_id", MERCHANT_ID);
        params.put("sender_email", email);
        params.put("server_callback_url", "https://m.easy-order-taxi.site/server-callback");

        Logger.d(context, TAG, "getStatusFondy: " + params);
        SignatureClient signatureClient = new SignatureClient();
// Передаем экземпляр SignatureCallback в метод generateSignature

        StringBuilder paramsBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (paramsBuilder.length() > 0) {
                paramsBuilder.append("&");
            }
            paramsBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        String queryString = paramsBuilder.toString();


        signatureClient.generateSignature(queryString, new SignatureClient.SignatureCallback() {
            @Override
            public void onSuccess(SignatureResponse response) {
                // Обработка успешного ответа
                String digest = response.getDigest();
                Logger.d(context, TAG, "Received signature digest: " + digest);

                RequestData paymentRequest = new RequestData(
                        order_id,
                        orderDescription,
                        amount,
                        MERCHANT_ID,
                        digest,
                        email
                );


                StatusRequestPay statusRequest = new StatusRequestPay(paymentRequest);
                Logger.d(context, TAG, "getUrlToPayment: " + statusRequest);

                Call<ApiResponsePay<SuccessResponseDataPay>> call = paymentApi.makePayment(statusRequest);

                call.enqueue(new Callback<ApiResponsePay<SuccessResponseDataPay>>() {

                    @Override
                    public void onResponse(@NonNull Call<ApiResponsePay<SuccessResponseDataPay>> call, Response<ApiResponsePay<SuccessResponseDataPay>> response) {
                        Logger.d(context, TAG, "onResponse: 1111" + response.code());

                        if (response.isSuccessful()) {
                            ApiResponsePay<SuccessResponseDataPay> apiResponse = response.body();

                            Logger.d(context, TAG, "onResponse: " +  new Gson().toJson(apiResponse));
                            try {
                                SuccessResponseDataPay responseBody = response.body().getResponse();

                                // Теперь у вас есть объект ResponseBodyRev для обработки
                                if (responseBody != null) {
                                    String responseStatus = responseBody.getResponseStatus();
                                    String checkoutUrl = responseBody.getCheckoutUrl();
                                    if ("success".equals(responseStatus)) {
                                        // Обработка успешного ответа

                                        MyBottomSheetCardPayment bottomSheetDialogFragment = new MyBottomSheetCardPayment(
                                                checkoutUrl,
                                                amount,
                                                uid,
                                                uid_Double,
                                                context
                                        );
                                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                                    } else {
                                        MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                                        callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                                        MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("fondy_payment", messageFondy, amount, context);
                                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                                    }
                                } else {
                                    // Обработка пустого тела ответа

                                    MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                                    callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                                    MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("fondy_payment", messageFondy, amount, context);
                                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                                }
                            } catch (JsonSyntaxException e) {
                                // Возникла ошибка при разборе JSON, возможно, сервер вернул неправильный формат ответа
                                Logger.d(context, TAG, "Error parsing JSON response: " + e.getMessage());
                                FirebaseCrashlytics.getInstance().recordException(e);

                                MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                                callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                                MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("fondy_payment", messageFondy, amount, context);
                                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                            }
                        } else {
                            // Обработка ошибки
                            Logger.d(context, TAG, "onFailure: " + response.code());

                            MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                            callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                            MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("fondy_payment", messageFondy, amount, context);
                            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponsePay<SuccessResponseDataPay>> call, Throwable t) {
                        Logger.d(context, TAG, "onFailure1111: " + t);
                        MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
                        callOrderIdMemory(MainActivity.order_id, uid, pay_method);
                        MyBottomSheetErrorPaymentFragment bottomSheetDialogFragment = new MyBottomSheetErrorPaymentFragment("fondy_payment", messageFondy, amount, context);
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }
                });
            }
            @Override
            public void onError(String error) {
                // Обработка ошибки
                Logger.d(context, TAG, "Received signature error: " + error);
            }
        });
         progressBar.setVisibility(View.GONE);
    }


    private void getUrlToPaymentMono(String amount, String reference, String comment) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.monobank.ua/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MonoApi monoApi = retrofit.create(MonoApi.class);
        int amountMono = Integer.parseInt(amount);
        RequestPayMono paymentRequest = new RequestPayMono(
                amountMono,
                reference,
                comment
        );

        Logger.d(context, TAG, "getUrlToPayment: " + paymentRequest);

        String token = context.getString(R.string.mono_key_storage); // Получение токена из ресурсов
        Call<ResponsePayMono> call = monoApi.invoiceCreate(token, paymentRequest);

        call.enqueue(new Callback<ResponsePayMono>() {

            @Override
            public void onResponse(@NonNull Call<ResponsePayMono> call, Response<ResponsePayMono> response) {
                Logger.d(context, TAG, "onResponse: 1111" + response.code());
                if (response.isSuccessful()) {
                    ResponsePayMono apiResponse = response.body();

                    Logger.d(context, TAG, "onResponse: " +  new Gson().toJson(apiResponse));
                    try {
                        assert response.body() != null;
                        String pageUrl = response.body().getPageUrl();
                        MainActivity.invoiceId = response.body().getInvoiceId();

                        MyBottomSheetCardPayment bottomSheetDialogFragment = new MyBottomSheetCardPayment(
                                pageUrl,
                                amount,
                                uid,
                                uid_Double,
                                context
                        );
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                    } catch (JsonSyntaxException e) {
                        // Возникла ошибка при разборе JSON, возможно, сервер вернул неправильный формат ответа
                        Logger.d(context, TAG, "Error parsing JSON response: " + e.getMessage());
                        FirebaseCrashlytics.getInstance().recordException(e);
                        cancelOrderDouble();
                    }
                } else {
                    // Обработка ошибки
                    Logger.d(context, TAG, "onFailure: " + response.code());
                    cancelOrderDouble();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponsePayMono> call, @NonNull Throwable t) {
                Logger.d(context, TAG, "onFailure1111: " + t);
                cancelOrderDouble();
            }


        });
    }
    private void updateAddCost(String addCost) {
        ContentValues cv = new ContentValues();
        Logger.d(context, TAG, "updateAddCost: addCost" + addCost);
        cv.put("addCost", addCost);

        // обновляем по id
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        database.update(MainActivity.TABLE_SETTINGS_INFO, cv, "id = ?",
                new String[] { "1" });
        database.close();
    }

    private void fetchBonus() {
        String url = baseUrl + "/bonusBalance/recordsBloke/" + uid + "/" +  context.getString(R.string.application);
        Call<BonusResponse> call = ApiClient.getApiService().getBonus(url);
        Logger.d(context, TAG, "fetchBonus: " + url);
        call.enqueue(new Callback<BonusResponse>() {
            @Override
            public void onResponse(@NonNull Call<BonusResponse> call, @NonNull Response<BonusResponse> response) {
                BonusResponse bonusResponse = response.body();
                if (response.isSuccessful()) {

                    assert bonusResponse != null;
                    String bonus = String.valueOf(bonusResponse.getBonus());
                    String message =  context.getString(R.string.block_mes) + " " + bonus + " " +  context.getString(R.string.bon);

                    MyBottomSheetMessageFragment bottomSheetDialogFragment = new MyBottomSheetMessageFragment(message);
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());

                } else {
                    MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment( context.getString(R.string.verify_internet));
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BonusResponse> call, @NonNull Throwable t) {
                // Обработка ошибок сети или других ошибок
                FirebaseCrashlytics.getInstance().recordException(t);
            }
        });
    }
    private void fetchCarFound() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl) // Замените BASE_URL на ваш базовый URL сервера
                .addConverterFactory(GsonConverterFactory.create())
                .build();

// Создайте экземпляр ApiServiceMapbox
        ApiService apiService = retrofit.create(ApiService.class);

// Вызов метода startNewProcessExecutionStatus с передачей параметров
        Call<Void> call = apiService.startNewProcessExecutionStatus(
                receivedMap.get("doubleOrder")
        );
        String url = call.request().url().toString();
        Logger.d(context, TAG, "URL запроса: " + url);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Обработайте ошибку при выполнении запроса
                FirebaseCrashlytics.getInstance().recordException(t);
            }
        });

    }

    public static void callOrderIdMemory(String orderId, String uid, String paySystem) {
        if(!no_pay) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);
            Call<Void> call = apiService.orderIdMemory(orderId, uid, paySystem);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    // Обработка ошибки
                    FirebaseCrashlytics.getInstance().recordException(t);
                }
            });
        }

    }
    private boolean connected() {

        boolean hasConnect = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            hasConnect = true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            hasConnect = true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            hasConnect = true;
        }

        return hasConnect;
    }
    @SuppressLint("Range")
    private List<String> logCursor(String table) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
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
        assert c != null;
        c.close();
        return list;
    }
    private void cancelOrder(String value) {
        List<String> listCity = logCursor(MainActivity.CITY_INFO);
        String city = listCity.get(1);
        String api = listCity.get(2);

        String url = baseUrl + "/" + api + "/android/webordersCancel/" + value + "/" + city  + "/" +  context.getString(R.string.application);

        Call<Status> call = ApiClient.getApiService().cancelOrder(url);
        Logger.d(context, TAG, "cancelOrderWithDifferentValue cancelOrderUrl: " + url);
        text_status.setText(R.string.sent_cancel_message);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(@NonNull Call<Status> call, @NonNull Response<Status> response) {
                if (response.isSuccessful()) {
                    Status status = response.body();
                    assert status != null;
                    Logger.d(context, TAG, "cancelOrder status: " + status);
                } else {
                    // Обработка неуспешного ответа
                    text_status.setText(R.string.verify_internet);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Status> call, @NonNull Throwable t) {
                // Обработка ошибок сети или других ошибок
                String errorMessage = t.getMessage();
                Logger.d(context, TAG, "onFailure: " + errorMessage);
                text_status.setText(R.string.verify_internet);
            }
        });
        progressBar.setVisibility(View.GONE);
    }
    private void cancelOrderDouble() {
        List<String> listCity = logCursor(MainActivity.CITY_INFO);
        String city = listCity.get(1);
        String api = listCity.get(2);

        String url = baseUrl + "/" + api + "/android/webordersCancelDouble/" + uid+ "/" + uid_Double + "/" + pay_method + "/" + city  + "/" +  context.getString(R.string.application);

        Call<Status> call = ApiClient.getApiService().cancelOrderDouble(url);
        Logger.d(context, TAG, "cancelOrderDouble: " + url);
        text_status.setText(R.string.sent_cancel_message);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(@NonNull Call<Status> call, @NonNull Response<Status> response) {
                if (response.isSuccessful()) {
                    Status status = response.body();
                    assert status != null;
                    Logger.d(context, TAG, "cancelOrderDouble status: " + status);
                } else {
                    // Обработка неуспешного ответа
                    text_status.setText(R.string.verify_internet);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Status> call, @NonNull Throwable t) {
                // Обработка ошибок сети или других ошибок
                String errorMessage = t.getMessage();
                Logger.d(context, TAG, "onFailure: " + errorMessage);
                text_status.setText(R.string.verify_internet);
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    public void statusOrderWithDifferentValue(String value) {
//        String message = text_status.getText().toString();
//        Logger.d(context, TAG, "message: " + message);
//        String canceledMessage = context.getString(R.string.ex_st_canceled);
//        Logger.d(context, TAG, "Expected message: " + canceledMessage);
//
//        if (message.equals(canceledMessage)) {
//            Logger.d(context, TAG, "Condition met, hiding elements");
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Logger.d(context, TAG, "Before hiding: carProgressBar visibility: " + carProgressBar.getVisibility());
//                    btn_cancel_order.setVisibility(View.GONE);
//                    carProgressBar.setVisibility(View.GONE);
//                    Logger.d(context, TAG, "After hiding: carProgressBar visibility: " + carProgressBar.getVisibility());
//                    handlerStatus.removeCallbacks(myTaskStatus);
//                }
//            });
//        } else {
//            Logger.d(context, TAG, "Condition not met, elements not hidden");
            List<String> listCity = logCursor(MainActivity.CITY_INFO);
            String city = listCity.get(1);
            String api = listCity.get(2);

            String url = baseUrl + "/" + api + "/android/historyUIDStatus/" + value + "/" + city  + "/" +  context.getString(R.string.application);

            Call<OrderResponse> call = ApiClient.getApiService().statusOrder(url);
            Logger.d(context, TAG, "/android/historyUIDStatus/: " + url);

            // Выполняем запрос асинхронно
            call.enqueue(new Callback<OrderResponse>() {
                @Override
                public void onResponse(@NonNull Call<OrderResponse> call, @NonNull Response<OrderResponse> response) {
                    if (response.isSuccessful()) {
                        // Получаем объект OrderResponse из успешного ответа
                        OrderResponse orderResponse = response.body();

                        // Далее вы можете использовать полученные данные из orderResponse
                        // например:
                        assert orderResponse != null;
                        String executionStatus = orderResponse.getExecutionStatus();
                        String orderCarInfo = orderResponse.getOrderCarInfo();
                        String driverPhone = orderResponse.getDriverPhone();
                        String requiredTime = orderResponse.getRequiredTime();
                        int closeReason = orderResponse.getCloseReason();
                        if (requiredTime != null && !requiredTime.isEmpty()) {
                            requiredTime = formatDate (orderResponse.getRequiredTime());
                        }

                        String message;
                        // Обработка различных вариантов executionStatus
                        switch (executionStatus) {
                            case "WaitingCarSearch":
                            case "SearchesForCar":
                                delayMillisStatus = 5 * 1000;
                                if(!cancel_btn_click) {
                                    message =  context.getString(R.string.ex_st_0);
                                } else {
                                    message =  context.getString(R.string.checkout_status);
                                }
                                btn_cancel_order.setVisibility(View.VISIBLE);
                                carProgressBar.setVisibility(View.VISIBLE);
                                break;
                            case "Canceled":
                                btn_cancel_order.setVisibility(View.GONE);
                                carProgressBar.setVisibility(View.GONE);
                                delayMillisStatus = 30 * 1000;
                                String newStatus = text_status.getText().toString();
                                if(closeReason == -1) {
                                    delayMillisStatus = 5 * 1000;
                                    message =  context.getString(R.string.status_checkout_message);
                                } else {
                                    if(!newStatus.contains( context.getString(R.string.time_out_text))
                                            || !newStatus.contains( context.getString(R.string.error_payment_card))
                                            || !newStatus.contains( context.getString(R.string.double_order_error))
                                            || !newStatus.contains( context.getString(R.string.call_btn_cancel)) ) {
                                        message =  context.getString(R.string.ex_st_canceled);
                                        carProgressBar.setVisibility(View.GONE);
                                    } else {
                                        message = newStatus;
                                    }
                                }
                                if (handlerStatus != null) {
                                    handlerStatus.removeCallbacks(myTaskStatus);
                                }
                                break;
                            case "CarFound":
                                btn_cancel_order.setVisibility(View.VISIBLE);
                                carProgressBar.setVisibility(View.GONE);
                                if(!cancel_btn_click) {
                                    delayMillisStatus = 30 * 1000;
                                    // Формируем сообщение с учетом возможных пустых значений переменных
                                    StringBuilder messageBuilder = new StringBuilder( context.getString(R.string.ex_st_2));

                                    if (orderCarInfo != null && !orderCarInfo.isEmpty()) {
                                        messageBuilder.append( context.getString(R.string.ex_st_3)).append(orderCarInfo);
                                    }

                                    if (driverPhone != null && !driverPhone.isEmpty()) {
                                        Logger.d(context, TAG, "onResponse:driverPhone " + driverPhone);
                                        btn_reset_status.setText( context.getString(R.string.phone_driver));
                                        btn_reset_status.setOnClickListener(v -> {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + driverPhone));
                                            startActivity(intent);
                                        });
                                        messageBuilder.append( context.getString(R.string.ex_st_4)).append(driverPhone);
                                    }

                                    if (requiredTime != null && !requiredTime.isEmpty()) {
                                        messageBuilder.append( context.getString(R.string.ex_st_5)).append(requiredTime);
                                    }
                                    message = messageBuilder.toString();
                                } else {
                                    message =  context.getString(R.string.ex_st_canceled);
                                }

                                break;
                            default:
                                btn_cancel_order.setVisibility(View.VISIBLE);
                                carProgressBar.setVisibility(View.VISIBLE);
                                delayMillisStatus = 30 * 1000;
                                message =  context.getString(R.string.status_checkout_message);
                                break;
                        }
                        progressBar.setVisibility(View.GONE);
                        text_status.setText(message);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                }
            });
//        }



    }

    private String formatDate (String requiredTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        // Формат для вывода в украинской локализации
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("uk", "UA"));
        // Преобразуем строку в объект Date
        Date date = null;
        try {
            date = inputFormat.parse(requiredTime);
        } catch (ParseException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Logger.d(context, TAG, "onCreate:" + new RuntimeException(e));
        }

        // Форматируем дату и время в украинском формате
        return outputFormat.format(date);

    }

}