package de.uhh.detectives.frontend.waitingroom;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import de.uhh.detectives.frontend.geofence.adapter.MapGeofenceAdapter;

public class GeofencePlacer {
    private MapGeofenceAdapter mapGeofenceAdapter;

    public GeofencePlacer(Activity activity, Bundle savedInstanceState , double centerX, double centerY, double radius) {
        LatLng centerOfMap = generateLatLngOutOfCenterPoints(centerX, centerY);
        mapGeofenceAdapter = new MapGeofenceAdapter((float)radius, centerOfMap, activity, savedInstanceState);
    }

    public void placeMapGeofence(){
        mapGeofenceAdapter.createGeofences();
    }

    private LatLng generateLatLngOutOfCenterPoints(double centerX, double centerY) {
        return new LatLng(centerY, centerX);
    }
}
