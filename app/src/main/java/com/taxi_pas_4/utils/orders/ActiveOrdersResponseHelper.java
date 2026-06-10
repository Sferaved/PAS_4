package com.taxi_pas_4.utils.orders;

import com.taxi_pas_4.ui.finish.RouteResponseCancel;

import java.util.List;

public final class ActiveOrdersResponseHelper {

    private ActiveOrdersResponseHelper() {
    }

    public static boolean isEmptyPlaceholder(RouteResponseCancel route) {
        return route != null
                && "*".equals(route.getRouteFrom())
                && "*".equals(route.getRouteFromNumber())
                && "*".equals(route.getRouteTo())
                && "*".equals(route.getRouteToNumber())
                && "*".equals(route.getWebCost())
                && "*".equals(route.getCloseReason())
                && "*".equals(route.getAuto())
                && "*".equals(route.getCreatedAt());
    }

    public static boolean hasRouteWithAsterisk(List<RouteResponseCancel> routes) {
        if (routes == null || routes.isEmpty()) {
            return false;
        }
        for (RouteResponseCancel route : routes) {
            if ("*".equals(route.getRouteFrom())) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldDisplayActiveOrders(List<RouteResponseCancel> routes) {
        return routes != null && !routes.isEmpty() && !hasRouteWithAsterisk(routes);
    }
}
