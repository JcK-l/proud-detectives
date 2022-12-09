package de.uhh.detectives.frontend;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.ActivityGameBinding;
import de.uhh.detectives.frontend.repository.HintRepository;
import de.uhh.detectives.frontend.waitingroom.GeofencePlacer;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    private final static Long gameStartTime = System.currentTimeMillis();
    private Bundle geofenceInformation;
    private Bundle savedInstanceState;
    private AppDatabase db;
    private HintRepository hintRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        geofenceInformation = getIntent().getExtras();
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = AppDatabase.getDatabase(getApplicationContext());
        hintRepository = db.getHintRepository();

        getSupportActionBar().hide();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(
                R.id.cluesGuessesFragment,
                R.id.hintsFragment,
                R.id.commsFragment)
                .build();

        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        Objects.requireNonNull(navHostFragment);
        final NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        placeGeofences();
    }

    public Long getGameStartTime() {
        return gameStartTime;
    }

    private void placeGeofences(){
        GeofencePlacer geofencePlacer = new GeofencePlacer(this,
                savedInstanceState,
                hintRepository.getAll(),
                (double) geofenceInformation.get("centerX"),
                (double) geofenceInformation.get("centerY"),
                (double) geofenceInformation.get("radius"));
        geofencePlacer.placeMapGeofence();
        geofencePlacer.placeHintGeofences();
    }
}
