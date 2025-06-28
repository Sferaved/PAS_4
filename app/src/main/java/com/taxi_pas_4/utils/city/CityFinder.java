package com.taxi_pas_4.utils.city;

import static android.content.Context.MODE_PRIVATE;
import static com.taxi_pas_4.androidx.startup.MyApplication.getCurrentActivity;
import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;
import com.taxi_pas_4.ui.card.CardInfo;
import com.taxi_pas_4.ui.cities.api.CityApiClient;
import com.taxi_pas_4.ui.cities.api.CityService;
import com.taxi_pas_4.ui.payment_system.PayApi;
import com.taxi_pas_4.ui.payment_system.ResponsePaySystem;
import com.taxi_pas_4.ui.visicom.VisicomFragment;
import com.taxi_pas_4.ui.wfp.token.CallbackResponseWfp;
import com.taxi_pas_4.ui.wfp.token.CallbackServiceWfp;
import com.taxi_pas_4.utils.data.DataArr;
import com.taxi_pas_4.utils.ip.ApiServiceCountry;
import com.taxi_pas_4.utils.ip.CountryResponse;
import com.taxi_pas_4.utils.log.Logger;

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

public class CityFinder {
    private static final String TAG = "CityFinder";
    Context context;
    String countryState;
    String baseUrl;
    String pay_method;
    String phoneNumber;

    double startLat;
    double startLan;
    String position;

    public CityFinder() {
        // Пустой конструктор без аргументов
    }

    public CityFinder (
            Context context
    ) {
        this.context = context;
    }

    public CityFinder (
            Context context,
            double startLat,
            double startLan,
            String position
    ) {
        this.context = context;
        this.startLat = startLat;
        this.startLan = startLan;
        this.position = position;
    }

