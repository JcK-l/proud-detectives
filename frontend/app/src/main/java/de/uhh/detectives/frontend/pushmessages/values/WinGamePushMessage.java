package de.uhh.detectives.frontend.pushmessages.values;

import android.app.Notification;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import de.uhh.detectives.frontend.R;

public class WinGamePushMessage implements PushMessage {

    private Notification winNotification;
    private final Context context;
    private final String winner;
    private final boolean win;

    public WinGamePushMessage(Context context, final String winner, final boolean win) {
        this.context = context;
        this.winner = winner;
        this.win = win;
        createNotification();
    }

    @Override
    public void createNotification() {
        String CHANNEL_ID = "GamChan";
        NotificationCompat.Builder builder;
        if (win) {
            builder = new NotificationCompat.Builder(context
                    , CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(winner + " has won the game!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{100L, 0L, 100L});
        } else {
            builder = new NotificationCompat.Builder(context
                    , CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Everyone got killed!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{100L, 0L, 100L});
        }
        winNotification = builder.build();
    }

    @Override
    public Notification getNotification() {
        return winNotification;
    }
}
