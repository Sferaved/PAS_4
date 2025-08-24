package com.taxi_pas_4.utils.worker.utils;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.taxi_pas_4.R;
import com.taxi_pas_4.utils.bottom_sheet.MyBottomSheetErrorFragment;
import com.taxi_pas_4.utils.log.Logger;

public class DialogHostActivity extends AppCompatActivity {
    private static final String TAG = "DialogHostActivity";
    public static MyBottomSheetErrorFragment bottomSheetDialogFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the new layout
        setContentView(R.layout.activity_dialog_host);


        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.background_color_new)
        );

        Logger.d(this, TAG, "DialogHostActivity запущена");

        String sentNotifyMessage = getString(R.string.sentNotifyMessage);
        bottomSheetDialogFragment =
                new MyBottomSheetErrorFragment(sentNotifyMessage);

        // Добавляем колбэк закрытия
        bottomSheetDialogFragment.setOnDismissListener(() -> {
            Logger.d(this, TAG, "BottomSheet закрыт — завершаем Activity");
            finish();
        });

        // Показываем BottomSheet
        bottomSheetDialogFragment.show(getSupportFragmentManager(), "PushDialog");
    }
}
