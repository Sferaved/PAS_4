package com.taxi_pas_4.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;
import com.taxi_pas_4.cities.Cherkasy.Cherkasy;
import com.taxi_pas_4.cities.Dnipro.Dnipro;
import com.taxi_pas_4.cities.Kyiv.KyivCity;
import com.taxi_pas_4.cities.Odessa.Odessa;
import com.taxi_pas_4.cities.Odessa.OdessaTest;
import com.taxi_pas_4.cities.Zaporizhzhia.Zaporizhzhia;
import com.taxi_pas_4.ui.finish.FinishActivity;
import com.taxi_pas_4.ui.maps.CostJSONParser;
import com.taxi_pas_4.ui.maps.ToJSONParser;
import com.taxi_pas_4.ui.open_map.OpenStreetMapActivity;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;


public class MyGeoMarkerDialogFragment extends BottomSheetDialogFragment {
    public TextView geoText;
    AppCompatButton button, btn_minus, btn_plus, btnOrder, buttonBonus;
    public String[] arrayStreet;
    private static String api;
    long firstCost;

    public static TextView text_view_cost, textViewTo;
    public static EditText to_number;

    public static String numberFlagTo;

    static MyGeoMarkerDialogFragment fragment;
    public static long cost;
    public static long addCost;
    public static String to;
    public static ProgressBar progressBar;

    public static MyGeoMarkerDialogFragment newInstance(String fromGeo) {
        fragment = new MyGeoMarkerDialogFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.geo_marker_layout, container, false);
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        final int initialMarginBottom = 0;

        final View decorView = requireActivity().getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int screenHeight = decorView.getHeight();
                int keypadHeight = screenHeight - rect.bottom;

                ConstraintLayout myLinearLayout = view.findViewById(R.id.constraint); // Замените на ваш ID представления
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) myLinearLayout.getLayoutParams();

                if (keypadHeight > screenHeight * 0.15) {
                    // Клавиатура отображается, установите отступ в зависимости от размера клавиатуры
                    layoutParams.bottomMargin = keypadHeight + initialMarginBottom;
                } else {
                    // Клавиатура скрыта, установите изначальный отступ
                    layoutParams.bottomMargin = initialMarginBottom;
                }

                myLinearLayout.setLayoutParams(layoutParams);
            }
        });
