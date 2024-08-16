package com.taxi_pas_4.ui.fondy.payment;

import com.google.gson.annotations.SerializedName;
import com.taxi_pas_4.ui.fondy.status.ErrorData;

public class ErrorResponse {
    @SerializedName("response")
    private ErrorData response;

    public ErrorData getResponse() {
        return response;
    }
}

