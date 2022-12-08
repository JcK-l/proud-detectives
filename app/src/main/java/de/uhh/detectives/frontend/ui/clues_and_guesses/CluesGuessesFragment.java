package de.uhh.detectives.frontend.ui.clues_and_guesses;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import de.uhh.detectives.frontend.GameGhostActivity;
import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentCluesGuessesBinding;
import de.uhh.detectives.frontend.model.CluesGuessesState;
import de.uhh.detectives.frontend.model.Message.CluesGuessesStateMessage;
import de.uhh.detectives.frontend.model.Message.EndGameMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.BasicEvent;
import de.uhh.detectives.frontend.repository.CluesGuessesStateRepository;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class CluesGuessesFragment extends Fragment {

    private FragmentCluesGuessesBinding binding;
    private ImageView imageSuspicionLeft;
    private ImageView imageSuspicionMiddle;
    private ImageView imageSuspicionRight;
    private ImageView numberOfGuesses;
    private CardView cardview;

    private final int MAX_TRIES = 3;

    private TcpMessageService tcpMessageService;

    private AppDatabase db;
    private UserData user;
    private CluesGuessesState cgState;
    private CluesGuessesStateRepository cluesGuessesStateRepository;

    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            tcpMessageService = ((TcpMessageService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            tcpMessageService = null;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCluesGuessesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = AppDatabase.getDatabase(getContext());
        user = db.getUserDataRepository().findFirst();

        cluesGuessesStateRepository = db.getCluesGuessesStateRepository();
        if (db.getCluesGuessesStateRepository().findFirst() == null) {
            CluesGuessesState cluesGuessesState = createDefaultCluesGuessesState();
            db.getCluesGuessesStateRepository().insert(cluesGuessesState);
        }
        cgState = db.getCluesGuessesStateRepository().findFromId(user.getUserId());

        Intent intent = new Intent(getActivity(), TcpMessageService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);

        initViews();
        setUpListeners();

        cardview.setCardBackgroundColor(ContextCompat.getColor(getContext(), cgState.getCardColor()));

        String iconName = "number_of_tries" + (MAX_TRIES - cgState.getNumberOfTries());
        final int iconIdentifier = getResources().getIdentifier(iconName,"drawable", getActivity().getPackageName());
        numberOfGuesses.setImageResource(iconIdentifier);

        imageSuspicionLeft.setTag(cgState.getSuspicionLeftTag());
        imageSuspicionMiddle.setTag(cgState.getSuspicionMiddleTag());
        imageSuspicionRight.setTag(cgState.getSuspicionRightTag());

        imageSuspicionLeft.setImageResource(cgState.getSuspicionLeft());
        imageSuspicionMiddle.setImageResource(cgState.getSuspicionMiddle());
        imageSuspicionRight.setImageResource(cgState.getSuspicionRight());

        final RecyclerView recyclerViewCluesGuesses = binding.recyclerViewCluesGuesses;
        final CluesGuessesAdapter adapter = new CluesGuessesAdapter(this.getContext(), cgState.getCells());
        recyclerViewCluesGuesses.setAdapter(adapter);

        recyclerViewCluesGuesses.setLayoutManager(new GridLayoutManager(this.getContext(), 5) {
            @Override
            public boolean canScrollVertically(){
                return false;
            }
        });

        return root;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEmptyMessage(BasicEvent basicEvent) {
        tcpMessageService.sendMessageToServer(new CluesGuessesStateMessage(
                cluesGuessesStateRepository.findFromId(user.getUserId())));
    }

    private CluesGuessesState createDefaultCluesGuessesState() {
        CluesGuessesState cluesGuessesState = new CluesGuessesState();
        cluesGuessesState.setCells(setUpCells());
        cluesGuessesState.setCardColor(R.color.white);
        cluesGuessesState.setSuspicionLeft(R.drawable.ic_suspicion_default);
        cluesGuessesState.setSuspicionMiddle(R.drawable.ic_suspicion_default);
        cluesGuessesState.setSuspicionRight(R.drawable.ic_suspicion_default);
        cluesGuessesState.setSuspicionLeftTag("waffe:default");
        cluesGuessesState.setSuspicionMiddleTag("person:default");
        cluesGuessesState.setSuspicionRightTag("ort:default");
        cluesGuessesState.setPlayerId(user.getUserId());
        return cluesGuessesState;
    }

    private void setUpListeners() {
        cardview.setOnClickListener( view -> {
            if (cgState.getNumberOfTries() == MAX_TRIES){
                createDeathScreen();
                return;
            }
            new MaterialAlertDialogBuilder(getContext(), R.style.AlertConfirmSelection)
                    .setTitle("Guess")
                    .setMessage("Do you want to confirm your selection?")
                    .setPositiveButton("Yes",
                            (dialog, which) -> {
                                guessingGame();
                                dialog.dismiss();
                            })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setIcon(R.drawable.ic_maybe)
                    .show();
        });

        setListenerFor(imageSuspicionLeft);
        setListenerFor(imageSuspicionMiddle);
        setListenerFor(imageSuspicionRight);
    }

    private void initViews() {
        cardview = binding.cardView;
        numberOfGuesses = binding.imageNumberOfGuesses;

        imageSuspicionLeft = binding.cardView.findViewById(R.id.image_suspicion_left);
        imageSuspicionMiddle = binding.cardView.findViewById(R.id.image_suspicion_middle);
        imageSuspicionRight = binding.cardView.findViewById(R.id.image_suspicion_right);
    }

    private void createDeathScreen() {
        View popUpView = getLayoutInflater().inflate(R.layout.dialog_you_died,
                null); // inflating popup layout
        PopupWindow popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true); // Creation of popup
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.showAtLocation(popUpView, Gravity.CENTER, 0, 0); // Displaying popup

        CardView cardView = popUpView.getRootView().findViewById(R.id.background);
        cardView.setOnClickListener(
                view -> {
                    tcpMessageService.sendMessageToServer(new EndGameMessage(user.getUserId(), false));
                    Intent intentGhost = new Intent(getActivity(), GameGhostActivity.class);
                    startActivity(intentGhost);
                    popupWindow.dismiss();
                    getActivity().finish();
                });
        TextView textView = popUpView.getRootView().findViewById(R.id.textView);
        final Animation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(1500);
        textView.startAnimation(animation);
    }

    private void setListenerFor(ImageView imageView) {
        imageView.setOnDragListener( (view, event) -> {
            String[] tokens = view.getTag().toString().split(":");
            String category = tokens[0];
            String description = tokens[1];

            switch(event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    String[] inTokens = event.getClipDescription().getLabel().toString().split(":");
                    String inCategory = inTokens[0];
                    String inDescription = inTokens[1];

                    // checks if image can be dragged to it
                    if (inCategory.equals(category) && !inDescription.equals(description)) {
                        view.setAlpha(0.5f);
                        view.invalidate();
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    view.setAlpha(0.5f);
                    view.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    view.setAlpha(1f);
                    view.invalidate();
                    String resultDescription = event.getClipData().getItemAt(0).getText().toString();

                    final int MAX_NUMBER_WEAPON = 9;
                    final int MAX_NUMBER_PERSON = 6;
                    int index = IntStream.range(0, cgState.getCells().size())
                            .filter(i -> cgState.getCells().get(i).getDescription().equals(resultDescription))
                            .findFirst()
                            .orElse(-1);
                    index += 1;
                    switch (category) {
                        case "ort":
                            index -= MAX_NUMBER_PERSON;
                        case "person":
                            index -= MAX_NUMBER_WEAPON;
                            break;
                    }

                    String iconName = "ic_hint_" + category + index;
                    final int drawableId = getResources().getIdentifier(iconName,"drawable", getActivity().getPackageName());

                    String tag = category + ":" + resultDescription;
                    saveSuspicionState(drawableId, category, tag);

                    cardview.setCardBackgroundColor(ContextCompat.getColor(getContext(), cgState.getCardColor()));

                    ((ImageView) view).setImageResource(drawableId);
                    view.setTag(tag);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    view.setAlpha(1f);
                    view.invalidate();
                    return true;
                default:
                    break;
            }
            return false;
        });
    }

    private void saveSuspicionState(final int drawableId, final String category, final String tag) {
        cluesGuessesStateRepository.updateCardColor(R.color.white, user.getUserId());
        switch (category) {
            case "waffe":
                cluesGuessesStateRepository.updateSuspicionLeft(drawableId, user.getUserId());
                cluesGuessesStateRepository.updateSuspicionLeft(tag, user.getUserId());
                break;
            case "person":
                cluesGuessesStateRepository.updateSuspicionMiddle(drawableId, user.getUserId());
                cluesGuessesStateRepository.updateSuspicionMiddle(tag, user.getUserId());
                break;
            case "ort":
                cluesGuessesStateRepository.updateSuspicionRight(drawableId, user.getUserId());
                cluesGuessesStateRepository.updateSuspicionRight(tag, user.getUserId());
                break;
        }
        cgState = cluesGuessesStateRepository.findFromId(user.getUserId());
        tcpMessageService.sendMessageToServer(new CluesGuessesStateMessage(cgState));
    }

    private void guessingGame() {
        SolutionVerifier solutionVerifier = new SolutionVerifier(getContext());
        String[] suspicion = new String[3];
        suspicion[0] = cgState.getSuspicionLeftTag().split(":")[1];
        suspicion[1] = cgState.getSuspicionMiddleTag().split(":")[1];
        suspicion[2] = cgState.getSuspicionRightTag().split(":")[1];
        switch (solutionVerifier.compareToSolution(suspicion)) {
            case SUCCESS:
                cluesGuessesStateRepository.updateCardColor(R.color.correct_guess, user.getUserId());
                tcpMessageService.sendMessageToServer(new EndGameMessage(user.getUserId(), true));
                break;
            case SEMIFAILED:
                cluesGuessesStateRepository.updateCardColor(R.color.mixed_guess, user.getUserId());
                cluesGuessesStateRepository.updateNumberOfTries(user.getUserId());
                break;
            case FAILED:
                cluesGuessesStateRepository.updateCardColor(R.color.wrong_guess, user.getUserId());
                cluesGuessesStateRepository.updateNumberOfTries(user.getUserId());
                break;
            case INVALID:
                Toast.makeText(getContext(), "invalid selection", Toast.LENGTH_SHORT).show();
                return;
        }
        cgState = cluesGuessesStateRepository.findFromId(user.getUserId());
        tcpMessageService.sendMessageToServer(new CluesGuessesStateMessage(cgState));

        String iconName = "number_of_tries" + (MAX_TRIES - cgState.getNumberOfTries());
        final int iconIdentifier = getResources().getIdentifier(iconName,"drawable", getActivity().getPackageName());
        numberOfGuesses.setImageResource(iconIdentifier);

        cardview.setCardBackgroundColor(ContextCompat.getColor(getContext(), cgState.getCardColor()));

        if (cgState.getNumberOfTries() == MAX_TRIES) {
            db.getPlayerRepository().setDead(true, user.getUserId());
            createDeathScreen();
        }
    }

    private List<Cell> setUpCells() {
        List<Cell> cells = new ArrayList<>();

        cells.addAll(createCellsFor("Waffe", getResources().getStringArray(R.array.weapon_names)));
        cells.addAll(createCellsFor("Person", getResources().getStringArray(R.array.person_names)));
        cells.addAll(createCellsFor("Ort", getResources().getStringArray(R.array.location_names)));

        return cells;
    }

    private List<Cell> createCellsFor(final String category, final String[] descriptions){
        List<Cell> cells = new ArrayList<>();

        String iconName;
        String category_lower = category.toLowerCase(Locale.ROOT);
        String description_lower;
        for ( int i = 0; i < descriptions.length; i++) {
            iconName = "ic_hint_" + category_lower + (i + 1);
            description_lower = descriptions[i].toLowerCase(Locale.ROOT);
            // different icons for presentation purposes
            final int iconIdentifier = getResources().getIdentifier(iconName,"drawable", getActivity().getPackageName());
            cells.add(new Cell(CellState.NEUTRAL, iconIdentifier, category_lower, description_lower));
        }

        return cells;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unbindService(connection);
        EventBus.getDefault().unregister(this);
        binding = null;
    }
}