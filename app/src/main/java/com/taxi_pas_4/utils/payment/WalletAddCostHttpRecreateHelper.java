package com.taxi_pas_4.utils.payment;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.taxi_pas_4.utils.cost.CostParseHelper;

/**
 * HTTP-ответ доплаты WFP ({@code chargeActiveTokenAddCost}) после пересоздания заказа
 * уже содержит новый uid и сумму — не ждать только Centrifugo (Mantis #31).
 */
public final class WalletAddCostHttpRecreateHelper {

    private WalletAddCostHttpRecreateHelper() {
    }

    public static boolean hasRecreatedOrder(@Nullable String uid) {
        return uid != null && !uid.trim().isEmpty();
    }

    @Nullable
    public static String resolveDisplayCostGrivna(
            @Nullable JsonElement clientCost,
            @Nullable JsonElement webCost
    ) {
        String fromClient = costFromJsonElement(clientCost);
        if (fromClient != null) {
            return fromClient;
        }
        return costFromJsonElement(webCost);
    }

    @Nullable
    public static String resolveDisplayCostGrivna(
            @Nullable Object clientCost,
            @Nullable Object webCost
    ) {
        String fromClient = costFromObject(clientCost);
        if (fromClient != null) {
            return fromClient;
        }
        return costFromObject(webCost);
    }

    /**
     * После успешного CHARGE с uid в теле — применять сразу; иначе ждать order_uid_new.
     */
    public static boolean shouldApplyHttpRecreateOnHoldSuccess(
            @Nullable String transactionStatus,
            @Nullable String uid
    ) {
        if (!hasRecreatedOrder(uid)) {
            return false;
        }
        if (transactionStatus == null) {
            return false;
        }
        return "Approved".equals(transactionStatus)
                || "WaitingAuthComplete".equals(transactionStatus);
    }

    @Nullable
    private static String costFromJsonElement(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull() || !element.isJsonPrimitive()) {
            return null;
        }
        try {
            return CostParseHelper.normalizeCostString(element.getAsString());
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Nullable
    private static String costFromObject(@Nullable Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JsonElement) {
            return costFromJsonElement((JsonElement) value);
        }
        return CostParseHelper.normalizeCostString(String.valueOf(value));
    }
}
