package com.taxi_pas_4.ui.finish;

import com.google.gson.annotations.SerializedName;

public class RouteResponseCancel {
    @SerializedName("uid")
    private String uid;

    @SerializedName("routefrom")
    private String routeFrom;

    @SerializedName("routefromnumber")
    private String routeFromNumber;
    @SerializedName("startLat")
    private String startLat;

    @SerializedName("startLan")
    private String startLan;

    @SerializedName("routeto")
    private String routeTo;

    @SerializedName("routetonumber")
    private String routeToNumber;
    @SerializedName("to_lat")
    private String to_lat;

    @SerializedName("to_lng")
    private String to_lng;


    @SerializedName("web_cost")
    private String webCost;

    @SerializedName("closeReason")
    private String closeReason;

    @SerializedName("auto")
    private String auto;

    @SerializedName("dispatching_order_uid_Double")
    private String dispatchingOrderUidDouble;

    @SerializedName("pay_method")
    private String pay_method;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("required_time")
    private String required_time;
    @SerializedName("flexible_tariff_name")
    private String flexible_tariff_name;
    @SerializedName("comment_info")
    private String comment_info;
    @SerializedName("extra_charge_codes")
    private String extra_charge_codes;


    public String getStartLat() {
        return startLat;
    }

    public String getStartLan() {
        return startLan;
    }

    public String getTo_lat() {
        return to_lat;
    }

    public String getTo_lng() {
        return to_lng;
    }

    // Геттеры и сеттеры для полей (не обязательно, но может быть полезным)

    public String getRouteFrom() {
        return routeFrom;
    }

    public String getRouteFromNumber() {
        return routeFromNumber;
    }

    public String getRouteTo() {
        return routeTo;
    }

    public String getRouteToNumber() {
        return routeToNumber;
    }

    public String getWebCost() {
        return webCost;
    }

    public String getAuto() {
        return auto;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDispatchingOrderUidDouble() {
        return dispatchingOrderUidDouble;
    }

    public String getPay_method() {
        return pay_method;
    }

    public void setPay_method(String pay_method) {
        this.pay_method = pay_method;
    }

    public String getRequired_time() {
        return required_time;
    }
    public String getFlexible_tariff_name() {
        return flexible_tariff_name;
    }
    public String getComment_info() {
        return comment_info;
    }
    public String getExtra_charge_codes() {
        return extra_charge_codes;
    }

    @Override
    public String toString() {
        return "RouteResponseCancel{" +
                "uid='" + uid + '\'' +
                ", routeFrom='" + routeFrom + '\'' +
                ", routeFromNumber='" + routeFromNumber + '\'' +
                ", startLat='" + startLat + '\'' +
                ", startLan='" + startLan + '\'' +
                ", routeTo='" + routeTo + '\'' +
                ", routeToNumber='" + routeToNumber + '\'' +
                ", to_lat='" + to_lat + '\'' +
                ", to_lng='" + to_lng + '\'' +
                ", webCost='" + webCost + '\'' +
                ", closeReason='" + closeReason + '\'' +
                ", auto='" + auto + '\'' +
                ", dispatchingOrderUidDouble='" + dispatchingOrderUidDouble + '\'' +
                ", pay_method='" + pay_method + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", required_time='" + required_time + '\'' +
                ", flexible_tariff_name='" + flexible_tariff_name + '\'' +
                ", comment_info='" + comment_info + '\'' +
                ", extra_charge_codes='" + extra_charge_codes + '\'' +
                '}';
    }
}
