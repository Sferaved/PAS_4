package com.taxi_pas_4.ui.wfp.purchase;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PurchaseService {
    @GET("/wfp/charge/{application}/{city}/{orderReference}/{amount}/{productName}/{clientEmail}/{clientPhone}/{recToken}")
    Call<PurchaseResponse> purchase(
            @Path("application") String application,
            @Path("city") String city,
            @Path("orderReference") String orderReference,
            @Path("amount") String amount,
            @Path("productName") String productName,
            @Path("clientEmail") String clientEmail,
            @Path("clientPhone") String clientPhone,
            @Path("recToken") String recToken
    );
}

