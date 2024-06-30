package com.taxi_pas_4.ui.open_map.visicom;

import retrofit2.Retrofit;

public class GeocodeApiClient {

    private final GeocodeApiService apiService;

    public GeocodeApiClient() {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        apiService = retrofit.create(GeocodeApiService.class);
    }

    public GeocodeApiService getApiService() {
        return apiService;
    }
}
