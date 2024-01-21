package com.taxi_pas_4.ui.open_map.visicom.key_mapbox;

import com.google.gson.annotations.SerializedName;

public class ApiResponseMapbox {

    @SerializedName("keyMapbox")
    private String keyMapbox;

    public String getKeyMapbox() {
        return keyMapbox;
    }
}
