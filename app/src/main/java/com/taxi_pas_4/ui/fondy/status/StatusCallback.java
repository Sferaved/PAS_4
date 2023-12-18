package com.taxi_pas_4.ui.fondy.status;

public interface StatusCallback {
    void onStatusReceived(String orderStatus);
    void onError(String errorMessage);
}
