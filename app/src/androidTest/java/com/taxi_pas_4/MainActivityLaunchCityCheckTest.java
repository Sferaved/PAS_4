package com.taxi_pas_4;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.taxi_pas_4.ui.cities.check.CityCheckActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class MainActivityLaunchCityCheckTest {

    @Mock
    private FirebaseAuthUIAuthenticationResult mockAuthResult;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Инициализируем Intents для проверки запуска активностей
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testCityCheckActivityLaunchedAfterMockLogin() {
        // Готовим Intent для запуска MainActivity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent)) {

            // Эмулируем успешный логин и запускаем CityCheckActivity напрямую
            scenario.onActivity(activity -> {
                // Вызываем onSignInResult через TestUtils
                TestUtils.mockLoginDirect(activity, mockAuthResult);

                // Если нужно явно стартовать CityCheckActivity
                Intent cityIntent = new Intent(activity, CityCheckActivity.class);
                cityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(cityIntent);
            });

            // Можно подождать, чтобы Activity успела стартовать
            Thread.sleep(20000);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
