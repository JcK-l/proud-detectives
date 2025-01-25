package de.uhh.detectives.frontend.pushmessages.values;

import android.app.Notification;

public interface PushMessage {
    void createNotification();
    Notification getNotification();
}
