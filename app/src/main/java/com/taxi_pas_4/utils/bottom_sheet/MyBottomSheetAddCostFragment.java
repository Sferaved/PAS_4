package com.taxi_pas_4.utils.bottom_sheet;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;
import static com.taxi_pas_4.MainActivity.viewModel;
import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;
import com.taxi_pas_4.ui.finish.ApiService;
import com.taxi_pas_4.ui.finish.Status;
import com.taxi_pas_4.ui.finish.fragm.FinishSeparateFragment;
import com.taxi_pas_4.ui.fondy.payment.UniqueNumberGenerator;
import com.taxi_pas_4.ui.wfp.purchase.PurchaseResponse;
import com.taxi_pas_4.ui.wfp.purchase.PurchaseService;
import com.taxi_pas_4.utils.hold.APIHoldService;
import com.taxi_pas_4.utils.hold.HoldResponse;
import com.taxi_pas_4.utils.log.Logger;
import com.uxcam.UXCam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyBottomSheetAddCostFragment extends BottomSheetDialogFragment {

    private static final String TAG = "MyBottomSheetAddCostFragment";
    TextView textViewCost;
    AppCompatButton btn_ok, btn_minus, btn_plus;
    String cost, uid, uid_Double, pay_method;
    Context context;
    FragmentManager fragmentManager;


    public MyBottomSheetAddCostFragment(
            String cost,
            String uid,
            String uid_Double,
            String pay_method,
            Context context,
            FragmentManager fragmentManager
    ) {
        this.cost = cost;
        this.uid = uid;
        this.uid_Double = uid_Double;
        this.pay_method = pay_method;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        UXCam.tagScreenName(TAG);

        View view = inflater.inflate(R.layout.add_cost_bottom_layout, container, false);

        btn_ok = view.findViewById(R.id.btn_ok);

        btn_minus = view.findViewById(R.id.btn_minus);
        btn_plus = view.findViewById(R.id.btn_plus);
        textViewCost = view.findViewById(R.id.text_view_cost);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Начальная стоимость
        int initialCost = Integer.parseInt(cost);
        int initialAddCost = 0;
// Текущее значение стоимости
        int[] currentCost = {initialCost};
        int[] currentAddCost = {initialAddCost};

        textViewCost.setText(String.valueOf(initialCost));

        btn_minus.setOnClickListener(v -> {
            if (currentCost[0] > initialCost) {
                currentCost[0] -= 5;
                currentAddCost[0] -= 5;
                textViewCost.setText(String.valueOf(currentCost[0]));
            }
        });

        btn_plus.setOnClickListener(v -> {
            currentCost[0] += 5;
            currentAddCost[0] += 5;
            textViewCost.setText(String.valueOf(currentCost[0]));
        });
        btn_ok.setOnClickListener(v -> {
            Logger.d(getActivity(), TAG, "btn_ok: " + currentAddCost[0]);
            if(currentAddCost[0] > 0) {
                // Выключить кнопку
                FinishSeparateFragment.btn_cancel_order.setEnabled(false);
                FinishSeparateFragment.btn_cancel_order.setClickable(false);
                FinishSeparateFragment.text_status.setText(context.getString(R.string.recounting_order));
                startAddCostUpdate(
                        uid,
                        String.valueOf(currentAddCost[0])
                );
            }

            dismiss();
        });

    }
    private void startAddCostUpdate(
            String uid,
            String addCost
    ) {

        String  baseUrl = sharedPreferencesHelperMain.getValue("baseUrl", "https://m.easy-order-taxi.site") + "/";
        FinishSeparateFragment.text_status.setText(context.getString(R.string.recounting_order));
        if ("nal_payment".equals(pay_method)) {
            viewModel.setAddCostViewUpdate(addCost);
            startAddCostWithUpdate(uid, addCost, baseUrl );
       }
        if ("wfp_payment".equals(pay_method)) {
            startAddCostCardUpdate(addCost);
        }
    }
    public void startAddCostWithUpdate(String uid, String addCost, String baseUrl) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl) // Замените BASE_URL на ваш базовый URL сервера
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            Call<Status> call = apiService.startAddCostWithAddBottomUpdate(uid, addCost);
            String url = call.request().url().toString();
            Logger.d(context, TAG, "URL запроса nal_payment: " + url);

            // Выполняем асинхронный запрос
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Status> call, @NonNull Response<Status> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Status status = response.body();
                        String responseStatus = status.getResponse();
                        Logger.d(context, TAG, "startAddCostUpdate nal_payment: " + responseStatus);

                    }
                }

                @Override
                public void onFailure(@NonNull Call<Status> call, @NonNull Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Logger.d(context, TAG, "URL запроса nal_payment: " + t.getMessage());
                    // Обработать ошибку выполнения запроса
                    FirebaseCrashlytics.getInstance().recordException(t);

                }
            });

    }

    private void startAddCostCardUpdate(String addCost) {
        Logger.d(context, TAG, "startAddCostCardUpdate: ");
        String rectoken = getCheckRectoken(MainActivity.TABLE_WFP_CARDS);
        Logger.d(context, TAG, "payWfp: rectoken " + rectoken);

        FinishSeparateFragment.text_status.setText(R.string.recounting_order);

        MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);

        wfpInvoice(MainActivity.order_id , addCost, uid);
        String messageFondy = context.getString(R.string.fondy_message);
        if (!rectoken.isEmpty()) {
            paymentByTokenWfp(messageFondy, addCost, MainActivity.order_id );
        }

    }

    @SuppressLint("Range")
    private String getCheckRectoken(String table) {
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);

        String[] columns = {"rectoken"}; // Указываем нужное поле
        String selection = "rectoken_check = ?";
        String[] selectionArgs = {"1"};
        String result = "";

        Cursor cursor = database.query(table, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(cursor.getColumnIndex("rectoken"));
                Logger.d(context, TAG, "Found rectoken with rectoken_check = 1" + ": " + result);
                return result;
            } while (cursor.moveToNext());
        }
        cursor.close();

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

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String rectokenCheck = cursor.getString(cursor.getColumnIndex("rectoken_check"));
                @SuppressLint("Range") String merchant = cursor.getString(cursor.getColumnIndex("merchant"));
                @SuppressLint("Range") String rectoken = cursor.getString(cursor.getColumnIndex("rectoken"));

                Logger.d(context, TAG, "rectoken_check: " + rectokenCheck + ", merchant: " + merchant + ", rectoken: " + rectoken);
            } while (cursor.moveToNext());
        }
        cursor.close();

        database.close();
    }


    private void wfpInvoice(String orderId, String amount, String uid) {
        String  baseUrl = sharedPreferencesHelperMain.getValue("baseUrl", "https://m.easy-order-taxi.site") + "/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<Void> call = apiService.wfpInvoice(orderId, amount, uid);

        call.enqueue(new Callback<>() {
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
    private void newOrderCardPayAdd20 (
            String addCost
    ) {

        viewModel.setAddCostViewUpdate(addCost);
        String  baseUrl = sharedPreferencesHelperMain.getValue("baseUrl", "https://m.easy-order-taxi.site") + "/";


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        List<String> stringList = logCursor(MainActivity.CITY_INFO);
        String city = stringList.get(1);
        if( MainActivity.order_id == null) {
            MainActivity.order_id = UniqueNumberGenerator.generateUniqueNumber(context);
        }
        Call<Status> call = apiService.startAddCostCardBottomUpdate(
                uid,
                uid_Double,
                pay_method,
                MainActivity.order_id,
                city,
                addCost

        );
        String url = call.request().url().toString();
        Logger.d(context, TAG, "URL запроса wfp_payment: " + url);


        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Status> call, @NonNull Response<Status> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Status status = response.body();
//                    String responseStatus = status.getResponse();
//                    Logger.d(context, TAG, "startAddCostUpdate wfp_payment status: " + responseStatus);
//                    if (!responseStatus.equals("200")) {
//                        // Обработка неуспешного ответа
//                        FinishSeparateFragment.text_status.setText(responseStatus);
//                    } else {
//                        viewModel.setAddCostViewUpdate(addCost);
//                    }
//                } else {
//                    // Обработка неуспешного ответа
//                    FinishSeparateFragment.text_status.setText(R.string.verify_internet);
//                }
            }

            @Override
            public void onFailure(@NonNull Call<Status> call, @NonNull Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (FinishSeparateFragment.btn_cancel_order != null) {
                    FinishSeparateFragment.btn_cancel_order.setVisibility(VISIBLE);
                    FinishSeparateFragment.btn_cancel_order.setEnabled(true);
                    FinishSeparateFragment.btn_cancel_order.setClickable(true);
                    Logger.d(context,TAG, "Cancel button enabled successfully");
                }
            }
        });
    }

    private void paymentByTokenWfp(
            String orderDescription,
            String amount,
            String order_id
    ) {
        String  baseUrl = sharedPreferencesHelperMain.getValue("baseUrl", "https://m.easy-order-taxi.site") + "/";

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS) // Тайм-аут на соединение
                .readTimeout(30, TimeUnit.SECONDS)    // Тайм-аут на чтение данных
                .writeTimeout(30, TimeUnit.SECONDS)   // Тайм-аут на запись данных
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        PurchaseService service = retrofit.create(PurchaseService.class);
        List<String> stringList = logCursor(MainActivity.CITY_INFO);
        String city = stringList.get(1);

        stringList = logCursor(MainActivity.TABLE_USER_INFO);
        String email = stringList.get(3);
        String phoneNumber = stringList.get(2);

        Call<PurchaseResponse> call = service.purchase(
                context.getString(R.string.application),
                city,
                order_id,
                amount,
                orderDescription,
                email,
                phoneNumber
        );
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PurchaseResponse> call, @NonNull Response<PurchaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PurchaseResponse statusResponse = response.body();

                    String orderStatus = statusResponse.getTransactionStatus();
                    Logger.d(context, TAG, "1 Transaction Status: " + orderStatus);


                    switch (orderStatus) {
                        case "Approved":
                        case "WaitingAuthComplete":
                            Logger.d(context, TAG, "onResponse: Positive status received: " + orderStatus);
                            sharedPreferencesHelperMain.saveValue("pay_error", "**");
                            new Handler(Looper.getMainLooper()).post(() -> {
                                newOrderCardPayAdd20(amount);
                            });
                            break;
                       default:
                           deleteInvoice(order_id);
                           Toast.makeText(context, context.getString(R.string.pay_failure_mes), Toast.LENGTH_SHORT).show();
                           Logger.d(context, TAG, "onResponse: Unexpected status: " + orderStatus);
                    }


                } else {
                    Logger.e(context, TAG, "onResponse: Unsuccessful response, code=" + response.code());
                }
                if (FinishSeparateFragment.btn_cancel_order != null) {
                    FinishSeparateFragment.btn_cancel_order.setVisibility(VISIBLE);
                    FinishSeparateFragment.btn_cancel_order.setEnabled(true);
                    FinishSeparateFragment.btn_cancel_order.setClickable(true);
                    Logger.d(context,"Pusher eventTransactionStatus", "Cancel button enabled successfully");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PurchaseResponse> call, @NonNull Throwable t) {
                // Ошибка при выполнении запроса
                FirebaseCrashlytics.getInstance().recordException(t);
                Logger.d(context, TAG, "Ошибка при выполнении запроса");
                if (FinishSeparateFragment.btn_cancel_order != null) {
                    FinishSeparateFragment.btn_cancel_order.setVisibility(VISIBLE);
                    FinishSeparateFragment.btn_cancel_order.setEnabled(true);
                    FinishSeparateFragment.btn_cancel_order.setClickable(true);
                    Logger.d(context,"Pusher eventTransactionStatus", "Cancel button enabled successfully");
                }
            }
        });

    }
    private void deleteInvoice(String orderReference) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Создание клиента OkHttpClient с подключенным логгером
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(loggingInterceptor);
        httpClient.connectTimeout(60, TimeUnit.SECONDS); // Тайм-аут для соединения
        httpClient.readTimeout(60, TimeUnit.SECONDS);    // Тайм-аут для чтения
        httpClient.writeTimeout(60, TimeUnit.SECONDS);   // Тайм-аут для записи

        String baseUrl = (String) sharedPreferencesHelperMain.getValue("baseUrl", "https://m.easy-order-taxi.site");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build()) // Подключение клиента OkHttpClient с логгером
                .build();


        APIHoldService apiService = retrofit.create(APIHoldService.class);
        Call<HoldResponse> call = apiService.deleteInvoice(orderReference);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<HoldResponse> call, @NonNull Response<HoldResponse> response) {

            }

            @Override
            public void onFailure(@NonNull Call<HoldResponse> call, @NonNull Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
            }
        });



    }

    @SuppressLint("Range")
    private List<String> logCursor(String table) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        Cursor c = database.query(table, null, null, null, null, null, null);
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
        database.close();
        c.close();
        return list;
    }

    private OnDismissListener dismissListener;

    public interface OnDismissListener {
        void onDismiss();
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.dismissListener = listener;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss(); // Уведомляем о закрытии
        }
    }
}