//        setCancelable(false);

            List<String> stringList = logCursor(MainActivity.CITY_INFO, getActivity());
        switch (stringList.get(1)){
            case "Dnipropetrovsk Oblast":
                arrayStreet = Dnipro.arrayStreet();
                api = MainActivity.apiDnipro;

                break;
            case "Odessa":
                arrayStreet = Odessa.arrayStreet();
                api = MainActivity.apiOdessa;

                break;
            case "Zaporizhzhia":
                arrayStreet = Zaporizhzhia.arrayStreet();
                api = MainActivity.apiZaporizhzhia;

                break;
            case "Cherkasy Oblast":
                arrayStreet = Cherkasy.arrayStreet();
                api = MainActivity.apiCherkasy;

                break;
            case "OdessaTest":
                arrayStreet = OdessaTest.arrayStreet();
                api = MainActivity.apiTest;

                break;
            default:
                arrayStreet = KyivCity.arrayStreet();
                api = MainActivity.apiKyiv;

                break;
        }


        addCost = 0;
        numberFlagTo = "2";
        progressBar = view.findViewById(R.id.progress_bar);
        geoText = view.findViewById(R.id.textGeo);
        geoText.setText(OpenStreetMapActivity.FromAdressString);

        text_view_cost = view.findViewById(R.id.text_view_cost);
        text_view_cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Вызывается перед изменением текста
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Вызывается во время изменения текста
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Вызывается после изменения текста
                String newText = editable.toString();
                // Здесь вы можете обработать новый текст
                firstCost = Long.parseLong(newText);
            }
        });



        textViewTo = view.findViewById(R.id.text_to);
        textViewTo.setText(OpenStreetMapActivity.ToAdressString);
        int inputTypeTo = textViewTo.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        textViewTo.setInputType(inputTypeTo);

        btn_minus = view.findViewById(R.id.btn_minus);
        btn_plus = view.findViewById(R.id.btn_plus);
        btnOrder = view.findViewById(R.id.btnOrder);


        button = view.findViewById(R.id.change);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenStreetMapActivity.progressBar.setVisibility(View.VISIBLE);
                geoText.setText("");
                startActivity(new Intent(getActivity(), OpenStreetMapActivity.class));
            }
        });


        AppCompatButton buttonAddServicesView =  view.findViewById(R.id.btnAdd);
        buttonAddServicesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyBottomSheetGeoMarkerFragment bottomSheetDialogFragment = new MyBottomSheetGeoMarkerFragment();
                bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    OpenStreetMapActivity.progressBar.setVisibility(View.VISIBLE);
                    order();
                }
            }
        });
        buttonBonus = view.findViewById(R.id.btnBonus);
        startCost();
        OpenStreetMapActivity.progressBar.setVisibility(View.INVISIBLE);
        String bonus = logCursor(MainActivity.TABLE_USER_INFO, getActivity()).get(5);
        if(Long.parseLong(bonus) >= cost * 100 ) {
            List<String> stringListBon = logCursor(MainActivity.CITY_INFO, getActivity());

            switch (stringListBon.get(1)) {
                case "Kyiv City":
                case "Dnipropetrovsk Oblast":
                case "Odessa":
                case "Zaporizhzhia":
                case "Cherkasy Oblast":
                    buttonBonus.setVisibility(View.GONE);
                    break;
                case "OdessaTest":
                    buttonBonus.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            buttonBonus.setVisibility(View.GONE);
        }
        buttonBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyBottomSheetBonusFragment bottomSheetDialogFragment = new MyBottomSheetBonusFragment(bonus, "marker", api, text_view_cost, "GeoMarker");
                bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
        return view;
    }

    private void startCost() {
        String urlCost = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<String> settings = new ArrayList<>();
            settings.add(String.valueOf(OpenStreetMapActivity.startLat));
            settings.add(String.valueOf(OpenStreetMapActivity.startLan));
            settings.add(String.valueOf(OpenStreetMapActivity.finishLat));
            settings.add(String.valueOf(OpenStreetMapActivity.finishLan));

            updateRoutMarker(settings);
            urlCost = getTaxiUrlSearchMarkers( "costSearchMarkers", getActivity());
        }

        Map<String, String> sendUrlMapCost = null;
        try {
            sendUrlMapCost = CostJSONParser.sendURL(urlCost);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        OpenStreetMapActivity.progressBar.setVisibility(View.INVISIBLE);
        String message = sendUrlMapCost.get("message");
        String orderCost = sendUrlMapCost.get("order_cost");
        Log.d("TAG", "startCost: orderCost " + orderCost);

        if (orderCost.equals("0")) {
            MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(message);
            bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
        }
        if (!orderCost.equals("0")) {
            List<String> stringList = logCursor(MainActivity.CITY_INFO, getActivity());

            switch (stringList.get(1)){
                case "Kyiv City":
                case "Dnipropetrovsk Oblast":
                case "Odessa":
                case "Zaporizhzhia":
                case "Cherkasy Oblast":
                    buttonBonus.setVisibility(View.GONE);
                    break;
                case "OdessaTest":
                    buttonBonus.setVisibility(View.VISIBLE);
                    break;
            }
            String discountText = logCursor(MainActivity.TABLE_SETTINGS_INFO, getContext()).get(3);
            long discountInt = Integer.parseInt(discountText);
            long discount;
            firstCost = Long.parseLong(orderCost);
            discount = firstCost * discountInt / 100;
            firstCost = firstCost + discount;
            addCost = discount;
            text_view_cost.setText(String.valueOf(firstCost));
            firstCost = Long.parseLong(text_view_cost.getText().toString());

            String bonus = logCursor(MainActivity.TABLE_USER_INFO, getActivity()).get(5);

            if(Long.parseLong(bonus) >= firstCost * 100 ) {
                List<String> stringL = logCursor(MainActivity.CITY_INFO, getActivity());

                switch (stringL.get(1)) {
                    case "Kyiv City":
                    case "Dnipropetrovsk Oblast":
                    case "Odessa":
                    case "Zaporizhzhia":
                    case "Cherkasy Oblast":
                        buttonBonus.setVisibility(View.GONE);
                        break;
                    case "OdessaTest":
                        buttonBonus.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                buttonBonus.setVisibility(View.GONE);
            }



            Log.d("TAG", "startCost: firstCost " + firstCost);
            Log.d("TAG", "startCost: addCost " + addCost);
            long MIN_COST_VALUE = (long) (firstCost * 0.1);
            long MAX_COST_VALUE = firstCost * 3;


            btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstCost -= 5;
                    addCost -= 5;
                    if (firstCost <= MIN_COST_VALUE) {
                        firstCost = MIN_COST_VALUE;
                        addCost = MIN_COST_VALUE - firstCost;
                    }
                    Log.d("TAG", "startCost: addCost " + addCost);
                    text_view_cost.setText(String.valueOf(firstCost));
                    String bonus = logCursor(MainActivity.TABLE_USER_INFO, getActivity()).get(5);

                    if(Long.parseLong(bonus) >= firstCost * 100 ) {
                        List<String> stringList = logCursor(MainActivity.CITY_INFO, getActivity());

                        switch (stringList.get(1)) {
                            case "Kyiv City":
                            case "Dnipropetrovsk Oblast":
                            case "Odessa":
                            case "Zaporizhzhia":
                            case "Cherkasy Oblast":
                                buttonBonus.setVisibility(View.GONE);
                                break;
                            case "OdessaTest":
                                buttonBonus.setVisibility(View.VISIBLE);
                                break;
                        }
                    } else {
                        buttonBonus.setVisibility(View.GONE);
                    }
                }
            });

            btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstCost += 5;
                    addCost += 5;
                    if (firstCost >= MAX_COST_VALUE) {
                        firstCost = MAX_COST_VALUE;
                        addCost = MAX_COST_VALUE - firstCost;
                    }
                    Log.d("TAG", "startCost: addCost " + addCost);
                    text_view_cost.setText(String.valueOf(firstCost));
                    String bonus = logCursor(MainActivity.TABLE_USER_INFO, getActivity()).get(5);

                    if(Long.parseLong(bonus) >= firstCost * 100 ) {
                        List<String> stringList = logCursor(MainActivity.CITY_INFO, getActivity());

                        switch (stringList.get(1)) {
                            case "Kyiv City":
                            case "Dnipropetrovsk Oblast":
                            case "Odessa":
                            case "Zaporizhzhia":
                            case "Cherkasy Oblast":
                                buttonBonus.setVisibility(View.GONE);
                                break;
                            case "OdessaTest":
                                buttonBonus.setVisibility(View.VISIBLE);
                                break;
                        }
                    } else {
                        buttonBonus.setVisibility(View.GONE);
                    }
                }
            });

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getTaxiUrlSearchMarkers(String urlAPI, Context context) {

        List<String> stringListRout = logCursor(MainActivity.ROUT_MARKER, context);
        Log.d("TAG", "getTaxiUrlSearch: stringListRout" + stringListRout);

        double originLatitude = Double.parseDouble(stringListRout.get(1));
        double originLongitude = Double.parseDouble(stringListRout.get(2));
        double toLatitude = Double.parseDouble(stringListRout.get(3));
        double toLongitude = Double.parseDouble(stringListRout.get(4));


        List<String> stringList = logCursor(MainActivity.TABLE_ADD_SERVICE_INFO, context);
        String time = stringList.get(1);
        String comment = stringList.get(2);
        String date = stringList.get(3);

        // Origin of route
        String str_origin = String.valueOf(originLatitude) + "/" + String.valueOf(originLongitude);

        // Destination of route
        String str_dest = String.valueOf(toLatitude) + "/" + String.valueOf(toLongitude);

        //        Cursor cursorDb = MainActivity.database.query(MainActivity.TABLE_SETTINGS_INFO, null, null, null, null, null, null);
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        String tarif = logCursor(MainActivity.TABLE_SETTINGS_INFO, context).get(2);


        // Building the parameters to the web service

        String parameters = null;
        String phoneNumber = "no phone";
        String userEmail = logCursor(MainActivity.TABLE_USER_INFO, context).get(3);
        String displayName = logCursor(MainActivity.TABLE_USER_INFO, context).get(4);

        if(urlAPI.equals("costSearchMarkers")) {
            Cursor c = database.query(MainActivity.TABLE_USER_INFO, null, null, null, null, null, null);

            if (c.getCount() == 1) {
                phoneNumber = logCursor(MainActivity.TABLE_USER_INFO, context).get(2);
                c.close();
            }
            parameters = str_origin + "/" + str_dest + "/" + tarif + "/" + phoneNumber + "/"
                    + displayName + "*" + userEmail  + "*" + MainActivity.bonusPayment;
        }
        if(urlAPI.equals("orderSearchMarkers")) {
            phoneNumber = logCursor(MainActivity.TABLE_USER_INFO, context).get(2);


            parameters = str_origin + "/" + str_dest + "/" + tarif + "/" + phoneNumber + "/"
                    + displayName + "*" + userEmail  + "*" + MainActivity.bonusPayment + "/" + addCost + "/" + time + "/" + comment + "/" + date;

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
            Log.d("TAG", "getTaxiUrlSearchGeo result:" + result + "/");
        } else {
            result = "no_extra_charge_codes";
        }

        String url = "https://m.easy-order-taxi.site/" + api + "/android/" + urlAPI + "/" + parameters + "/" + result;


        database.close();


        return url;

    }



    private boolean connected() {

        Boolean hasConnect = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(
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

    @Override
    public void onPause() {
        super.onPause();
        OpenStreetMapActivity.fab_open_marker.setVisibility(View.VISIBLE);
//        startActivity(new Intent(getActivity(), OpenStreetMapActivity.class));
    }
    @SuppressLint("Range")
    public static List<String> logCursor(String table, Context context) {
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
        return list;
    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void order() {
        if(!verifyOrder(getContext())) {
            MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(getString(R.string.black_list_message));
            bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
            return;
        }

        if (!verifyPhone(getContext())) {
            getPhoneNumber();
        }
        if (!verifyPhone(getContext())) {
            MyPhoneDialogFragment bottomSheetDialogFragment = new MyPhoneDialogFragment("marker");
            bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
            OpenStreetMapActivity.progressBar.setVisibility(View.INVISIBLE);
        }
        if(connected()) {
            if (verifyPhone(getContext())) {
                try {


                    String urlOrder = getTaxiUrlSearchMarkers("orderSearchMarkers", getActivity());
                    Map<String, String> sendUrlMap = ToJSONParser.sendURL(urlOrder);
                    Log.d("TAG", "Map sendUrlMap = ToJSONParser.sendURL(urlOrder); " + sendUrlMap);

                    String orderWeb = sendUrlMap.get("order_cost");

                    if (!orderWeb.equals("0")) {
                        String to_name;
                        if (Objects.equals(sendUrlMap.get("routefrom"), sendUrlMap.get("routeto"))) {
                            to_name = getString(R.string.on_city_tv);
                            if (!sendUrlMap.get("lat").equals("0")) {
                                insertRecordsOrders(
                                        sendUrlMap.get("routefrom"), sendUrlMap.get("routefrom"),
                                        sendUrlMap.get("routefromnumber"), sendUrlMap.get("routefromnumber"),
                                        Double.toString(OpenStreetMapActivity.startLat), Double.toString(OpenStreetMapActivity.startLan),
                                        Double.toString(OpenStreetMapActivity.startLat), Double.toString(OpenStreetMapActivity.startLan),
                                        getActivity()
                                );
                            }
                        } else {
                            if(sendUrlMap.get("routeto").equals("Точка на карте")) {
                                to_name = getActivity().getString(R.string.end_point_marker);
                            } else {
                                to_name = sendUrlMap.get("routeto") + " " + sendUrlMap.get("to_number");
                            }

                            if (!sendUrlMap.get("lat").equals("0")) {
                                insertRecordsOrders(
                                        sendUrlMap.get("routefrom"), to_name,
                                        sendUrlMap.get("routefromnumber"), sendUrlMap.get("to_number"),
                                        Double.toString(OpenStreetMapActivity.startLat), Double.toString(OpenStreetMapActivity.startLan),
                                        sendUrlMap.get("lat"), sendUrlMap.get("lng"), getActivity()
                                );
                            }
                        }
                        String messageResult = getString(R.string.thanks_message) +
                                OpenStreetMapActivity.FromAdressString + " " + getString(R.string.to_message) +
                                to_name + "." +
                                getString(R.string.call_of_order) + orderWeb + getString(R.string.UAH);


                        Intent intent = new Intent(getActivity(), FinishActivity.class);
                        intent.putExtra("messageResult_key", messageResult);
                        intent.putExtra("messageCost_key", orderWeb);
                        intent.putExtra("sendUrlMap", new HashMap<>(sendUrlMap));
                        intent.putExtra("UID_key", Objects.requireNonNull(sendUrlMap.get("dispatching_order_uid")));
                        startActivity(intent);
                    } else {

                        MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(sendUrlMap.get("message"));
                        bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
                        OpenStreetMapActivity.progressBar.setVisibility(View.INVISIBLE);
                    }


                } catch (MalformedURLException ignored) {

                }


            }
        } else {
            MyBottomSheetErrorFragment bottomSheetDialogFragment = new MyBottomSheetErrorFragment(getString(R.string.verify_internet));
            bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
            OpenStreetMapActivity.progressBar.setVisibility(View.INVISIBLE);
        }
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

    private boolean verifyPhone(Context context) {
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.query(MainActivity.TABLE_USER_INFO, null, null, null, null, null, null);
        boolean verify = true;
        if (cursor.getCount() == 1) {

            if (logCursor(MainActivity.TABLE_USER_INFO, context).get(2).equals("+380")) {
                verify = false;
            }
            cursor.close();
        }

        return verify;
    }
    private void getPhoneNumber () {
        String mPhoneNumber;
        TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Manifest.permission.READ_PHONE_NUMBERS: " + ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_NUMBERS));
            Log.d("TAG", "Manifest.permission.READ_PHONE_STATE: " + ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE));
            return;
        }
        mPhoneNumber = tMgr.getLine1Number();
        if(mPhoneNumber != null) {
            String PHONE_PATTERN = "((\\+?380)(\\d{9}))$";
            boolean val = Pattern.compile(PHONE_PATTERN).matcher(mPhoneNumber).matches();
            Log.d("TAG", "onClick No validate: " + val);
            if (val == false) {
                Toast.makeText(getActivity(), getString(R.string.format_phone) , Toast.LENGTH_SHORT).show();
                Log.d("TAG", "onClick:phoneNumber.getText().toString() " + mPhoneNumber);
            } else {
                updateRecordsUser(mPhoneNumber, getContext());
            }
        }

    }

    private void updateRecordsUser(String result, Context context) {
        ContentValues cv = new ContentValues();

        cv.put("phone_number", result);

        // обновляем по id
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        int updCount = database.update(MainActivity.TABLE_USER_INFO, cv, "id = ?",
                new String[] { "1" });
        Log.d("TAG", "updated rows count = " + updCount);
    }

    private static void insertRecordsOrders( String from, String to,
                                             String from_number, String to_number,
                                             String from_lat, String from_lng,
                                             String to_lat, String to_lng, Context context) {

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
    private void updateRoutMarker(List<String> settings) {
        ContentValues cv = new ContentValues();

        cv.put("startLat",  Double.parseDouble(settings.get(0)));
        cv.put("startLan", Double.parseDouble(settings.get(1)));
        cv.put("to_lat", Double.parseDouble(settings.get(2)));
        cv.put("to_lng", Double.parseDouble(settings.get(3)));

        // обновляем по id
        SQLiteDatabase database = requireActivity().openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        database.update(MainActivity.ROUT_MARKER, cv, "id = ?",
                new String[] { "1" });
        database.close();
    }
}


