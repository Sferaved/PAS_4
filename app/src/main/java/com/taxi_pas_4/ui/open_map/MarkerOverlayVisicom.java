package com.taxi_pas_4.ui.open_map;


import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONException;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.net.MalformedURLException;

public class MarkerOverlayVisicom extends Overlay {
    Marker marker;
    String point;
    private String TAG = "TAG_OPENMAP";


    public MarkerOverlayVisicom(Context context, String point) {
        super(context);
        this.point = point;
    }
    public MarkerOverlayVisicom(Context context) {
        super(context);
    }

    @Override
    public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView) {


    // Удаление старого маркера
        if(marker != null) {
            mapView.getOverlays().remove(marker);
            mapView.invalidate();
            marker = null;
        }
        mapView.invalidate();

        GeoPoint pointGeo = (GeoPoint) mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
        Log.d(TAG, "onSingleTapConfirmed: point " + point);
        switch (point) {
            case "startMarker":
                OpenStreetMapVisicomActivity.startPoint = pointGeo;
                try {


                        OpenStreetMapVisicomActivity.dialogMarkerStartPoint();

                } catch (MalformedURLException e) {
                    Log.d("TAG", "onCreate:" + new RuntimeException(e));
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
                break;
            case "finishMarker":
                OpenStreetMapVisicomActivity.endPoint = pointGeo;
                try {


                        OpenStreetMapVisicomActivity.dialogMarkersEndPoint();

                } catch (MalformedURLException | JSONException | InterruptedException e) {
                    Log.d("TAG", "onCreate:" + new RuntimeException(e));
                }
                break;


        }





        return true;
    }


}

