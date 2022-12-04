package de.uhh.detectives.frontend.location;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

public class MapGeofence {
    private static final String MAP_ID = "GAME_FIELD";

    private GeofenceHandler geofenceHandler;
    private LocationHandler locationHandler;
    private Activity activity;

    public MapGeofence(Activity activity, Bundle savedInstanceState) {
        this.activity = activity;
        geofenceHandler = new GeofenceHandler(activity);
        locationHandler = new LocationHandler(activity, savedInstanceState);
    }

    public void placeMapGeofence(LatLng positionOfPlayer, float selectedRadiusOfGameField) {
        Geofence geofence = geofenceHandler.getGeofence(MAP_ID,
                positionOfPlayer,
                selectedRadiusOfGameField,
                Geofence.GEOFENCE_TRANSITION_EXIT |
                        Geofence.GEOFENCE_TRANSITION_ENTER);
        locationHandler.placeGeofence(activity, geofence);
    }
}
