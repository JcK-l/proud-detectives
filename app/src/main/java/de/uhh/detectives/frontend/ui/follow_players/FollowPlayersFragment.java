package de.uhh.detectives.frontend.ui.follow_players;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentFollowPlayersBinding;
import de.uhh.detectives.frontend.model.CluesGuessesState;
import de.uhh.detectives.frontend.model.Message.CluesGuessesStateMessage;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.CluesGuessesStateMessageEvent;
import de.uhh.detectives.frontend.model.event.JoinGameMessageEvent;
import de.uhh.detectives.frontend.ui.clues_and_guesses.Cell;
import de.uhh.detectives.frontend.ui.clues_and_guesses.CellState;

public class FollowPlayersFragment extends Fragment {

    private AppDatabase db;

    private FragmentFollowPlayersBinding binding;

    private List<Player> players;
    private List<Cell> cells;

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private TabLayoutMediator tabLayoutMediator;
    private FollowPlayersViewPagerAdapter followPlayersViewPagerAdapter;

    private final float MIN_SCALE = 0.75f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        binding = FragmentFollowPlayersBinding.inflate(getLayoutInflater());

        View root = binding.getRoot();

        db = AppDatabase.getDatabase(getContext());
        UserData user = db.getUserDataRepository().findFirst();

        // user to last position
//        List<Player> playersTemp = db.getPlayerRepository().getAll();
//        players = playersTemp.stream()
//                .filter(p -> !p.getId().equals(user.getUserId()))
//                .collect(Collectors.toList());
//        players.add(db.getPlayerRepository().getPlayerWithUserId(user.getUserId()));
        players = db.getPlayerRepository().getAll();

        cells = setUpCells();

        initFollowPlayers(root);

        return root;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveCluesGuessesStateMessage(CluesGuessesStateMessageEvent cluesGuessesStateMessageEvent) {
        CluesGuessesStateMessage cluesGuessesStateMessage = cluesGuessesStateMessageEvent.getMessage();
        CluesGuessesState cluesGuessesState = cluesGuessesStateMessage.getCluesGuessesState();
        int index = IntStream.range(0, players.size())
                .filter(i -> players.get(i).getId().equals(cluesGuessesState.getPlayerId()))
                .findFirst()
                .orElse(-1);
        followPlayersViewPagerAdapter.notifyItemChanged(index);
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

    private void initFollowPlayers(View root){

        viewPager2 = root.findViewById(R.id.viewPager);

        followPlayersViewPagerAdapter = new FollowPlayersViewPagerAdapter(getContext(), players, cells, getActivity());

        viewPager2.setAdapter(followPlayersViewPagerAdapter);

        viewPager2.setPageTransformer(
                (view, position) -> {
                    int pageWidth = view.getWidth();

                    if (position < -1) { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        view.setAlpha(0f);

                    } else if (position <= 0) { // [-1,0]
                        // Use the default slide transition when moving to the left page
                        view.setAlpha(1f);
                        view.setTranslationX(0f);
                        view.setTranslationZ(0f);
                        view.setScaleX(1f);
                        view.setScaleY(1f);

                    } else if (position <= 1) { // (0,1]
                        // Fade the page out.
                        view.setAlpha(1 - position);

                        // Counteract the default slide transition
                        view.setTranslationX(pageWidth * -position);
                        // Move it behind the left page
                        view.setTranslationZ(-1f);

                        // Scale the page down (between MIN_SCALE and 1)
                        float scaleFactor = MIN_SCALE
                                + (1 - MIN_SCALE) * (1 - Math.abs(position));
                        view.setScaleX(scaleFactor);
                        view.setScaleY(scaleFactor);

                    } else { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        view.setAlpha(0f);
                    }
                }
        );

        tabLayout = binding.viewTabs.findViewById(R.id.tabLayout);
        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    tab.setText(players.get(position).getPseudonym());
                }
        );
        tabLayoutMediator.attach();
    }

    @Subscribe
    public void receiveJoinGameMessage(JoinGameMessageEvent joinGameMessageEvent) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
