package de.uhh.detectives.frontend.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import de.uhh.detectives.frontend.model.ChatMessage;
import de.uhh.detectives.frontend.event.ChatMessageEvent;
import de.uhh.detectives.frontend.model.ParsedMessage;

public class TcpMessageService extends Service {
    private Looper serviceLooper;
    private final IBinder binder = new LocalBinder();

    private final Object syncObject = new Object();
    private Socket socket = null;
    private BufferedReader in;
    ObjectOutputStream out;
    private final String host = "dos-wins-04.informatik.uni-hamburg.de";
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

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

    public void sendMessageToServer(final ChatMessage message) {
        final Thread thread = new Thread(sendMessage(message));
        thread.start();
    }

    public void syncDatabase(){
        // TODO:
        return;
    }

    public void registerUser() {
        // TODO:
        return;
    }

    public void loginUser() {
        // TODO:
        return;
    }

    private Runnable sendMessage(final ChatMessage message) {
        return () -> {
            try {
                out.writeObject(message.toString());
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
                    serverInput = in.readLine();
                    ParsedMessage parsedMessage = new ParsedMessage(serverInput);
                    switch (parsedMessage.getMessageType()){
                        case CHAT_MESSAGE:
                            ChatMessage chatMessage = new ChatMessage(parsedMessage);
                            ChatMessageEvent chatMessageEvent = new ChatMessageEvent(chatMessage);
                            EventBus.getDefault().post(chatMessageEvent);
                            break;
                        case REGISTER_MESSAGE:
                            break;
                        case UNKNOWN:
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Runnable establishTcpConnection() {
        return () -> {
            out = null;
            in = null;
            try {
                socket = new Socket(host, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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