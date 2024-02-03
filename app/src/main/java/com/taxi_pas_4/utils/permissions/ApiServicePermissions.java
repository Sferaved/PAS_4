package com.taxi_pas_4.utils.permissions;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiServicePermissions {
    @GET("android/permissions/{email}")
    Call<PermissionsResponse> getPermissions(@Path("email") String email);
}
