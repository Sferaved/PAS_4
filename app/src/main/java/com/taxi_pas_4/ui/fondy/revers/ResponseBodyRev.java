package com.taxi_pas_4.ui.fondy.revers;

import com.google.gson.annotations.SerializedName;

public class ResponseBodyRev {
    @SerializedName("response")
    private SuccessResponseDataRevers successResponse;
    @Override
    public String toString() {
        return "ResponseBodyRev{" +
                "successResponse=" + successResponse +
                '}';
    }
}
