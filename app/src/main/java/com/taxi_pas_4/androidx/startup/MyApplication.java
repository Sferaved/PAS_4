package com.taxi_pas_4.androidx.startup;

import static com.taxi_pas_4.ui.clear.AppDataUtils.clearAllSharedPreferences;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.multidex.BuildConfig;
import androidx.navigation.NavOptions;

import com.github.anrwatchdog.ANRWatchDog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;
import com.taxi_pas_4.utils.connect.NetworkUtils;
import com.taxi_pas_4.utils.helpers.TelegramUtils;
import com.taxi_pas_4.utils.log.Logger;
import com.taxi_pas_4.utils.preferences.SharedPreferencesHelper;
import com.taxi_pas_4.utils.time_ut.IdleTimeoutManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    private boolean isAppInForeground = false;
    private final String TAG = "MyApplication";
    private static final String LOG_FILE_NAME = "app_log.txt";
    @SuppressLint("StaticFieldLeak")
    private static MyApplication instance;
    @SuppressLint("StaticFieldLeak")
    private static Activity currentActivity = null;

    public static SharedPreferencesHelper sharedPreferencesHelperMain;

    private ThreadPoolExecutor threadPoolExecutor;
    private IdleTimeoutManager idleTimeoutManager;
    private long backgroundStartTime = 0;
    private long lastMemoryWarningTime = 0;
    private long lastInternetWarningTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferencesHelperMain = new SharedPreferencesHelper(this);


        instance = this;

        applyLocale();

