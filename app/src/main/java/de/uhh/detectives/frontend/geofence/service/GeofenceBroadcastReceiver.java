package de.uhh.detectives.frontend.geofence.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.model.Hint;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;
import de.uhh.detectives.frontend.repository.HintRepository;
import de.uhh.detectives.frontend.ui.hints.HintAdapter;

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
        for (Geofence geofence: geofences) {
            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    if(isHintGeofenceTriggered(geofence)) {
                        handleHintFound(geofences);
                    }else{
                        handleMapEntered();
                    }
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    if(!isHintGeofenceTriggered(geofence)) {
                        handleMapEntered();
                    }
                    break;
                default:
                    Log.d(TAG, "geofencingEvent was Null");
            }
        }
    }

    private boolean isHintGeofenceTriggered(Geofence geofence){
        return !(geofence.getRequestId().equals("GAME_FIELD"));
    }

    private void handleHintFound(List<Geofence> geofences){
        for(Geofence hintGeofence: geofences){
            List<Hint> hints = hintRepository.findHintsById(hintGeofence.getRequestId());
            for (Hint hint: hints) {
                if(!hint.getReceived()) {
                    pushMessageService.pushFindHintMessage(hint.getCategory(), hint.getDescription());
                    hint.setReceived(true);
                    hintRepository.updateReceived(true, hint.getDescription());
                }
                HintAdapter.setNewHintFound(true);
            }
            Log.d(TAG, "GEOFENCE_TRANSITION_ENTER_HINT");
        }
    }

    private void handleMapEntered(){
        Log.d(TAG, "GEOFENCE_TRANSITION_ENTER_MAP");
        pushMessageService.pushEnteredMapMessage();
    }

    private void handleMapExit(){
        Log.d(TAG, "GEOFENCE_TRANSITION_EXIT_MAP");
        pushMessageService.pushMapExitMessage();
    }
}

