package com.taxi_pas_4.ui.open_map.mapbox;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Geometry {
    @SerializedName("coordinates")
    private List<Double> coordinates;

    public List<Double> getCoordinates() {
        return coordinates;
    }
}