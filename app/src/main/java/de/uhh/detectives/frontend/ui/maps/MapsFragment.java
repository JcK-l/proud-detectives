package de.uhh.detectives.frontend.ui.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mapbox.maps.MapView;

import com.mapbox.maps.Style;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.databinding.FragmentMapsBinding;

public class MapsFragment extends Fragment {

    private FragmentMapsBinding binding;
    private MapView mapView;

    private static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = binding.mapView;
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS,
                (style) -> addUserIndicatorToMap(this.getContext(), getActivity(), style)
        );
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addUserIndicatorToMap(final Context context, final Activity activity, final Style style) {
        checkLocationPermissions(context, activity);
        updateUserLocation(style, context);
    }

    private void checkLocationPermissions(@NonNull final Context context, @NonNull final Activity activity) {
        final int fineLocation = context.checkSelfPermission(ACCESS_FINE_LOCATION);
        final int coarseLocation = context.checkSelfPermission(ACCESS_FINE_LOCATION);
        final boolean fineLocationGranted = fineLocation == PackageManager.PERMISSION_GRANTED;
        final boolean coarseLocationGranted = coarseLocation == PackageManager.PERMISSION_GRANTED;
        if (!fineLocationGranted || !coarseLocationGranted) {
            ActivityCompat.requestPermissions(activity,
                    new String[] {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 99);
        }
    }

    private void updateUserLocation(final Style style, final Context context) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_red_circle);
        final DrawableToBitmapConverter converter = new DrawableToBitmapConverter();
        final Bitmap marker = converter.convert(drawable);
        // TODO: mark user location
    }
}