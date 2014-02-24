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


package com.andfchat.core.connection.handler;

import java.util.List;

import roboguice.util.Ln;
import android.content.Context;

import com.andfchat.core.connection.ClientToken;
import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.connection.ServerToken;
import com.google.inject.Inject;

/**
 * HelloServerI'mAlivePingHandler
 * @author AndFChat
 */
public class PingHandler extends TokenHandler {

    private static final long MAX_TIME_BETWEEN_PINGS = 30 * 1000;
    private static final long MIN_TIME_BETWEEN_PINGS = 10 * 1000;

    @Inject
    private Context context;
    @Inject
    private FlistWebSocketConnection connection;

    private long lastPIN = System.currentTimeMillis();
    private boolean running = false;

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) {
        if (System.currentTimeMillis() - lastPIN > MIN_TIME_BETWEEN_PINGS) {
            connection.sendMessage(ClientToken.PIN);
            lastPIN = System.currentTimeMillis();
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.PIN};
    }

    @Override
    public void connected() {
        running = true;

        Runnable timeoutChecker = new Runnable() {
            @Override
            public void run() {
                try {
                    int losts = 0;
                    while(running) {
                        Ln.v("Check PIN messages");

                        if (System.currentTimeMillis() - lastPIN > MAX_TIME_BETWEEN_PINGS) {
                            losts++;
                        } else {
                            losts = 0;
                        }

                        if (losts == 1 && System.currentTimeMillis() - lastPIN > MIN_TIME_BETWEEN_PINGS) {
                            connection.sendMessage(ClientToken.PIN);
                        }
                        else if (losts == 2) {
                            connection.closeConnection(context);
                        }

                        Thread.sleep(MAX_TIME_BETWEEN_PINGS);
                    }
                } catch (InterruptedException e) {
                    Ln.e(e);
                }
            }
        };

        new Thread(timeoutChecker).start();
    }

    @Override
    public void closed() {
        running = false;
        connection.closeConnection(context);
    }

}
