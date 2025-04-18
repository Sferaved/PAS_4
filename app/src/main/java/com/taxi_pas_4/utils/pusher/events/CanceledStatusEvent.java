package com.taxi_pas_4.utils.pusher.events;

public class CanceledStatusEvent {
    private final String canceledStatus;

    public CanceledStatusEvent(String canceledStatus) {
        this.canceledStatus = canceledStatus;
    }

    public String getCanceledStatus() {
        return canceledStatus;
    }
}
