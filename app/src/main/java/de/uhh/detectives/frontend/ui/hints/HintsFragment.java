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
import java.util.List;
import java.util.Locale;

import de.uhh.detectives.frontend.GameActivity;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentHintsBinding;
import de.uhh.detectives.frontend.model.Hint;
import de.uhh.detectives.frontend.repository.HintRepository;

public class HintsFragment extends Fragment {

    private FragmentHintsBinding binding;

    private AppDatabase db;
    private HintRepository hintRepository;
    private boolean newHintFound = true;
    private List<HintModel> hintsForUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHintsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = AppDatabase.getDatabase(getContext());
        hintRepository = db.getHintRepository();

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
        final List<Hint> hints = hintRepository.getAllHintsWhere(true);
        final List<HintModel> hintModels = new ArrayList<>();
        final GameActivity gameActivity = (GameActivity) getActivity();
        if (gameActivity != null && gameActivity.getGameStartTime() != null) {
            for (int i = 0; i < hints.size(); i++) {
                if (hints.get(i).getReceived()
                        && !hintModels.contains(createHintModel(hints.get(i)))
                        && ((hints.get(i).getPossessorId() == null)
                        || db.getUserDataRepository().findFirst().getUserId()
                        == hints.get(i).getPossessorId())) {
                    hintModels.add(createHintModel(hints.get(i)));
                }
            }
        }
        return hintModels;
    }

    private HintModel createHintModel(Hint hint) {
        final HintModel hintModel;
        String iconName;
        iconName = "ic_hint_" + hint.getDescription().toLowerCase(Locale.ROOT);
        final int iconIdentifier = getResources().getIdentifier(iconName,
                "drawable",
                getActivity().getPackageName());
        hintModel = new HintModel(hint.getCategory(),
                hint.getDescription() + " ist es nicht!",
                iconIdentifier);
        return hintModel;
    }

    private Runnable setUpNotificationRunnable(final HintAdapter adapter, final Handler handler) {
        return new Runnable() {
            @Override
            public void run() {
                if (HintAdapter.isNewHintFound()) {
                    adapter.notifyItemInserted(hintsForUser.size());
                }
                handler.post(this);
                HintAdapter.setNewHintFound(false);
            }
        };
    }
}