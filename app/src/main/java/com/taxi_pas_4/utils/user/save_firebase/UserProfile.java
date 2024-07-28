package com.taxi_pas_4.utils.user.save_firebase;

public class UserProfile {
    private String phone;

    public UserProfile() {
        // Необходим для Firebase
    }

    public UserProfile(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
