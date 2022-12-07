package de.uhh.detectives.frontend.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.Message.api.Message;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.model.event.JoinGameMessageEvent;
import de.uhh.detectives.frontend.model.event.RegisterMessageEvent;
import de.uhh.detectives.frontend.model.event.StartGameMessageEvent;
import de.uhh.detectives.frontend.model.event.WinGameMessageEvent;
import de.uhh.detectives.frontend.model.event.api.MessageEvent;

public class TcpMessageService extends Service {
    private final IBinder binder = new LocalBinder();
    private final List<MessageEvent> messageEventList = new ArrayList<>();
    private UserData user;

    private final Object syncObject = new Object();
    private Socket socket = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final String host = "dos-wins-04.informatik.uni-hamburg.de";
//   private final String host = "10.0.2.2";
    private final int port = 22527;

    public class LocalBinder extends Binder {
        public TcpMessageService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TcpMessageService.this;
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        Thread threadTcpConnection = new Thread(establishTcpConnection());
        threadTcpConnection.start();

        messageEventList.addAll(Arrays.asList(new ChatMessageEvent(), new JoinGameMessageEvent(),
                new RegisterMessageEvent(), new StartGameMessageEvent(), new WinGameMessageEvent()));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread threadListenForMessage = new Thread(listenForMessage());
        threadListenForMessage.start();

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public void sendMessageToServer(final Message message) {
        final Thread thread = new Thread(sendMessage(message));
        thread.start();
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public void syncDatabase(){
        // TODO:
        return;
    }

    public void loginUser() {
        // TODO:
        return;
    }

    private Runnable sendMessage(final Message message) {
        return () -> {
            try {
                out.writeUTF("OPEN_CONNECTION_FOR:" + user.getUserId());
                out.writeUTF(message.toString());
                out.flush();
            } catch (IOException ignored) {
                // handle connection failure
            }
        };
    }

    private Runnable listenForMessage() {
        return () -> {
            String serverInput;
            synchronized (syncObject) {
                try {
                    syncObject.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            while(true){
                try {
                    serverInput = in.readUTF();
                    Log.i("Message", "got Message: " + serverInput);
                    MessageType messageType = MessageType.valueOf(getMessageTypeFromResponse(serverInput));
                    for (final MessageEvent messageEvent : messageEventList) {
                        if (messageEvent.accepts(messageType)) {
                            EventBus.getDefault().post(messageEvent.generateMessageEvent(serverInput));
                        }
                    }
                } catch (IOException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private String getMessageTypeFromResponse(String serverInput) {
        String[] tokens = serverInput.split(";");
        return tokens[0].substring(tokens[0].indexOf(":") + 1);
    }

    private Runnable establishTcpConnection() {
        return () -> {
            out = null;
            in = null;
            try {
                socket = new Socket(host, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host: " + host);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for host: " + host);
                System.exit(1);
            }
            try {
                socket.setKeepAlive(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            synchronized (syncObject) {
                syncObject.notify();
            }
        };
    }

    private Runnable heartbeat() {
        // TODO:
        return () -> {};
    }
}