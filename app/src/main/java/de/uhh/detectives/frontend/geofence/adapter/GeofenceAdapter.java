package de.uhh.detectives.frontend.geofence.adapter;

import com.google.android.gms.location.Geofence;

public interface GeofenceAdapter {
    public void createGeofences();
    public void destroyGeofence(String id);
    public Geofence getGeofence(String id);
}
