package com.taxi_pas_4.utils.pusher.events;

public class TransactionStatusEvent {
    private final String status;

    public TransactionStatusEvent(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}