package com.taxi_pas_4.utils.ip;

import com.google.gson.annotations.SerializedName;

public class CountryResponse {
    @SerializedName("response")
    private String country;

    public String getCountry() {
        return country;
    }
}
