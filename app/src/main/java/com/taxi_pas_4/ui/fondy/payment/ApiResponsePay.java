package com.taxi_pas_4.ui.fondy.payment;

import com.google.gson.annotations.SerializedName;

public class ApiResponsePay<T> {
    @SerializedName("response")
    private T response;

    public T getResponse() {
        return response;
    }
}



