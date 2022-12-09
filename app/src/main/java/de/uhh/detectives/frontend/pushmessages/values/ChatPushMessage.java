package de.uhh.detectives.frontend.pushmessages.values;

import android.app.Notification;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import de.uhh.detectives.frontend.R;

public class ChatPushMessage implements PushMessage {

    private Notification chatNotification;
    private final Context context;
    private final String senderName;
    private final String message;

    public ChatPushMessage(Context context, final String senderName, final String message) {
        this.context = context;
        this.senderName = senderName;
        this.message = message;
        createNotification();
    }

    @Override
    public void createNotification() {
        String CHANNEL_ID = "GamChan";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context
                , CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(senderName + " sent a message!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{100L, 0L, 100L});
        chatNotification = builder.build();
    }

    @Override
    public Notification getNotification() {
        return chatNotification;
    }
}
