package de.uhh.detectives.frontend.geofence.service;

import android.app.PendingIntent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;

import java.util.ArrayList;
import java.util.List;

public class GeofenceDestroyService {
    private final String TAG = "GeofenceDestroyService";
    private GeofencingClient geofencingClient;
    private GeofenceCreatorService geofenceCreatorService;

    public GeofenceDestroyService(GeofencingClient geofencingClient, GeofenceCreatorService geofenceCreatorService) {
        this.geofencingClient = geofencingClient;
        this.geofenceCreatorService = geofenceCreatorService;
    }

    public void removeGeofences(List<Geofence> geofences){
        geofencingClient.removeGeofences(getGeofencesRequestId(geofences))
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: Geofence removed"))
                .addOnFailureListener(e -> {
                    String errorMessage = geofenceCreatorService.getErrorString(e);
                    Log.d(TAG, "onFailure" + errorMessage);
                });
    }

    public void removeAllGeofences(PendingIntent pendingIntent) {
        geofencingClient.removeGeofences(pendingIntent)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: Geofence removed");
                }) .addOnFailureListener(e -> {
                    String errorMessage = geofenceCreatorService.getErrorString(e);
                    Log.d(TAG, "onFailure" + errorMessage);
                });
    }

    private List<String> getGeofencesRequestId(List<Geofence> geofences){
        List<String> requestIds = new ArrayList<>();
        for (Geofence geofence: geofences) {
            requestIds.add(geofence.getRequestId());
        }
        return requestIds;
    }

}
