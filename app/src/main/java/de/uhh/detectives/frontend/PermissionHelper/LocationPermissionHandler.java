package de.uhh.detectives.frontend.PermissionHelper;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class LocationPermissionHandler extends ContextWrapper {
    private boolean isFineLocationPermissionGranted;
    private boolean isCoarseLocationPermissionGranted;

    public LocationPermissionHandler(Context base) {
        super(base);
    }

    public void askFineLocationPermissions() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(
                            PermissionGrantedResponse permissionGrantedResponse) {
                        isFineLocationPermissionGranted = true;
                    }

                    @Override
                    public void onPermissionDenied(
                            PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(LocationPermissionHandler.this,
                                "Location Permission Denied",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            PermissionRequest permissionRequest,
                            PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public void askCoarseLocation() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(
                            PermissionGrantedResponse permissionGrantedResponse) {
                        isCoarseLocationPermissionGranted = true;
                    }

                    @Override
                    public void onPermissionDenied(
                            PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(LocationPermissionHandler.this,
                                "Location Permission Denied",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            PermissionRequest permissionRequest,
                            PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public boolean isFineLocationPermissionGranted() {
        return isFineLocationPermissionGranted && isCoarseLocationPermissionGranted;
    }
}
