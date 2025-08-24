package com.taxi_pas_4;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// Указываем, что это тестовый сьют
@RunWith(Suite.class)
// Список тестовых классов, которые будут запущены последовательно
@Suite.SuiteClasses({
        AppPackageNameTest.class,
        MainActivityUITest.class,
        CityCheckActivityLaunchTest.class,
        NavigationViewTest.class,
        MainActivityLoginTest.class,
        DialogHostActivityTest.class,
})
public class AllTestsSuite {
    // Класс остается пустым, так как он только организует запуск тестов
}
