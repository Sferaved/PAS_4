package com.taxi_pas_4.ui.card.unlink;

import com.taxi_pas_4.ui.wfp.token.CallbackResponseSetActivCardWfp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UnlinkApi {
    @GET("/delete-card-token/{rectoken}")
    Call<Void> deleteCardTokenFondy(
            @Path("rectoken") String rectoken
    );

    @GET("wfp/deleteCardToken/{id}")
    Call<CallbackResponseSetActivCardWfp> deleteCardToken(
            @Path("id") String id
    );


}

