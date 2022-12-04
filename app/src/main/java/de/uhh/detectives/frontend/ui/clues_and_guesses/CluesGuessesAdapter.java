package de.uhh.detectives.frontend.ui.clues_and_guesses;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.uhh.detectives.frontend.R;

public class CluesGuessesAdapter extends RecyclerView.Adapter<CluesGuessesAdapter.CluesGuessesViewHolder> {

    final Context context;
    final List<Cell> cells;

    public CluesGuessesAdapter(final Context context, final List<Cell> cells) {
        this.context = context;
        this.cells = cells;
    }


    @NonNull
    @Override
    public CluesGuessesAdapter.CluesGuessesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_cell, parent, false);
        return new CluesGuessesAdapter.CluesGuessesViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull CluesGuessesAdapter.CluesGuessesViewHolder holder, int position) {
        holder.bind(cells.get(position));
        holder.imageView.setImageResource(cells.get(position).getImage());
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return cells.size();
    }

    public static class CluesGuessesViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;
        final Context context;

        public CluesGuessesViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_cell_value);
            this.context = context;
        }

        public void bind(final Cell cell){
            imageView.setTag(cell.getCategory() + ":" + cell.getDescription());
            // Initiating Drag
            imageView.setOnLongClickListener(enableDraggable(cell));
            // set backgroundcolor before click
            setBackgroundColorBeforeClick(cell);
            // set backgroundcolor on click
            imageView.setOnClickListener(enableBackgroundColorchangeOnClick(cell));
        }

        private View.OnLongClickListener enableDraggable(final Cell cell) {
            return view -> {
                ClipData.Item item = new ClipData.Item((CharSequence) cell.getDescription());
                ClipData dragData = new ClipData(
                        (CharSequence) view.getTag(),
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(imageView);
                view.startDragAndDrop(dragData, myShadow, view, 0);
                return true;
            };
        }

        private View.OnClickListener enableBackgroundColorchangeOnClick(final Cell cell) {
            return view -> {
                switch (cell.getState()){
                    case NEUTRAL:
                        itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.minesweeper_wrong));
                        cell.setState(CellState.NEGATIVE);
                        break;
                    case NEGATIVE:
                        itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.minesweeper_correct));
                        cell.setState(CellState.POSITIVE);
                        break;
                    case POSITIVE:
                        itemView.setBackgroundColor(Color.WHITE);
                        cell.setState(CellState.NEUTRAL);
                        break;
                }
            };
        }

        private void setBackgroundColorBeforeClick(final Cell cell) {
            switch (cell.getState()){
                case NEUTRAL:
                    itemView.setBackgroundColor(Color.WHITE);
                    break;
                case NEGATIVE:
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.minesweeper_wrong));
                    break;
                case POSITIVE:
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.minesweeper_correct));
                    break;
            }
        }
    }
}
