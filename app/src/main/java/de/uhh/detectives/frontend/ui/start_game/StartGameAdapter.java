package de.uhh.detectives.frontend.ui.start_game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.model.Player;

public class StartGameAdapter extends RecyclerView.Adapter<StartGameAdapter.StartGameViewHolder>{

    final Context context;
    final private List<Player> playerNames;

    public StartGameAdapter(final Context context, List<Player> playerNames) {
        this.context = context;
        this.playerNames = playerNames;
    }

    @NonNull
    @Override
    public StartGameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_player_name, parent, false);
        return new StartGameAdapter.StartGameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StartGameViewHolder holder, int position) {
        holder.text.setText(playerNames.get(position).getPseudonym());
    }

    @Override
    public int getItemCount() {
        return playerNames.size();
    }

    static class StartGameViewHolder extends RecyclerView.ViewHolder {
        final TextView text;

        StartGameViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textView2);
        }
    }
}
