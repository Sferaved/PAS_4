package com.taxi_pas_4.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;
import com.taxi_pas_4.cities.api.CityApiClient;
import com.taxi_pas_4.cities.api.CityResponse;
import com.taxi_pas_4.cities.api.CityResponseMerchantFondy;
import com.taxi_pas_4.cities.api.CityService;
import com.taxi_pas_4.ui.visicom.VisicomFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MyBottomSheetCityFragment extends BottomSheetDialogFragment {

    private static final String TAG = "TAG_CITY";
    ListView listView;
    String city;
    AppCompatButton btn_ok;
    private String cityMenu;
    private String message;

    public MyBottomSheetCityFragment() {
        // Пустой конструктор без аргументов
    }

    public MyBottomSheetCityFragment(String city) {
        this.city = city;
    }

    private final String[] cityList = new String[]{
            "Київ",
            "Дніпро",
            "Одеса",
            "Запоріжжя",
            "Черкаси",
            "Тест"
    };
    private final String[] cityCode = new String[]{
            "Kyiv City",
            "Dnipropetrovsk Oblast",
            "Odessa",
            "Zaporizhzhia",
            "Cherkasy Oblast",
            "OdessaTest",
    };

    int positionFirst;
    /**
     * Phone section
     */
    public static final String Kyiv_City_phone = "tel:0674443804";
    public static final String Dnipropetrovsk_Oblast_phone = "tel:0667257070";
    public static final String Odessa_phone = "tel:0737257070";
    public static final String Zaporizhzhia_phone = "tel:0687257070";
    public static final String Cherkasy_Oblast_phone = "tel:0962294243";
    String phoneNumber;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cities_list_layout, container, false);
        listView = view.findViewById(R.id.listViewBonus);
        VisicomFragment.progressBar.setVisibility(View.INVISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.services_adapter_layout, cityList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if (city == null) {
            List<String> listCity = logCursor(MainActivity.CITY_INFO);
            city = listCity.get(1);
        }
        switch (city) {
            case "Dnipropetrovsk Oblast":
                positionFirst = 1;
                phoneNumber = Dnipropetrovsk_Oblast_phone;
                cityMenu = getString(R.string.city_dnipro);
                break;
            case "Odessa":
                positionFirst = 2;
                phoneNumber = Odessa_phone;
                cityMenu = getString(R.string.city_odessa);
                break;
            case "Zaporizhzhia":
                positionFirst = 3;
                phoneNumber = Zaporizhzhia_phone;
                cityMenu = getString(R.string.city_zaporizhzhia);
                break;
            case "Cherkasy Oblast":
                positionFirst = 4;
                phoneNumber = Cherkasy_Oblast_phone;
                cityMenu = getString(R.string.city_cherkasy);
                break;
            case "OdessaTest":
                positionFirst = 5;
                phoneNumber = Kyiv_City_phone;
                cityMenu = "Test";
                break;
            default:
                phoneNumber = Kyiv_City_phone;
                positionFirst = 0;
                cityMenu = getString(R.string.city_kyiv);
                break;
        }
        ContentValues cv = new ContentValues();
        SQLiteDatabase database = view.getContext().openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        cv.put("city", cityCode[positionFirst]);
        cv.put("phone", phoneNumber);
        database.update(MainActivity.CITY_INFO, cv, "id = ?",
                new String[]{"1"});
        database.close();

        cityMaxPay(cityCode[positionFirst], getContext());
        merchantFondy(cityCode[positionFirst], getContext());
        listView.setItemChecked(positionFirst, true);
        int positionFirstOld = positionFirst;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionFirst = position;
                switch (cityCode[positionFirst]){
                    case "Dnipropetrovsk Oblast":
                        positionFirst = 1;
                        phoneNumber = Dnipropetrovsk_Oblast_phone;
                        cityMenu = getString(R.string.city_dnipro);
                        break;
                    case "Odessa":
                        positionFirst = 2;
                        phoneNumber = Odessa_phone;
                        cityMenu = getString(R.string.city_odessa);
                        break;
                    case "Zaporizhzhia":
                        positionFirst = 3;
                        phoneNumber = Zaporizhzhia_phone;
                        cityMenu = getString(R.string.city_zaporizhzhia);
                        break;
                    case "Cherkasy Oblast":
                        positionFirst = 4;
                        phoneNumber = Cherkasy_Oblast_phone;
                        cityMenu = getString(R.string.city_cherkasy);
                        break;
                    case "OdessaTest":
                        positionFirst = 5;
                        phoneNumber = Kyiv_City_phone;
                        cityMenu = "Test";
                        break;
                    default:
                        phoneNumber = Kyiv_City_phone;
                        positionFirst = 0;
                        cityMenu = getString(R.string.city_kyiv);
                        break;
                }
                if (positionFirstOld != positionFirst) {
                    ContentValues cv = new ContentValues();
                    SQLiteDatabase database = view.getContext().openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);

                    cv.put("city", cityCode[positionFirst]);
                    cv.put("phone", phoneNumber);
                    database.update(MainActivity.CITY_INFO, cv, "id = ?",
                            new String[]{"1"});
                    database.close();

                    cityMaxPay(cityCode[positionFirst], getContext());
                    merchantFondy(cityCode[positionFirst], getContext());

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                    resetRoutHome();
                    resetRoutMarker();

                    updateMyPosition(cityCode[positionFirst]);
                    navController.navigate(R.id.nav_visicom);

                    message = getString(R.string.change_message) + getString(R.string.hi_mes) + " "+ getString(R.string.menu_city) + " " + cityMenu + ".";

                    if (MainActivity.navVisicomMenuItem != null) {
                        // Новый текст элемента меню
                        String newTitle =  getString(R.string.menu_city) + " " + cityMenu;
                        // Изменяем текст элемента меню
                        MainActivity.navVisicomMenuItem.setTitle(newTitle);
                    }

                    MyBottomSheetMessageFragment bottomSheetDialogFragment = new MyBottomSheetMessageFragment(message);
                    bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
                }
                dismiss();
            }
        });

        return view;
    }

    private void updateMyPosition(String city) {

        double startLat;
        double startLan;
        String position;

        switch (city){
            case "Dnipropetrovsk Oblast":
                // Днепр
                position = "просп.Дмитра Яворницького (Карла Маркса), буд.52, місто Дніпро\t";
                startLat = 48.4647;
                startLan = 35.0462;
                break;
            case "Odessa":
            case "OdessaTest":
                // Одесса
                position = "вул.Пантелеймонівська, буд. 64, місто Одеса\t";
                startLat = 46.4694;
                startLan = 30.7404;
                break;
            case "Zaporizhzhia":
                // Запорожье
                position = "просп. Соборний, буд. 139, місто Запоріжжя\t";
                startLat = 47.84015;
                startLan = 35.13634;
                break;
            case "Cherkasy Oblast":
                // Черкассы
                position = "вул.Байди Вишневецького, буд.36, місто Черкаси\t";
                startLat = 49.44469;
                startLan = 32.05728;
                break;
            default:
                position = "вул.Хрещатик, буд.22, місто Київ\t";
                startLat = 50.4501;
                startLan = 30.5234;
                break;
        }



        SQLiteDatabase database = requireActivity().openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
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


        List<String> settings = new ArrayList<>();


        settings.add(Double.toString(startLat));
        settings.add(Double.toString(startLan));
        settings.add(Double.toString(startLat));
        settings.add(Double.toString(startLan));
        settings.add(position);
        settings.add(getString(R.string.on_city_tv));
        updateRoutMarker(settings);

    }

    private void updateRoutMarker(List<String> settings) {
        Log.d(TAG, "updateRoutMarker: " + settings.toString());
        ContentValues cv = new ContentValues();

        cv.put("startLat", Double.parseDouble(settings.get(0)));
        cv.put("startLan", Double.parseDouble(settings.get(1)));
        cv.put("to_lat", Double.parseDouble(settings.get(2)));
        cv.put("to_lng", Double.parseDouble(settings.get(3)));
        cv.put("start", settings.get(4));
        cv.put("finish", settings.get(5));

        // обновляем по id
        SQLiteDatabase database = requireActivity().openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        database.update(MainActivity.ROUT_MARKER, cv, "id = ?",
                new String[]{"1"});
        database.close();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private void cityMaxPay(String city, Context context) {
        CityService cityService = CityApiClient.getClient().create(CityService.class);

        // Замените "your_city" на фактическое название города
        Call<CityResponse> call = cityService.getMaxPayValues(city);

        call.enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(Call<CityResponse> call, Response<CityResponse> response) {
                if (response.isSuccessful()) {
                    CityResponse cityResponse = response.body();
                    if (cityResponse != null) {
                        int cardMaxPay = cityResponse.getCardMaxPay();
                        int bonusMaxPay = cityResponse.getBonusMaxPay();

                        ContentValues cv = new ContentValues();
                        cv.put("card_max_pay", cardMaxPay);
                        cv.put("bonus_max_pay", bonusMaxPay);

                            SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                            database.update(MainActivity.CITY_INFO, cv, "id = ?",
                                    new String[]{"1"});

                            database.close();


                        // Добавьте здесь код для обработки полученных значений
                    }
                } else {
                    Log.e("Request", "Failed. Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CityResponse> call, @NonNull Throwable t) {
                Log.e("Request", "Failed. Error message: " + t.getMessage());
            }
        });
    }

    private void merchantFondy(String city, Context context) {
        CityService cityService = CityApiClient.getClient().create(CityService.class);

        // Замените "your_city" на фактическое название города
        Call<CityResponseMerchantFondy> call = cityService.getMerchantFondy(city);

        call.enqueue(new Callback<CityResponseMerchantFondy>() {
            @Override
            public void onResponse(@NonNull Call<CityResponseMerchantFondy> call, @NonNull Response<CityResponseMerchantFondy> response) {
                if (response.isSuccessful()) {
                    CityResponseMerchantFondy cityResponse = response.body();
                    Log.d(TAG, "onResponse: cityResponse" + cityResponse);
                    if (cityResponse != null) {
                        String merchant_fondy = cityResponse.getMerchantFondy();
                        String fondy_key_storage = cityResponse.getFondyKeyStorage();

                        ContentValues cv = new ContentValues();
                        cv.put("merchant_fondy", merchant_fondy);
                        cv.put("fondy_key_storage", fondy_key_storage);


                            SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                            database.update(MainActivity.CITY_INFO, cv, "id = ?",
                                    new String[]{"1"});

                            database.close();



                        Log.d(TAG, "onResponse: merchant_fondy" + merchant_fondy);
                        Log.d(TAG, "onResponse: fondy_key_storage" + fondy_key_storage);


                        // Добавьте здесь код для обработки полученных значений
                    }
                } else {
                    Log.e("Request", "Failed. Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CityResponseMerchantFondy> call, @NonNull Throwable t) {
                Log.e("Request", "Failed. Error message: " + t.getMessage());
            }
        });
    }

    public void resetRoutHome() {
        ContentValues cv = new ContentValues();

        cv.put("from_street", " ");
        cv.put("from_number", " ");
        cv.put("to_street", " ");
        cv.put("to_number", " ");

        // обновляем по id
        SQLiteDatabase database = requireContext().openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        database.update(MainActivity.ROUT_HOME, cv, "id = ?",
                new String[]{"1"});
        database.close();
    }
    private void resetRoutMarker() {
        List<String> settings = new ArrayList<>();

            settings.add("0");
            settings.add("0");
            settings.add("0");
            settings.add("0");
            settings.add("");
            settings.add("");

        ContentValues cv = new ContentValues();

        cv.put("startLat", Double.parseDouble(settings.get(0)));
        cv.put("startLan", Double.parseDouble(settings.get(1)));
        cv.put("to_lat", Double.parseDouble(settings.get(2)));
        cv.put("to_lng", Double.parseDouble(settings.get(3)));
        cv.put("start", settings.get(4));
        cv.put("finish", settings.get(5));

        // обновляем по id
        SQLiteDatabase database = requireActivity().openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        database.update(MainActivity.ROUT_MARKER, cv, "id = ?",
                new String[]{"1"});
        database.close();

    }

    @SuppressLint("Range")
    public List<String> logCursor(String table) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getContext().openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        Cursor c = db.query(table, null, null, null, null, null, null);
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
        db.close();
        return list;
    }
}

