package com.taxi_pas_4.ui.fondy.status;

import com.google.gson.annotations.SerializedName;

public class StatusResponseData {
    @SerializedName("response")
    private SuccessfulResponseData response; // Изменено на SuccessfulResponseData

    public SuccessfulResponseData getResponse() {
        return response;
    }
}

