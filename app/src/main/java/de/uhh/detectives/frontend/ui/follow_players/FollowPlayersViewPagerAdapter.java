package de.uhh.detectives.frontend.ui.follow_players;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.model.CluesGuessesState;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.repository.CluesGuessesStateRepository;
import de.uhh.detectives.frontend.ui.clues_and_guesses.Cell;

public class FollowPlayersViewPagerAdapter extends RecyclerView.Adapter<FollowPlayersViewPagerAdapter.ViewHolder> {

    private final Context context;
    private final List<Player> players;
    private final List<Cell> cells;
    private final AppDatabase db;
    private final Activity activity;

    public FollowPlayersViewPagerAdapter(final Context context, final List<Player> players,
                                         final List<Cell> cells, Activity activity) {
        this.context = context;
        this.players = players;
        this.cells = cells;
        this.activity = activity;
        this.db = AppDatabase.getDatabase(context);
    }

    @NonNull
    @Override
    public FollowPlayersViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_follow_player, parent, false);
        return new FollowPlayersViewPagerAdapter.ViewHolder(view, context, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowPlayersViewPagerAdapter.ViewHolder holder, int position) {
        Long playerId = players.get(position).getId();
        if (db.getPlayerRepository().getPlayerWithUserId(playerId).isDead()) {
            holder.setForeground();
        }
        CluesGuessesStateRepository cluesGuessesStateRepository = db.getCluesGuessesStateRepository();
        if (cluesGuessesStateRepository.findFromId(playerId) != null){
            holder.bind(cluesGuessesStateRepository.findFromId(playerId));
        } else {
            holder.bind(cells);
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Ui components
        private final RecyclerView recyclerView;
        private final Context context;
        List<Integer> ghostIndex;
        private final Activity activity;

        public ViewHolder(@NonNull View itemView, Context context, Activity activity) {
            super(itemView);
            this.recyclerView = itemView.findViewById(R.id.recycler_view_clues_guesses);
            this.context = context;
            this.activity = activity;
            ghostIndex = IntStream.range(1, 7).boxed().collect(Collectors.toList());
        }

        public void bind(CluesGuessesState cluesGuessesState) {
            List<Cell> cells = cluesGuessesState.getCells();
            FollowPlayersAdapter followPlayersAdapter = new FollowPlayersAdapter(context, cells);

            recyclerView.setAdapter(followPlayersAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 5) {
                @Override
                public boolean canScrollVertically(){
                    return false;
                }
            });
            CardView cardview = itemView.findViewById(R.id.cardView);
            ImageView numberOfGuesses = itemView.findViewById(R.id.imageNumberOfGuesses);

            ImageView imageSuspicionLeft = itemView.findViewById(R.id.image_suspicion_left);
            ImageView imageSuspicionMiddle = itemView.findViewById(R.id.image_suspicion_middle);
            ImageView imageSuspicionRight = itemView.findViewById(R.id.image_suspicion_right);

            cardview.setCardBackgroundColor(ContextCompat.getColor(context, cluesGuessesState.getCardColor()));

            int MAX_TRIES = 3;
            String iconName = "number_of_tries" + (MAX_TRIES - cluesGuessesState.getNumberOfTries());
            final int iconIdentifier = context.getResources().getIdentifier(iconName,"drawable", activity.getPackageName());
            numberOfGuesses.setImageResource(iconIdentifier);

            imageSuspicionLeft.setImageResource(cluesGuessesState.getSuspicionLeft());
            imageSuspicionMiddle.setImageResource(cluesGuessesState.getSuspicionMiddle());
            imageSuspicionRight.setImageResource(cluesGuessesState.getSuspicionRight());
        }

        public void bind(List<Cell> cells) {
            FollowPlayersAdapter followPlayersAdapter = new FollowPlayersAdapter(context, cells);

            recyclerView.setAdapter(followPlayersAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 5) {
                @Override
                public boolean canScrollVertically(){
                    return false;
                }
            });
        }

        public void setForeground() {
            if (ghostIndex.isEmpty()) {
                ghostIndex = IntStream.range(1, 7).boxed().collect(Collectors.toList());
            }
            int randomIndex = ThreadLocalRandom.current().nextInt(0,6);

            String iconName = "ic_ghost" + ghostIndex.get(randomIndex);
            final int iconIdentifier = context.getResources().getIdentifier(iconName,"drawable", activity.getPackageName());

            ImageView ghost = itemView.findViewById(R.id.ghost);

            ghost.setImageResource(iconIdentifier);
            ghost.setVisibility(View.VISIBLE);

            ghostIndex.remove(randomIndex);
        }
    }
}
