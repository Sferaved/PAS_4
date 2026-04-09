package com.taxi_pas_4.ui.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {
    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private int visibility;
    private Wind wind;
    private Clouds clouds;
    private long dt;
    private Sys sys;
    private int timezone;
    private int id;
    private String name;
    private int cod;

    // Прогноз на 5 дней
    @SerializedName("list")
    private List<ForecastItem> forecastList;

    public static class Coord {
        private double lon;
        private double lat;
        // геттеры и сеттеры
        public double getLon() { return lon; }
        public double getLat() { return lat; }
    }

    public static class Weather {
        private int id;
        private String main;
        private String description;
        private String icon;

        public int getId() { return id; }
        public String getMain() { return main; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }

    public static class Main {
        private double temp;
        @SerializedName("feels_like")
        private double feelsLike;
        @SerializedName("temp_min")
        private double tempMin;
        @SerializedName("temp_max")
        private double tempMax;
        private int pressure;
        private int humidity;

        public double getTemp() { return temp; }
        public double getFeelsLike() { return feelsLike; }
        public double getTempMin() { return tempMin; }
        public double getTempMax() { return tempMax; }
        public int getPressure() { return pressure; }
        public int getHumidity() { return humidity; }
    }

    public static class Wind {
        private double speed;
        private int deg;

        public double getSpeed() { return speed; }
        public int getDeg() { return deg; }
    }

    public static class Clouds {
        private int all;
        public int getAll() { return all; }
    }

    public static class Sys {
        private int type;
        private int id;
        private String country;
        private long sunrise;
        private long sunset;

        public String getCountry() { return country; }
        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
    }

    public static class ForecastItem {
        private long dt;
        private Main main;
        private List<Weather> weather;
        private Wind wind;
        private Clouds clouds;
        @SerializedName("dt_txt")
        private String dtTxt;

        public long getDt() { return dt; }
        public Main getMain() { return main; }
        public List<Weather> getWeather() { return weather; }
        public Wind getWind() { return wind; }
        public Clouds getClouds() { return clouds; }
        public String getDtTxt() { return dtTxt; }
    }

    // Геттеры и сеттеры
    public Coord getCoord() { return coord; }
    public List<Weather> getWeather() { return weather; }
    public Main getMain() { return main; }
    public Wind getWind() { return wind; }
    public Clouds getClouds() { return clouds; }
    public long getDt() { return dt; }
    public Sys getSys() { return sys; }
    public int getTimezone() { return timezone; }
    public String getName() { return name; }
    public int getCod() { return cod; }
    public List<ForecastItem> getForecastList() { return forecastList; }
}