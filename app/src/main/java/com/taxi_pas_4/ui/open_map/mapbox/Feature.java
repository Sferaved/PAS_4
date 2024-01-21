package com.taxi_pas_4.ui.open_map.mapbox;

import com.google.gson.annotations.SerializedName;

public class Feature {
    @SerializedName("geometry")
    private Geometry geometry;

    @SerializedName("place_name")
    private String placeName;

    public Geometry getGeometry() {
        return geometry;
    }

    public String getPlaceName() {
        return placeName;
    }
}

