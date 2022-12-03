package de.uhh.detectives.frontend.location;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import de.uhh.detectives.frontend.PermissionHelper.LocationPermissionHandler;


public class LocationHandler {
    private static final String KEY_LOCATION = "location";
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1002;
    private static final String TAG = "MapActivity";
    private final String CHANNEL_ID = "100";

    private final LatLng mapMiddelPoint = new LatLng(53.568780, 10.019750);
    private final float radiusOfTheGameField = 100f;
    private Geofence mapGeofence;
    private GeofencingRequest geofencingRequest;

    private String geofenceID = "0";


    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private LocationPermissionHandler permissionHandler;

    private GeofencingClient geofencingClient;
    private GeofenceHandler geofenceHandler;

    private boolean isPlayerInMap;
    private int playerOnTheMapStatus;


    public LocationHandler(final Activity activity, Bundle savedInstanceState) {

        permissionHandler = new LocationPermissionHandler(activity);
        permissionHandler.askFineLocationPermissions();
        permissionHandler.askCoarseLocation();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        geofencingClient = LocationServices.getGeofencingClient(activity);
        geofenceHandler = new GeofenceHandler(activity);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        placeGeofence(activity, radiusOfTheGameField, mapMiddelPoint);
    }

    private void getDeviceLocation(Activity activity) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (permissionHandler.isFineLocationPermissionGranted()) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {

                        }
                    } else {

                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void setMapGeofence(Activity activity) {
        placeGeofence(activity, radiusOfTheGameField, mapMiddelPoint);
    }

    public void placeGeofence(Activity activity, float radiusOfTheGameField, LatLng latlng) {
        //checking and asking for BackgroundLocationPermission
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                addGeofence(activity, radiusOfTheGameField, latlng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission
                        .ACCESS_BACKGROUND_LOCATION)) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission
                            .ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission
                            .ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        } else {
            addGeofence(activity, radiusOfTheGameField, latlng);
        }
    }

    private void addGeofence(Activity activity, float geofenceRadius, LatLng latLng) {
        mapGeofence = geofenceHandler.getGeofence(generateGeofenceID(),
                latLng,
                geofenceRadius,
                Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_ENTER);
        geofencingRequest = geofenceHandler.getGeofencingRequest(mapGeofence);
        PendingIntent pendingIntent = geofenceHandler.getPendingIntent();
        if ((ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Geofence Added");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHandler.getErrorString(e);
                        Log.d(TAG, "onFailure" + errorMessage);
                    }
                });
    }

    public int getPlayerOnTheMapStatus() {
        return playerOnTheMapStatus;
    }

    /**
     * Generates a unique ID (during a Session)
     *
     * @return GeofenceID starting at 1
     */
    private String generateGeofenceID() {
        int id = Integer.parseInt(geofenceID);
        id++;
        geofenceID = String.valueOf(id);
        return geofenceID;
    }

}
