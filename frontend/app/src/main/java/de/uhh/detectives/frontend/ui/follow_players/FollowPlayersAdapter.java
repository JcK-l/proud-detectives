package de.uhh.detectives.frontend.ui.follow_players;

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
import java.util.Objects;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.ui.clues_and_guesses.Cell;

public class FollowPlayersAdapter extends RecyclerView.Adapter<FollowPlayersAdapter.FollowPlayersViewHolder> {

    final Context context;
    final List<Cell> cells;

    public FollowPlayersAdapter(final Context context, final List<Cell> cells) {
        this.context = context;
        this.cells = cells;
    }

    @NonNull
    @Override
    public FollowPlayersAdapter.FollowPlayersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_cell, parent, false);
        return new FollowPlayersAdapter.FollowPlayersViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowPlayersAdapter.FollowPlayersViewHolder holder, int position) {
        holder.setBackGroundColor(cells.get(position));
        holder.imageView.setImageResource(cells.get(position).getImage());
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return cells.size();
    }

    public static class FollowPlayersViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final Context context;

        public FollowPlayersViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_cell_value);
            this.context = context;
            Drawable cancel = AppCompatResources.getDrawable(context, R.drawable.ic_cancel);
            Drawable maybe = AppCompatResources.getDrawable(context, R.drawable.ic_maybe);
            Objects.requireNonNull(cancel).setAlpha(230);
            Objects.requireNonNull(maybe).setAlpha(230);
        }

        public void setBackGroundColor(final Cell cell){
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
