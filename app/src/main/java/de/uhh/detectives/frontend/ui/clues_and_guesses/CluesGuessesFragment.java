package de.uhh.detectives.frontend.ui.clues_and_guesses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.uhh.detectives.frontend.databinding.FragmentCluesGuessesBinding;

public class CluesGuessesFragment extends Fragment {

    private FragmentCluesGuessesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CluesGuessesViewModel cluesGuessesViewModel =
                new ViewModelProvider(this).get(CluesGuessesViewModel.class);

        binding = FragmentCluesGuessesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCluesAndGuesses;
        cluesGuessesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}