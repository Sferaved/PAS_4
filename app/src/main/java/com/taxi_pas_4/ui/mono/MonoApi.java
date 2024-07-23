package com.taxi_pas_4.ui.mono;

import  com.taxi_pas_4.ui.mono.cancel.RequestCancelMono;
import  com.taxi_pas_4.ui.mono.cancel.ResponseCancelMono;
import  com.taxi_pas_4.ui.mono.payment.RequestPayMono;
import  com.taxi_pas_4.ui.mono.payment.ResponsePayMono;
import  com.taxi_pas_4.ui.mono.status.ResponseStatusMono;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MonoApi {
    @Headers({
            "Content-Type: application/json"
    })
    @POST("merchant/invoice/create")
    Call<ResponsePayMono> invoiceCreate(
            @Header("X-Token") String token,
            @Body RequestPayMono paymentRequest
    );
    @Headers({
            "Content-Type: application/json"
    })
    @POST("merchant/invoice/cancel")
    Call<ResponseCancelMono> invoiceCancel(
            @Header("X-Token") String token,
            @Body RequestCancelMono paymentRequest
    );
    @GET("merchant/invoice/status")
    Call<ResponseStatusMono> getInvoiceStatus(
            @Header("X-Token") String token,
            @Query("invoiceId") String invoiceId);


}


