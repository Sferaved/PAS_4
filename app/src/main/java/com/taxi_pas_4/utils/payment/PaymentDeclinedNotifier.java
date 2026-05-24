package com.taxi_pas_4.utils.payment;

import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import android.content.Context;

import androidx.annotation.Nullable;

import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;
import com.taxi_pas_4.androidx.startup.MyApplication;
import com.taxi_pas_4.utils.notify.NotificationHelper;

/**
 * Ошибка оплаты (Declined): bottom sheet на переднем плане, иначе system notification.
 */
public final class PaymentDeclinedNotifier {

    private PaymentDeclinedNotifier() {
    }

    public static boolean shouldHandleDeclined() {
        String declinedInvoice = String.valueOf(
                sharedPreferencesHelperMain.getValue("declined_invoice", "**"));
        return !declinedInvoice.equals(MainActivity.order_id);
    }

    public static void markDeclinedHandled() {
        sharedPreferencesHelperMain.saveValue("declined_invoice", MainActivity.order_id);
    }

    public static void notifyDeclined(Context context, @Nullable Runnable showBottomSheet) {
        if (context == null || !shouldHandleDeclined()) {
            return;
        }
        markDeclinedHandled();
        sharedPreferencesHelperMain.saveValue("add_show_flag", false);

        if (MainActivity.viewModel != null) {
            MainActivity.viewModel.setCancelStatus(true);
        }

        if (MyApplication.isInForeground() && showBottomSheet != null) {
            showBottomSheet.run();
        } else {
            showPaymentErrorNotification(context);
        }
    }

    public static void showPaymentErrorNotification(Context context) {
        NotificationHelper.sendPaymentErrorNotification(
                context,
                context.getString(R.string.paymentErrMes),
                context.getString(R.string.pay_failure_mes)
        );
    }
}
