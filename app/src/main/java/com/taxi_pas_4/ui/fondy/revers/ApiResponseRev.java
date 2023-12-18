package com.taxi_pas_4.ui.fondy.revers;

import com.google.gson.annotations.SerializedName;

public class ApiResponseRev<T> {
    @SerializedName("response")
    private T response;

    public T getResponse() {
        return response;
    }
}



