package de.uhh.detectives.frontend.geofence.service.hintservices;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.maps.model.LatLng;

import de.uhh.detectives.frontend.geofence.service.GeofenceCreatorService;
import de.uhh.detectives.frontend.geofence.service.GeofencePlacementService;
import de.uhh.detectives.frontend.geofence.service.mapservices.MapGeofencePlacementService;

public class HintGeofencePlacerService {
    private final Activity activity;
    private GeofenceCreatorService geofenceCreatorService;
    private GeofencePlacementService hintGeofencePlacementService;

    public HintGeofencePlacerService(Activity activity,
                                     Bundle savedInstanceState,
                                     GeofencingClient geofencingClient,
                                     GeofenceCreatorService geofenceCreatorService) {
        this.activity = activity;
        this.geofenceCreatorService = geofenceCreatorService;
        hintGeofencePlacementService = new MapGeofencePlacementService(activity,
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
        hintGeofencePlacementService.placeGeofence(activity, geofence);
        return geofence;
    }
}
