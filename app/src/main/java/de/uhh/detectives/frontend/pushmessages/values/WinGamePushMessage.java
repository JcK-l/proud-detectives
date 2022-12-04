package de.uhh.detectives.frontend.pushmessages.values;

import android.app.Notification;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import de.uhh.detectives.frontend.R;

public class WinGamePushMessage implements PushMessage {

    private Notification winNotification;
    private final String CHANNEL_ID = "GamChan";
    private Context context;
    private String winner;

    public WinGamePushMessage(Context context, final String winner) {
        this.context = context;
        this.winner = winner;
        createNotification();
    }

    @Override
    public void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context
                , CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(winner + " has won the game!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{100L, 0L, 100L});
        winNotification = builder.build();
    }

    @Override
    public Notification getNotification() {
        return winNotification;
    }
}
