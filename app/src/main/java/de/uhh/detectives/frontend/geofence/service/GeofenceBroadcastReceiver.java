package de.uhh.detectives.frontend.geofence.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiver";

    //This method is called when the BroadcastReceiver is receiving; mostly used for debugging
    @Override
    public void onReceive(Context context, Intent intent) {
        PushMessageService pushMessageService = new PushMessageService(context);


        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent != null && geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event");
            return;
        }

        //List of every triggered Geofence
        List<Geofence> geofenceList = null;
        if (geofencingEvent != null) {
            geofenceList = geofencingEvent.getTriggeringGeofences();
        }

        //Location of the triggered Geofence
        if (geofencingEvent != null) {
            Location location = geofencingEvent.getTriggeringLocation();
        }
        //TransitionType of the Geofencing Event
        int transitionType = 0;
        if (geofencingEvent != null) {
            transitionType = geofencingEvent.getGeofenceTransition();
        }

        switch(transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d(TAG, "GEOFENCE_TRANSITION_ENTER");
                pushMessageService.pushEnteredMapMessage();
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.d(TAG, "GEOFENCE_TRANSITION_DWELL");
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d(TAG, "GEOFENCE_TRANSITION_EXIT");
                pushMessageService.pushMapExitMessage();
                break;
            default:
                Log.d(TAG, "geofencingEvent was Null");
        }
    }

}

