package de.uhh.detectives.frontend.ui.maps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mapbox.maps.MapView;

import de.uhh.detectives.frontend.databinding.FragmentMapsBinding;

public class MapsFragment extends Fragment {

    private FragmentMapsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Context context = this.getContext();
        final Activity activity = this.getActivity();

        if (context == null || activity == null) {
            return root;
        }

        final MapView mapView = binding.mapView;
        final LocationHandler locationHandler = new LocationHandler();
        locationHandler.handleLocation(context, activity);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}