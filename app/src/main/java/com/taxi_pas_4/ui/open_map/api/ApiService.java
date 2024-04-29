package com.taxi_pas_4.ui.open_map.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("reverseAddress/{latitude}/{longitude}")
    Call<ApiResponse> reverseAddress(
            @Path("latitude") double latitude,
            @Path("longitude") double longitude
    );
    @GET("reverseAddressLocal/{latitude}/{longitude}/{local}")
    Call<ApiResponse> reverseAddressLocal(
            @Path("latitude") double latitude,
            @Path("longitude") double longitude,
            @Path("local") String local
    );
}
