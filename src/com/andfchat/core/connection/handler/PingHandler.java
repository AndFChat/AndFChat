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

    @Inject
    private FlistWebSocketConnection connection;

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) {
        connection.sendMessage(ClientToken.PIN);
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.PIN};
    }

}
