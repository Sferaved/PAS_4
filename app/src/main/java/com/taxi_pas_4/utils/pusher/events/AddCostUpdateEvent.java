package com.taxi_pas_4.utils.pusher.events;

public class AddCostUpdateEvent {
    private String addCost;

    public AddCostUpdateEvent(String addCost) {
        this.addCost = addCost;
    }

    public String getAddCost() {
        return addCost;
    }
}