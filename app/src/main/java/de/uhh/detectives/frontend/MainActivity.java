package de.uhh.detectives.frontend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.ThreadLocalRandom;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.Message.DirectMessage;
import de.uhh.detectives.frontend.model.Message.StartGameMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.model.event.JoinGameMessageEvent;
import de.uhh.detectives.frontend.model.event.StartGameMessageEvent;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;
import de.uhh.detectives.frontend.repository.ChatMessageRepository;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private UserData user;

    private ChatMessageRepository chatMessageRepository;

    private PushMessageService pushMessageService;

    private final static Long gameStartTime = System.currentTimeMillis();


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                } else {
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        Intent intentService = new Intent(this, TcpMessageService.class);
        startService(intentService);

        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);

        db = AppDatabase.getDatabase(getApplicationContext());
        chatMessageRepository = db.getChatMessageRepository();
        user = db.getUserDataRepository().findFirst();

        if (Build.VERSION.SDK_INT == 33) {
            if (!(ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED)) {
                requestPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS);
            }
        }
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

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}