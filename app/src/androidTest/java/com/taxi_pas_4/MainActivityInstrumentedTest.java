package com.taxi_pas_4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.taxi_pas_4.utils.connect.NetworkUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @Mock
    private FirebaseAuth mockFirebaseAuth;

    @Mock
    private FirebaseUser mockFirebaseUser;

    @Mock
    private NavController mockNavController;

    @Mock
    private FirebaseAuthUIAuthenticationResult mockAuthResult;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getEmail()).thenReturn("test@example.com");
    }




    @Test
    public void testTitleTextViewDisplaysCity() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            Context context = ApplicationProvider.getApplicationContext();
            String expectedText = context.getString(R.string.menu_city) + " " + context.getString(R.string.city_kyiv);
            onView(withId(R.id.action_bar_title))
                    .check(matches(withText(expectedText)));
        }
    }

    @Test
    public void testDatabaseInitializationForUserInfoTable() {
        // Launch the MainActivity using ActivityScenario
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            // Get the application context
            Context context = ApplicationProvider.getApplicationContext();

            // Access the database
            SQLiteDatabase database = context.openOrCreateDatabase(MainActivity.DB_NAME, Context.MODE_PRIVATE, null);

            // Query the TABLE_USER_INFO to check if it was initialized correctly
            Cursor cursor = database.query(MainActivity.TABLE_USER_INFO, null, null, null, null, null, null);

            try {
                // Verify that the table has exactly one row (as per insertUserInfo logic)
                assertEquals("Table USER_INFO should contain exactly one row after initialization", 1, cursor.getCount());

                // Move to the first row
                assertTrue("Cursor should move to first row", cursor.moveToFirst());

                // Verify the default values inserted into TABLE_USER_INFO
                assertEquals("verifyOrder should be '0'", "0", cursor.getString(cursor.getColumnIndex("verifyOrder")));
                assertEquals("phone_number should be '+38'", "+38", cursor.getString(cursor.getColumnIndex("phone_number")));
                assertEquals("email should be 'email'", "email", cursor.getString(cursor.getColumnIndex("email")));
                assertEquals("username should be 'username'", "username", cursor.getString(cursor.getColumnIndex("username")));
                assertEquals("bonus should be '0'", "0", cursor.getString(cursor.getColumnIndex("bonus")));
                assertEquals("card_pay should be '1'", "1", cursor.getString(cursor.getColumnIndex("card_pay")));
                assertEquals("bonus_pay should be '1'", "1", cursor.getString(cursor.getColumnIndex("bonus_pay")));
            } finally {
                // Close the cursor and database
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                if (database.isOpen()) {
                    database.close();
                }
            }
        }
    }

    @Test
    public void testNavigationToCityFragmentOnActionBarClick() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            TestUtils.mockLogin(scenario, mockAuthResult);

            // Проверяем, что элементы Action Bar отображаются
            onView(withId(R.id.action_bar_title))
                    .check(matches(isDisplayed()));
            onView(withId(R.id.button1))
                    .check(matches(isDisplayed()));

            // Мокаем NavController и его текущее направление
            scenario.onActivity(activity -> {
                Navigation.setViewNavController(activity.findViewById(R.id.nav_host_fragment_content_main), mockNavController);
            });

            // Тест 1: Навигация при наличии сети и текущий фрагмент не nav_finish_separate
            when(mockNavController.getCurrentDestination()).thenReturn(mock(NavDestination.class));
            when(mockNavController.getCurrentDestination().getId()).thenReturn(R.id.nav_home);
            when(NetworkUtils.isNetworkAvailable(ApplicationProvider.getApplicationContext())).thenReturn(true);

            // Выполняем клик по кнопке в Action Bar
            onView(withId(R.id.button1)).perform(click());

            // Проверяем, что NavController вызвал навигацию к nav_city
            verify(mockNavController).navigate(ArgumentMatchers.eq(R.id.nav_city), any(), any());

            // Тест 2: Отсутствие навигации, если текущий фрагмент nav_finish_separate
            when(mockNavController.getCurrentDestination().getId()).thenReturn(R.id.nav_finish_separate);

            // Выполняем клик по кнопке
            onView(withId(R.id.button1)).perform(click());

            // Проверяем, что навигация не была вызвана
            verify(mockNavController, never()).navigate(anyInt(), any(), any());

            // Тест 3: Навигация к nav_restart при отсутствии сети
            when(mockNavController.getCurrentDestination().getId()).thenReturn(R.id.nav_home);
            when(NetworkUtils.isNetworkAvailable(ApplicationProvider.getApplicationContext())).thenReturn(false);

            // Выполняем клик по кнопке
            onView(withId(R.id.button1)).perform(click());

            // Проверяем, что NavController вызвал навигацию к nav_restart
            verify(mockNavController).navigate(ArgumentMatchers.eq(R.id.nav_restart), any(), any());
        }
    }
}