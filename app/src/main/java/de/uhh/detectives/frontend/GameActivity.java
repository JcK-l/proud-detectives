package de.uhh.detectives.frontend;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.ActivityGameBinding;
import de.uhh.detectives.frontend.model.CluesGuessesState;
import de.uhh.detectives.frontend.model.Message.CluesGuessesStateMessage;
import de.uhh.detectives.frontend.model.Message.EndGameMessage;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.CluesGuessesStateMessageEvent;
import de.uhh.detectives.frontend.model.event.EndGameMessageEvent;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageHandler;
import de.uhh.detectives.frontend.repository.CluesGuessesStateRepository;

public class GameActivity extends AppCompatActivity {

    private final static Long gameStartTime = System.currentTimeMillis();

    private PushMessageHandler pushMessageHandler;
    private AppDatabase db;
    private UserData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        de.uhh.detectives.frontend.databinding.ActivityGameBinding binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EventBus.getDefault().register(this);

        Objects.requireNonNull(getSupportActionBar()).hide();

        pushMessageHandler = new PushMessageHandler(getApplicationContext());
        db = AppDatabase.getDatabase(getApplicationContext());
        user = db.getUserDataRepository().findFirst();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(
                R.id.cluesGuessesFragment,
                R.id.hintsFragment,
                R.id.commsFragment)
                .build();

        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_game);
        Objects.requireNonNull(navHostFragment);
        final NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Subscribe
    public void receiveCluesGuessesStateMessage(CluesGuessesStateMessageEvent cluesGuessesStateMessageEvent) {
        CluesGuessesStateMessage cluesGuessesStateMessage = cluesGuessesStateMessageEvent.getMessage();
        CluesGuessesStateRepository cluesGuessesStateRepository = db.getCluesGuessesStateRepository();
        CluesGuessesState cluesGuessesState = cluesGuessesStateMessage.getCluesGuessesState();
        if (cluesGuessesState.getPlayerId().equals(user.getUserId())) {
            return;
        }

        // turn player dead if not already dead
        int MAX_TRIES = 3;
        if (cluesGuessesState.getNumberOfTries() == MAX_TRIES) {
            db.getPlayerRepository().setDead(true, cluesGuessesState.getPlayerId());
        }

        cluesGuessesStateRepository.insert(cluesGuessesState);
    }

    @Subscribe
    public void receiveEndGameMessage(EndGameMessageEvent endGameMessageEvent) {
        EndGameMessage endGameMessage = endGameMessageEvent.getMessage();

        if (endGameMessage.isWin()) {
            Player winner = db.getPlayerRepository().getPlayerWithUserId(endGameMessage.getWinnerId());
            pushMessageHandler.pushWinGameMessage(winner.getPseudonym());
        }

        db.getPlayerRepository().deleteAll();
        db.getSolutionRepository().deleteAll();
        db.getHintRepository().deleteAll();
        db.getChatMessageRepository().deleteAll();
        db.getDirectMessageRepository().deleteAll();
        db.getCluesGuessesStateRepository().deleteAll();

        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
        finish();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public Long getGameStartTime() {
        return gameStartTime;
    }
}
