package de.uhh.detectives.frontend.ui.clues_and_guesses;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
    private Button button;
    private CardView cardview;
    private CluesGuessesViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCluesGuessesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(CluesGuessesViewModel.class);

        initViews();
        setUpViewModel();

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

    private void setUpViewModel() {
        if (viewModel.cells == null){
            viewModel.cells = setUpCells();
        }
        if (viewModel.suspicion_left != null)
            image_suspicion_left.setImageDrawable(viewModel.suspicion_left);
        if (viewModel.suspicion_middle != null)
            image_suspicion_middle.setImageDrawable(viewModel.suspicion_middle);
        if (viewModel.suspicion_right != null)
            image_suspicion_right.setImageDrawable(viewModel.suspicion_right);

    }

    private void initViews() {
        cardview = binding.cardView;
        button = binding.button;

        button.setOnClickListener( view -> cardview.setCardBackgroundColor(Color.GREEN));

        image_suspicion_left = binding.cardView.findViewById(R.id.image_suspicion_left);
        setListenerFor(image_suspicion_left, "Waffe");

        image_suspicion_middle = binding.cardView.findViewById(R.id.image_suspicion_middle);
        setListenerFor(image_suspicion_middle, "Person");

        image_suspicion_right = binding.cardView.findViewById(R.id.image_suspicion_right);
        setListenerFor(image_suspicion_right, "Ort");
    }

    private void setListenerFor(ImageView imageView, String category) {
        imageView.setOnDragListener( (view, event) -> {
            switch(event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // checks if image can be dragged to it
                    if (event.getClipDescription().getLabel().toString().equals(category.toLowerCase(Locale.ROOT))) {
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

                    saveSuspicionState(drawable, category);

                    ((ImageView) view).setImageDrawable(drawable);
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

    private void saveSuspicionState(Drawable drawable, String category) {
        switch (category) {
            case "Waffe":
                viewModel.suspicion_left = drawable;
                break;
            case "Person":
                viewModel.suspicion_middle = drawable;
                break;
            case "Ort":
                viewModel.suspicion_right = drawable;
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
        for ( int i = 0; i < descriptions.length; i++) {
            iconName = "ic_hint_" + category.toLowerCase(Locale.ROOT) + (i + 1);
            // different icons for presentation purposes
            final int iconIdentifier = getResources().getIdentifier(iconName,"drawable", getActivity().getPackageName());
            cells.add(new Cell(CellState.NEUTRAL, iconIdentifier, category.toLowerCase(Locale.ROOT)));
        }

        return cells;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}