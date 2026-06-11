package com.taxi_pas_4.ui.wfp.googlepay;

import com.google.gson.annotations.SerializedName;

public class GooglePayConfigResponse {

    @SerializedName("merchantAccount")
    private String merchantAccount;

    @SerializedName("gateway")
    private String gateway;

    @SerializedName("error")
    private String error;

    public String getMerchantAccount() {
        return merchantAccount;
    }

    public String getGateway() {
        return gateway;
    }

    public String getError() {
        return error;
    }
}
