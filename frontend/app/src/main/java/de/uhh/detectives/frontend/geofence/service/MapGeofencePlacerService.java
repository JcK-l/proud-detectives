package de.uhh.detectives.frontend.geofence.service;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.maps.model.LatLng;

import de.uhh.detectives.frontend.geofence.service.GeofenceCreatorService;
import de.uhh.detectives.frontend.geofence.service.GeofencePlacementService;

public class MapGeofencePlacerService {
    private static final String MAP_ID = "GAME_FIELD";

    private GeofenceCreatorService geofenceCreatorService;
    private GeofencePlacementService mapGeofencePlacementService;
    private Activity activity;

    public MapGeofencePlacerService(Activity activity,
                                    Bundle savedInstanceState,
                                    GeofencingClient geofencingClient,
                                    GeofenceCreatorService geofenceCreatorService) {
        this.activity = activity;
        this.geofenceCreatorService = geofenceCreatorService;
        mapGeofencePlacementService = new GeofencePlacementService(activity,
                savedInstanceState,
                geofencingClient,
                geofenceCreatorService);
    }

    public Geofence placeGeofence(LatLng positionOfGeofence, float radiusOfGeofence) {
        Geofence geofence = geofenceCreatorService.getGeofence(MAP_ID,
                positionOfGeofence,
                radiusOfGeofence,
                Geofence.GEOFENCE_TRANSITION_EXIT |
                        Geofence.GEOFENCE_TRANSITION_ENTER);
        mapGeofencePlacementService.placeGeofence(activity, geofence);
        return geofence;
    }
}
