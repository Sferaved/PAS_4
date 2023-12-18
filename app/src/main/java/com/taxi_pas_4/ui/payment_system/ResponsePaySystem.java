package com.taxi_pas_4.ui.payment_system;

import com.google.gson.annotations.SerializedName;

public class ResponsePaySystem {
    @SerializedName("pay_system")
    private String pay_system;

    public String getPay_system() {
        return pay_system;
    }
}
