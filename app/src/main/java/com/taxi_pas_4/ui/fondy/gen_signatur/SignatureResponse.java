package com.taxi_pas_4.ui.fondy.gen_signatur;

import com.google.gson.annotations.SerializedName;

public class SignatureResponse {
    @SerializedName("digest")
    private String digest;

    public String getDigest() {
        return digest;
    }
}

