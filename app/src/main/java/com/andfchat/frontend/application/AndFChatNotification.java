package com.andfchat.frontend.application;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.andfchat.R;
import com.andfchat.core.data.SessionData;
import com.andfchat.frontend.activities.ChatScreen;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import roboguice.util.Ln;

@Singleton
public class AndFChatNotification {

    @Inject
    private NotificationManager notificationManager;
    @Inject
    private SessionData sessionData;

    private Context context;

    @Inject
    public AndFChatNotification(Context context) {
        this.context = context;
    }

    public void updateNotification(int messages) {
        if (sessionData.getSessionSettings().showNotifications()) {
            int icon = R.drawable.ic_st_connected;
            String msg = context.getString(R.string.notification_connected);
            if (messages > 0) {
                icon = R.drawable.ic_st_attention;
                msg = context.getResources().getQuantityString(R.plurals.notification_attention, messages, messages);
                Ln.i(msg);
                startNotification(msg, icon, true, messages);
            } else {
                startNotification(msg, icon, false, 0);
            }
        }
    }

    public void disconnectNotification() {
        if (sessionData.getSessionSettings().showNotifications()) {
            int icon = R.drawable.ic_st_disconnected;
            String msg = context.getString(R.string.notification_disconnect);
            startNotification(msg, icon, false, 0);
        }
    }

    public void cancelLedNotification() {
        notificationManager.cancel(AndFChatApplication.LED_NOTIFICATION_ID);
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }

    private void startNotification(String msg, int icon, boolean messages, int amount) {
        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ChatScreen.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(new Intent(context, ChatScreen.class));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(BitmapFactory.decodeResource(context.getResources(), (R.drawable.wearable_background)));

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context)
                .setOngoing(false)
                .setSmallIcon(icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(msg)
                .setContentIntent(resultPendingIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(false)
                .extend(wearableExtender)
                .setColor(context.getResources().getColor(R.color.primary_color));

        if (amount > 0) {
            nBuilder.setNumber(amount);
        }

        Notification notif;
        if(messages) {
            nBuilder.setPriority(2)
            .setVibrate(new long[]{1, 1, 1});
            // If audio is allowed, do it on new messages!
            if (sessionData.getSessionSettings().audioFeedback()) {
                nBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.tone));
            }

            notif = nBuilder.build();

            notif.ledARGB = 0xFFffffff;
            notif.flags = Notification.FLAG_SHOW_LIGHTS;
            //notif.ledOnMS = 300;
            //notif.ledOffMS = 300;

        } else {
            nBuilder.setPriority(0);
            notif = nBuilder.build();
        }

        notificationManager.notify(AndFChatApplication.NOTIFICATION_ID, notif);
    }
}
