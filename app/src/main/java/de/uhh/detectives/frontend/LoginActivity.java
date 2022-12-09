package de.uhh.detectives.frontend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.ActivityLoginJoinGameBinding;
import de.uhh.detectives.frontend.databinding.ActivityLoginRegisterBinding;
import de.uhh.detectives.frontend.location.api.LocationHandler;
import de.uhh.detectives.frontend.location.impl.LocationHandlerImpl;
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

    public final static int PERMISSION_GRANTED = 0;
    public final static int PERMISSION_DENIED = -1;

    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            tcpMessageService = ((TcpMessageService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            tcpMessageService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        handleLocationPermissionSdkHigher29();

        Intent intent = new Intent(this, TcpMessageService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        LocationHandler locationHandler = new LocationHandlerImpl(this.getApplicationContext(), this);

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        userDataRepository = db.getUserDataRepository();

        bindingJoinGame = ActivityLoginJoinGameBinding.inflate(getLayoutInflater());

        if (userDataRepository.findFirst() == null) {

            TextInputLayout prenameLayout, surnameLayout, pseudonymLayout;
            TextInputEditText prenameText, surnameText, pseudonymText;

            ActivityLoginRegisterBinding bindingRegister = ActivityLoginRegisterBinding.inflate(getLayoutInflater());
            setContentView(bindingRegister.getRoot());

            prenameLayout = bindingRegister.prenameLayout;
            surnameLayout = bindingRegister.surnameLayout;
            pseudonymLayout = bindingRegister.pseudonymLayout;

            prenameText = bindingRegister.prenameInput;
            surnameText = bindingRegister.surnameInput;
            pseudonymText = bindingRegister.pseudonymInput;

            pseudonymText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > pseudonymLayout.getCounterMaxLength()) {
                        pseudonymLayout.setError("Player Name is too long");
                    } else {
                        pseudonymLayout.setError(null);
                    }
                }
            });

            bindingRegister.buttonRegister.setOnClickListener(
                    view -> {
                        boolean canSend = true;
                        prenameLayout.setError(null);
                        surnameLayout.setError(null);

                        if (Objects.requireNonNull(pseudonymText.getText()).toString().length() <=
                                pseudonymLayout.getCounterMaxLength()) {
                            pseudonymLayout.setError(null);
                        } else {
                            canSend = false;
                        }

                        if (Objects.requireNonNull(prenameText.getText()).toString().equals("")) {
                            prenameLayout.setError("This field can't be emtpy");
                            canSend = false;
                        }
                        if (Objects.requireNonNull(surnameText.getText()).toString().equals("")) {
                            surnameLayout.setError("This field can't be emtpy");
                            canSend = false;
                        }
                        if (pseudonymText.getText().toString().equals("")) {
                            pseudonymLayout.setError("This field can't be emtpy");
                            canSend = false;
                        }

                        if (canSend) {
                            String prename = prenameText.getText().toString();
                            String surname = surnameText.getText().toString();
                            String pseudonym = pseudonymText.getText().toString();
                            user = new UserData(System.currentTimeMillis(), pseudonym, prename, surname);

                            tcpMessageService.setUser(user);
                            tcpMessageService.sendMessageToServer(new RegisterMessage(user));
                        }
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
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        EventBus.getDefault().unregister(this);
    }

    void handleLocationPermissionSdkHigher29() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                        .ACCESS_BACKGROUND_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        }
    }
}

