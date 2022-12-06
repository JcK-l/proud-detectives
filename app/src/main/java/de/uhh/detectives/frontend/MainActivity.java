package de.uhh.detectives.frontend;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.location.MapGeofence;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.Message.DirectMessage;
import de.uhh.detectives.frontend.model.Message.StartGameMessage;
import de.uhh.detectives.frontend.model.Message.WinGameMessage;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.model.event.StartGameMessageEvent;
import de.uhh.detectives.frontend.model.event.WinGameMessageEvent;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageHandler;
import de.uhh.detectives.frontend.repository.ChatMessageRepository;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private Bundle savedInstanceState;

    // LocationHandler in MainActivity einmal initialisieren, um state zu halten
    private MapGeofence mapGeofence;
    private ChatMessageRepository chatMessageRepository;

    private PushMessageHandler pushMessageHandler;

    private final static Long gameStartTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        EventBus.getDefault().register(this);

        Intent intentService = new Intent(this, TcpMessageService.class);
        startService(intentService);

        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);

        db = AppDatabase.getDatabase(getApplicationContext());
        chatMessageRepository = db.getChatMessageRepository();

        pushMessageHandler = new PushMessageHandler(getApplicationContext());
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


    @Subscribe
    public void receiveChatMessage(ChatMessageEvent chatMessageEvent) {
        ChatMessage chatMessage = chatMessageEvent.getMessage();
        chatMessageRepository.insert(chatMessage);

        // listen for messages directed at us
        if (chatMessage.getReceiverId() != null && chatMessage.getReceiverId().equals(db.getUserDataRepository().findFirst().getUserId())) {
            DirectMessage directMessage = new DirectMessage(chatMessage.getSenderId().toString(), chatMessage.getPseudonym());
            directMessage.setPosition(0);

            db.getDirectMessageRepository().prepareForInsertion();
            db.getDirectMessageRepository().insert(directMessage);
        }
    }

    @Subscribe
    public void saveInfoFromServerOnStartGame(StartGameMessageEvent startGameMessageEvent) {
        StartGameMessage startGameMessage = startGameMessageEvent.getMessage();

        db.getChatMessageRepository().deleteAll();
        db.getPlayerRepository().deleteAll();

        db.getPlayerRepository().insertAll(startGameMessage.getPlayers());
        db.getSolutionRepository().insert(startGameMessage.getSolution());
        db.getHintRepository().insertAll(startGameMessage.getHints());
    }

    @Subscribe
    public void receiveWinGameMessage(WinGameMessageEvent winGameMessageEvent) {
        WinGameMessage winGameMessage = winGameMessageEvent.getMessage();
        Player winner = db.getPlayerRepository().getPlayerWithUserId(winGameMessage.getWinnerId());

        pushMessageHandler.pushWinGameMessage(winner.getPseudonym());

        db.getPlayerRepository().deleteAll();
        db.getSolutionRepository().deleteAll();
        db.getHintRepository().deleteAll();
        db.getChatMessageRepository().deleteAll();
        db.getDirectMessageRepository().deleteAll();

        getViewModelStore().clear();

        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
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