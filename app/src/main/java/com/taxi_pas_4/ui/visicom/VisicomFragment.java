package com.taxi_pas_4.ui.visicom;


import static android.content.Context.MODE_PRIVATE;

import static com.taxi_pas_4.utils.notify.NotificationHelper.checkForUpdate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavOptions;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;
import com.taxi_pas_4.cities.check.CityCheckActivity;
import com.taxi_pas_4.databinding.FragmentVisicomBinding;
import com.taxi_pas_4.ui.finish.ApiClient;
import com.taxi_pas_4.ui.finish.RouteResponseCancel;
import com.taxi_pas_4.utils.bottom_sheet.MyBottomSheetBonusFragment;
import com.taxi_pas_4.utils.bottom_sheet.MyBottomSheetErrorFragment;
import com.taxi_pas_4.utils.bottom_sheet.MyBottomSheetGPSFragment;
import com.taxi_pas_4.utils.bottom_sheet.MyBottomSheetGeoFragment;
import com.taxi_pas_4.utils.bottom_sheet.MyPhoneDialogFragment;
import com.taxi_pas_4.ui.open_map.OpenStreetMapActivity;
import com.taxi_pas_4.ui.visicom.visicom_search.ActivityVisicomOnePage;
import com.taxi_pas_4.utils.connect.NetworkUtils;
import com.taxi_pas_4.utils.cost_json_parser.CostJSONParserRetrofit;
import com.taxi_pas_4.utils.data.DataArr;
import com.taxi_pas_4.utils.db.DatabaseHelper;
import com.taxi_pas_4.utils.db.DatabaseHelperUid;
import com.taxi_pas_4.utils.download.AppUpdater;
import com.taxi_pas_4.utils.from_json_parser.FromJSONParserRetrofit;
import com.taxi_pas_4.utils.ip.RetrofitClient;
import com.taxi_pas_4.utils.log.Logger;
import com.taxi_pas_4.utils.preferences.SharedPreferencesHelper;
import com.taxi_pas_4.utils.tariff.DatabaseHelperTariffs;
import com.taxi_pas_4.utils.tariff.TariffInfo;
import com.taxi_pas_4.utils.to_json_parser.ToJSONParserRetrofit;
import com.taxi_pas_4.utils.user.user_verify.VerifyUserTask;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VisicomFragment extends Fragment{

    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;
    private FragmentVisicomBinding binding;
    private static final String TAG = "VisicomFragment";
    private static final int REQUEST_LOCATION_PERMISSION = 1;


    public static AppCompatButton btn_minus, btn_plus, btnOrder, buttonBonus, gpsbut, btnCallAdmin;
    @SuppressLint("StaticFieldLeak")
    public static TextView geoText;
    static String api;

    public static long firstCost;

    @SuppressLint("StaticFieldLeak")
    public static TextView text_view_cost;
    @SuppressLint("StaticFieldLeak")
    public static TextView textViewTo;
    @SuppressLint("StaticFieldLeak")
    public static EditText to_number;
    public static String numberFlagTo;

    public static long cost;
    public static long addCost;
    public static String to;
    public static String geo_marker;
    String pay_method;
    public static String urlOrder;
    public static long MIN_COST_VALUE;
    public static long firstCostForMin;

    public static AppCompatButton btnAdd, btn_clear_from_text, ubt_btn;
    @SuppressLint("StaticFieldLeak")
    static ImageButton btn1;
    @SuppressLint("StaticFieldLeak")
    static ImageButton btn2;
    @SuppressLint("StaticFieldLeak")
    static ImageButton btn3;

    @SuppressLint("StaticFieldLeak")
    public static TextView textwhere, num2;
    private AlertDialog alertDialog;
    @SuppressLint("StaticFieldLeak")
    public static TextView textfrom;
    @SuppressLint("StaticFieldLeak")
    public static TextView num1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    @SuppressLint("StaticFieldLeak")
    static ConstraintLayout linearLayout;
    Activity context;
    FragmentManager fragmentManager;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @SuppressLint("StaticFieldLeak")
    static FrameLayout frame_1;
    @SuppressLint("StaticFieldLeak")
    static FrameLayout frame_2;
    @SuppressLint("StaticFieldLeak")
    static FrameLayout frame_3;
    @SuppressLint("StaticFieldLeak")
    public static TextView schedule;
    ImageButton shed_down;
    @SuppressLint("StaticFieldLeak")
    static ConstraintLayout constr2;
    private List<RouteResponseCancel> routeListCancel;
    DatabaseHelper databaseHelper;
    DatabaseHelperUid databaseHelperUid;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVisicomBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        context = requireActivity();
        sharedPreferencesHelper = new SharedPreferencesHelper(context);

        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;

        // Устанавливаем слушатель для распознавания жеста свайпа вниз
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Метод, который нужно запустить по свайпу вниз

                startActivity(new Intent(context, MainActivity.class));

                // После завершения обновления, уберите индикатор загрузки
                swipeRefreshLayout.setRefreshing(false);
            }
        });

            fragmentManager = getParentFragmentManager();
            progressBar = binding.progressBar;
            linearLayout = binding.linearLayoutButtons;


            btnCallAdmin = binding.btnCallAdmin;
            btnCallAdmin.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                List<String> stringList = logCursor(MainActivity.CITY_INFO, requireActivity());
                String phone = stringList.get(3);
                intent.setData(Uri.parse(phone));
                startActivity(intent);
            });
            btn1 = binding.button1;
            btn2 = binding.button2;
            btn3 = binding.button3;

            frame_1 = binding.frame1;
            frame_2 = binding.frame2;
            frame_3 = binding.frame3;

        ubt_btn = binding.btn1;


        ubt_btn.setOnClickListener(v -> {
            AppUpdater appUpdater = new AppUpdater();
            Logger.d(requireActivity(), TAG, "Starting app update process");

            // Установка слушателя для обновления состояния установки
            appUpdater.setOnUpdateListener(() -> {
                // Показать пользователю сообщение о завершении обновления
                Toast.makeText(requireActivity(), R.string.update_finish_mes, Toast.LENGTH_SHORT).show();

                // Перезапуск приложения для применения обновлений
                restartApplication(requireActivity());
            });

            // Регистрация слушателя
            appUpdater.registerListener();

            // Проверка наличия обновлений
            checkForUpdate(requireActivity());
        });
        schedule = binding.schedule;
        shed_down = binding.shedDown;

        btnAdd = binding.btnAdd;
        constr2 = binding.constr2;

        constr2.setVisibility(View.INVISIBLE);

    return root;
    }

    private void scheduleUpdate() {
        schedule.setText(R.string.on_now);
        if(!MainActivity.firstStart) {
            ContentValues cv = new ContentValues();
            cv.put("time", "no_time");
            cv.put("date", "no_date");

            // обновляем по id
            SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
            database.update(MainActivity.TABLE_ADD_SERVICE_INFO, cv, "id = ?",
                    new String[] { "1" });
            database.close();
        }

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnVisible(View.INVISIBLE);
                MyBottomSheetGeoFragment bottomSheetDialogFragment = new MyBottomSheetGeoFragment(text_view_cost);
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
            }
        });

        shed_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnVisible(View.INVISIBLE);
                MyBottomSheetGeoFragment bottomSheetDialogFragment = new MyBottomSheetGeoFragment(text_view_cost);
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
            }
        });
    }

    public void updateApp() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Доступны обновления
                ubt_btn.setVisibility(View.VISIBLE);
                Logger.d(requireActivity(), TAG, "Available updates found");
            }
        });


    }



    public static void  addCheck(Context context) {

            int newCheck = 0;
            List<String> services = logCursor(MainActivity.TABLE_SERVICE_INFO, context);
            for (int i = 0; i < DataArr.arrayServiceCode().length; i++) {
                if(services.get(i+1).equals("1")) {
                    newCheck++;
                }
            }
            String mes = context.getString(R.string.add_services);
            if(newCheck != 0) {
                mes = context.getString(R.string.add_services) + " (" + newCheck + ")";
            }
            btnAdd.setText(mes);

    }


    private static void restartApplication(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

     public static void btnVisible(int visible) {
         btn_clear_from_text.setVisibility(View.INVISIBLE);
         if (visible == View.INVISIBLE) {
             progressBar.setVisibility(View.VISIBLE);
         } else {
             progressBar.setVisibility(View.GONE);
         }
         linearLayout.setVisibility(visible);


//         gpsbut.setVisibility(visible);
         btnAdd.setVisibility(visible);

         buttonBonus.setVisibility(visible);
         btn_minus.setVisibility(visible);
         text_view_cost.setVisibility(visible);
         btn_plus.setVisibility(visible);
         btnOrder.setVisibility(visible);
     }
    @Override
    public void onPause() {
        super.onPause();
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {

            progressBar.setVisibility(View.GONE);;

            btn_clear_from_text.setText(getString(R.string.try_again));
            btn_clear_from_text.setVisibility(View.VISIBLE);
            btn_clear_from_text.setOnClickListener(v -> {
                startActivity(new Intent(context, MainActivity.class));
            });
            geoText.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.GONE);;


            textwhere.setVisibility(View.INVISIBLE);
            num2.setVisibility(View.INVISIBLE);
            textViewTo.setVisibility(View.INVISIBLE);

            btnAdd.setVisibility(View.INVISIBLE);

            buttonBonus.setVisibility(View.INVISIBLE);
            btn_minus.setVisibility(View.INVISIBLE);
            text_view_cost.setVisibility(View.INVISIBLE);
            btn_plus.setVisibility(View.INVISIBLE);
            btnOrder.setVisibility(View.INVISIBLE);
        }
    }
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        Logger.d(context, TAG, "checkPermission: " + permission);
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(context, new String[]{permission}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.d(context, TAG, "onRequestPermissionsResult: " + requestCode);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0) {
                SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
                for (int i = 0; i < permissions.length; i++) {
                    editor.putInt(permissions[i], grantResults[i]);

                }
                editor.apply();

                int permissionRequestCount = loadPermissionRequestCount();

                // Увеличение счетчика запросов разрешений при необходимости
                permissionRequestCount++;

                // Сохранение обновленного значения счетчика
                savePermissionRequestCount(permissionRequestCount);
                Log.d("loadPermission", "permissionRequestCount: " + permissionRequestCount);
                // Далее вы можете загрузить сохраненные разрешения и их результаты в любом месте вашего приложения,
                // используя тот же самый объект SharedPreferences
            }
        }
    }


    // Метод для сохранения количества запросов разрешений в SharedPreferences
    private void savePermissionRequestCount(int count) {
        SharedPreferences.Editor editor = MainActivity.sharedPreferencesCount.edit();
        editor.putInt(MainActivity.PERMISSION_REQUEST_COUNT_KEY, count);
        editor.apply();
    }

    // Метод для загрузки количества запросов разрешений из SharedPreferences
    private int loadPermissionRequestCount() {
        return MainActivity.sharedPreferencesCount.getInt(MainActivity.PERMISSION_REQUEST_COUNT_KEY, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        RetrofitClient.getInstance().cancelAllRequests();

        binding = null;
    }
    @SuppressLint("Range")
    private static List<String> logCursor(String table, Context context) {
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

    private String cleanString(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("\\s+", " ").replaceAll("\\s{2,}$", " ");
    }
    private boolean newRout() {
        boolean result = false;
        progressBar.setVisibility(View.VISIBLE);
        Logger.d(context, TAG, "newRout: ");
        String query = "SELECT * FROM " + MainActivity.ROUT_MARKER + " LIMIT 1";
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();

        // Получите значения полей из первой записи

        @SuppressLint("Range") double originLatitude = cursor.getDouble(cursor.getColumnIndex("startLat"));
        @SuppressLint("Range") double toLatitude = cursor.getDouble(cursor.getColumnIndex("to_lat"));
        @SuppressLint("Range") String start = cursor.getString(cursor.getColumnIndex("start"));
        @SuppressLint("Range") String finish = cursor.getString(cursor.getColumnIndex("finish"));
        Logger.d(context, TAG, "visicomCost: start" + start);
        Logger.d(context, TAG, "visicomCost: finish" + finish);
        Logger.d(context, TAG, "visicomCost: startLat" + originLatitude);

        Logger.d(context, TAG, "visicomCost: originLatitude: " + originLatitude);
        Logger.d(context, TAG, "visicomCost: toLatitude: " + toLatitude);
        if (originLatitude == 0.0) {
            result = true;

        } else {
            if(MainActivity.firstStart) {
                result = true;
            } else {
                geoText.setText(start);
            }

        }
        if(originLatitude == toLatitude) {
            textViewTo.setText(context.getString(R.string.on_city_tv));
        } else {
            textViewTo.setText(finish);
        }
        cursor.close();
        database.close();


        return result;
    }
    @SuppressLint("Range")
    public String getTaxiUrlSearchMarkers(String urlAPI, Context context) {
        Logger.d(context, TAG, "getTaxiUrlSearchMarkers: " + urlAPI);

        String query = "SELECT * FROM " + MainActivity.ROUT_MARKER + " LIMIT 1";
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();

        // Получите значения полей из первой записи

        double originLatitude = cursor.getDouble(cursor.getColumnIndex("startLat"));
        double originLongitude = cursor.getDouble(cursor.getColumnIndex("startLan"));
        double toLatitude = cursor.getDouble(cursor.getColumnIndex("to_lat"));
        double toLongitude = cursor.getDouble(cursor.getColumnIndex("to_lng"));
        String start = cursor.getString(cursor.getColumnIndex("start"));
        String finish = cursor.getString(cursor.getColumnIndex("finish"));
        if(finish.equals(context.getString(R.string.on_city_tv))) {
            finish = start;
        }
        if(originLatitude == toLatitude) {
            textViewTo.setText(getString(R.string.on_city_tv));
        }
        Logger.d(context, TAG, "getTaxiUrlSearchMarkers: start " + start);
        Logger.d(context, TAG, "getTaxiUrlSearchMarkers: finish " + finish);

        // Заменяем символ '/' в строках
        if(start != null) {
            start = start.replace("/", "|");
        }
        if(finish != null) {
            finish = finish.replace("/", "|");
        }
        // Origin of route
        String str_origin = originLatitude + "/" + originLongitude;

        // Destination of route
        String str_dest = toLatitude + "/" + toLongitude;

        cursor.close();


        List<String> stringList = logCursor(MainActivity.TABLE_ADD_SERVICE_INFO, context);
        String time = stringList.get(1);
        String comment = stringList.get(2);
        String date = stringList.get(3);

        List<String> stringListInfo = logCursor(MainActivity.TABLE_SETTINGS_INFO, context);
        String tarif =  stringListInfo.get(2);
        String payment_type = stringListInfo.get(4);
        String addCost = stringListInfo.get(5);
        // Building the parameters to the web service

        String parameters = null;
        String phoneNumber = "no phone";
        String userEmail = logCursor(MainActivity.TABLE_USER_INFO, context).get(3);
        String displayName = logCursor(MainActivity.TABLE_USER_INFO, context).get(4);

        if(urlAPI.equals("costSearchMarkersTime")) {
            Cursor c = database.query(MainActivity.TABLE_USER_INFO, null, null, null, null, null, null);

            if (c.getCount() == 1) {
                phoneNumber = logCursor(MainActivity.TABLE_USER_INFO, context).get(2);
                c.close();
            }
            parameters = str_origin + "/" + str_dest + "/" + tarif + "/" + phoneNumber + "/"
            + displayName + " (" + context.getString(R.string.version_code) + ") " + "*" + userEmail  + "*" + payment_type + "/"
                    + time + "/" + date ;
        }
        if(urlAPI.equals("orderSearchMarkersVisicom")) {
            phoneNumber = logCursor(MainActivity.TABLE_USER_INFO, context).get(2);


            parameters = str_origin + "/" + str_dest + "/" + tarif + "/" + phoneNumber + "/"
                    + displayName + " (" + context.getString(R.string.version_code) + ") " + "*" + userEmail  + "*" + payment_type + "/" + addCost + "/"
                    + time + "/" + comment + "/" + date+ "/" + start + "/" + finish;

            ContentValues cv = new ContentValues();

            cv.put("time", "no_time");
            cv.put("comment", "no_comment");
            cv.put("date", "no_date");

            // обновляем по id
            database.update(MainActivity.TABLE_ADD_SERVICE_INFO, cv, "id = ?",
                    new String[] { "1" });

        }

        // Building the url to the web service
        List<String> services = logCursor(MainActivity.TABLE_SERVICE_INFO, context);
        List<String> servicesChecked = new ArrayList<>();
        String result;
        boolean servicesVer = false;
        for (int i = 1; i < services.size()-1 ; i++) {
            if(services.get(i).equals("1")) {
                servicesVer = true;
                break;
            }
        }
        if(servicesVer) {
            for (int i = 0; i < OpenStreetMapActivity.arrayServiceCode().length; i++) {
                if(services.get(i+1).equals("1")) {
                    servicesChecked.add(OpenStreetMapActivity.arrayServiceCode()[i]);
                }
            }
            for (int i = 0; i < servicesChecked.size(); i++) {
                if(servicesChecked.get(i).equals("CHECK_OUT")) {
                    servicesChecked.set(i, "CHECK");
                }
            }
            result = String.join("*", servicesChecked);
            Logger.d(context, TAG, "getTaxiUrlSearchGeo result:" + result + "/");
        } else {
            result = "no_extra_charge_codes";
        }

        List<String> listCity = logCursor(MainActivity.CITY_INFO, context);
        String city = listCity.get(1);
        String api = listCity.get(2);

        String url = "/" + api + "/android/" + urlAPI + "/"
                + parameters + "/" + result + "/" + city  + "/" + context.getString(R.string.application);

        database.close();

        return url;
    }
    @SuppressLint("Range")
    public void costSearchMarkersLocalTariffs(Context context) {

        String query = "SELECT * FROM " + MainActivity.ROUT_MARKER + " LIMIT 1";
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();

        // Получите значения полей из первой записи

        double originLatitude = cursor.getDouble(cursor.getColumnIndex("startLat"));
        double originLongitude = cursor.getDouble(cursor.getColumnIndex("startLan"));
        double toLatitude = cursor.getDouble(cursor.getColumnIndex("to_lat"));
        double toLongitude = cursor.getDouble(cursor.getColumnIndex("to_lng"));


        cursor.close();

        List<String> stringListInfo = logCursor(MainActivity.TABLE_SETTINGS_INFO, context);

        String payment_type = stringListInfo.get(4);

        String userEmail = logCursor(MainActivity.TABLE_USER_INFO, context).get(3);
        String displayName = logCursor(MainActivity.TABLE_USER_INFO, context).get(4);



        // Building the url to the web service
        List<String> services = logCursor(MainActivity.TABLE_SERVICE_INFO, context);
        List<String> servicesChecked = new ArrayList<>();
        String result;
        boolean servicesVer = false;
        for (int i = 1; i < services.size()-1 ; i++) {
            if(services.get(i).equals("1")) {
                servicesVer = true;
                break;
            }
        }
        if(servicesVer) {
            for (int i = 0; i < OpenStreetMapActivity.arrayServiceCode().length; i++) {
                if(services.get(i+1).equals("1")) {
                    servicesChecked.add(OpenStreetMapActivity.arrayServiceCode()[i]);
                }
            }
            for (int i = 0; i < servicesChecked.size(); i++) {
                if(servicesChecked.get(i).equals("CHECK_OUT")) {
                    servicesChecked.set(i, "CHECK");
                }
            }
            result = String.join("*", servicesChecked);
            Logger.d(context, TAG, "getTaxiUrlSearchGeo result:" + result + "/");
        } else {
            result = "no_extra_charge_codes";
        }

        List<String> listCity = logCursor(MainActivity.CITY_INFO, context);
        String city = listCity.get(1);


        String user = displayName + "*" + userEmail  + "*" + payment_type;
        database.close();

        List<String> stringList = logCursor(MainActivity.TABLE_ADD_SERVICE_INFO, context);
        String time = stringList.get(1);
        String date = stringList.get(3);


        TariffInfo tariffInfo = new TariffInfo(context);
        tariffInfo.fetchOrderCostDetails(
                originLatitude,
                originLongitude,
                toLatitude,
                toLongitude,
                user,
                time,
                date,
                result,
                city,
                context.getString(R.string.application)
        );
    }

    @SuppressLint("SetTextI18n")
    public static void readTariffInfo(Context context){
        // Создаем экземпляр класса для работы с базой данных

        try (DatabaseHelperTariffs dbHelper = new DatabaseHelperTariffs(context)) {

            progressBar.setVisibility(View.GONE);;
            String searchTariffName = "Базовый";
            List<String> finalTariffDetailsList1 = dbHelper.getTariffDetailsByFlexibleTariffName(searchTariffName, new ArrayList<>());
            Logger.d(context, TAG, "readTariffInfo 1: " + finalTariffDetailsList1);
            if (!finalTariffDetailsList1.isEmpty() && finalTariffDetailsList1.size() > 2) {

                btn1. setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        ContentValues cv = new ContentValues();
                        cv.put("tarif", "Базовый");

                        // обновляем по id
                        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                        database.update(MainActivity.TABLE_SETTINGS_INFO, cv, "id = ?",
                                new String[] { "1" });
                        database.close();
                        frame_1.setBackgroundResource(R.drawable.input);
                        frame_2.setBackgroundResource(R.drawable.buttons);
                        frame_3.setBackgroundResource(R.drawable.buttons);

                        text_view_cost.setText(finalTariffDetailsList1.get(2));

                    }
                });
            }

            if (finalTariffDetailsList1.size() > 2 && finalTariffDetailsList1.get(2).equals("0")) {
                frame_1.setVisibility(View.GONE);
            } else {
                frame_1.setVisibility(View.VISIBLE);
            }

            searchTariffName = "Универсал";

            List<String> finalTariffDetailsList2 = dbHelper.getTariffDetailsByFlexibleTariffName(searchTariffName, new ArrayList<>());
            Logger.d(context, TAG, "readTariffInfo 2: " + finalTariffDetailsList2);
            if (!finalTariffDetailsList2.isEmpty() && finalTariffDetailsList2.size() > 2) {

                btn2.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {


                        ContentValues cv = new ContentValues();
                        cv.put("tarif", "Универсал");

                        // обновляем по id
                        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                        database.update(MainActivity.TABLE_SETTINGS_INFO, cv, "id = ?",
                                new String[]{"1"});
                        database.close();

                        text_view_cost.setText(finalTariffDetailsList2.get(2));
                        frame_1.setBackgroundResource(R.drawable.buttons);
                        frame_2.setBackgroundResource(R.drawable.input);
                        frame_3.setBackgroundResource(R.drawable.buttons);
                    }
                });
            }
            if (finalTariffDetailsList2.size() > 2 && finalTariffDetailsList2.get(2).equals("0")) {
                frame_2.setVisibility(View.GONE);
            } else {
                frame_2.setVisibility(View.VISIBLE);
            }
            searchTariffName = "Микроавтобус";

            List<String> finalTariffDetailsList3 = dbHelper.getTariffDetailsByFlexibleTariffName(searchTariffName, new ArrayList<>());
            Logger.d(context, TAG, "readTariffInfo 3: " + finalTariffDetailsList3);
            if (!finalTariffDetailsList3.isEmpty() && finalTariffDetailsList3.size() > 2) {

                btn3.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {


                        ContentValues cv = new ContentValues();
                        cv.put("tarif", "Микроавтобус");

                        // обновляем по id
                        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                        database.update(MainActivity.TABLE_SETTINGS_INFO, cv, "id = ?",
                                new String[]{"1"});
                        database.close();

                        text_view_cost.setText(finalTariffDetailsList3.get(2));
                        frame_1.setBackgroundResource(R.drawable.buttons);
                        frame_2.setBackgroundResource(R.drawable.buttons);
                        frame_3.setBackgroundResource(R.drawable.input);
                    }
                });
            }
            if (finalTariffDetailsList3.size() > 2 && finalTariffDetailsList3.get(2).equals("0")) {
                frame_3.setVisibility(View.GONE);
            }   else {
                frame_3.setVisibility(View.VISIBLE);
            }
            if (!finalTariffDetailsList1.isEmpty()
                    || !finalTariffDetailsList2.isEmpty()
                    || !finalTariffDetailsList3.isEmpty()) {
                linearLayout.setVisibility(View.VISIBLE);
            }
        }


    }
    @SuppressLint("ResourceAsColor")
    private boolean orderRout() {
        boolean black_list_yes = verifyOrder(requireContext());
        Logger.d(context, TAG, "orderRout:verifyOrder(requireContext() " + black_list_yes);
        if(!black_list_yes) {
            MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(getString(R.string.black_list_message));
            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
            progressBar.setVisibility(View.GONE);;
            return false;
        } else {
            urlOrder = getTaxiUrlSearchMarkers( "orderSearchMarkersVisicom", context);
            Logger.d(context, TAG, "order:  urlOrder "  + urlOrder);
            return true;
        }

    }
    public void orderFinished() throws MalformedURLException {
        if (!verifyPhone()){
            MyPhoneDialogFragment bottomSheetDialogFragment = new MyPhoneDialogFragment(context, "visicom");
            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
            progressBar.setVisibility(View.GONE);;
        } else {
            Toast.makeText(context, R.string.check_order_mes, Toast.LENGTH_SHORT).show();
            ToJSONParserRetrofit parser = new ToJSONParserRetrofit();

            Logger.d(context, TAG, "orderFinished: "  + "https://m.easy-order-taxi.site"+ urlOrder);
            parser.sendURL(urlOrder, new Callback<Map<String, String>>() {
                @Override
                public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                    Map<String, String> sendUrlMap = response.body();

                    assert sendUrlMap != null;
                    String orderWeb = sendUrlMap.get("order_cost");
                    String message = sendUrlMap.get("message");
                    Logger.d(context, TAG, "orderFinished: message " + message);
                    assert orderWeb != null;
                    if (!orderWeb.equals("0")) {
                        String to_name;
                        if (Objects.equals(sendUrlMap.get("routefrom"), sendUrlMap.get("routeto"))) {
                            to_name = getString(R.string.on_city_tv);
                            Logger.d(context, TAG, "orderFinished: to_name 1 " + to_name);
                            if (!Objects.equals(sendUrlMap.get("lat"), "0")) {
                                insertRecordsOrders(
                                        sendUrlMap.get("routefrom"), sendUrlMap.get("routefrom"),
                                        sendUrlMap.get("routefromnumber"), sendUrlMap.get("routefromnumber"),
                                        sendUrlMap.get("from_lat"), sendUrlMap.get("from_lng"),
                                        sendUrlMap.get("from_lat"), sendUrlMap.get("from_lng"),
                                        context
                                );
                            }
                        } else {

                            if(Objects.equals(sendUrlMap.get("routeto"), "Точка на карте")) {
                                to_name = context.getString(R.string.end_point_marker);
                            } else {
                                to_name = sendUrlMap.get("routeto") + " " + sendUrlMap.get("to_number");
                            }
                            Logger.d(context, TAG, "orderFinished: to_name 2 " + to_name);
                            if (!Objects.equals(sendUrlMap.get("lat"), "0")) {
                                insertRecordsOrders(
                                        sendUrlMap.get("routefrom"), to_name,
                                        sendUrlMap.get("routefromnumber"), sendUrlMap.get("to_number"),
                                        sendUrlMap.get("from_lat"), sendUrlMap.get("from_lng"),
                                        sendUrlMap.get("lat"), sendUrlMap.get("lng"),
                                        context
                                );
                            }
                        }
                        Logger.d(context, TAG, "orderFinished: to_name 3" + to_name);
                        String to_name_local = to_name;
                        if(to_name.contains("по місту")
                                ||to_name.contains("по городу")
                                || to_name.contains("around the city")
                        ) {
                            to_name_local = getString(R.string.on_city_tv);
                        }
                        Logger.d(context, TAG, "orderFinished: to_name 4" + to_name_local);
                        String pay_method_message = getString(R.string.pay_method_message_main);

                        switch (pay_method) {
                            case "bonus_payment":
                                pay_method_message += " " + getString(R.string.pay_method_message_bonus);
                                break;
                            case "card_payment":
                            case "fondy_payment":
                            case "mono_payment":
                            case "wfp_payment":
                                pay_method_message += " " + getString(R.string.pay_method_message_card);
                                break;
                            default:
                                pay_method_message += " " + getString(R.string.pay_method_message_nal);
                        }
                        String required_time = sendUrlMap.get("required_time");
                        if(required_time != null && !required_time.contains("01.01.1970")) {
                            required_time = context.getString(R.string.time_order) + required_time + ". ";
                        } else {
                            required_time = "";
                        }

                        String messageResult = getString(R.string.thanks_message) +
                                sendUrlMap.get("routefrom") + " " + getString(R.string.to_message) +
                                to_name_local + "." +
                                required_time +
                                getString(R.string.call_of_order) + orderWeb + getString(R.string.UAH) + " " + pay_method_message;
                        messageResult = cleanString(messageResult);

                        String messageFondy = getString(R.string.fondy_message) + " " +
                                sendUrlMap.get("routefrom") + " " + getString(R.string.to_message) +
                                to_name_local + ".";


                        Logger.d(context, TAG, "orderFinished: messageResult " + messageResult);
                        Logger.d(context, TAG, "orderFinished: to_name " + to_name);

// Создайте Bundle для передачи данных
                        Bundle bundle = new Bundle();
                        bundle.putString("messageResult_key", messageResult);
                        bundle.putString("messageFondy_key", messageFondy);
                        bundle.putString("messageCost_key", orderWeb);
                        bundle.putSerializable("sendUrlMap", new HashMap<>(sendUrlMap));
                        bundle.putString("UID_key", Objects.requireNonNull(sendUrlMap.get("dispatching_order_uid")));

// Установите Bundle как аргументы фрагмента
                        MainActivity.navController.navigate(R.id.nav_finish, bundle, new NavOptions.Builder()
                                .setPopUpTo(R.id.nav_visicom, true)
                                .build());

                    } else {
                        btnVisible(View.VISIBLE);
                        assert message != null;
                        if (message.contains("Дублирование")) {
                            message = getResources().getString(R.string.double_order_error);
                            MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(message);
                            bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
                        } else if (message.equals("ErrorMessage")) {
                            message = getResources().getString(R.string.server_error_connected);
                            MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(message);
                            bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
                        } else {
                            switch (pay_method) {
                                case "bonus_payment":
                                case "card_payment":
                                case "fondy_payment":
                                case "mono_payment":
                                case "wfp_payment":
                                    changePayMethodToNal(getString(R.string.to_nal_payment));
                                    break;
                                default:
                                    message = getResources().getString(R.string.error_message);
                                    MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(message);
                                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                            }
                        }

                        btnVisible(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                    btnVisible(View.VISIBLE);
                    FirebaseCrashlytics.getInstance().recordException(t);
                }
            });
        }

    }

    private boolean verifyPhone() {

        List<String> stringList =  logCursor(MainActivity.TABLE_USER_INFO, requireActivity());

        String phone = stringList.get(2);

        Logger.d(requireActivity(), TAG, "onClick befor validate: ");
        String PHONE_PATTERN = "\\+38 \\d{3} \\d{3} \\d{2} \\d{2}";
        boolean val = Pattern.compile(PHONE_PATTERN).matcher(phone).matches();
        Logger.d(requireActivity(), TAG, "onClick No validate: " + val);
        return val;
    }

    private boolean verifyOrder(Context context) {
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.query(MainActivity.TABLE_USER_INFO, null, null, null, null, null, null);

        boolean verify = true;
        if (cursor.getCount() == 1) {

            if (logCursor(MainActivity.TABLE_USER_INFO, context).get(1).equals("0")) {
                verify = false;Log.d("TAG", "verifyOrder:verify " +verify);
            }
            cursor.close();
        }
        database.close();
        return verify;
    }

    private static void insertRecordsOrders( String from, String to,
                                             String from_number, String to_number,
                                             String from_lat, String from_lng,
                                             String to_lat, String to_lng, Context context) {
        Logger.d(context, TAG, "insertRecordsOrders: from_lat" + from_lat);
        Logger.d(context, TAG, "insertRecordsOrders: from_lng" + from_lng);
        Logger.d(context, TAG, "insertRecordsOrders: to_lat" + to_lat);
        Logger.d(context, TAG, "insertRecordsOrders: to_lng" + to_lng);

        String selection = "from_street = ?";
        String[] selectionArgs = new String[] {from};
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        Cursor cursor_from = database.query(MainActivity.TABLE_ORDERS_INFO,
                null, selection, selectionArgs, null, null, null);

        selection = "to_street = ?";
        selectionArgs = new String[] {to};

        Cursor cursor_to = database.query(MainActivity.TABLE_ORDERS_INFO,
                null, selection, selectionArgs, null, null, null);



        if (cursor_from.getCount() == 0 || cursor_to.getCount() == 0) {

            String sql = "INSERT INTO " + MainActivity.TABLE_ORDERS_INFO + " VALUES(?,?,?,?,?,?,?,?,?);";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();
            try {
                statement.clearBindings();
                statement.bindString(2, from);
                statement.bindString(3, from_number);
                statement.bindString(4, from_lat);
                statement.bindString(5, from_lng);
                statement.bindString(6, to);
                statement.bindString(7, to_number);
                statement.bindString(8, to_lat);
                statement.bindString(9, to_lng);

                statement.execute();
                database.setTransactionSuccessful();

            } finally {
                database.endTransaction();
            }

        }

        cursor_from.close();
        cursor_to.close();

    }

    private void changePayMethodMax(String textCost, String paymentType) {
        List<String> stringListCity = logCursor(MainActivity.CITY_INFO, context);

        String card_max_pay =  stringListCity.get(4);
        String bonus_max_pay =  stringListCity.get(5);
        // Инфлейтим макет для кастомного диалога
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(false);
        // Настраиваем элементы макета


        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        messageTextView.setText(R.string.max_limit_message);

        Button okButton = dialogView.findViewById(R.id.dialog_ok_button);
        okButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            switch (paymentType) {
                case "bonus_payment":
                    if (Long.parseLong(bonus_max_pay) <= Long.parseLong(textCost) * 100) {
                        paymentType();
                    }
                    break;
                case "card_payment":
                case "fondy_payment":
                case "mono_payment":
                case "wfp_payment":
                    if (Long.parseLong(card_max_pay) <= Long.parseLong(textCost)) {
                        paymentType();
                    }
                    break;
            }

            try {
                if(orderRout()) {
                    orderFinished();
                }
            } catch (MalformedURLException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                throw new RuntimeException(e);
            }
            progressBar.setVisibility(View.GONE);
            alertDialog.dismiss();
        });

        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        cancelButton.setOnClickListener(v -> {
            btnVisible(View.VISIBLE);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }



    private void changePayMethodToNal(String message) {
        // Инфлейтим макет для кастомного диалога
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(false);
        // Настраиваем элементы макета


        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        messageTextView.setText(message);

        Button okButton = dialogView.findViewById(R.id.dialog_ok_button);
        okButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            paymentType();

            try {
                if(orderRout()){
                    orderFinished();
                }
            } catch (MalformedURLException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                throw new RuntimeException(e);
            }
            progressBar.setVisibility(View.GONE);
            alertDialog.dismiss();
        });

        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void paymentType() {
        ContentValues cv = new ContentValues();
        cv.put("payment_type", "nal_payment");
        // обновляем по id
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        database.update(MainActivity.TABLE_SETTINGS_INFO, cv, "id = ?",
                new String[] { "1" });
        database.close();
        pay_method = "nal_payment";
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onResume() {
        super.onResume();
        databaseHelper = new DatabaseHelper(context);
        databaseHelperUid = new DatabaseHelperUid(context);

        new Thread(this::fetchRoutesCancel).start();

        if(!sharedPreferencesHelper.getValue("CityCheckActivity", "**").equals("run")) {
            startActivity(new Intent(getActivity(), CityCheckActivity.class));
        }

        new VerifyUserTask(context).execute();

            List<String> listCity = logCursor(MainActivity.CITY_INFO, context);
            String city = listCity.get(1);
            String cityMenu;

            switch (city){
                case "Kyiv City":
                    cityMenu = getString(R.string.city_kyiv);
                    break;
                case "Dnipropetrovsk Oblast":
                    cityMenu = getString(R.string.city_dnipro);
                    break;
                case "Odessa":
                    cityMenu = getString(R.string.city_odessa);
                    break;
                case "Zaporizhzhia":
                    cityMenu = getString(R.string.city_zaporizhzhia);
                    break;
                case "Cherkasy Oblast":
                    cityMenu = getString(R.string.city_cherkasy);
                    break;
                case "OdessaTest":
                    cityMenu = "Test";
                    break;
                default:
                    cityMenu = getString(R.string.foreign_countries);
                    break;
            }


            String newTitle =  getString(R.string.menu_city) + " " + cityMenu;
            // Изменяем текст элемента меню
            MainActivity.navVisicomMenuItem.setTitle(newTitle);
            AppCompatActivity activity = (AppCompatActivity) context;
            Objects.requireNonNull(activity.getSupportActionBar()).setTitle(newTitle);

            List<String> stringList = logCursor(MainActivity.CITY_INFO, context);
            api =  stringList.get(2);

            buttonBonus = binding.btnBonus;
            textfrom = binding.textfrom;
            num1 = binding.num1;

            addCost = 0;
            updateAddCost(String.valueOf(addCost));

            numberFlagTo = "2";

            geoText = binding.textGeo;
            geoText.setOnClickListener(v -> {
                if (fusedLocationProviderClient != null && locationCallback != null) {
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    gpsbut.setText(R.string.change);
                }
                sharedPreferencesHelper.saveValue("gps_upd", false);
                Intent intent = new Intent(getContext(), ActivityVisicomOnePage.class);
                intent.putExtra("start", "ok");
                intent.putExtra("end", "no");
                startActivity(intent);
            });

            btn_clear_from_text = binding.btnClearFromText;

            btn_clear_from_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ActivityVisicomOnePage.class);
                    intent.putExtra("start", "ok");
                    intent.putExtra("end", "no");
                    startActivity(intent);

                }
            });



            text_view_cost = binding.textViewCost;

            geo_marker = "visicom";

            Logger.d(context, TAG, "onCreateView: geo_marker " + geo_marker);

            buttonBonus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnVisible(View.INVISIBLE);
                    String costText = text_view_cost.getText().toString().trim();
                    if (!costText.isEmpty() && costText.matches("\\d+")) {
                        updateAddCost("0");
                        MyBottomSheetBonusFragment bottomSheetDialogFragment = new MyBottomSheetBonusFragment(Long.parseLong(costText), geo_marker, api, text_view_cost);
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }
                }
            });

            textViewTo = binding.textTo;
            textViewTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textViewTo.setText("");
                    Intent intent = new Intent(getContext(), ActivityVisicomOnePage.class);
                    intent.putExtra("start", "no");
                    intent.putExtra("end", "ok");
                    startActivity(intent);
                }
            });

            List<String> addresses = new ArrayList<>();

            btn_minus = binding.btnMinus;
            btn_plus = binding.btnPlus;
            btnOrder = binding.btnOrder;


            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnVisible(View.INVISIBLE);
                    MyBottomSheetGeoFragment bottomSheetDialogFragment = new MyBottomSheetGeoFragment(text_view_cost);
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                }
            });

            btn_minus.setOnClickListener(v -> {

                List<String> stringListInfo = logCursor(MainActivity.TABLE_SETTINGS_INFO, context);

                String costString = text_view_cost.getText().toString();
                if (!costString.isEmpty()) {
                    cost = Long.parseLong(costString);
                }

                String addCostString = stringListInfo.get(5);
                if (!addCostString.isEmpty()) {
                    addCost = Long.parseLong(addCostString);
                }

                cost -= 5;
                addCost -= 5;
                if (cost >= MIN_COST_VALUE) {
                    updateAddCost(String.valueOf(addCost));
                    text_view_cost.setText(String.valueOf(cost));
                }
            });

            btn_plus.setOnClickListener(v -> {
                List<String> stringListInfo = logCursor(MainActivity.TABLE_SETTINGS_INFO, context);

                String costString = text_view_cost.getText().toString();
                if (!costString.isEmpty()) {
                    cost = Long.parseLong(costString);
                }

                String addCostString = stringListInfo.get(5);
                if (!addCostString.isEmpty()) {
                    addCost = Long.parseLong(addCostString);
                }

                cost += 5;
                addCost += 5;
                updateAddCost(String.valueOf(addCost));
                text_view_cost.setText(String.valueOf(cost));
            });
            btnOrder.setOnClickListener(v -> {
                linearLayout.setVisibility(View.GONE);
                btnVisible(View.INVISIBLE);
                List<String> stringList1 = logCursor(MainActivity.CITY_INFO, context);

                sharedPreferencesHelper.saveValue("gps_upd", true);
                sharedPreferencesHelper.saveValue("gps_upd_address", true);

                pay_method =  logCursor(MainActivity.TABLE_SETTINGS_INFO, context).get(4);

                switch (stringList1.get(1)) {
                    case "Kyiv City":
                    case "Dnipropetrovsk Oblast":
                    case "Odessa":
                    case "Zaporizhzhia":
                    case "Cherkasy Oblast":
                        break;
                    case "OdessaTest":
                        if(pay_method.equals("bonus_payment")) {
                            String bonus = logCursor(MainActivity.TABLE_USER_INFO, context).get(5);
                            if(Long.parseLong(bonus) < cost * 100 ) {
                                paymentType();
                            }
                        }
                        break;
                }

                Logger.d(context, TAG, "onClick: pay_method " + pay_method );



                List<String> stringListCity = logCursor(MainActivity.CITY_INFO, context);
                String card_max_pay = stringListCity.get(4);
                Logger.d(context, TAG, "onClick:card_max_pay " + card_max_pay);

                String bonus_max_pay = stringListCity.get(5);
                switch (pay_method) {
                    case "bonus_payment":
                        if (Long.parseLong(bonus_max_pay) <= Long.parseLong(text_view_cost.getText().toString()) * 100) {
                            changePayMethodMax(text_view_cost.getText().toString(), pay_method);
                        } else {
                            if(orderRout()) {
                                try {
                                    orderFinished();
                                } catch (MalformedURLException e) {
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        break;
                    case "card_payment":
                    case "fondy_payment":
                    case "mono_payment":
                    case "wfp_payment":
                        if (Long.parseLong(card_max_pay) <= Long.parseLong(text_view_cost.getText().toString())) {
                            changePayMethodMax(text_view_cost.getText().toString(), pay_method);
                        } else {
                            if(orderRout()) {
                                try {
                                    orderFinished();
                                } catch (MalformedURLException e) {
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        break;
                    default:
                        if(orderRout()) {
                            try {
                                orderFinished();
                            } catch (MalformedURLException e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                throw new RuntimeException(e);
                            }
                        }

                }

            });

            textwhere = binding.textwhere;
            num2 = binding.num2;

            gpsbut = binding.gpsbut;
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            gpsbut.setOnClickListener(v -> {

                if (locationManager != null) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        if(loadPermissionRequestCount() >= 3 && !MainActivity.location_update) {
                            MyBottomSheetGPSFragment bottomSheetDialogFragment = new MyBottomSheetGPSFragment(getString(R.string.location_on));
                            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // Обработка отсутствия необходимых разрешений
                                    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                                }
                            } else {
                                // Для версий Android ниже 10
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // Обработка отсутствия необходимых разрешений
                                    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                                    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                                }
                            }
                        }

                        // Обработка отсутствия необходимых разрешений
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                // Обработка отсутствия необходимых разрешений
                                MainActivity.location_update = true;
                            }
                        } else MainActivity.location_update = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

                        // GPS включен, выполните ваш код здесь
                        if (!NetworkUtils.isNetworkAvailable(context)) {
                            Toast.makeText(context, getString(R.string.verify_internet), Toast.LENGTH_SHORT).show();
                        } else if (isAdded() && isVisible() && MainActivity.location_update)  {
                            List<String> settings = new ArrayList<>();

                            String query = "SELECT * FROM " + MainActivity.ROUT_MARKER + " LIMIT 1";

                            SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                            Cursor cursor = database.rawQuery(query, null);

                            cursor.moveToFirst();

                            // Получите значения полей из первой записи

                            @SuppressLint("Range") double toLatitude = cursor.getDouble(cursor.getColumnIndex("to_lat"));
                            @SuppressLint("Range") double toLongitude = cursor.getDouble(cursor.getColumnIndex("to_lng"));
                            @SuppressLint("Range") String ToAdressString = cursor.getString(cursor.getColumnIndex("finish"));
                            Logger.d(context, TAG, "autoClickButton:ToAdressString " + ToAdressString);
                            cursor.close();
                            database.close();

                            settings.add(Double.toString(0));
                            settings.add(Double.toString(0));
                            settings.add(Double.toString(toLatitude));
                            settings.add(Double.toString(toLongitude));
                            settings.add(getString(R.string.search));
                            settings.add(ToAdressString);

                            updateRoutMarker(settings);

                            firstLocation();
                        }

                    } else {
                        // GPS выключен, выполните необходимые действия

                        MyBottomSheetGPSFragment bottomSheetDialogFragment = new MyBottomSheetGPSFragment("");
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }
                } else {

                    MyBottomSheetGPSFragment bottomSheetDialogFragment = new MyBottomSheetGPSFragment("");
                    bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                }



            });
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    gpsbut.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                    gpsbut.setTextColor(Color.BLACK);
                } else {
                    gpsbut.setBackground(getResources().getDrawable(R.drawable.btn_green));
                    gpsbut.setTextColor(Color.WHITE);
                }
            } else {
                gpsbut.setBackground(getResources().getDrawable(R.drawable.btn_red));
                gpsbut.setTextColor(Color.WHITE);
            }




            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if(geoText.getText().toString().isEmpty()) {
                    btn_clear_from_text.setVisibility(View.VISIBLE);
                    String unuString = new String(Character.toChars(0x1F449));
                    unuString += " " + getString(R.string.search_text);
                    btn_clear_from_text.setText(unuString);
                    binding.textfrom.setVisibility(View.INVISIBLE);
                    num1.setVisibility(View.INVISIBLE);
                    binding.textwhere.setVisibility(View.INVISIBLE);
                }

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            Logger.d(context, TAG, "onResume: 1");
                            progressBar.setVisibility(View.VISIBLE);
                            try {
                                String userEmail = logCursor(MainActivity.TABLE_USER_INFO, context).get(3);
                                if(!userEmail.equals("email")) {
                                    visicomCost();

                                }

                            } catch (MalformedURLException e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                btn_clear_from_text.setVisibility(View.INVISIBLE);
                                textfrom.setVisibility(View.VISIBLE);
                                num1.setVisibility(View.VISIBLE);
                                geoText.setVisibility(View.VISIBLE);

                                binding.textwhere.setVisibility(View.VISIBLE);
                                num2.setVisibility(View.VISIBLE);
                                textViewTo.setVisibility(View.VISIBLE);
                            }

                    } else {
                        boolean gps_upd = (boolean) sharedPreferencesHelper.getValue("gps_upd", false);
                        boolean gps_upd_address = (boolean) sharedPreferencesHelper.getValue("gps_upd_address", false);
                        Logger.d(context, TAG, "gps_upd" +sharedPreferencesHelper.getValue("gps_upd", false));
                        Logger.d(context, TAG, "gps_upd_address" +sharedPreferencesHelper.getValue("gps_upd_address", false));

                        if(gps_upd && gps_upd_address){
                            textfrom.setVisibility(View.VISIBLE);
                            num1.setVisibility(View.VISIBLE);
                            geoText.setVisibility(View.VISIBLE);
                            binding.textwhere.setVisibility(View.VISIBLE);
                            num2.setVisibility(View.VISIBLE);
                            textViewTo.setVisibility(View.VISIBLE);
                            Logger.d(context, TAG, "onResume: 3");
                            firstLocation();
                        } else {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                                gpsbut.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                                gpsbut.setTextColor(Color.BLACK);
                            }

                                Logger.d(context, TAG, "onResume: 4");
                                progressBar.setVisibility(View.VISIBLE);
                                try {
                                    visicomCost();
                                } catch (MalformedURLException e) {
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                    btn_clear_from_text.setVisibility(View.INVISIBLE);
                                    textfrom.setVisibility(View.VISIBLE);
                                    num1.setVisibility(View.VISIBLE);
                                    geoText.setVisibility(View.VISIBLE);

                                    binding.textwhere.setVisibility(View.VISIBLE);
                                    num2.setVisibility(View.VISIBLE);
                                    textViewTo.setVisibility(View.VISIBLE);
                                }
                        }

                    }
                } else {
                        Logger.d(context, TAG, "onResume: 6");
                        try {
                            visicomCost();
                        } catch (MalformedURLException e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            Logger.d(context, TAG, "onResume: 7");

                            btn_clear_from_text.setVisibility(View.INVISIBLE);
                            textfrom.setVisibility(View.VISIBLE);
                            num1.setVisibility(View.VISIBLE);
                            geoText.setVisibility(View.VISIBLE);

                            binding.textwhere.setVisibility(View.VISIBLE);
                            num2.setVisibility(View.VISIBLE);
                            textViewTo.setVisibility(View.VISIBLE);

                            btnVisible(View.INVISIBLE);
                        }

                }


            } else {

                binding.textwhere.setVisibility(View.INVISIBLE);

                progressBar.setVisibility(View.GONE);


                btn_clear_from_text.setText(getString(R.string.try_again));
                btn_clear_from_text.setVisibility(View.VISIBLE);
                btn_clear_from_text.setOnClickListener(v -> {
                    startActivity(new Intent(context, MainActivity.class));
                });
            }

             scheduleUpdate();
             addCheck(context);


        updateApp();
    }


    private void firstLocation() {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(requireContext(), getString(R.string.search), Toast.LENGTH_SHORT).show();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        btnVisible(View.INVISIBLE);
        gpsbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fusedLocationProviderClient != null && locationCallback != null) {
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                }

                gpsbut.setText(R.string.change);
                gpsbut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        if (locationManager != null) {
                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                // GPS включен, выполните ваш код здесь
                                if (!NetworkUtils.isNetworkAvailable(context)) {
                                    Toast.makeText(context, getString(R.string.verify_internet), Toast.LENGTH_SHORT).show();
                                } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                                    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                                } else if (isAdded() && isVisible())  {
                                    List<String> settings = new ArrayList<>();

                                    String query = "SELECT * FROM " + MainActivity.ROUT_MARKER + " LIMIT 1";

                                    SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                                    Cursor cursor = database.rawQuery(query, null);

                                    cursor.moveToFirst();

                                    // Получите значения полей из первой записи

                                    @SuppressLint("Range") double toLatitude = cursor.getDouble(cursor.getColumnIndex("to_lat"));
                                    @SuppressLint("Range") double toLongitude = cursor.getDouble(cursor.getColumnIndex("to_lng"));
                                    @SuppressLint("Range") String ToAdressString = cursor.getString(cursor.getColumnIndex("finish"));
                                    Logger.d(context, TAG, "autoClickButton:ToAdressString " + ToAdressString);
                                    cursor.close();
                                    database.close();

                                    settings.add(Double.toString(0));
                                    settings.add(Double.toString(0));
                                    settings.add(Double.toString(toLatitude));
                                    settings.add(Double.toString(toLongitude));
                                    settings.add(getString(R.string.search));
                                    settings.add(ToAdressString);

                                    updateRoutMarker(settings);
                                    Toast.makeText(requireContext(), getString(R.string.search), Toast.LENGTH_SHORT).show();
                                    firstLocation();
                                }

                            } else {
                                // GPS выключен, выполните необходимые действия
                                // Например, показать диалоговое окно с предупреждением о включении GPS
                                MyBottomSheetGPSFragment bottomSheetDialogFragment = new MyBottomSheetGPSFragment("");
                                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                            }
                        } else {

                            MyBottomSheetGPSFragment bottomSheetDialogFragment = new MyBottomSheetGPSFragment("");
                            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                        }
                    }
                });
            }
        });
        locationCallback = new LocationCallback() {
             
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                // Обработка полученных местоположений
                stopLocationUpdates();

                // Обработка полученных местоположений
                List<Location> locations = locationResult.getLocations();
                Logger.d(context, TAG, "onLocationResult: locations 222222" + locations);

                if (!locations.isEmpty()) {
                    Location firstLocation = locations.get(0);

                    double latitude = firstLocation.getLatitude();
                    double longitude = firstLocation.getLongitude();


                    List<String> stringList = logCursor(MainActivity.CITY_INFO, context);
                    String api =  stringList.get(2);

                    Locale locale = Locale.getDefault();
                    String language = locale.getLanguage(); // Получаем язык устройства

                    String urlFrom = "https://m.easy-order-taxi.site/" + api + "/android/fromSearchGeoLocal/" + latitude + "/" + longitude + "/" + language;
                    String mes_city = context.getString(R.string.on_city_tv);
                    FromJSONParserRetrofit.sendURL(urlFrom, result -> {
                        // Обработка результата в основном потоке
                        if (result != null) {
                            Logger.d(context, TAG, "Результат: " + result);
                            String FromAdressString = result.get("route_address_from");

                            if (FromAdressString != null && FromAdressString.contains("Точка на карте")) {
                                List<String> stringListCity = logCursor(MainActivity.CITY_INFO, context);
                                String city = getString(R.string.foreign_countries);
                                switch (stringListCity.get(1)) {
                                    case "Kyiv City":
                                        city = getString(R.string.Kyiv_city);
                                        break;
                                    case "Dnipropetrovsk Oblast":
                                        break;
                                    case "Odessa":
                                    case "OdessaTest":
                                        city = getString(R.string.Odessa);
                                        break;
                                    case "Zaporizhzhia":
                                        city = getString(R.string.Zaporizhzhia);
                                        break;
                                    case "Cherkasy Oblast":
                                        city = getString(R.string.Cherkasy);
                                        break;
                                    default:
                                        city = getString(R.string.foreign_countries);
                                        break;
                                }
                                FromAdressString = getString(R.string.startPoint) + ", " + getString(R.string.city_loc) + " " + city;
                            }

                            updateMyPosition(latitude, longitude, FromAdressString, context);

                            geoText.setText(FromAdressString);
                            progressBar.setVisibility(View.GONE);
                            String query = "SELECT * FROM " + MainActivity.ROUT_MARKER + " LIMIT 1";
                            SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                            @SuppressLint("Recycle") Cursor cursor = database.rawQuery(query, null);

                            cursor.moveToFirst();

                            // Получите значения полей из первой записи
                            @SuppressLint("Range") double originLatitude = cursor.getDouble(cursor.getColumnIndex("startLat"));
                            @SuppressLint("Range") double toLatitude = cursor.getDouble(cursor.getColumnIndex("to_lat"));
                            @SuppressLint("Range") double toLongitude = cursor.getDouble(cursor.getColumnIndex("to_lng"));
                            @SuppressLint("Range") String ToAdressString = cursor.getString(cursor.getColumnIndex("finish"));


                            Logger.d(context, TAG, "onLocationResult:FromAdressString " + FromAdressString);
                            Logger.d(context, TAG, "onLocationResult:ToAdressString " + ToAdressString);


                            List<String> settings = new ArrayList<>();
                            if (FromAdressString != null && ToAdressString != null) {
                                boolean addressesEqual = FromAdressString.equals(ToAdressString);
                                Logger.d(context, TAG, "onLocationResult: FromAdressString.equals(ToAdressString): " + addressesEqual);
                            } else {
                                Log.w(TAG, "onLocationResult: One or both address strings are null");
                            }
                            // Пример кода для установки текста в TextView

                            if (originLatitude == toLatitude) {
                                textViewTo.setText(mes_city.isEmpty() ? "" : mes_city);
                            } else {
                                textViewTo.setText(ToAdressString);
                                Logger.d(context, TAG, "onLocationResult:ToAdressString " + ToAdressString);
                            }

                            assert ToAdressString != null;

                            if(ToAdressString.equals(mes_city) ||
                                    ToAdressString.isEmpty()) {
                                settings.add(Double.toString(latitude));
                                settings.add(Double.toString(longitude));
                                settings.add(Double.toString(latitude));
                                settings.add(Double.toString(longitude));
                                settings.add(FromAdressString);
                                settings.add(FromAdressString);
                                updateRoutMarker(settings);
                            } else {


                                if(isAdded()) {

                                    settings.add(Double.toString(latitude));
                                    settings.add(Double.toString(longitude));
                                    settings.add(Double.toString(toLatitude));
                                    settings.add(Double.toString(toLongitude));
                                    settings.add(FromAdressString);
                                    settings.add(ToAdressString);
                                    updateRoutMarker(settings);
                                }

                            }
                            gpsbut.setText(R.string.change);
                            gpsbut.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                                    if (locationManager != null) {
                                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                            // GPS включен, выполните ваш код здесь
                                            if (!NetworkUtils.isNetworkAvailable(context)) {
                                                Toast.makeText(context, getString(R.string.verify_internet), Toast.LENGTH_SHORT).show();
                                            } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                                    || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                                                checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                                            } else if (isAdded() && isVisible())  {
                                                List<String> settings = new ArrayList<>();

                                                String query = "SELECT * FROM " + MainActivity.ROUT_MARKER + " LIMIT 1";

                                                SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                                                Cursor cursor = database.rawQuery(query, null);

                                                cursor.moveToFirst();

                                                // Получите значения полей из первой записи

                                                @SuppressLint("Range") double toLatitude = cursor.getDouble(cursor.getColumnIndex("to_lat"));
                                                @SuppressLint("Range") double toLongitude = cursor.getDouble(cursor.getColumnIndex("to_lng"));
                                                @SuppressLint("Range") String ToAdressString = cursor.getString(cursor.getColumnIndex("finish"));
                                                Logger.d(context, TAG, "autoClickButton:ToAdressString " + ToAdressString);
                                                cursor.close();
                                                database.close();

                                                settings.add(Double.toString(0));
                                                settings.add(Double.toString(0));
                                                settings.add(Double.toString(toLatitude));
                                                settings.add(Double.toString(toLongitude));
                                                settings.add(getString(R.string.search));
                                                settings.add(ToAdressString);

                                                updateRoutMarker(settings);
                                                Toast.makeText(requireContext(), getString(R.string.search), Toast.LENGTH_SHORT).show();
                                                firstLocation();
                                            }

                                        } else {
                                            // GPS выключен, выполните необходимые действия
                                            // Например, показать диалоговое окно с предупреждением о включении GPS

                                            MyBottomSheetGPSFragment bottomSheetDialogFragment = new MyBottomSheetGPSFragment("");
                                            bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                                        }
                                    } else {
                                        MyBottomSheetGPSFragment bottomSheetDialogFragment = new MyBottomSheetGPSFragment("");
                                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                                    }
                                }
                            });

                            try {
                                visicomCost();
                            } catch (MalformedURLException e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                throw new RuntimeException(e);
                            }
                        } else {
                            Logger.d(context, TAG, "Ошибка при выполнении запроса");
                        }
                    });
                }
            }

        };

        startLocationUpdates();
    }
    private void startLocationUpdates() {
        LocationRequest locationRequest = createLocationRequest();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000); // Интервал обновления местоположения в миллисекундах
        locationRequest.setFastestInterval(100); // Самый быстрый интервал обновления местоположения в миллисекундах
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Приоритет точного местоположения
        return locationRequest;
    }
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Показываем объяснение пользователю, почему мы запрашиваем разрешение
            // Можно использовать диалоговое окно или другой пользовательский интерфейс
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
        } else {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }
    private void updateRoutMarker(List<String> settings) {
        Logger.d(context, TAG, "updateRoutMarker: " + settings.toString());
        ContentValues cv = new ContentValues();

        cv.put("startLat", Double.parseDouble(settings.get(0)));
        cv.put("startLan", Double.parseDouble(settings.get(1)));
        cv.put("to_lat", Double.parseDouble(settings.get(2)));
        cv.put("to_lng", Double.parseDouble(settings.get(3)));
        cv.put("start", settings.get(4));
        cv.put("finish", settings.get(5));
        if(isAdded()) {
            // обновляем по id
            SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
            database.update(MainActivity.ROUT_MARKER, cv, "id = ?",
                    new String[]{"1"});
            database.close();
        }
    }
    private static void updateMyPosition(Double startLat, Double startLan, String position, Context context) {
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        ContentValues cv = new ContentValues();

        cv.put("startLat", startLat);
        database.update(MainActivity.TABLE_POSITION_INFO, cv, "id = ?",
                new String[] { "1" });
        cv.put("startLan", startLan);
        database.update(MainActivity.TABLE_POSITION_INFO, cv, "id = ?",
                new String[] { "1" });
        cv.put("position", position);
        database.update(MainActivity.TABLE_POSITION_INFO, cv, "id = ?",
                new String[] { "1" });
        database.close();

    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    private void visicomCost() throws MalformedURLException {

        constr2.setVisibility(View.INVISIBLE);
        btn_clear_from_text.setVisibility(View.INVISIBLE);
        textfrom.setVisibility(View.VISIBLE);

        num1.setVisibility(View.VISIBLE);
        geoText.setVisibility(View.VISIBLE);

        binding.textwhere.setVisibility(View.VISIBLE);
        num2.setVisibility(View.VISIBLE);
        textViewTo.setVisibility(View.VISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            // Выполнение тяжелой операции в фоновом потоке
            String query = "SELECT * FROM " + MainActivity.ROUT_MARKER + " LIMIT 1";
            SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
            @SuppressLint("Recycle") Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();

            @SuppressLint("Range") double originLatitude = cursor.getDouble(cursor.getColumnIndex("startLat"));
            @SuppressLint("Range") double toLatitude = cursor.getDouble(cursor.getColumnIndex("to_lat"));

            @SuppressLint("Range") String start = cursor.getString(cursor.getColumnIndex("start"));
            @SuppressLint("Range") String finish = cursor.getString(cursor.getColumnIndex("finish"));

            String discountText = logCursor(MainActivity.TABLE_SETTINGS_INFO, context).get(3);
            String urlCost = getTaxiUrlSearchMarkers("costSearchMarkersTime", context);

            Logger.d(context, TAG, "visicomCost: " + urlCost);

            CostJSONParserRetrofit parser = new CostJSONParserRetrofit();
            try {
                parser.sendURL(urlCost, new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                geoText.setText(start);
                                if (originLatitude == toLatitude) {
                                    textViewTo.setText(context.getString(R.string.on_city_tv));
                                } else {
                                    textViewTo.setText(finish);
                                }

                                Map<String, String> sendUrlMapCost = response.body();
                                if (sendUrlMapCost == null) {
                                    Toast.makeText(context, context.getString(R.string.server_error_connected), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String orderCost = sendUrlMapCost.get("order_cost");
                                String orderMessage = sendUrlMapCost.get("Message");

                                assert orderCost != null;
                                if (orderCost.equals("0")) {
                                    progressBar.setVisibility(View.GONE);
                                    String message = context.getString(R.string.error_message);
                                    if (orderMessage.equals("ErrorMessage")) {
                                        message = context.getString(R.string.server_error_connected);
                                    }

                                    if (!isStateSaved() && isAdded()) {
                                        MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(message);
                                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.server_error_connected), Toast.LENGTH_SHORT).show();
                                    }

                                } else {

                                    if (discountText.matches("[+-]?\\d+") || discountText.equals("0")) {
                                        long discountInt = Integer.parseInt(discountText);
                                        long discount;

                                        firstCost = Long.parseLong(orderCost);
                                        discount = firstCost * discountInt / 100;
                                        firstCost = VisicomFragment.firstCost + discount;
                                        updateAddCost(String.valueOf(discount));
                                        text_view_cost.setText(String.valueOf(VisicomFragment.firstCost));
                                        MIN_COST_VALUE = (long) (VisicomFragment.firstCost * 0.6);
                                        firstCostForMin = VisicomFragment.firstCost;

                                        geoText.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);

                                        textfrom.setVisibility(View.VISIBLE);
                                        num1.setVisibility(View.VISIBLE);
                                        textwhere.setVisibility(View.VISIBLE);
                                        num2.setVisibility(View.VISIBLE);
                                        textViewTo.setVisibility(View.VISIBLE);

                                        btnAdd.setVisibility(View.VISIBLE);

                                        buttonBonus.setVisibility(View.VISIBLE);
                                        btn_minus.setVisibility(View.VISIBLE);
                                        text_view_cost.setVisibility(View.VISIBLE);
                                        btn_plus.setVisibility(View.VISIBLE);
                                        btnOrder.setVisibility(View.VISIBLE);

                                        btn_clear_from_text.setVisibility(View.GONE);
                                        constr2.setVisibility(View.VISIBLE);
                                    }
                                    costSearchMarkersLocalTariffs(context);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseCrashlytics.getInstance().recordException(t);
                                Toast.makeText(context, context.getString(R.string.server_error_connected), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (MalformedURLException ignored) {

            }
        }).start();
    }

    private void fetchRoutesCancel() {
        Logger.d(context, TAG, "fetchRoutesCancel: ");
        String userEmail = logCursor(MainActivity.TABLE_USER_INFO, context).get(3);
        if (!userEmail.equals("email"))
            {
                databaseHelper.clearTable();

                databaseHelperUid.clearTableUid();

                routeListCancel = new ArrayList<>();

                String baseUrl = "https://m.easy-order-taxi.site";

                List<String> stringList = logCursor(MainActivity.CITY_INFO,context);
                String city = stringList.get(1);

                String url = baseUrl + "/android/UIDStatusShowEmailCancelApp/" + userEmail + "/" + city + "/" +  context.getString(R.string.application);

                Call<List<RouteResponseCancel>> call = ApiClient.getApiService().getRoutesCancel(url);
                Logger.d(context, TAG, "fetchRoutesCancel: " + url);
                call.enqueue(new Callback<List<RouteResponseCancel>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<RouteResponseCancel>> call, @NonNull Response<List<RouteResponseCancel>> response) {
                        if (response.isSuccessful()) {
                            List<RouteResponseCancel> routes = response.body();
                            assert routes != null;
                            Logger.d(context, TAG, "onResponse: " + routes.toString());
                            if (routes.size() == 1) {
                                RouteResponseCancel route = routes.get(0);
                                if ("*".equals(route.getRouteFrom()) && "*".equals(route.getRouteFromNumber()) &&
                                        "*".equals(route.getRouteTo()) && "*".equals(route.getRouteToNumber()) &&
                                        "*".equals(route.getWebCost()) && "*".equals(route.getCloseReason()) &&
                                        "*".equals(route.getAuto()) && "*".equals(route.getCreatedAt())) {
                                    databaseHelper.clearTableCancel();
                                    databaseHelperUid.clearTableCancel();
                                    return;
                                }
                            }
                            if (!routes.isEmpty()) {
                                boolean hasRouteWithAsterisk = false;
                                for (RouteResponseCancel route : routes) {
                                    if ("*".equals(route.getRouteFrom())) {
                                        // Найден объект с routefrom = "*"
                                        hasRouteWithAsterisk = true;
                                        break;  // Выход из цикла, так как условие уже выполнено
                                    }
                                }
                                if (!hasRouteWithAsterisk) {
                                    if (routeListCancel == null) {
                                        routeListCancel = new ArrayList<>();
                                    }
                                    routeListCancel.addAll(routes);
                                    processCancelList();
                                }

                            }
                        }
                    }

                    public void onFailure(@NonNull Call<List<RouteResponseCancel>> call, @NonNull Throwable t) {
                        // Обработка ошибок сети или других ошибок
                        FirebaseCrashlytics.getInstance().recordException(t);
                    }
                });
        }

    }

    private void processCancelList() {
        if (routeListCancel == null || routeListCancel.isEmpty()) {
            Logger.d(context, TAG, "routeListCancel is null or empty");
            return;
        }

        // Создайте массив строк

        databaseHelper.clearTableCancel();
        databaseHelperUid.clearTableCancel();

        String closeReasonText = getString(R.string.close_resone_def);

        for (int i = 0; i < routeListCancel.size(); i++) {
            RouteResponseCancel route = routeListCancel.get(i);
            String uid = route.getUid();
            String routeFrom = route.getRouteFrom();
            String routefromnumber = route.getRouteFromNumber();
            String routeTo = route.getRouteTo();
            String routeTonumber = route.getRouteToNumber();
            String webCost = route.getWebCost();
            String createdAt = route.getCreatedAt();
            String closeReason = route.getCloseReason();
            String auto = route.getAuto();
            String dispatchingOrderUidDouble = route.getDispatchingOrderUidDouble();
            String pay_method = route.getPay_method();
            String required_time = route.getRequired_time();

            switch (closeReason) {
                case "-1":
                    closeReasonText = getString(R.string.close_resone_in_work);
                    break;
                case "0":
                    closeReasonText = getString(R.string.close_resone_0);
                    break;
                case "1":
                    closeReasonText = getString(R.string.close_resone_1);
                    break;
                case "2":
                    closeReasonText = getString(R.string.close_resone_2);
                    break;
                case "3":
                    closeReasonText = getString(R.string.close_resone_3);
                    break;
                case "4":
                    closeReasonText = getString(R.string.close_resone_4);
                    break;
                case "5":
                    closeReasonText = getString(R.string.close_resone_5);
                    break;
                case "6":
                    closeReasonText = getString(R.string.close_resone_6);
                    break;
                case "7":
                    closeReasonText = getString(R.string.close_resone_7);
                    break;
                case "8":
                    closeReasonText = getString(R.string.close_resone_8);
                    break;
                case "9":
                    closeReasonText = getString(R.string.close_resone_9);
                    break;
            }

            if (routeFrom.equals("Місце відправлення")) {
                routeFrom = getString(R.string.start_point_text);
            }

            if (routeTo.equals("Точка на карте")) {
                routeTo = getString(R.string.end_point_marker);
            }
            if (routeTo.contains("по городу")) {
                routeTo = getString(R.string.on_city);
            }
            if (routeTo.contains("по місту")) {
                routeTo = getString(R.string.on_city);
            }
            String routeInfo = "";

            if (auto == null) {
                auto = "??";
            }
            if(required_time != null && !required_time.contains("01.01.1970")) {
                required_time = getString(R.string.time_order) + required_time;
            } else {
                required_time = "";
            }
            if (routeFrom.equals(routeTo)) {
                routeInfo = getString(R.string.close_resone_from) + routeFrom + " " + routefromnumber
                        + getString(R.string.close_resone_to)
                        + getString(R.string.on_city)
                        + required_time
                        + getString(R.string.close_resone_cost) + webCost + " " + getString(R.string.UAH)
                        + getString(R.string.auto_info) + " " + auto + " "
                        + getString(R.string.close_resone_time)
                        + createdAt + getString(R.string.close_resone_text) + closeReasonText;
            } else {
                routeInfo = getString(R.string.close_resone_from) + routeFrom + " " + routefromnumber
                        + getString(R.string.close_resone_to) + routeTo + " " + routeTonumber + "."
                        + required_time
                        + getString(R.string.close_resone_cost) + webCost + " " + getString(R.string.UAH)
                        + getString(R.string.auto_info) + " " + auto + " "
                        + getString(R.string.close_resone_time)
                        + createdAt + getString(R.string.close_resone_text) + closeReasonText;
            }

            databaseHelper.addRouteCancel(uid, routeInfo);
            List<String> settings = new ArrayList<>();

            settings.add(uid);
            settings.add(webCost);
            settings.add(routeFrom);
            settings.add(routefromnumber);
            settings.add(routeTo);
            settings.add(routeTonumber);
            settings.add(dispatchingOrderUidDouble);
            settings.add(pay_method);
            settings.add(required_time);

            Logger.d(context, TAG, settings.toString());
            databaseHelperUid.addCancelInfoUid(settings);
        }

        String[] array = databaseHelper.readRouteCancel();
        Logger.d(context, TAG, "processRouteList: array " + Arrays.toString(array));
        if (array != null) {
            String message = getString(R.string.order_to_cancel_true);
            MyBottomSheetErrorFragment myBottomSheetMessageFragment = new MyBottomSheetErrorFragment(message);
            myBottomSheetMessageFragment.show(fragmentManager, myBottomSheetMessageFragment.getTag());
        } else {
            databaseHelper.clearTableCancel();
            databaseHelperUid.clearTableCancel();
        }
    }
}