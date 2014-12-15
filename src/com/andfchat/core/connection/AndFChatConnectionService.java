package com.andfchat.core.connection;

import roboguice.util.Ln;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import de.tavendo.autobahn.WebSocketConnection;

public class AndFChatConnectionService extends Service {

    private final ConnectionServiceBinder binder = new ConnectionServiceBinder();

    private WebSocketConnection connection;

    public class ConnectionServiceBinder extends Binder {
        public AndFChatConnectionService getService() {
            return AndFChatConnectionService.this;
        }
    }

    public WebSocketConnection getConnection() {
        return connection;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        Ln.i(getClass().getSimpleName() + " in onCreate()");
        connection = new WebSocketConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Ln.i(getClass().getSimpleName() + ".onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }
}