//        checkAndClearPrefs(this);
        // Установка глобального обработчика исключений
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(this));

        initializeFirebaseAndCrashlytics();
        setupANRWatchDog();
        setDefaultOrientation();
        registerActivityLifecycleCallbacks();
        initializeThreadPoolExecutor();
    }
    private static final String PREFS_VERSION_KEY = "SharedPrefsVersion";

    private void checkAndClearPrefs(Context context) {
        SharedPreferences prefs = sharedPreferencesHelperMain.getSharedPreferences();
        int savedVersion = prefs.getInt(PREFS_VERSION_KEY, -1);
        int currentVersion = BuildConfig.VERSION_CODE;

        // Очищаем SharedPreferences только при новой установке (ключ отсутствует)
        if (savedVersion == -1) {
            clearAllSharedPreferences(context);
            // Сохраняем текущую версию после очистки
            prefs.edit().putInt(PREFS_VERSION_KEY, currentVersion).apply();
        }
    }
    private void initializeThreadPoolExecutor() {
        // Настройка ThreadPoolExecutor
        threadPoolExecutor = new ThreadPoolExecutor(
                4,  // минимальное количество потоков
                8,  // максимальное количество потоков
                1, TimeUnit.MINUTES, // время ожидания новых задач
                new LinkedBlockingQueue<>() // очередь для задач
        );
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setDefaultOrientation() {
        // Установка ориентации экрана в портретный режим
        // Это может не сработать для всех активити
        Activity activity = getCurrentActivity();
        if (activity != null) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    // Для получения текущей активити (необходимый метод, чтобы использовать его в setDefaultOrientation)
    private Activity getCurrentActivity() {
        return currentActivity;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    private void initializeFirebaseAndCrashlytics() {
        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Set up Firebase Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    }

    private void setupANRWatchDog() {
        // Set default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());

        // Configure ANRWatchDog for ANR detection
        new ANRWatchDog().setANRListener(error -> {
            // Use Handler to show Toast on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(getApplicationContext(), R.string.anr_message, Toast.LENGTH_LONG).show();
            });
            // Log the error
            Logger.e(getApplicationContext(),TAG, "ANR occurred: " + error.toString());

            // Log the ANR event to Firebase Crashlytics
            FirebaseCrashlytics.getInstance().recordException(error);
        }).start();
    }

    private void registerActivityLifecycleCallbacks() {
        // Register ActivityLifecycleCallbacks to track foreground/background state
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                currentActivity = activity;
                idleTimeoutManager = new IdleTimeoutManager(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                startMemoryMonitoring();
                if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                    if (MainActivity.currentNavDestination != R.id.nav_restart) {
                        MainActivity.currentNavDestination = R.id.nav_restart; // Устанавливаем текущий экран
                        MainActivity.navController.navigate(R.id.nav_restart, null, new NavOptions.Builder()
                                .setPopUpTo(R.id.nav_restart, true)
                                .build());
                    }
                    return;
                }


                // Проверка длительного времени в фоне
//                if (backgroundStartTime > 0) {
//                    long timeInBackground = System.currentTimeMillis() - backgroundStartTime;
//                    if (timeInBackground > 30 * 60 * 1000) { // 30 минут
//                        restartApplication(activity);
//                        backgroundStartTime = 0;
//                        return;
//                    }
//                }
                isAppInForeground = true;
                if (idleTimeoutManager != null) {
                    idleTimeoutManager.resetTimer();
                }
            }


            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                isAppInForeground = false;
//                backgroundStartTime = System.currentTimeMillis();
                stopMemoryMonitoring(); // Останавливаем мониторинг при паузе
                currentActivity = null;
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (currentActivity == activity) {
                    currentActivity = null;
                }
            }
        });
    }

    private final Handler memoryCheckHandler = new Handler();
    private final Runnable memoryCheckRunnable = new Runnable() {
        @Override
        public void run() {
            // Выполняем проверку памяти
            if (currentActivity != null) {
                checkMemoryUsage(currentActivity);
            }
            // Повторяем выполнение через 5 секунд (5000 миллисекунд)
            memoryCheckHandler.postDelayed(this, 5000);
        }
    };

    // Запуск мониторинга
    public void startMemoryMonitoring() {
        memoryCheckHandler.post(memoryCheckRunnable);
    }

    // Остановка мониторинга
    public void stopMemoryMonitoring() {
        memoryCheckHandler.removeCallbacks(memoryCheckRunnable);
    }



    private void checkMemoryUsage(Activity activity) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);

        if (memoryInfo.lowMemory) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMemoryWarningTime > 60 * 1000) { // Уведомление раз в 60 секунд
                // Отобразите уведомление пользователю

                String message = getString(R.string.low_memory_0) + memoryInfo.availMem + getString(R.string.low_memory_1) + memoryInfo.lowMemory;
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

                lastMemoryWarningTime = currentTime;

            }


        }


        long currentTime = System.currentTimeMillis();
        if (currentTime - lastInternetWarningTime > 30 * 1000) { // Уведомление раз в 30 секунд
            NetworkUtils.isInternetStable(new NetworkUtils.ApiCallback() {
                @Override
                public void onSuccess(boolean isStable) {
                    if (isStable) {
                        Logger.d(activity,"NetworkCheck", "Internet is stable.");
                    } else {
                        activity.runOnUiThread(() ->
                                Toast.makeText(activity, R.string.low_connect, Toast.LENGTH_SHORT).show()
                        );
                        Logger.d(activity,"NetworkCheck", "Internet is unstable.");
                    }

                    // Запуск Toast в основном потоке


                    lastInternetWarningTime = currentTime;
                }

                @Override
                public void onFailure(Throwable t) {
                    Logger.e(activity,"NetworkCheck", "Error checking internet stability." + t);
                }
            });

        }


        Logger.d(activity,"MemoryMonitor", "Свободная память: " + memoryInfo.availMem + " байт");
        Logger.d(activity,"MemoryMonitor", "Состояние нехватки памяти: " + memoryInfo.lowMemory);
    }


    private void applyLocale() {
        Log.d(TAG, "applyLocale: " + Locale.getDefault().toString());
        String localeCode = (String) sharedPreferencesHelperMain.getValue("locale", Locale.getDefault().toString());
        Locale locale = new Locale(localeCode.split("_")[0]);

        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void restartApplication(Activity activity) {
        Intent intent = activity.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish(); // Завершаем текущую активность
            Runtime.getRuntime().exit(0); // Полный выход
        }
    }

    public boolean isAppInForeground() {
        return isAppInForeground;
    }

    // Новый обработчик необработанных исключений для записи логов и Firebase Crashlytics
    private static class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
            // Логирование исключений
            Logger.d(currentActivity,"MyExceptionHandler", "Uncaught Exception occurred: " + throwable.getMessage() + throwable);

            // Запись ошибки в Firebase Crashlytics
            FirebaseCrashlytics.getInstance().recordException(throwable);

            // Возможная перезагрузка или очистка данных
        }
    }

    private class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        public MyUncaughtExceptionHandler(MyApplication myApplication) {
        }

        @Override
        public void uncaughtException(Thread t, @NonNull Throwable e) {
            // Запись лога
            writeLog(Log.getStackTraceString(e));

            // Сообщение об ошибке
            String errorMessage = "Uncaught exception in thread " + t.getName() + ": " + e.getMessage();

            // Отправка ошибки в Telegram

            String logFilePath = getExternalFilesDir(null) + "/app_log.txt"; // Путь к лог-файлу
            TelegramUtils.sendErrorToTelegram(errorMessage, logFilePath);
            // Перезапуск приложения или завершение работы
            System.exit(1); // Завершаем приложение
        }
    }


    public void writeLog(String log) {
        if (isExternalStorageWritable()) {
            File logFile = new File(getExternalFilesDir(null), LOG_FILE_NAME);
            try (FileOutputStream fos = new FileOutputStream(logFile, true);
                 OutputStreamWriter osw = new OutputStreamWriter(fos)) {

                // Установка украинского времени
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

                osw.write(sdf.format(new Date()) + " - " + log);
                osw.write("\n");

                Logger.e(getApplicationContext(),TAG, "Log written to " + logFile.getAbsolutePath());
            } catch (IOException e) {
                Logger.d(getApplicationContext(),"MyAppLogger", "Failed to write log" + e);
            }
        } else {
            Logger.d(getApplicationContext(),"MyAppLogger", "External storage is not writable");
        }
    }

    // Метод для проверки доступности внешнего хранилища
    private boolean isExternalStorageWritable() {
        String state = android.os.Environment.getExternalStorageState();
        return android.os.Environment.MEDIA_MOUNTED.equals(state);
    }

    // Пример использования ThreadPoolExecutor для асинхронных задач
    public void executeBackgroundTask(Runnable task) {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.execute(task);
        }
    }
}
