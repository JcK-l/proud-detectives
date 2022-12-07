package de.uhh.detectives.frontend.ui.clues_and_guesses;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentCluesGuessesBinding;
import de.uhh.detectives.frontend.model.CluesGuessesState;
import de.uhh.detectives.frontend.model.Message.WinGameMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.repository.CluesGuessesStateRepository;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class CluesGuessesFragment extends Fragment {

    private FragmentCluesGuessesBinding binding;
    private ImageView image_suspicion_left;
    private ImageView image_suspicion_middle;
    private ImageView image_suspicion_right;
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

        initViews();
        setUpListeners();

        cardview.setCardBackgroundColor(ContextCompat.getColor(getContext(), cgState.getCardColor()));

        String iconName = "number_of_tries" + (MAX_TRIES - cgState.getNumberOfTries());
        final int iconIdentifier = getResources().getIdentifier(iconName,"drawable", getActivity().getPackageName());
        numberOfGuesses.setImageResource(iconIdentifier);

        image_suspicion_left.setTag(cgState.getSuspicion_left_tag());
        image_suspicion_middle.setTag(cgState.getSuspicion_middle_tag());
        image_suspicion_right.setTag(cgState.getSuspicion_right_tag());

        image_suspicion_left.setImageResource(cgState.getSuspicion_left());
        image_suspicion_middle.setImageResource(cgState.getSuspicion_middle());
        image_suspicion_right.setImageResource(cgState.getSuspicion_right());

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

    private CluesGuessesState createDefaultCluesGuessesState() {
        CluesGuessesState cluesGuessesState = new CluesGuessesState();
        cluesGuessesState.setCells(setUpCells());
        cluesGuessesState.setCardColor(R.color.white);
        cluesGuessesState.setSuspicion_left(R.drawable.ic_suspicion_default);
        cluesGuessesState.setSuspicion_middle(R.drawable.ic_suspicion_default);
        cluesGuessesState.setSuspicion_right(R.drawable.ic_suspicion_default);
        cluesGuessesState.setSuspicion_left_tag("waffe:default");
        cluesGuessesState.setSuspicion_middle_tag("person:default");
        cluesGuessesState.setSuspicion_right_tag("ort:default");
        cluesGuessesState.setPlayerId(user.getUserId());
        return cluesGuessesState;
    }

    private void setUpListeners() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    guessingGame();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };
        cardview.setOnClickListener( view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Guess").setMessage("Do you want to confirm your selection?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });

        setListenerFor(image_suspicion_left);
        setListenerFor(image_suspicion_middle);
        setListenerFor(image_suspicion_right);
    }

    private void initViews() {
        cardview = binding.cardView;
        numberOfGuesses = binding.imageNumberOfGuesses;

        image_suspicion_left = binding.cardView.findViewById(R.id.image_suspicion_left);
        image_suspicion_middle = binding.cardView.findViewById(R.id.image_suspicion_middle);
        image_suspicion_right = binding.cardView.findViewById(R.id.image_suspicion_right);
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
    }

    private void guessingGame() {
        if (cgState.getNumberOfTries() == MAX_TRIES){
            Toast.makeText(getContext(), "YOU LOST ALREADY!!!", Toast.LENGTH_LONG).show();
            return;
        }

        SolutionVerifier solutionVerifier = new SolutionVerifier(getContext());
        String[] suspicion = new String[3];
        suspicion[0] = cgState.getSuspicion_left_tag().split(":")[1];
        suspicion[1] = cgState.getSuspicion_middle_tag().split(":")[1];
        suspicion[2] = cgState.getSuspicion_right_tag().split(":")[1];
        switch (solutionVerifier.compareToSolution(suspicion)) {
            case SUCCESS:
                cluesGuessesStateRepository.updateCardColor(R.color.correct_guess, user.getUserId());
                String message = "I won the game with: " + String.valueOf(solutionVerifier.getSolutionWithAmongus());

                // this will be replaced once we get EndGameMessage going
                tcpMessageService.sendMessageToServer(new WinGameMessage(user.getUserId()));
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
                Toast.makeText(getContext(), "INVALID SELECTION!!!", Toast.LENGTH_SHORT).show();
                return;
        }
        cgState = cluesGuessesStateRepository.findFromId(user.getUserId());

        String iconName = "number_of_tries" + (MAX_TRIES - cgState.getNumberOfTries());
        final int iconIdentifier = getResources().getIdentifier(iconName,"drawable", getActivity().getPackageName());
        numberOfGuesses.setImageResource(iconIdentifier);

        if (cgState.getNumberOfTries() == MAX_TRIES) {
            Toast.makeText(getContext(), "YOU LOSE!!!", Toast.LENGTH_LONG).show();
        }
        cardview.setCardBackgroundColor(ContextCompat.getColor(getContext(), cgState.getCardColor()));
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
        binding = null;
    }
}