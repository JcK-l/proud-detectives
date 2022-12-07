package de.uhh.detectives.frontend.geofence.service.mapservices;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;

public class MapGeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MapGeofenceBroadcastReceiver";

    //This method is called when the BroadcastReceiver is receiving; mostly used for debugging
    @Override
    public void onReceive(Context context, Intent intent) {
        PushMessageService pushMessageService = new PushMessageService(context);
        int transitionType = 0;
        List<Geofence> geofences = new ArrayList<>();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent != null && geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event");
            return;
        }

        if (geofencingEvent != null) {
            transitionType = geofencingEvent.getGeofenceTransition();
            geofences = geofencingEvent.getTriggeringGeofences();
        }

        switch(transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d(TAG, "GEOFENCE_TRANSITION_ENTER");
                pushMessageService.pushEnteredMapMessage();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                if(geofences.size() > 0) {
                    Log.d(TAG, "GEOFENCE_TRANSITION_EXIT");
                }
                pushMessageService.pushMapExitMessage();
                break;
            default:
                Log.d(TAG, "geofencingEvent was Null");
        }
    }

}

