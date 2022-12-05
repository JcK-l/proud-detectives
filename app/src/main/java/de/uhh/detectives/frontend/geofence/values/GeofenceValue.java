package de.uhh.detectives.frontend.geofence.values;

import android.app.PendingIntent;

import com.google.android.gms.location.Geofence;

public interface GeofenceValue {
    public Geofence getGeofence();
    public PendingIntent getPendingIntent();
}
