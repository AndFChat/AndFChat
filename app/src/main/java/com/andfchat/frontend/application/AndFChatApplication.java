/*******************************************************************************
 *     This file is part of AndFChat.
 *
 *     AndFChat is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     AndFChat is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with AndFChat.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


package com.andfchat.frontend.application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.IBinder;

import com.andfchat.R;
import com.andfchat.core.connection.AndFChatConnectionService;
import com.andfchat.core.connection.AndFChatConnectionService.ConnectionServiceBinder;

import de.tavendo.autobahn.WebSocketConnection;
import roboguice.RoboGuice;

@ReportsCrashes(formKey = "", // will not be used
                mailTo = "githublimon@gmail.com",
                mode = ReportingInteractionMode.TOAST,
                resToastText = R.string.crash_toast_text)
public class AndFChatApplication extends Application {

    public final static boolean DEBUGGING_MODE = false;

    public final static String DEBUG_CHANNEL_NAME = "Console";

    public final static int LED_NOTIFICATION_ID = 1337;

    public final static int NOTIFICATION_ID = 3531;

    private Point screenSize;

    private AndFChatConnectionService connectionService;

    private final ServiceConnection networkServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            connectionService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectionServiceBinder localBinder = (ConnectionServiceBinder)service;
            connectionService = localBinder.getService();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Intent serviceIntent = new Intent(this, AndFChatConnectionService.class);
        bindService(serviceIntent, networkServiceConnection, Context.BIND_AUTO_CREATE);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

    public boolean isBinded() {
        return connectionService != null;
    }

    public WebSocketConnection getConnection() {
        return connectionService.getConnection();
    }

    public void quitApplication() {
        unbindService(networkServiceConnection);
    }

    public void setScreenDimension(Point size) {
        screenSize = size;
    }

    public Point getScreenDimension() {
        return screenSize;
    }
}
