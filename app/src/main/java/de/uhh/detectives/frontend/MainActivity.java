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
import de.uhh.detectives.frontend.databinding.ActivityMainBinding;
import de.uhh.detectives.frontend.location.api.LocationHandler;
import de.uhh.detectives.frontend.location.impl.LocationHandlerImpl;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.repository.ChatMessageRepository;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppDatabase db;

    // LocationHandler in MainActivity einmal initialisieren, um state zu halten
    private LocationHandler locationHandler;
    private ChatMessageRepository chatMessageRepository;

    private final static Long gameStartTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        Intent intentService = new Intent(this, TcpMessageService.class);
        startService(intentService);

        Intent intentActivity = new Intent(this, LoginActivity.class);
        startActivity(intentActivity);


        db = AppDatabase.getDatabase(getApplicationContext());
        chatMessageRepository = db.getChatMessageRepository();

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


    @Subscribe
    public void addChatmessageToDatabase(ChatMessageEvent chatMessageEvent) {
        ChatMessage chatMessage = chatMessageEvent.getMessage();
        chatMessageRepository.insert(chatMessage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}