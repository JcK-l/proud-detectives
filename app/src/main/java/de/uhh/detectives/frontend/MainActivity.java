package de.uhh.detectives.frontend;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.ActivityMainBinding;
import de.uhh.detectives.frontend.location.LocationHandler;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.repository.UserDataRepository;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppDatabase db;

    // LocationHandler in MainActivity einmal initialisieren, um state zu halten
    private LocationHandler locationHandler;

    private final static Long gameStartTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        locationHandler = new LocationHandler(this, savedInstanceState);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(
                    R.id.cluesGuessesFragment,
                    R.id.hintsFragment,
                    R.id.commsFragment)
                .build();
        locationHandler.setMapGeofence(this);
        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        Objects.requireNonNull(navHostFragment);
        final NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        setUpDatabase();
    }

    public Long getGameStartTime() {
        return gameStartTime;
    }

    private void setUpDatabase() {
        db = AppDatabase.getDatabase(getApplicationContext());

        // TODO: generate userId on start screen
        // but for now lookup if there is a user already and if not, generate one
        final UserDataRepository userDataRepository = db.getUserDataRepository();
        if (userDataRepository.findFirst() == null) {
            final UserData user = new UserData();
            userDataRepository.insertAll(user);
        }
    }

}