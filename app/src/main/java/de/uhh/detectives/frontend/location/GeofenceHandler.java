package de.uhh.detectives.frontend.location;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import de.uhh.detectives.frontend.location.impl.LocationHandlerImpl;
import de.uhh.detectives.frontend.permissionhelper.LocationPermissionHandler;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageHandler;


public class GeofenceHandler extends ContextWrapper {
    private static final String KEY_LOCATION = "location";
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private static final String TAG = "MapActivity";

    private String geofenceID = "0";

    private FusedLocationProviderClient fusedLocationProviderClient;

    private final GeofencingClient geofencingClient;
    private final GeofenceHelper geofenceHelper;

    public GeofenceHandler(final Activity activity, Bundle savedInstanceState) {
        super(activity);
        LocationPermissionHandler locationPermissionHandler = new LocationPermissionHandler(activity);
        PushMessageHandler pushMessageHandler = new PushMessageHandler(activity);
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        geofencingClient = LocationServices.getGeofencingClient(activity);
        geofenceHelper = new GeofenceHelper(activity);
        LocationHandlerImpl locationHandler = new LocationHandlerImpl(activity.getApplicationContext(), activity);

        onCreate(activity);

        if (savedInstanceState != null) {
            Location lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
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
       // locationHandler.enableLocationUpdates(activity);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.IDReceiver");
        Log.d(TAG, "Register receiver now");
        registerReceiver(receiver, filter);
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
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: Geofence Added"))
                .addOnFailureListener(e -> {
                    String errorMessage = geofenceHelper.getErrorString(e);
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
