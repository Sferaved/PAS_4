package com.taxi_pas_4.ui.finish.fragm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExecutionStatusViewModel extends ViewModel {
    private final MutableLiveData<String> executionStatusCancel = new MutableLiveData<>();

    public LiveData<String> getExecutionStatusCancel() {
        return executionStatusCancel;
    }

    public void setExecutionStatusCancel(String status) {
        executionStatusCancel.postValue(status);
    }
}
