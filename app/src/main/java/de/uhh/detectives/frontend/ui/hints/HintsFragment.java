package de.uhh.detectives.frontend.ui.hints;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.uhh.detectives.frontend.MainActivity;
import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.databinding.FragmentHintsBinding;

public class HintsFragment extends Fragment {

    private FragmentHintsBinding binding;

    // variable can be removed once we load hints from backend
    private List<HintModel> hintModels;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHintsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        hintModels = setUpHintModels(); // row can be removed once we load hints from backend

        final List<HintModel> hintsForUser = getHintsForUser();
        final RecyclerView recyclerViewHints = binding.recyclerViewHints;
        final HintAdapter adapter = new HintAdapter(this.getContext(), hintsForUser);
        recyclerViewHints.setAdapter(adapter);
        recyclerViewHints.setLayoutManager(new LinearLayoutManager(this.getContext()));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private List<HintModel> getHintsForUser() {
        // TODO: ask backend for actual hints
        // but for now we just take hints depending on application up-time
        final List<HintModel> hints = new ArrayList<>();
        final MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null && mainActivity.getGameStartTime() != null) {
            final long applicationUpTime = System.currentTimeMillis() - mainActivity.getGameStartTime();
            // one hint every 5 seconds
            for (int i = 0; i < applicationUpTime / 3000; i++) {
                if (i < hintModels.size()) {
                    hints.add(hintModels.get(i));
                }
            }
        }
        return hints;
    }

    // method can be removed once we load hints from backend
    private List<HintModel> setUpHintModels() {
        final List<HintModel> hintModels = new ArrayList<>();
        hintModels.addAll(createHintsFor("Waffe", getResources().getStringArray(R.array.weapon_names)));
        hintModels.addAll(createHintsFor("Person", getResources().getStringArray(R.array.person_names)));
        hintModels.addAll(createHintsFor("Ort", getResources().getStringArray(R.array.location_names)));
        return hintModels;
    }

    private List<HintModel> createHintsFor(final String category, final String[] descriptions) {
        final List<HintModel> hintModels = new ArrayList<>();
        for (final String description : descriptions) {
            hintModels.add(new HintModel(category, description + " ist es nicht!", R.drawable.ic_food));
        }
        return hintModels;
    }
}