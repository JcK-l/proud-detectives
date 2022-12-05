package de.uhh.detectives.frontend.geofence.service;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.maps.model.LatLng;

public class HintGeofencePlacerService {
    private final Activity activity;
    private GeofenceCreatorService geofenceCreatorService;
    private GeofencePlacementService geofencePlacementService;

    public HintGeofencePlacerService(Activity activity,
                                     Bundle savedInstanceState,
                                     GeofencingClient geofencingClient,
                                     GeofenceCreatorService geofenceCreatorService) {
        this.activity = activity;
        this.geofenceCreatorService = geofenceCreatorService;
        geofencePlacementService = new GeofencePlacementService(activity,
                savedInstanceState,
                geofencingClient,
                geofenceCreatorService);
    }

    public Geofence placeGeofence(LatLng positionOfGeofence,
                                  float radiusOfGeofence,
                                  String hintName) {
        Geofence geofence = geofenceCreatorService.getGeofence(hintName,
                positionOfGeofence,
                radiusOfGeofence,
                Geofence.GEOFENCE_TRANSITION_ENTER);
        geofencePlacementService.placeGeofence(activity, geofence, true);
        return geofence;
    }
}
