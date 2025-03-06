package com.taxi_pas_4.ui.restart;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.taxi_pas_4.MainActivity;
import com.taxi_pas_4.R;
import com.taxi_pas_4.databinding.FragmentRestartBinding;
import com.taxi_pas_4.utils.ui.BackPressBlocker;

public class RestartFragment extends Fragment {

    private FragmentRestartBinding binding;
    AppCompatButton btn_restart;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRestartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Включаем блокировку кнопки "Назад" Применяем блокировку кнопки "Назад"
        BackPressBlocker backPressBlocker = new BackPressBlocker();
        backPressBlocker.setBackButtonBlocked(true);
        backPressBlocker.blockBackButtonWithCallback(this);


        return root;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onResume() {
        super.onResume();

        btn_restart = binding.btnClearFromText;
        btn_restart.setText(requireActivity().getString(R.string.try_again));
        btn_restart.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);

        });

    }
}

