package com.taxi_pas_4.utils.route;

import androidx.annotation.Nullable;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Проверка ответа OSRM и запасные endpoints.
 * При ошибке osmbonuspack возвращает «дорогу» из двух точек (прямая линия) —
 * такую отрисовывать нельзя.
 */
public final class OsrmRouteHelper {

    public static final String SERVICE_OSM_DE = "https://routing.openstreetmap.de/";
    public static final String MEAN_CAR_OSM_DE = OSRMRoadManager.MEAN_BY_CAR;
    public static final String SERVICE_PROJECT_OSRM = "https://router.project-osrm.org/";
    public static final String MEAN_CAR_PROJECT = "route/v1/driving/";

    public static final class Endpoint {
        public final String serviceUrl;
        public final String meanUrl;

        public Endpoint(String serviceUrl, String meanUrl) {
            this.serviceUrl = serviceUrl;
            this.meanUrl = meanUrl;
        }
    }

    private OsrmRouteHelper() {
    }

    public static List<Endpoint> fallbackEndpoints() {
        return Collections.unmodifiableList(Arrays.asList(
                new Endpoint(SERVICE_OSM_DE, MEAN_CAR_OSM_DE),
                new Endpoint(SERVICE_PROJECT_OSRM, MEAN_CAR_PROJECT)
        ));
    }

    public static boolean isUsableRoad(@Nullable Road road) {
        if (road == null || road.mStatus != Road.STATUS_OK) {
            return false;
        }
        if (road.mRouteHigh == null || road.mRouteHigh.size() < 2) {
            return false;
        }
        // Прямая из двух waypoints — типичный fallback osmbonuspack при ошибке.
        if (road.mRouteHigh.size() == 2) {
            return false;
        }
        return true;
    }

    public static int pointCount(@Nullable Road road) {
        if (road == null || road.mRouteHigh == null) {
            return 0;
        }
        return road.mRouteHigh.size();
    }

    public static Road straightFallbackRoad(List<GeoPoint> waypoints) {
        return new Road(new java.util.ArrayList<>(waypoints));
    }
}
