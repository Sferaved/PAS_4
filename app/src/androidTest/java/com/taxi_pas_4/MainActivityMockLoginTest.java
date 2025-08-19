package com.taxi_pas_4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class MainActivityMockLoginTest {

    @Mock
    private FirebaseAuthUIAuthenticationResult mockAuthResult;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMockLoginWorks() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {

            // Вызов утилиты mockLogin
            TestUtils.mockLogin(scenario, mockAuthResult);

            // Проверяем, что поля MainActivity заполнились
            scenario.onActivity(activity -> {
                String userEmail = activity.getUserEmailForTest();
                assertNotNull("Пользователь должен быть авторизован после mockLogin", userEmail);
                assertEquals("Email должен быть 'test@example.com'",
                        "test@example.com", userEmail);

                String username = activity.getUsernameForTest();
                assertEquals("username должен быть 'username'", "username", username);
            });
        }
    }
}
