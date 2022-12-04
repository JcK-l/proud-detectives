package de.uhh.detectives.frontend;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.ActivityMainBinding;
import de.uhh.detectives.frontend.location.MapGeofence;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.repository.UserDataRepository;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppDatabase db;
    private Bundle savedInstanceState;

    // LocationHandler in MainActivity einmal initialisieren, um state zu halten
    private MapGeofence mapGeofence;

    private final static Long gameStartTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        setUpDatabase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapGeofence = new MapGeofence(this, savedInstanceState);
        mapGeofence.placeMapGeofence(new LatLng(53.5690, 10.0205),
                20f);
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