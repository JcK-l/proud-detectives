package de.uhh.detectives.frontend.pushMessages.values;

import android.app.Notification;

public interface PushMessage {
    public void createNotification();
    public Notification getNotification();
}
