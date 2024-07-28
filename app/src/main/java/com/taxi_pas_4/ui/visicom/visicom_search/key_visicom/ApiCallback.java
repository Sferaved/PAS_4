package com.taxi_pas_4.ui.visicom.visicom_search.key_visicom;

public interface ApiCallback {
    void onVisicomKeyReceived(String key);
    void onApiError(int errorCode);
    void onApiFailure(Throwable t);
}

