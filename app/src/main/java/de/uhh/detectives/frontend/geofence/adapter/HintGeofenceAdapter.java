package de.uhh.detectives.frontend.geofence.adapter;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uhh.detectives.frontend.geofence.service.GeofenceCreatorService;
import de.uhh.detectives.frontend.geofence.service.GeofenceDestroyService;
import de.uhh.detectives.frontend.geofence.service.hintservices.HintGeofencePlacerService;
import de.uhh.detectives.frontend.model.Hint;

public class HintGeofenceAdapter implements GeofenceAdapter {
    private static final float RADIUS_OF_HINT = 100000;
    private final List<Hint> hints;
    private final GeofenceDestroyService geofenceDestroyService;
    private final GeofencingClient geofencingClient;
    private final GeofenceCreatorService geofenceCreatorService;
    private final HintGeofencePlacerService hintGeofencePlacerService;

    private Map<String, Geofence> hintGeofences;

    public HintGeofenceAdapter(List<Hint> hints, Activity activity, Bundle savedInstanceState) {
        this.hints = hints;

        hintGeofences = new HashMap<>();
        geofencingClient = LocationServices.getGeofencingClient(activity);
        geofenceCreatorService = new GeofenceCreatorService(activity);
        geofenceDestroyService = new GeofenceDestroyService(geofencingClient);
        hintGeofencePlacerService = new HintGeofencePlacerService(activity,
                savedInstanceState, geofencingClient, geofenceCreatorService);
    }

    @Override
    public void createGeofences() {
        for (Hint hint : hints) {
            if (!hint.getReceived()) {
                LatLng pos = createLatLngForHint(hint);
                hintGeofences.put(hint.getDescription(), hintGeofencePlacerService.placeGeofence(pos,
                        RADIUS_OF_HINT, hint.getDescription()));
            }
        }
    }

    @Override
    public void destroyGeofence(String id) {
        List<Geofence> geofences = new ArrayList<>();
        geofences.add(hintGeofences.get(id));
        if (geofences.get(0) != null) {
            geofenceDestroyService.removeGeofences(geofences);
        }
    }

    @Override
    public Geofence getGeofence(String id) {
        return hintGeofences.get(id);
    }

    public void destroyAllHintGeofences() {
        geofenceDestroyService.removeAllGeofences(geofenceCreatorService.getPendingIntent());
    }

    private LatLng createLatLngForHint(Hint hint) {
        return new LatLng(hint.getLatitude(), hint.getLongitude());
    }
}
