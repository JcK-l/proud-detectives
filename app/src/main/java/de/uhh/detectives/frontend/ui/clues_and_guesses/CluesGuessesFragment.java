package de.uhh.detectives.frontend.ui.clues_and_guesses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.databinding.FragmentCluesGuessesBinding;

public class CluesGuessesFragment extends Fragment {

    private FragmentCluesGuessesBinding binding;
    private ImageView image_suspicion_left;
    private ImageView image_suspicion_middle;
    private ImageView image_suspicion_right;
    private TextView textNumber;
    private CardView cardview;
    private CluesGuessesViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCluesGuessesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(CluesGuessesViewModel.class);

        initViews();
        setUpDefaults();
        setUpListeners();

        cardview.setCardBackgroundColor(viewModel.cardColor);
        textNumber.setText(String.format(Locale.ROOT,"Guesses Left: %d", viewModel.numberOfTries));
        image_suspicion_left.setTag(viewModel.suspicion_left_tag);
        image_suspicion_middle.setTag(viewModel.suspicion_middle_tag);
        image_suspicion_right.setTag(viewModel.suspicion_right_tag);

        final RecyclerView recyclerViewCluesGuesses = binding.recyclerViewCluesGuesses;
        final CluesGuessesAdapter adapter = new CluesGuessesAdapter(this.getContext(), viewModel.cells);
        recyclerViewCluesGuesses.setAdapter(adapter);

        recyclerViewCluesGuesses.setLayoutManager(new GridLayoutManager(this.getContext(), 5) {
            @Override
            public boolean canScrollVertically(){
                return false;
            }
        });

        return root;
    }

    private void setUpDefaults() {
        if (viewModel.cells == null) {
            viewModel.cells = setUpCells();
        }

        if (viewModel.numberOfTries == 0) {
            viewModel.numberOfTries = 3;
        }

        if (viewModel.cardColor == 0) {
            viewModel.cardColor = Color.WHITE;
        }

        if (viewModel.suspicion_left != null) {
            image_suspicion_left.setImageDrawable(viewModel.suspicion_left);
        }
        if (viewModel.suspicion_middle != null) {
            image_suspicion_middle.setImageDrawable(viewModel.suspicion_middle);
        }
        if (viewModel.suspicion_right != null) {
            image_suspicion_right.setImageDrawable(viewModel.suspicion_right);
        }

        if (viewModel.suspicion_left_tag == null) {
            viewModel.suspicion_left_tag = "waffe:default";
            viewModel.suspicion_middle_tag = "person:default";
            viewModel.suspicion_right_tag = "ort:default";
        }
    }

    private void setUpListeners() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        guessingGame();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        cardview.setOnClickListener( view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Guess").setMessage("Do you want to confirm you selection?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });

        setListenerFor(image_suspicion_left);
        setListenerFor(image_suspicion_middle);
        setListenerFor(image_suspicion_right);
    }

    private void initViews() {
        cardview = binding.cardView;
        textNumber = binding.textView;

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
                        ((ImageView)view).setColorFilter(Color.GRAY);
                        view.invalidate();
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    ((ImageView)view).setColorFilter(Color.GRAY);
                    view.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    ((ImageView)view).clearColorFilter();
                    view.invalidate();

                    ImageView dragView = (ImageView) event.getLocalState();
                    Drawable drawable = dragView.getDrawable();

                    String tag = category + ":" + event.getClipData().getItemAt(0).getText().toString();
                    saveSuspicionState(drawable, category, tag);

                    CardView card = (CardView) view.getParent().getParent();
                    card.setCardBackgroundColor(viewModel.cardColor);

                    ((ImageView) view).setImageDrawable(drawable);
                    view.setTag(tag);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    ((ImageView)view).clearColorFilter();
                    view.invalidate();
                    return true;
                default:
                    break;
            }
            return false;
        });
    }

    private void guessingGame() {
        if (viewModel.numberOfTries == -1){
            Toast.makeText(getContext(), "YOU LOST ALREADY!!!", Toast.LENGTH_LONG).show();
            return;
        }

        ForPresentation backend = new ForPresentation();
        String[] suspicion = new String[3];
        suspicion[0] = viewModel.suspicion_left_tag.split(":")[1];
        suspicion[1] = viewModel.suspicion_middle_tag.split(":")[1];
        suspicion[2] = viewModel.suspicion_right_tag.split(":")[1];
        switch (backend.compareToSolution(suspicion)) {
            case SUCCESS:
                viewModel.cardColor = Color.GREEN;
                Toast.makeText(getContext(), "YOU WIN!!!", Toast.LENGTH_LONG).show();
                break;
            case SEMIFAILED:
                viewModel.cardColor = Color.YELLOW;
                if (viewModel.numberOfTries > 1)
                    Toast.makeText(getContext(), "KINDA WRONG!!!", Toast.LENGTH_SHORT).show();
                viewModel.numberOfTries -= 1;
                break;
            case FAILED:
                viewModel.cardColor = Color.RED;
                if (viewModel.numberOfTries > 1)
                    Toast.makeText(getContext(), "WRONG!!!", Toast.LENGTH_SHORT).show();
                viewModel.numberOfTries -= 1;
                break;
            case INVALID:
                Toast.makeText(getContext(), "INVALID SELECTION!!!", Toast.LENGTH_SHORT).show();
                return;
        }
        textNumber.setText(String.format(Locale.ROOT,"Guesses Left: %d", viewModel.numberOfTries));
        if (viewModel.numberOfTries == 0) {
            Toast.makeText(getContext(), "YOU LOSE!!!", Toast.LENGTH_LONG).show();
            viewModel.numberOfTries = -1;
        }
        cardview.setCardBackgroundColor(viewModel.cardColor);
    }

    private void saveSuspicionState(Drawable drawable, String category, String tag) {
        viewModel.cardColor = Color.WHITE;
        switch (category) {
            case "waffe":
                viewModel.suspicion_left = drawable;
                viewModel.suspicion_left_tag = tag;
                break;
            case "person":
                viewModel.suspicion_middle = drawable;
                viewModel.suspicion_middle_tag = tag;
                break;
            case "ort":
                viewModel.suspicion_right = drawable;
                viewModel.suspicion_right_tag = tag;
                break;
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
        binding = null;
    }
}