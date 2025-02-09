package com.taxi_pas_4.ui.wfp.token;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CallbackServiceWfp {

    @GET("wfp/getCardTokenIdApp/{application}/{city}/{email}/{pay_system}")
    Call<CallbackResponseWfp> handleCallbackWfpCardsId(
            @Path("application") String application,
            @Path("city") String city,
            @Path("email") String email,
            @Path("pay_system") String pay_system
    );

    @GET("wfp/setActiveCard/{email}/{id}/{city}/{application}")
    Call<CallbackResponseSetActivCardWfp> setActiveCard(
            @Path("email") String email,
            @Path("id") String id,
            @Path("city") String city,
            @Path("application") String application
    );
}
