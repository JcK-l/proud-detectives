package de.uhh.detectives.frontend.service;

import android.content.Context;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.model.Hint;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;
import de.uhh.detectives.frontend.repository.HintRepository;
import de.uhh.detectives.frontend.ui.hints.HintAdapter;

public class HintServiceImpl implements HintService {

    private final PushMessageService pushMessageService;
    private final HintRepository hintRepository;

    private static final double MAX_DISTANCE_FOR_FOUND_IN_METRES = 10.0d;

    public HintServiceImpl(final Context context) {
        this.pushMessageService = new PushMessageService(context);
        final AppDatabase db = AppDatabase.getDatabase(context.getApplicationContext());
        this.hintRepository = db.getHintRepository();
    }

    @Override
    public void checkHintsOnLocationUpdate(final Location location) {
        final List<Hint> hints = hintRepository.getAllHintsWhere(false);
        final List<Hint> foundHints = getHintsNearUser(location, hints);
        for (final Hint hint : foundHints) {
            pushMessageService.pushFindHintMessage(hint.getCategory(), hint.getDescription());
            hint.setReceived(true);
            hintRepository.updateReceived(true, hint.getDescription());
        }
        // TODO hint tab should update automatically and not only when navigating out and back in
        HintAdapter.setNewHintFound(true);
    }

    private List<Hint> getHintsNearUser(final Location userLocation, final List<Hint> hints) {
        final List<Hint> hintsFound = new ArrayList<>();
        for (final Hint hint : hints) {
            if (hintCloseEnoughToBeFound(userLocation, hint.getLongitude(), hint.getLatitude())) {
                hintsFound.add(hint);
            }
        }
        return hintsFound;
    }

    private boolean hintCloseEnoughToBeFound(final Location userLocation, final Double hintX, final Double hintY) {

        if (hintX == null || hintY == null) {
            return false;
        }

        final double userX = userLocation.getLongitude();
        final double userY = userLocation.getLatitude();
        return calculateDistance(userX, userY, hintX, hintY) < MAX_DISTANCE_FOR_FOUND_IN_METRES;
    }

    private double calculateDistance(final double userX, final double userY,  final Double hintX, final Double hintY) {
        // calculations taken from https://www.movable-type.co.uk/scripts/latlong.html
        double R = 6371000; // metres
        double phi1 = userY * Math.PI/180;
        double phi2 = hintY * Math.PI/180;
        double deltaPhi = (hintY-userY) * Math.PI/180;
        double deltaLambda = (hintX-userX) * Math.PI/180;
        
        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) + Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }


}
