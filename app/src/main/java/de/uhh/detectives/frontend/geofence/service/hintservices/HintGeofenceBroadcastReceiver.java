package de.uhh.detectives.frontend.geofence.service.hintservices;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.geofence.service.GeofenceDestroyService;
import de.uhh.detectives.frontend.model.Hint;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;
import de.uhh.detectives.frontend.repository.HintRepository;

public class HintGeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "HintGeofenceBroadcastReceiver";
    private List<Geofence> triggeredGeofences;
    private PushMessageService pushMessageService;
    private AppDatabase db;
    private HintRepository hintRepository;
    private GeofenceDestroyService geofenceDestroyService;
    private GeofencingClient geofencingClient;

    public HintGeofenceBroadcastReceiver() {
//        this.geofencingClient = geofencingClient;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        pushMessageService = new PushMessageService(context);
        int transitionType = 0;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        db = AppDatabase.getDatabase(context.getApplicationContext());
        hintRepository = db.getHintRepository();
//        geofenceDestroyService = new GeofenceDestroyService(geofencingClient);

        if (geofencingEvent != null && geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event");
            return;
        }

        if (geofencingEvent != null) {
            transitionType = geofencingEvent.getGeofenceTransition();
            triggeredGeofences = geofencingEvent.getTriggeringGeofences();
        }

        switch(transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d(TAG, "GEOFENCE_TRANSITION_ENTER");
                handleHintFound();
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.d(TAG, "GEOFENCE_TRANSITION_DWELL");
                handleHintFound();
                break;
            default:
                Log.d(TAG, "geofencingEvent was Null");
        }
    }

    private void handleHintFound(){
        for(Geofence hintGeofence: triggeredGeofences){
            List<Hint> hints = hintRepository.findHintsById(hintGeofence.getRequestId());
            for (Hint hint: hints) {
                pushMessageService.pushFindHintMessage(hint.getCategory(), hint.getDescription());
                hint.setReceived(true);
            }
//            geofenceDestroyService.removeGeofences(triggeredGeofences);
            Log.d(TAG, "Destroyed " + hintGeofence.getRequestId() + "geofence");
        }
    }

}

