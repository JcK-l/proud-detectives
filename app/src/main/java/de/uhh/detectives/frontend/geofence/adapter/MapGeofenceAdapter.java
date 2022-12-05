package de.uhh.detectives.frontend.geofence.adapter;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import de.uhh.detectives.frontend.geofence.service.GeofenceCreatorService;
import de.uhh.detectives.frontend.geofence.service.GeofenceDestroyService;
import de.uhh.detectives.frontend.geofence.service.MapGeofencePlacerService;

public class MapGeofenceAdapter implements GeofenceAdapter {
    private final float radiusOfMap;
    private final LatLng positionOfPlayer;
    private final GeofenceDestroyService geofenceDestroyService;
    private final GeofencingClient geofencingClient;
    private final GeofenceCreatorService geofenceCreatorService;
    private final MapGeofencePlacerService mapGeofencePlacerService;

    private Geofence mapGeofence;

    public MapGeofenceAdapter(float RadiusOfMap, LatLng positionOfPlayer, Activity activity, Bundle savedInstanceState) {
        this.positionOfPlayer = positionOfPlayer;
        radiusOfMap = RadiusOfMap;
        geofencingClient = LocationServices.getGeofencingClient(activity);
        geofenceCreatorService = new GeofenceCreatorService(activity);
        geofenceDestroyService = new GeofenceDestroyService(geofencingClient,
                geofenceCreatorService);
        mapGeofencePlacerService = new MapGeofencePlacerService(activity,
                savedInstanceState, geofencingClient, geofenceCreatorService);
    }

    @Override
    public void createGeofences() {
        mapGeofence = mapGeofencePlacerService.placeGeofence(positionOfPlayer, radiusOfMap);
    }

    @Override
    public void destroyGeofence(String id) {
        List<Geofence> geofences = new ArrayList<>();
        geofences.add(mapGeofence);
        if(geofences.get(0) != null){
            geofenceDestroyService.removeGeofences(geofences);
        }
    }

    @Override
    public Geofence getGeofence(String id) {
        return mapGeofence;
    }

}
