package de.uhh.detectives.frontend.pushmessages.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.pushmessages.values.EnteredMapMessage;
import de.uhh.detectives.frontend.pushmessages.values.FindHintMessage;
import de.uhh.detectives.frontend.pushmessages.values.MapExitMessage;
import de.uhh.detectives.frontend.pushmessages.values.WinGamePushMessage;

public class PushMessageHandler {
    private static final String LOCATION_CHANNEL_ID = "LocChan";
    private static final String GAME_EVENTS_CHANNEL_ID = "GamChan";

    private final NotificationManagerCompat notificationManagerCompat;
    private final Context context;

    public PushMessageHandler(Context context) {
        this.context = context;
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager.areNotificationsEnabled()) {
        }
        createLocationNotificationChannel();
        createGameEventNotificationChannel();
        notificationManagerCompat = NotificationManagerCompat.from(context);
    }

    public void createLocationNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = String.valueOf((R.string.location_channel_name));
            String description = String.valueOf(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(LOCATION_CHANNEL_ID,
                    new StringBuilder(name),
                    importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context
                    .getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createGameEventNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = String.valueOf((R.string.game_event_channel_name));
            String description = String.valueOf(R.string.game_event_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(GAME_EVENTS_CHANNEL_ID,
                    new StringBuilder(name),
                    importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context
                    .getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void pushWinGameMessage(final String winner) {
        WinGamePushMessage winGamePushMessage = new WinGamePushMessage(context, winner);
        notificationManagerCompat.notify(122, winGamePushMessage.getNotification());
    }

    public void pushMapExitMessage() {
        MapExitMessage mapExitMessage = new MapExitMessage(context);
        notificationManagerCompat.notify(123, mapExitMessage.getNotification());
    }

    public void pushEnteredMapMessage() {
        EnteredMapMessage enteredMapMessage = new EnteredMapMessage(context);
        notificationManagerCompat.notify(124, enteredMapMessage.getNotification());
    }

    public void pushFindHintMessage(String hintName, String hintDescription){
        FindHintMessage findHintMessage = new FindHintMessage(context, hintName, hintDescription);
        notificationManagerCompat.notify(125, findHintMessage.getNotification());
    }
}
