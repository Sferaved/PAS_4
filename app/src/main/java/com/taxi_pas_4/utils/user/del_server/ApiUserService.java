package com.taxi_pas_4.utils.user.del_server;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiUserService {
    @GET("findUser/{email}")
    Call<UserFindResponse> findUser(@Path("email") String email);
}

