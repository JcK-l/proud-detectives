package de.uhh.detectives.frontend.geofence.service;

import android.app.Activity;

import com.google.android.gms.location.Geofence;

public interface GeofencePlacementService {
    public void placeGeofence(Activity activity, Geofence geofence);
    public void addGeofence(Activity activity, Geofence geofence);
    public void onCreate(Activity activity);
}
