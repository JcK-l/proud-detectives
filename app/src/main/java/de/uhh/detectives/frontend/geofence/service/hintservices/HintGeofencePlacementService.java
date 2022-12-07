package de.uhh.detectives.frontend.geofence.service.hintservices;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContextWrapper;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;

import de.uhh.detectives.frontend.geofence.service.GeofenceCreatorService;
import de.uhh.detectives.frontend.geofence.service.GeofencePlacementService;


public class HintGeofencePlacementService extends ContextWrapper implements GeofencePlacementService {
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private static final String TAG = "HintGeofencePlacementService";

    private String geofenceID = "0";

    private GeofencingClient geofencingClient;
    private GeofenceCreatorService geofenceCreatorService;


    private final HintGeofenceBroadcastReceiver receiver;

    public HintGeofencePlacementService(final Activity activity,
                                        Bundle savedInstanceState,
                                        GeofencingClient geofencingClient,
                                        GeofenceCreatorService geofenceCreatorService) {
        super(activity);
        this.geofencingClient = geofencingClient;
        this.geofenceCreatorService =  geofenceCreatorService;
        receiver = new HintGeofenceBroadcastReceiver();
        onCreate(activity);
    }
    @Override
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

    @Override
    public void addGeofence(Activity activity, Geofence geofence) {
        GeofencingRequest geofencingRequest;

            geofencingRequest = geofenceCreatorService.getGeofencingRequestHint(geofence);

        PendingIntent pendingIntent = geofenceCreatorService.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: Geofence Added");
                })
                .addOnFailureListener(e -> {
                    String errorMessage = geofenceCreatorService.getErrorString(e);
                    Log.d(TAG, "Geofence placement failed: " + errorMessage);
                });
    }

    @Override
    public void onCreate(Activity activity) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.IDReceiver");
        Log.d(TAG, "Register receiver now");
        registerReceiver(receiver, filter);
    }
}
