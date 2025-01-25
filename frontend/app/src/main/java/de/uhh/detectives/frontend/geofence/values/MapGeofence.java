package de.uhh.detectives.frontend.geofence.values;

import android.app.PendingIntent;

import com.google.android.gms.location.Geofence;

public class MapGeofence implements GeofenceValue {
    private final Geofence geofence;
    private final PendingIntent pendingIntent;

    public MapGeofence(Geofence geofence, PendingIntent pendingIntent) {
        this.geofence = geofence;
        this.pendingIntent = pendingIntent;
    }

    @Override
    public Geofence getGeofence() {
        return geofence;
    }

    @Override
    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }
}
