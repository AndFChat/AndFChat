package com.andfchat.frontend.application;

import android.app.Notification;
import android.app.NotificationManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AndFChatNotification {

    @Inject
    private NotificationManager notificationManager;

    public void updateNotification(int messages) {

    }

    public void disconnectNotification() {

    }

    public void cancelNotification() {
        notificationManager.cancel(AndFChatApplication.NOTIFICATION_ID);
    }

    public void cancelLedNotification() {
        notificationManager.cancel(AndFChatApplication.LED_NOTIFICATION_ID);
    }

    public void showLedNotification() {
        Notification notif = new Notification();
        notif.ledARGB = 0xFFffffff;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = 200;
        notif.ledOffMS = 200;
        notificationManager.notify(AndFChatApplication.LED_NOTIFICATION_ID, notif);
    }
}
