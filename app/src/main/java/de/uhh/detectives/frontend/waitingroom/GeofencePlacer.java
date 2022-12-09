package de.uhh.detectives.frontend.waitingroom;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import de.uhh.detectives.frontend.geofence.adapter.HintGeofenceAdapter;
import de.uhh.detectives.frontend.geofence.adapter.MapGeofenceAdapter;
import de.uhh.detectives.frontend.model.Hint;

public class GeofencePlacer {
    private MapGeofenceAdapter mapGeofenceAdapter;
    private HintGeofenceAdapter hintGeofenceAdapter;

    public GeofencePlacer(Activity activity, Bundle savedInstanceState , List<Hint> hints, double centerX, double centerY, double radius) {
        LatLng centerOfMap = generateLatLngOutOfCenterPoints(centerX, centerY);
        mapGeofenceAdapter = new MapGeofenceAdapter((float)radius, centerOfMap, activity, savedInstanceState);
        hintGeofenceAdapter = new HintGeofenceAdapter(hints, activity, savedInstanceState);
    }

    public void placeMapGeofence(){
        mapGeofenceAdapter.createGeofences();
    }

    public void placeHintGeofences(){
        hintGeofenceAdapter.createGeofences();
    }

    private LatLng generateLatLngOutOfCenterPoints(double centerX, double centerY) {
        return new LatLng(centerY, centerX);
    }
}
