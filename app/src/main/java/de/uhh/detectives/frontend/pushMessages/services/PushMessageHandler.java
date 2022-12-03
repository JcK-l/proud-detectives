package de.uhh.detectives.frontend.pushMessages.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.pushMessages.values.MapExitMessage;

public class PushMessageHandler {
    private static final String CHANNEL_ID = "LocChan";

    private NotificationManagerCompat notificationManagerCompat;
    private Context context;
    private MapExitMessage mapExitMessage;

    public PushMessageHandler(Context context) {
        this.context = context;
        createNotificationChannel();
        notificationManagerCompat = NotificationManagerCompat.from(context);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = String.valueOf((R.string.channel_name));
            String description = String.valueOf(R.string.map_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID
                    , new StringBuilder(name)
                    , importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void pushMapExitMessage(){
        mapExitMessage =  new MapExitMessage(context);
        notificationManagerCompat.notify(123, mapExitMessage.getNotification());
    }
}
