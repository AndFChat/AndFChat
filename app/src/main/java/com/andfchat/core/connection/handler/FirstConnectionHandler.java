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

import org.json.JSONException;

import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.Channel;
import com.andfchat.core.data.Chatroom;
import com.andfchat.frontend.application.AndFChatApplication;
import com.andfchat.frontend.application.AndFChatNotification;
import com.andfchat.frontend.events.ConnectionEventListener;
import com.google.inject.Inject;

/**
 * After Identification this handler is called first.
 * @author AndFChat
 */
public class FirstConnectionHandler extends TokenHandler {

    @Inject
    private FlistWebSocketConnection connection;
    @Inject
    private AndFChatNotification notification;

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        String channel = sessionData.getSessionSettings().getInitialChannel();
        if (channel != null) {
            connection.joinChannel(channel);
        }
        connection.requestOfficialChannels();

        sessionData.setIsInChat(true);

        // Add Console
        if (chatroomManager.getChatRooms().size() == 0) {
            chatroomManager.addChatroom(new Chatroom(new Channel(AndFChatApplication.DEBUG_CHANNEL_NAME, Chatroom.ChatroomType.CONSOLE), 50000));
        }

        eventManager.fire(ConnectionEventListener.ConnectionEventType.CHAR_CONNECTED);

        // Update notification
        if (sessionData.isVisible() == false) {
            notification.updateNotification(0);
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.IDN};
    }

}
