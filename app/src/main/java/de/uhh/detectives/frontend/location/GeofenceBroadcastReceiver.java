package de.uhh.detectives.frontend.location;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import de.uhh.detectives.frontend.pushMessages.services.PushMessageHandler;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiv";

    //This method is called when the BroadcastReceiver is receiving; mostly used for debugging
    @Override
    public void onReceive(Context context, Intent intent) {
        PushMessageHandler pushMessageHandler = new PushMessageHandler(context);

        Toast.makeText(context, "Geofence triggered", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "GEOFENCE_TRANSITION_ENTER");
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "GEOFENCE_TRANSITION_DWELL");
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "GEOFENCE_TRANSITION_EXIT");
                pushMessageHandler.pushMapExitMessage();
                break;
            default:
                Toast.makeText(context, "geofencingEvent was Null", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "geofencingEvent was Null");
        }
    }
}

