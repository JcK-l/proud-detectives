package de.uhh.detectives.frontend;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.ActivityMainBinding;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.Message.DirectMessage;
import de.uhh.detectives.frontend.model.Message.StartGameMessage;
import de.uhh.detectives.frontend.model.Message.WinGameMessage;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.model.event.StartGameMessageEvent;
import de.uhh.detectives.frontend.model.event.WinGameMessageEvent;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;
import de.uhh.detectives.frontend.repository.ChatMessageRepository;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppDatabase db;
    private Bundle savedInstanceState;

    // LocationHandler in MainActivity einmal initialisieren, um state zu halten
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

        pushMessageService = new PushMessageService(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
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

        pushMessageService.pushWinGameMessage(winner.getPseudonym());

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