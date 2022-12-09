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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.uhh.detectives.frontend.GameActivity;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentHintsBinding;
import de.uhh.detectives.frontend.model.Hint;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;
import de.uhh.detectives.frontend.repository.HintRepository;

public class HintsFragment extends Fragment {

    private FragmentHintsBinding binding;

    private AppDatabase db;
    private UserData user;
    private HintRepository hintRepository;
    private boolean newHintFound = true;
    private List<HintModel> hintsForUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHintsBinding.inflate(inflater, container, false);
        EventBus.getDefault().register(this);
        View root = binding.getRoot();
        db = AppDatabase.getDatabase(getContext());
        user = db.getUserDataRepository().findFirst();
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
        final int imageIndex = HintImages.getIndex(hint.getDescription());
        final String categoryName = translateCategory(hint.getCategory());
        final String iconName = "ic_hint_" + categoryName + imageIndex;
        final int iconIdentifier = getResources().getIdentifier(iconName,
                "drawable",
                requireActivity().getPackageName());
        hintModel = new HintModel(hint.getCategory(),
                hint.getDescription() + " ist es nicht!",
                iconIdentifier);
        return hintModel;
    }

    private String translateCategory(final String category) {
        if ("weapon".equalsIgnoreCase(category)) {
            return "waffe";
        } else if ("location".equalsIgnoreCase(category)) {
            return "ort";
        } else {
            return "person";
        }
    }

    private Runnable setUpNotificationRunnable(final HintAdapter adapter, final Handler handler) {
        // Thread to update notification for presentation purposes
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

    // TODO: NICHT DIE BESTE SOLUTION, ABER FÜR DIE PRÄSENTATION IST ES GUT GENUG
    @Subscribe
    public void listenForChatPushMessage(ChatMessageEvent chatMessageEvent) {
        ChatMessage chatMessage = chatMessageEvent.getMessage();
        PushMessageService pushMessageService = new PushMessageService(requireContext());
        if (Objects.requireNonNull(chatMessage.getReceiverId()).equals(user.getUserId())) {
            pushMessageService.pushChatNotification(chatMessage.getPseudonym(), chatMessage.getMessage());
        }
    }

    private static class HintImages {
        protected static int getIndex(final String description) {
            switch (description) {
                case "Dennis Gatow":
                    return 1;
                case "Felix Bloom":
                    return 2;
                case "Tom Gruen":
                    return 3;
                case "Klara Porz":
                    return 4;
                case "Gloria Roth":
                    return 5;
                case "Diana Weiss":
                    return 6;
                case "Kueche":
                    return 1;
                case "Musikzimmer":
                    return 2;
                case "Schlafzimmer":
                    return 3;
                case "Speisezimmer":
                    return 4;
                case "Keller":
                    return 5;
                case "Billardzimmer":
                    return 6;
                case "Bibliothek":
                    return 7;
                case "Garten":
                    return 8;
                case "Eingangshalle":
                    return 9;
                case "Arbeitszimmer":
                    return 10;
                case "Pistole":
                    return 1;
                case "Dolch":
                    return 2;
                case "Seil":
                    return 3;
                case "Kerzenleuchter":
                    return 4;
                case "Rohrzange":
                    return 5;
                case "Heizungsrohr":
                    return 6;
                case "Messer":
                    return 7;
                case "Gift":
                    return 8;
                case "Hantel":
                    return 9;
                default:
                    return 0;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}