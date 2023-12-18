package com.taxi_pas_4.ui.fondy.status;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {
    @SerializedName("response")
    private StatusResponseData response;

    public StatusResponseData getResponse() {
        return response;
    }
}
