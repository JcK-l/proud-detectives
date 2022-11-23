package de.uhh.detectives.frontend.ui.clues_and_guesses;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.ui.clues_and_guesses.Cell;

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
        return new CluesGuessesAdapter.CluesGuessesViewHolder(view);
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

        public CluesGuessesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_cell_value);
        }

        public void bind(final Cell cell){
            imageView.setTag(cell.getCategory());
            // Initiating Drag
            imageView.setOnLongClickListener( view -> {
                ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
                ClipData dragData = new ClipData(
                        (CharSequence) view.getTag(),
                        new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(imageView);
                view.startDragAndDrop(dragData, myShadow, view, 0);
                return true;
            });
            // set backgroundcolor before click
            switch (cell.getState()){
                case NEUTRAL:
                    itemView.setBackgroundColor(Color.WHITE);
                    break;
                case NEGATIVE:
                    itemView.setBackgroundColor(Color.RED);
                    break;
                case POSTIVE:
                    itemView.setBackgroundColor(Color.GREEN);
                    break;
            }
            // set backgroundcolor on click
            imageView.setOnClickListener(view -> {
                switch (cell.getState()){
                    case NEUTRAL:
                        itemView.setBackgroundColor(Color.RED);
                        cell.setState(CellState.NEGATIVE);
                        break;
                    case NEGATIVE:
                        itemView.setBackgroundColor(Color.GREEN);
                        cell.setState(CellState.POSTIVE);
                        break;
                    case POSTIVE:
                        itemView.setBackgroundColor(Color.WHITE);
                        cell.setState(CellState.NEUTRAL);
                        break;
                }
            });

        }
    }
}
