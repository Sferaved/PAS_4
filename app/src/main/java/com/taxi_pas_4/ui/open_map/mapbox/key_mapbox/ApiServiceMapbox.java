package com.taxi_pas_4.ui.open_map.mapbox.key_mapbox;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiServiceMapbox {

    @GET("maxBoxKeyInfo/{appName}")
    Call<ApiResponseMapbox> getMaxboxKeyInfo(
            @Path("appName") String appName
    );
}
