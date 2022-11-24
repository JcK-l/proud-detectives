package de.uhh.detectives.frontend;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

import de.uhh.detectives.frontend.databinding.ActivityMainBinding;
import de.uhh.detectives.frontend.location.api.LocationHandler;
import de.uhh.detectives.frontend.location.impl.LocationHandlerImpl;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // LocationHandler in MainActivity einmal initialisieren, um state zu halten
    private LocationHandler locationHandler;

    private final static Long gameStartTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationHandler = new LocationHandlerImpl(this.getApplicationContext(), this);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(
                        R.id.cluesGuessesFragment,
                        R.id.hintsFragment,
                        R.id.mapsFragment,
                        R.id.commsFragment)
                .build();

        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        Objects.requireNonNull(navHostFragment);
        final NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public Long getGameStartTime() {
        return gameStartTime;
    }

    public LocationHandler getLocationHandler() {
        return locationHandler;
    }

}