package com.andfchat.frontend.application;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.andfchat.R;
import com.andfchat.frontend.activities.ChatScreen;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.text.Format;

@Singleton
public class AndFChatNotification {

    @Inject
    private NotificationManager notificationManager;

    private Context context;

    @Inject
    public AndFChatNotification(Context context) {
        this.context = context;
    }

    public void updateNotification(int messages) {
        int icon =  R.drawable.ic_st_connected;
        String msg = context.getString(R.string.notification_connected);
        if (messages > 0) {
            icon = R.drawable.ic_st_attention;
            msg = String.format(context.getString(R.string.notification_attention), messages);
        }

        startNotification(msg, icon);
    }

    public void disconnectNotification() {
        int icon =  R.drawable.ic_st_disconnected;
        String msg = context.getString(R.string.notification_disconnect);
        startNotification(msg, icon);
    }

    public void cancelNotification() {
        notificationManager.cancel(AndFChatApplication.NOTIFICATION_ID);
    }

    public void cancelLedNotification() {
        notificationManager.cancel(AndFChatApplication.LED_NOTIFICATION_ID);
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }

    private void startNotification(String msg, int icon) {
        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ChatScreen.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(new Intent(context, ChatScreen.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(msg)
                .setContentIntent(resultPendingIntent);

        notificationManager.notify(AndFChatApplication.NOTIFICATION_ID, nBuilder.build());
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
