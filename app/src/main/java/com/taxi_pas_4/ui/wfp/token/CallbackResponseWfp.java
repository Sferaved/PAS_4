package com.taxi_pas_4.ui.wfp.token;

import com.google.gson.annotations.SerializedName;
import com.taxi_pas_4.ui.card.CardInfo;

import java.util.List;

public class CallbackResponseWfp {
    @SerializedName("cards")
    private List<CardInfo> cards;

    public List<CardInfo> getCards() {
        return cards;
    }
}


