package de.uhh.detectives.frontend.ui.maps;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import de.uhh.detectives.frontend.MainActivity;
import de.uhh.detectives.frontend.databinding.FragmentMapsBinding;
import de.uhh.detectives.frontend.location.api.LocationHandler;

public class MapsFragment extends Fragment {

    private FragmentMapsBinding binding;
    private LocationHandler locationHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Context context = this.getContext();
        if (context == null) {
            return root;
        }

        final MainActivity activity = (MainActivity) context;
        locationHandler = activity.getLocationHandler();
        if (!locationHandler.isLocationUpdatesEnabled()) {
            locationHandler.enableLocationUpdates(activity);
        }
        Location location = locationHandler.getCurrentLocation(context);

        // TODO: do something useful with location but for now just toast it once
        if (location != null) {
            final String toastMessage = String.format(Locale.ROOT, "Lat. %f, Long. %f",
                    location.getLatitude(), location.getLongitude());
            Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}