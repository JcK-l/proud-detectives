package de.uhh.detectives.frontend.pushmessages.values;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.geofence.service.mapservices.MapGeofenceBroadcastReceiver;

public class MapExitPushMessage implements PushMessage {
    private Notification locationNotification;
    private final String CHANNEL_ID = "LocChan";
    private Context context;

    public MapExitPushMessage(Context context) {
        this.context = context;
        createNotification();
    }

    @Override
    public void createNotification() {
        Intent intent = new Intent(context, MapGeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context
                , CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Get back on the Map")
                .setContentText("You have left the map, please enter the map againg!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{100L, 0L, 100L});
        locationNotification = builder.build();
    }

    @Override
    public Notification getNotification(){
        return locationNotification;
    }
}
