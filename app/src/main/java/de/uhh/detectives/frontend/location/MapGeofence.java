package de.uhh.detectives.frontend.location;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

public class MapGeofence {
    private static final String MAP_ID = "GAME_FIELD";

    private final GeofenceHelper geofenceHelper;
    private final GeofenceHandler geofenceHandler;
    private final Activity activity;

    public MapGeofence(Activity activity, Bundle savedInstanceState) {
        this.activity = activity;
        geofenceHelper = new GeofenceHelper(activity);
        geofenceHandler = new GeofenceHandler(activity, savedInstanceState);
    }

    public void placeMapGeofence(LatLng positionOfPlayer, float selectedRadiusOfGameField) {
        Geofence geofence = geofenceHelper.getGeofence(MAP_ID,
                positionOfPlayer,
                selectedRadiusOfGameField,
                Geofence.GEOFENCE_TRANSITION_EXIT |
                        Geofence.GEOFENCE_TRANSITION_ENTER);
        geofenceHandler.placeGeofence(activity, geofence);
    }
}
