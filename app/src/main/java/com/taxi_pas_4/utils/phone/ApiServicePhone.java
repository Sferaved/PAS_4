package com.taxi_pas_4.utils.phone;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiServicePhone {
    @GET("userPhoneReturn/{email}")
    Call<UserPhoneResponse> getUserPhone(@Path("email") String email);
}
