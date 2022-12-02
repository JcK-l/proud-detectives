package de.uhh.detectives.frontend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.ActivityLoginRegisterBinding;
import de.uhh.detectives.frontend.databinding.ActivityLoginStartgameBinding;
import de.uhh.detectives.frontend.model.Message.JoinGameMessage;
import de.uhh.detectives.frontend.model.Message.RegisterMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.JoinGameMessageEvent;
import de.uhh.detectives.frontend.model.event.RegisterMessageEvent;
import de.uhh.detectives.frontend.repository.UserDataRepository;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginRegisterBinding bindingRegister;
    private ActivityLoginStartgameBinding bindingStartgame;
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

        bindingStartgame = ActivityLoginStartgameBinding.inflate(getLayoutInflater());

        if (userDataRepository.findFirst() == null) {
            user = new UserData(System.currentTimeMillis(), "testPseudonym", "testPrename", "testSurname");

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
            setContentView(bindingStartgame.getRoot());
        }

        bindingStartgame.buttonStartgame.setOnClickListener(
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

        setContentView(bindingStartgame.getRoot());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessageStartgame(final JoinGameMessageEvent joinGameMessageEvent) {
        JoinGameMessage joinGameMessage = joinGameMessageEvent.getMessage();

        // Server is a teapot
        if (joinGameMessage.getStatus() == 418) {
            finish();
            return;
        }

        if (!(joinGameMessage.getStatus() == 200)) {
            tcpMessageService.sendMessageToServer(new JoinGameMessage(user));
            return;
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        EventBus.getDefault().unregister(this);
    }
}

