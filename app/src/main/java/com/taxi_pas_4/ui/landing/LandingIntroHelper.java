package com.taxi_pas_4.ui.landing;

/**
 * Решает, нужно ли один раз показать лендинг после обновления приложения
 * (без очистки данных / переустановки).
 */
public final class LandingIntroHelper {

    private LandingIntroHelper() {
    }

    /**
     * @param storedVersionCode версия, для которой лендинг уже показывали (0 = ещё не показывали)
     * @param currentVersionCode {@code BuildConfig.VERSION_CODE} текущей сборки
     */
    public static boolean shouldShowIntroAfterUpdate(int storedVersionCode, int currentVersionCode) {
        return storedVersionCode < currentVersionCode;
    }

    /**
     * Не уводить с экрана активного заказа на лендинг после обновления (Mantis #30).
     *
     * @param hasActiveOrderUid есть сохранённый uid активного заказа
     * @param onFinishScreen сейчас открыт экран заказа
     */
    public static boolean shouldBlockIntroDuringActiveOrder(
            boolean hasActiveOrderUid,
            boolean onFinishScreen
    ) {
        return hasActiveOrderUid || onFinishScreen;
    }
}
