package de.uhh.detectives.frontend;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.ThreadLocalRandom;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.location.MapGeofence;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.Message.DirectMessage;
import de.uhh.detectives.frontend.model.Message.EndGameMessage;
import de.uhh.detectives.frontend.model.Message.StartGameMessage;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.Message.StartGameMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.model.event.EndGameMessageEvent;
import de.uhh.detectives.frontend.model.event.StartGameMessageEvent;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;
import de.uhh.detectives.frontend.model.event.JoinGameMessageEvent;
import de.uhh.detectives.frontend.model.event.StartGameMessageEvent;
import de.uhh.detectives.frontend.repository.ChatMessageRepository;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private UserData user;
    private Bundle savedInstanceState;

    // LocationHandler in MainActivity einmal initialisieren, um state zu halten
    private MapGeofence mapGeofence;
    private ChatMessageRepository chatMessageRepository;

    private PushMessageService pushMessageService;

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
        user = db.getUserDataRepository().findFirst();

        pushMessageService = new PushMessageService(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Subscribe
    public void receiveJoinGameMessage(JoinGameMessageEvent joinGameMessageEvent) {
        user = db.getUserDataRepository().findFirst();
    }


    @Subscribe
    public void receiveChatMessage(ChatMessageEvent chatMessageEvent) {
        ChatMessage chatMessage = chatMessageEvent.getMessage();
        Character[] symbols = new Character[] {'O', 'o'};
        StringBuilder ghostMessage = new StringBuilder();
        int randomNum;
        // scramble text message
        if (chatMessage.isSenderDead() && !db.getPlayerRepository().getPlayerWithUserId(user.getUserId()).isDead()) {
            for (int i = 0; i < chatMessage.getMessage().length(); i++) {
                randomNum = ThreadLocalRandom.current().nextInt(0,  2);
                ghostMessage.append(symbols[randomNum]);
            }
            chatMessage.setMessage(ghostMessage.toString());
        } else if (chatMessage.isSenderDead() && db.getPlayerRepository().getPlayerWithUserId(user.getUserId()).isDead()) {
            chatMessage.setPseudonym(chatMessage.getPseudonym() + " (GHOST)");
        }
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
    public void receiveWinGameMessage(EndGameMessageEvent endGameMessageEvent) {
        EndGameMessage endGameMessage = endGameMessageEvent.getMessage();

        if (endGameMessage.isWin()) {
            Player winner = db.getPlayerRepository().getPlayerWithUserId(endGameMessage.getWinnerId());
            pushMessageService.pushWinGameMessage(winner.getPseudonym());
        }

        db.getPlayerRepository().deleteAll();
        db.getSolutionRepository().deleteAll();
        db.getHintRepository().deleteAll();
        db.getChatMessageRepository().deleteAll();
        db.getDirectMessageRepository().deleteAll();
        db.getCluesGuessesStateRepository().deleteAll();

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