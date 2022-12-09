package de.uhh.detectives.frontend;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.ActivityWaitingRoomBinding;
import de.uhh.detectives.frontend.location.api.LocationHandler;
import de.uhh.detectives.frontend.location.impl.LocationHandlerImpl;
import de.uhh.detectives.frontend.model.Message.JoinGameMessage;
import de.uhh.detectives.frontend.model.Message.StartGameMessage;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.event.JoinGameMessageEvent;
import de.uhh.detectives.frontend.model.event.StartGameMessageEvent;

public class WaitingRoomActivity extends AppCompatActivity {

    private LocationHandler locationHandler;

    private AppDatabase db;

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        de.uhh.detectives.frontend.databinding.ActivityWaitingRoomBinding binding = ActivityWaitingRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EventBus.getDefault().register(this);

        locationHandler = new LocationHandlerImpl(this.getApplicationContext(), this);

        db = AppDatabase.getDatabase(getApplicationContext());

        // if you're already in a game: join game directly
        if (db.getSolutionRepository().findFirst() != null) {
            Intent intentGame = new Intent(this, GameActivity.class);
            startActivity(intentGame);
            finish();
        }

        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        String[] names = intent.getExtras().getStringArray("names");
        for (int i = 0; i < names.length; i++) {
            db.getPlayerRepository().insert(new Player(System.currentTimeMillis() + i, names[i]));
        }

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(
                R.id.commsFragment,
                R.id.startGameFragment)
                .build();

        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        Objects.requireNonNull(navHostFragment);
        final NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessageJoinGame(final JoinGameMessageEvent joinGameMessageEvent) {
        JoinGameMessage joinGameMessage = joinGameMessageEvent.getMessage();

        if (joinGameMessage.getPlayerNames() == null) return;

        db.getPlayerRepository().deleteAll();
        for (int i = 0; i < joinGameMessage.getPlayerNames().size(); i++) {
            db.getPlayerRepository().insert(new Player(System.currentTimeMillis() + i,
                    joinGameMessage.getPlayerNames().get(i)));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessageStartGame(final StartGameMessageEvent startGameMessageEvent) {
        StartGameMessage startGameMessage = startGameMessageEvent.getMessage();

        if (!(startGameMessage.getStatus() == 200)) {
            return;
        }

        Intent intentGame = new Intent(this, GameActivity.class);
        intentGame.putExtra("centerX", startGameMessage.getCenterX());
        intentGame.putExtra("centerY", startGameMessage.getCenterY());
        intentGame.putExtra("radius", startGameMessage.getRadius());
        startActivity(intentGame);
        finish();
    }

    public Location getLocation() {
        Location location = locationHandler.getCurrentLocation(getApplicationContext());
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Location data is unavailable, try again!", Toast.LENGTH_SHORT).show();
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivity(intentLogin);
            finish();
        }
        return location;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
