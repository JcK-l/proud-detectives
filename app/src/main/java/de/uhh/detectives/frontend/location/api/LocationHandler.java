package de.uhh.detectives.frontend.location.api;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

public interface LocationHandler {
    Location getCurrentLocation(final Context context);
    boolean isLocationUpdatesEnabled();
    void enableLocationUpdates(final Activity activity);
    void disableLocationUpdates(final Activity activity);
}
