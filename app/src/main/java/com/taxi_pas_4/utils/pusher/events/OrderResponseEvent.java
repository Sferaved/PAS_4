package com.taxi_pas_4.utils.pusher.events;

import com.taxi_pas_4.ui.finish.OrderResponse;

public class OrderResponseEvent {
    private final OrderResponse orderResponse;

    public OrderResponseEvent(OrderResponse orderResponse) {
        this.orderResponse = orderResponse;
    }

    public OrderResponse getOrderResponse() {
        return orderResponse;
    }
}