    public void findCity(double latitude, double longitude) {
        CityApiService apiService = RetrofitClient.getClient().create(CityApiService.class);
        Call<CityResponse> call = apiService.findCity(latitude, longitude);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CityResponse> call, @NonNull Response<CityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String city = response.body().getCity();
                    Log.d(TAG, "City: " + city);

                    cityVerify(city);
                } else {
                    Log.e(TAG, "Request failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CityResponse> call, @NonNull Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.e(TAG, "Error: " + t.getMessage(), t);
            }
        });
    }
    
    private void cityVerify (String city) {
        String cityResult;
        switch (city) {
            case "city_kiev":
                cityResult = "Kyiv City";
                break;
            case "city_cherkassy":
                cityResult = "Cherkasy Oblast";
                break;
            case "city_odessa":
                cityResult = "Odessa";
                break;
            case "city_zaporizhzhia":
                cityResult = "Zaporizhzhia";
                break;
            case "city_dnipro":
                cityResult = "Dnipropetrovsk Oblast";
                break;
            case "city_lviv":
                cityResult = "Lviv";
                break;
            case "city_ivano_frankivsk":
                cityResult = "Ivano_frankivsk";
                break;
            case "city_vinnytsia":
                cityResult = "Vinnytsia";
                break;
            case "city_poltava":
                cityResult = "Poltava";
                break;
            case "city_sumy":
                cityResult = "Sumy";
                break;
            case "city_kharkiv":
                cityResult = "Kharkiv";
                break;
            case "city_chernihiv":
                cityResult = "Chernihiv";
                break;
            case "city_rivne":
                cityResult = "Rivne";
                break;
            case "city_ternopil":
                cityResult = "Ternopil";
                break;
            case "city_khmelnytskyi":
                cityResult = "Khmelnytskyi";
                break;
            case "city_zakarpattya":
                cityResult = "Zakarpattya";
                break;
            case "city_zhytomyr":
                cityResult = "Zhytomyr";
                break;
            case "city_kropyvnytskyi":
                cityResult = "Kropyvnytskyi";
                break;
            case "city_mykolaiv":
                cityResult = "Mykolaiv";
                break;
            case "city_chernivtsi":
                cityResult = "Сhernivtsi";
                break;
            case "city_lutsk":
                cityResult = "Lutsk";
                break;
            default:
                cityResult = "all";
        }
        List<String> stringList = logCursor(MainActivity.CITY_INFO);
        city = stringList.get(1);

        if(!city.equals(cityResult)) {
            sharedPreferencesHelperMain.saveValue("setStatusX", false);
            Log.d(TAG, "City: " + city);
            Log.d(TAG, "cityResult: " + cityResult);

            String newTitle;


            String Kyiv_City_phone = "tel:0674443804";
            String Dnipropetrovsk_Oblast_phone = "tel:0667257070";
            String Odessa_phone = "tel:0737257070";
            String Zaporizhzhia_phone = "tel:0687257070";
            String Cherkasy_Oblast_phone = "tel:0962294243";

            String cityMenu;

            switch (cityResult){
                case "Kyiv City":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_kyiv);
                    countryState = "UA";
                    break;
                case "Dnipropetrovsk Oblast":

                    phoneNumber = Dnipropetrovsk_Oblast_phone;
                    cityMenu = context.getString(R.string.city_dnipro);
                    countryState = "UA";
                    break;
                case "Odessa":

                    phoneNumber = Odessa_phone;
                    cityMenu = context.getString(R.string.city_odessa);
                    countryState = "UA";
                    break;
                case "Zaporizhzhia":

                    phoneNumber = Zaporizhzhia_phone;
                    cityMenu = context.getString(R.string.city_zaporizhzhia);
                    countryState = "UA";
                    break;
                case "Cherkasy Oblast":

                    phoneNumber = Cherkasy_Oblast_phone;
                    cityMenu = context.getString(R.string.city_cherkassy);
                    countryState = "UA";
                    break;
                case "Lviv":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_lviv);
                    countryState = "UA";
                    break;
                case "Ivano_frankivsk":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_ivano_frankivsk);
                    countryState = "UA";
                    break;
                case "Vinnytsia":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_vinnytsia);
                    countryState = "UA";
                    break;
                case "Poltava":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_poltava);
                    countryState = "UA";
                    break;
                case "Sumy":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_sumy);
                    countryState = "UA";
                    break;
                case "Kharkiv":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_kharkiv);
                    countryState = "UA";
                    break;
                case "Chernihiv":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_chernihiv);
                    countryState = "UA";
                    break;
                case "Rivne":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_rivne);
                    countryState = "UA";
                    break;
                case "Ternopil":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_ternopil);
                    countryState = "UA";
                    break;
                case "Khmelnytskyi":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_khmelnytskyi);
                    countryState = "UA";
                    break;
                case "Zakarpattya":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_zakarpattya);
                    countryState = "UA";
                    break;
                case "Zhytomyr":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_zhytomyr);
                    countryState = "UA";
                    break;
                case "Kropyvnytskyi":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_kropyvnytskyi);
                    countryState = "UA";
                    break;
                case "Mykolaiv":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_mykolaiv);
                    countryState = "UA";
                    break;
                case "Chernivtsi":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_chernivtsi);
                    countryState = "UA";
                    break;
                case "Lutsk":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.city_chernivtsi);
                    countryState = "UA";
                    break;
                case "OdessaTest":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = "Test";
                    countryState = "UA";
                    break;
                case "foreign countries":

                    phoneNumber = Kyiv_City_phone;
                    cityMenu = context.getString(R.string.foreign_countries);
                    break;
                default:
                    phoneNumber = Kyiv_City_phone;
                    getPublicIPAddress();
                    cityMenu = context.getString(R.string.city_kyiv);
                    countryState = "UA";
                    break;
            }

            newTitle =  context.getString(R.string.menu_city) + " " + cityMenu;
            sharedPreferencesHelperMain.saveValue("newTitle", newTitle);

            sharedPreferencesHelperMain.saveValue("countryState", countryState);

            Logger.d(context, TAG, "onItemClick: pay_method" + pay_method);

            updateMyPosition(
                    cityResult,
                    startLat,
                    startLan,
                    position
            );

        }

    }
    private void updateMyPosition(
            String city,
            double startLat,
            double startLan,
            String position
    ) {

        Logger.d(context, TAG, "updateMyPosition:city "+ city);

        switch (city){
            case "Dnipropetrovsk Oblast":
            case "Odessa":
            case "Zaporizhzhia":
            case "Cherkasy Oblast":
            case "Kyiv City":
            case "Lviv":
            case "Ivano_frankivsk":
            case "Vinnytsia":
            case "Poltava":
            case "Sumy":
            case "Kharkiv":
            case "Chernihiv":
            case "Rivne":
            case "Ternopil":
            case "Khmelnytskyi":
            case "Zakarpattya":
            case "Zhytomyr":
            case "Kropyvnytskyi":
            case "Mykolaiv":
            case "Chernivtsi":
            case "Lutsk":
                sharedPreferencesHelperMain.saveValue("baseUrl", "https://m.easy-order-taxi.site");
                break;
            case "OdessaTest":
//                sharedPreferencesHelperMain.saveValue("baseUrl", "https://test-taxi.kyiv.ua");
                sharedPreferencesHelperMain.saveValue("baseUrl", "https://t.easy-order-taxi.site");
                break;
            default:
                sharedPreferencesHelperMain.saveValue("baseUrl", "https://m.easy-order-taxi.site");
                city = "foreign countries";
        }


//        pay_system(city);

        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);

        ContentValues cv = new ContentValues();

        cv.put("city", city);
        cv.put("phone", phoneNumber);
        database.update(MainActivity.CITY_INFO, cv, "id = ?", new String[]{"1"});

        cv = new ContentValues();
        cv.put("startLat", startLat);
        cv.put("startLan", startLan);
        cv.put("position", position);
        database.update(MainActivity.TABLE_POSITION_INFO, cv, "id = ?",
                new String[] { "1" });

        cv = new ContentValues();
        cv.put("tarif", " ");
        database.update(MainActivity.TABLE_SETTINGS_INFO, cv, "id = ?",
                new String[] { "1" });

        cv = new ContentValues();
        cv.put("payment_type", "nal_payment");

        database.update(MainActivity.TABLE_SETTINGS_INFO, cv, "id = ?",
                new String[] { "1" });
        database.close();

        List<String> settings = new ArrayList<>();

        settings.add(Double.toString(startLat));
        settings.add(Double.toString(startLan));
        settings.add(Double.toString(startLat));
        settings.add(Double.toString(startLan));
        settings.add(position);
        settings.add("");

        updateRoutMarker(settings);
        clearTABLE_SERVICE_INFO();
        sharedPreferencesHelperMain.saveValue("time", "no_time");
        sharedPreferencesHelperMain.saveValue("date", "no_date");
        sharedPreferencesHelperMain.saveValue("comment", "no_comment");
        sharedPreferencesHelperMain.saveValue("tarif", " ");
        sharedPreferencesHelperMain.saveValue("CityCheckActivity", "run");

