package de.uhh.detectives.frontend.location.impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;

import de.uhh.detectives.frontend.location.api.LocationHandler;

public class LocationHandlerImpl implements LocationHandler {

    private static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTET_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final String TAG = LocationHandlerImpl.class.getSimpleName();

    private final FusedLocationProviderClient locationClient;
    private final SettingsClient settingsClient;
    private final LocationRequest locationRequest;
    private final LocationSettingsRequest locationSettingsRequest;
    private final LocationCallback locationCallback;

    private Location currentLocation;

    private boolean locationUpdatesEnabled = false;

    public LocationHandlerImpl(final Context context, final Activity activity) {
        checkLocationPermissions(context, activity);

        locationClient = LocationServices.getFusedLocationProviderClient(activity);
        settingsClient = LocationServices.getSettingsClient(activity);

        locationSettingsRequest = setUpLocationSettingsRequest();
        locationRequest = setUpLocationRequest();
        locationCallback = setUpLocationCallback();
    }

    @Override
    public Location getCurrentLocation(final Context context) {
        if (!locationUpdatesEnabled) {
            final String errorMessage = "Location requested but updates are currently disabled!";
            Log.e(TAG, errorMessage);
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
        return currentLocation;
    }

    @Override
    public boolean isLocationUpdatesEnabled() {
        return locationUpdatesEnabled;
    }

    @Override
    public void enableLocationUpdates(final Activity activity) {
        startLocationUpdates(activity);
    }

    @Override
    public void disableLocationUpdates(final Activity activity) {
        stopLocationUpdates(activity);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(final Activity activity) {
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(activity, locationSettingsResponse -> {
                    locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    locationUpdatesEnabled = true;
                })
                .addOnFailureListener(activity, e -> {
                    locationUpdatesEnabled = false;
                    int statusCode = ((ApiException) e).getStatusCode();
                    handleFailure(activity, statusCode, e);
                });
    }

    private void stopLocationUpdates(final Activity activity) {
        locationClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(activity, task -> Log.d(TAG, "Location update stopped."));
    }

    private void checkLocationPermissions(@NonNull final Context context, @NonNull final Activity activity) {
        final int fineLocation = context.checkSelfPermission(ACCESS_FINE_LOCATION);
        final int coarseLocation = context.checkSelfPermission(ACCESS_FINE_LOCATION);
        final boolean fineLocationGranted = fineLocation == PackageManager.PERMISSION_GRANTED;
        final boolean coarseLocationGranted = coarseLocation == PackageManager.PERMISSION_GRANTED;
        if (!fineLocationGranted || !coarseLocationGranted) {
            ActivityCompat.requestPermissions(activity,
                    new String[] {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_CHECK_SETTINGS);
        }
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
}
