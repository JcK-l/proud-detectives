package de.uhh.detectives.frontend.ui.hints;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.uhh.detectives.frontend.databinding.FragmentHintsBinding;

public class HintsFragment extends Fragment {

    private FragmentHintsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HintsViewModel hintsViewModel =
                new ViewModelProvider(this).get(HintsViewModel.class);

        binding = FragmentHintsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHints;
        hintsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}