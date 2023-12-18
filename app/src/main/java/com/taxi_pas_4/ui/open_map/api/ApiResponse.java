package com.taxi_pas_4.ui.open_map.api;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }
}
