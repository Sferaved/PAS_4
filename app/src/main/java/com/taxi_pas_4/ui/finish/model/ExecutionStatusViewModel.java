package com.taxi_pas_4.ui.finish.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.taxi_pas_4.ui.finish.OrderResponse;

public class ExecutionStatusViewModel extends ViewModel {


    private final MutableLiveData<String> canceledStatus = new MutableLiveData<>();
    private final MutableLiveData<OrderResponse> orderResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isTenMinutesRemaining = new MutableLiveData<>();
    public final LiveData<Boolean> isTenMinutesRemaining = _isTenMinutesRemaining;

    //
    private final SingleLiveEvent<String> transactionStatus = new SingleLiveEvent<>();

    public LiveData<String> getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String status) {
        transactionStatus.postValue(status);
    }



    //Проверка отмены по оплате
    public LiveData<String> getCanceledStatus() {return canceledStatus;}
    public void setCanceledStatus(String canceled) {canceledStatus.postValue(canceled);}

    //Опрос вилки
    public LiveData<OrderResponse> getOrderResponse() {return orderResponse;}
    public void updateOrderResponse(OrderResponse response) {
        if (response == null) {
            Log.e("ViewModel", "Received null OrderResponse");
            return;
        }

        // Логируем полученные данные
        Log.i("ViewModel", "Updating order response: " + response.getDispatchingOrderUid());

        // Обновляем LiveData
        orderResponse.setValue(response);
    }
    public void clearOrderResponse() {
        orderResponse.setValue(null); // Сбрасываем в null
    }

    public void setIsTenMinutesRemaining(boolean value) {
        _isTenMinutesRemaining.setValue(value);
    }

    // Метод установки значения с учётом выполнения на главном потоке
    public void setIsTenMinutesRemainingFromBackground(boolean value) {
        _isTenMinutesRemaining.postValue(value); // Для вызовов из фонового потока
    }

    // Метод чтения значения (getter)
    public Boolean getIsTenMinutesRemaining() {
        return _isTenMinutesRemaining.getValue();
    }

    // Метод чтения через LiveData для наблюдения
    public LiveData<Boolean> observeIsTenMinutesRemaining() {
        return isTenMinutesRemaining;
    }


}
