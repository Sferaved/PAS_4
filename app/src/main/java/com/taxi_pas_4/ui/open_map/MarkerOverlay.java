package com.taxi_pas_4.ui.open_map;


import android.content.Context;
import android.view.MotionEvent;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

public class MarkerOverlay extends Overlay {

    public static Marker marker;

    public MarkerOverlay(Context context) {
        super(context);
        marker = null;
    }

    @Override
    public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView) {
        if (marker != null) {
            mapView.getOverlays().remove(marker);
            mapView.invalidate();
        }
        if (OpenStreetMapActivity.m != null) {
            mapView.getOverlays().remove(OpenStreetMapActivity.m);
            mapView.invalidate();
        }


        OpenStreetMapActivity.enspoint = (GeoPoint) mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
        String target = OpenStreetMapActivity.epm;
        OpenStreetMapActivity.setMarker(OpenStreetMapActivity.enspoint.getLatitude(), OpenStreetMapActivity.enspoint.getLongitude(), target);
//        OpenStreetMapActivity.buttonAddServices.setVisibility(View.VISIBLE);
//        OpenStreetMapActivity.buttonAddServices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyServicesDialogFragment bottomSheetDialogFragment = new MyServicesDialogFragment();
//                bottomSheetDialogFragment.show(bottomSheetDialogFragment.getChildFragmentManager(), bottomSheetDialogFragment.getTag());
//            }
//        });
//        try {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                OpenStreetMapActivity.dialogMarkers(OpenStreetMapActivity.fragmentManager);
//            }
//        } catch (MalformedURLException | JSONException | InterruptedException e) {
//            Log.d("TAG", "onCreate:" + new RuntimeException(e));
//        }

        return true;
    }


}

