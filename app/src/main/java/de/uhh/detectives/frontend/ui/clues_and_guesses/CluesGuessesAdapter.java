package de.uhh.detectives.frontend.ui.clues_and_guesses;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.model.UserData;

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
        return new CluesGuessesAdapter.CluesGuessesViewHolder(view, cells, context);
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

        private final ImageView imageView;
        private final Context context;
        private Drawable cancel;
        private Drawable maybe;
        private final List<Cell> cells;
        private AppDatabase db;
        private UserData user;

        public CluesGuessesViewHolder(@NonNull View itemView, final List<Cell> cells, final Context context) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_cell_value);
            this.context = context;
            this.cancel = AppCompatResources.getDrawable(context, R.drawable.ic_cancel);
            this.maybe = AppCompatResources.getDrawable(context, R.drawable.ic_maybe);
            this.cells = cells;
            cancel.setAlpha(230);
            maybe.setAlpha(230);
            db = AppDatabase.getDatabase(context);
            user = db.getUserDataRepository().findFirst();
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
                view.startDragAndDrop(dragData, myShadow, null, 0);
                return true;
            };
        }

        private View.OnClickListener enableBackgroundColorchangeOnClick(final Cell cell) {
            return view -> {
                switch (cell.getState()){
                    case NEUTRAL:
                        itemView.setForeground(cancel);
                        cell.setState(CellState.NEGATIVE);
                        break;
                    case NEGATIVE:
                        itemView.setForeground(maybe);
                        cell.setState(CellState.POSITIVE);
                        break;
                    case POSITIVE:
                        itemView.setForeground(null);
                        cell.setState(CellState.NEUTRAL);
                        break;
                }
                db.getCluesGuessesStateRepository().updateCells(cells, user.getUserId());
            };
        }

        private void setBackgroundColorBeforeClick(final Cell cell) {
            switch (cell.getState()){
                case NEUTRAL:
                    itemView.setForeground(null);
                    break;
                case NEGATIVE:
                    itemView.setForeground(AppCompatResources.getDrawable(context, R.drawable.ic_cancel));
                    break;
                case POSITIVE:
                    itemView.setForeground(AppCompatResources.getDrawable(context, R.drawable.ic_maybe));
                    break;
            }
        }
    }
}
