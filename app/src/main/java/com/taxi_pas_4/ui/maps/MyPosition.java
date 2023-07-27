package com.taxi_pas_4.ui.maps;

public class MyPosition {
    public double latitude;
    public double longitude;

    public MyPosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "MyPosition{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