//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        context.startActivity(intent);
        NavController navController = Navigation.findNavController(getCurrentActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.nav_visicom, null, new NavOptions.Builder()
                .setPopUpTo(R.id.nav_visicom, true)
                .build());
    }
    private void clearTABLE_SERVICE_INFO () {
        String[] arrayServiceCode = DataArr.arrayServiceCode();
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        for (int i = 0; i < arrayServiceCode.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put(arrayServiceCode[i], "0");
            database.update(MainActivity.TABLE_SERVICE_INFO, cv, "id = ?",
                    new String[] { "1" });
        }
        database.close();
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

        // обновляем по id
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        database.update(MainActivity.ROUT_MARKER, cv, "id = ?",
                new String[]{"1"});
        database.close();
    }

    private void pay_system(String city) {
        baseUrl = (String) sharedPreferencesHelperMain.getValue("baseUrl", "https://m.easy-order-taxi.site");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PayApi apiService = retrofit.create(PayApi.class);
        Call<ResponsePaySystem> call = apiService.getPaySystem();
        call.enqueue(new Callback<ResponsePaySystem>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePaySystem> call, @NonNull Response<ResponsePaySystem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Обработка успешного ответа
                    ResponsePaySystem responsePaySystem = response.body();
                    assert responsePaySystem != null;
                    String paymentCode = responsePaySystem.getPay_system();

                    switch (paymentCode) {
                        case "wfp":
                            pay_method = "wfp_payment";
                            cityMaxPay(city);
                            Logger.d(context, TAG, "2");
                            getCardTokenWfp(city);
                            break;
//                        case "fondy":
//                            pay_method = "fondy_payment";
//                            cityMaxPay(cityCodeNew);
//                            Logger.d(context, TAG, "3");
//                            merchantFondy(cityCodeNew, context);
//                            break;
//                        case "mono":
//                            pay_method = "mono_payment";
//                            break;
                    }

                        ContentValues cv = new ContentValues();
                        cv.put("payment_type", pay_method);
                        // обновляем по id
                        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                        database.update(MainActivity.TABLE_SETTINGS_INFO, cv, "id = ?",
                                new String[] { "1" });
                        database.close();

                    }

            }

            @Override
            public void onFailure(@NonNull Call<ResponsePaySystem> call, @NonNull Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Logger.d(context, TAG, "Failed. Error message: " + t.getMessage());

            }
        });
    }

    private void getCardTokenWfp(String city) {
        String tableName = MainActivity.TABLE_WFP_CARDS; // Например, "wfp_cards"
        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        database.execSQL("DELETE FROM " + tableName + ";");
        database.close();


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        baseUrl = (String) sharedPreferencesHelperMain.getValue("baseUrl", "https://m.easy-order-taxi.site");

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS) // Тайм-аут на соединение
                .readTimeout(30, TimeUnit.SECONDS)    // Тайм-аут на чтение данных
                .writeTimeout(30, TimeUnit.SECONDS)   // Тайм-аут на запись данных
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl) // Замените на фактический URL вашего сервера
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        // Создайте сервис
        CallbackServiceWfp service = retrofit.create(CallbackServiceWfp.class);
        Logger.d(context, TAG, "getCardTokenWfp: ");
        String email = logCursor(MainActivity.TABLE_USER_INFO).get(3);
        // Выполните запрос
        Call<CallbackResponseWfp> call = service.handleCallbackWfpCardsId(
                context.getString(R.string.application),
                city,
                email,
                "wfp"
        );
        call.enqueue(new Callback<CallbackResponseWfp>() {
            @Override
            public void onResponse(@NonNull Call<CallbackResponseWfp> call, @NonNull Response<CallbackResponseWfp> response) {
                Logger.d(context, TAG, "onResponse: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    CallbackResponseWfp callbackResponse = response.body();
                    if (callbackResponse != null) {
                        List<CardInfo> cards = callbackResponse.getCards();
                        Logger.d(context, TAG, "onResponse: cards" + cards);
                        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                        database.delete(MainActivity.TABLE_WFP_CARDS, "1", null);
                        if (cards != null && !cards.isEmpty()) {
                            for (CardInfo cardInfo : cards) {
                                String masked_card = cardInfo.getMasked_card(); // Маска карты
                                String card_type = cardInfo.getCard_type(); // Тип карты
                                String bank_name = cardInfo.getBank_name(); // Название банка
                                String rectoken = cardInfo.getRectoken(); // Токен карты
                                String merchant = cardInfo.getMerchant(); // Токен карты

                                Logger.d(context, TAG, "onResponse: card_token: " + rectoken);
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
                            if (cursor.moveToFirst()) {
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

                }
            }

            @Override
            public void onFailure(@NonNull Call<CallbackResponseWfp> call, @NonNull Throwable t) {
                // Обработка ошибки запроса
                FirebaseCrashlytics.getInstance().recordException(t);
                Logger.d(context, TAG, "Failed. Error message: " + t.getMessage());

            }
        });
    }

    private void cityMaxPay(String city) {


        String BASE_URL =sharedPreferencesHelperMain.getValue("baseUrl", "https://m.easy-order-taxi.site") + "/";
        CityApiClient cityApiClient = new CityApiClient(BASE_URL);
        CityService cityService = cityApiClient.getClient().create(CityService.class);

        // Замените "your_city" на фактическое название города
        Call<com.taxi_pas_4.ui.cities.api.CityResponse> call = cityService.getMaxPayValues(city, context.getString(R.string.application));

        call.enqueue(new Callback<com.taxi_pas_4.ui.cities.api.CityResponse>() {
            @Override
            public void onResponse(@NonNull Call<com.taxi_pas_4.ui.cities.api.CityResponse> call, @NonNull Response<com.taxi_pas_4.ui.cities.api.CityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.taxi_pas_4.ui.cities.api.CityResponse cityResponse = response.body();
                    if (cityResponse != null) {
                        int cardMaxPay = cityResponse.getCardMaxPay();
                        int bonusMaxPay = cityResponse.getBonusMaxPay();
                        String black_list = cityResponse.getBlack_list();

                        ContentValues cv = new ContentValues();
                        cv.put("card_max_pay", cardMaxPay);
                        cv.put("bonus_max_pay", bonusMaxPay);
                        sharedPreferencesHelperMain.saveValue("black_list", black_list);
                        Logger.d(context, TAG, "black_list 2" + black_list);

                        SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
                        database.update(MainActivity.CITY_INFO, cv, "id = ?",
                                new String[]{"1"});

                        database.close();
                    }
                } else {
                    Logger.d(context, TAG, "Failed. Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.taxi_pas_4.ui.cities.api.CityResponse> call, @NonNull Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Logger.d(context, TAG, "Failed. Error message: " + t.getMessage());
            }
        });
    }

    public void getPublicIPAddress() {
        getCountryByIP();
    }

    private void getCountryByIP() {
        ApiServiceCountry apiService = com.taxi_pas_4.utils.ip.RetrofitClient.getClient().create(ApiServiceCountry.class);
        Call<CountryResponse> call = apiService.getCountryByIP("ipAddress");
        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CountryResponse> call, @NonNull Response<CountryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CountryResponse countryResponse = response.body();
                    assert countryResponse != null;
                    Logger.d(context, TAG, "onResponse:countryResponse.getCountry(); " + countryResponse.getCountry());
                    countryState = countryResponse.getCountry();
                } else {
                    countryState = "UA";
                }
                sharedPreferencesHelperMain.saveValue("countryState", countryState);
            }

            @Override
            public void onFailure(@NonNull Call<CountryResponse> call, @NonNull Throwable t) {
                Logger.d(context, TAG, "Error: " + t.getMessage());
                FirebaseCrashlytics.getInstance().recordException(t);
                VisicomFragment.progressBar.setVisibility(View.GONE);;
                sharedPreferencesHelperMain.saveValue("countryState", "UA");
            }
        });
    }

    @SuppressLint("Range")
    public List<String> logCursor(String table) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = context.openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        @SuppressLint("Recycle") Cursor c = db.query(table, null, null, null, null, null, null);
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
        db.close();
        return list;
    }

}

