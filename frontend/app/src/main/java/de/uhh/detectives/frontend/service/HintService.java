package de.uhh.detectives.frontend.service;

import android.location.Location;

public interface HintService {
    void checkHintsOnLocationUpdate(final Location location);
}
