package de.uhh.detectives.frontend.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import de.uhh.detectives.frontend.permissionhelper.LocationPermissionHandler;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageHandler;


public class LocationHandler extends ContextWrapper {
    private static final String KEY_LOCATION = "location";
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private static final String TAG = "MapActivity";
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTET_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;


    private String geofenceID = "0";

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;

    private GeofencingClient geofencingClient;
    private GeofenceHandler geofenceHandler;
    private PushMessageHandler pushMessageHandler;
    private LocationPermissionHandler locationPermissionHandler;

    private final LocationSettingsRequest locationSettingsRequest;
    private final SettingsClient settingsClient;
    private final LocationCallback locationCallback;
    private final LocationRequest locationRequest;

    private boolean locationUpdatesEnabled = false;
    private Location currentLocation;

    public LocationHandler(final Activity activity,Bundle savedInstanceState) {
        super(activity);
        locationPermissionHandler = new LocationPermissionHandler(activity);
        pushMessageHandler = new PushMessageHandler(activity);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        geofencingClient = LocationServices.getGeofencingClient(activity);
        settingsClient = LocationServices.getSettingsClient(activity);
        geofenceHandler = new GeofenceHandler(activity);
        locationSettingsRequest = setUpLocationSettingsRequest();
        locationRequest = setUpLocationRequest();
        locationCallback = setUpLocationCallback();
        onCreate(activity);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
    }

    private final GeofenceBroadcastReceiver receiver = new GeofenceBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Setting ID now");
            int transitionType = intent.getIntExtra("incomingTransition", 0);
            switch (transitionType) {
                case 1:
                    Log.d(TAG, "erste aktivität");
                    break;
                case 2:
                    Log.d(TAG, "andere aktivität");
                    break;
                default:
                    Log.d(TAG, "TransitionType did not match ENTER or EXIT");
            }
        }
    };

    private void onCreate(Activity activity){
        enableLocationUpdates(activity);
        getDeviceLocation(activity);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.IDReceiver");
        Log.d(TAG, "Register receiver now");
        registerReceiver(receiver, filter);
    }

    public Location getCurrentLocation(final Context context) {
        if (!locationUpdatesEnabled) {
            final String errorMessage = "Location requested but updates are currently disabled!";
            Log.e(TAG, errorMessage);
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
        return currentLocation;
    }

    public boolean isLocationUpdatesEnabled() {
        return locationUpdatesEnabled;
    }

    public void enableLocationUpdates(final Activity activity) {
        startLocationUpdates(activity);
    }

    public void disableLocationUpdates(final Activity activity) {
        stopLocationUpdates(activity);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(final Activity activity) {
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(activity, locationSettingsResponse -> {
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                            locationCallback,
                            Looper.myLooper());
                    locationUpdatesEnabled = true;
                })
                .addOnFailureListener(activity, e -> {
                    locationUpdatesEnabled = false;
                    int statusCode = ((ApiException) e).getStatusCode();
                    handleFailure(activity, statusCode, e);
                });
    }


    private void stopLocationUpdates(final Activity activity) {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(activity, task -> Log.d(TAG, "Location update stopped."));
    }


    private void handleFailure(final Activity activity, final int statusCode, final Exception e) {
        switch (statusCode) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied");
                try {
                    final ResolvableApiException rae = (ResolvableApiException) e;
                    rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sie) {
                    Log.i(TAG, "PendingIntent unable to execute request");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                final String errorMessage = "Location settings are inadequate.";
                Log.e(TAG, errorMessage);

                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private LocationCallback setUpLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    private LocationRequest setUpLocationRequest() {
        return new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_IN_MILLISECONDS)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(FASTET_UPDATE_INTERVAL_IN_MILLISECONDS)
                .build();
    }

    private LocationSettingsRequest setUpLocationSettingsRequest() {
        return new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build();
    }


    private Location getDeviceLocation(Activity activity) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionHandler.isFineLocationPermissionGranted()) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                    }
                    else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());

                    }
                });
            }
            else {
                locationPermissionHandler.askFineLocationPermissions();
                locationPermissionHandler.askCoarseLocation();
            }
        }
        catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
        return lastKnownLocation;
    }

    public void placeGeofence(Activity activity, Geofence geofence) {
        //checking and asking for BackgroundLocationPermission
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                addGeofence(activity, geofence);
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
            addGeofence(activity, geofence);
        }
    }

    private void addGeofence(Activity activity, Geofence geofence) {
        GeofencingRequest geofencingRequest = geofenceHandler.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHandler.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: Geofence Added");
                })
                .addOnFailureListener(e -> {
                    String errorMessage = geofenceHandler.getErrorString(e);
                    Log.d(TAG, "onFailure" + errorMessage);
                });
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
