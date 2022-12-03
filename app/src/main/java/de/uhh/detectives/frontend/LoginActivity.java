package de.uhh.detectives.frontend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.ActivityLoginJoinGameBinding;
import de.uhh.detectives.frontend.databinding.ActivityLoginRegisterBinding;
import de.uhh.detectives.frontend.model.Message.JoinGameMessage;
import de.uhh.detectives.frontend.model.Message.RegisterMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.JoinGameMessageEvent;
import de.uhh.detectives.frontend.model.event.RegisterMessageEvent;
import de.uhh.detectives.frontend.repository.UserDataRepository;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginRegisterBinding bindingRegister;
    private ActivityLoginJoinGameBinding bindingJoinGame;
    private TcpMessageService tcpMessageService;
    private UserDataRepository userDataRepository;

    private AppDatabase db;

    private UserData user;

    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            tcpMessageService = ((TcpMessageService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            tcpMessageService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        Intent intent = new Intent(this, TcpMessageService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        db = AppDatabase.getDatabase(getApplicationContext());
        userDataRepository = db.getUserDataRepository();

        bindingJoinGame = ActivityLoginJoinGameBinding.inflate(getLayoutInflater());

        if (userDataRepository.findFirst() == null) {
            user = new UserData(System.currentTimeMillis(), "Tim", "testPrename", "testSurname");

            bindingRegister = ActivityLoginRegisterBinding.inflate(getLayoutInflater());
            setContentView(bindingRegister.getRoot());

            bindingRegister.buttonRegister.setOnClickListener(
                    view -> {
                        tcpMessageService.setUser(user);
                        tcpMessageService.sendMessageToServer(new RegisterMessage(user));
                    }
            );
        } else {
            user = userDataRepository.findFirst();
            setContentView(bindingJoinGame.getRoot());
        }

        bindingJoinGame.buttonJoinGame.setOnClickListener(
                view -> {
                    tcpMessageService.setUser(user);
                    tcpMessageService.sendMessageToServer(new JoinGameMessage(user));
                }
        );


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessageRegister(final RegisterMessageEvent registerMessageEvent) {
        RegisterMessage registerMessage = registerMessageEvent.getMessage();

        if (!(registerMessage.getStatus() == 200)) {
            tcpMessageService.sendMessageToServer(new RegisterMessage(user));
            return;
        }
        userDataRepository.insertAll(user);

        setContentView(bindingJoinGame.getRoot());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessageJoinGame(final JoinGameMessageEvent joinGameMessageEvent) {
        JoinGameMessage joinGameMessage = joinGameMessageEvent.getMessage();

        // Server is a teapot
        if (joinGameMessage.getStatus() == 418) {
            startWaitingRoom(new String[0]);
            finish();
            return;
        }

        assert joinGameMessage.getPlayerNames() != null;
        int namesCount = joinGameMessage.getPlayerNames().size();
        String[] names = joinGameMessage.getPlayerNames().toArray(new String[namesCount]);

        if (!(joinGameMessage.getStatus() == 200)) {
            tcpMessageService.sendMessageToServer(new JoinGameMessage(user));
            return;
        }

        startWaitingRoom(names);
        finish();
    }

    private void startWaitingRoom(String[] names) {
        Intent intentWaitingRoom = new Intent(this, WaitingRoomActivity.class);
        intentWaitingRoom.putExtra("names", names);
        startActivity(intentWaitingRoom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        EventBus.getDefault().unregister(this);
    }
}

