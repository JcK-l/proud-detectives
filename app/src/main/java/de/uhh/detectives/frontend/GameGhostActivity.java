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
import de.uhh.detectives.frontend.databinding.ActivityGameGhostBinding;
import de.uhh.detectives.frontend.model.Message.EndGameMessage;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.event.EndGameMessageEvent;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageHandler;

public class GameGhostActivity extends AppCompatActivity {

    private ActivityGameGhostBinding binding;

    private PushMessageHandler pushMessageHandler;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGameGhostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EventBus.getDefault().register(this);

        getSupportActionBar().hide();

        pushMessageHandler = new PushMessageHandler(getApplicationContext());
        db = AppDatabase.getDatabase(getApplicationContext());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(
                R.id.commsFragment)
                .build();

        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_game_ghost);
        Objects.requireNonNull(navHostFragment);
        final NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
