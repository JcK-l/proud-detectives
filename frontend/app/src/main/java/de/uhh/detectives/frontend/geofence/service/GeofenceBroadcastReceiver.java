package de.uhh.detectives.frontend.geofence.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;
import de.uhh.detectives.frontend.repository.HintRepository;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MapGeofenceBroadcastReceiver";
    private PushMessageService pushMessageService;
    private AppDatabase db;
    private HintRepository hintRepository;

    @Override
    public void onReceive(Context context, Intent intent) {
        pushMessageService = new PushMessageService(context);
        int transitionType = 0;
        db = AppDatabase.getDatabase(context.getApplicationContext());
        hintRepository = db.getHintRepository();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent != null && geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event");
            return;
        }

        if (geofencingEvent != null) {
            transitionType = geofencingEvent.getGeofenceTransition();
        }
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                handleMapEntered();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                handleMapExit();
                break;
            default:
                Log.d(TAG, "geofencingEvent was Null");
        }
    }

    private void handleMapEntered() {
        Log.d(TAG, "GEOFENCE_TRANSITION_ENTER_MAP");
        pushMessageService.pushEnteredMapMessage();
    }

    private void handleMapExit() {
        Log.d(TAG, "GEOFENCE_TRANSITION_EXIT_MAP");
        pushMessageService.pushMapExitMessage();

    }
}

