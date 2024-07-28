package com.taxi_pas_4.utils.user.save_server;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("user_name")
    private String userName;

    public String getUserName() {
        return userName;
    }
}

