package de.uhh.detectives.frontend.ui.hints;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.uhh.detectives.frontend.GameActivity;
import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.databinding.FragmentHintsBinding;
import de.uhh.detectives.frontend.ui.clues_and_guesses.SolutionVerifier;

public class HintsFragment extends Fragment {

    private FragmentHintsBinding binding;

    // variable can be removed once we load hints from backend
    private List<HintModel> hintModels;
    private List<HintModel> hintsForUser;

    private static final int NOTIFICATION_DELAY = 3000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHintsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        hintModels = setUpHintModels(); // row can be removed once we load hints from backend

        Collections.shuffle(hintModels, new Random(322)); // so it is bit more random for presentation purposes

        hintsForUser = getHintsForUser();
        final RecyclerView recyclerViewHints = binding.recyclerViewHints;
        final HintAdapter adapter = new HintAdapter(this.getContext(), hintsForUser);
        recyclerViewHints.setAdapter(adapter);
        recyclerViewHints.setLayoutManager(new LinearLayoutManager(this.getContext()));

        final Handler handler = new Handler();
        handler.post(setUpNotificationRunnable(adapter, handler));

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
        final GameActivity gameActivity = (GameActivity) getActivity();
        if (gameActivity != null && gameActivity.getGameStartTime() != null) {
            final long applicationUpTime = System.currentTimeMillis() - gameActivity.getGameStartTime();
            // one hint every 3 seconds
            for (int i = 0; i < applicationUpTime / NOTIFICATION_DELAY; i++) {
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
        final SolutionVerifier solutionVerifier = new SolutionVerifier(getContext());
        String iconName;
        for ( int i = 0; i < descriptions.length; i++) {
            if (solutionVerifier.getSolution().contains(descriptions[i].toLowerCase(Locale.ROOT)))
                continue;
            iconName = "ic_hint_" + category.toLowerCase(Locale.ROOT) + (i + 1);
            // different icons for presentation purposes
            final int iconIdentifier = getResources().getIdentifier(iconName,"drawable", requireActivity().getPackageName());
            hintModels.add(new HintModel(category, descriptions[i] + " ist es nicht!", iconIdentifier));
        }
        return hintModels;
    }

    private Runnable setUpNotificationRunnable(final HintAdapter adapter, final Handler handler) {
        // Thread to update notification for presentation purposes
        return new Runnable() {
            @Override
            public void run() {
                if (hintsForUser.size() < hintModels.size()) {
                    hintsForUser.add(hintModels.get(hintsForUser.size()));
                    adapter.notifyItemInserted(hintsForUser.size());
                }
                handler.postDelayed(this, NOTIFICATION_DELAY);
            }
        };
    }
}