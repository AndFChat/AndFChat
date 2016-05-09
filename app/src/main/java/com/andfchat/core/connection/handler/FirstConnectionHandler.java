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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

import roboguice.util.Ln;

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
        connection.askForPrivateChannel();
        connection.requestOfficialChannels();

        Set<String> channels = sessionData.getSessionSettings().getInitialChannel();
        if (channels != null) {
            Object[] channelObjArray = channels.toArray();
            String[] channelArray = Arrays.copyOf(channelObjArray, channelObjArray.length, String[].class);
            for (String aChannelArray : channelArray) {
                Ln.i("Joining Channel " + aChannelArray);
                connection.joinChannel(aChannelArray);
            }
        }

        Set<String> privChannels = sessionData.getSessionSettings().getInitialPrivateChannel();
        if (privChannels != null) {
            if (privChannels.isEmpty()) {
                Ln.i("privChannels is empty.");
            } else {
                Ln.i("privChannels isn't empty.");
            }
            Object[] privChannelObjArray = privChannels.toArray();
            String[] privChannelArray = Arrays.copyOf(privChannelObjArray, privChannelObjArray.length, String[].class);
            for (String aPrivChannelArray : privChannelArray) {
                Ln.i("Joining Channel " + aPrivChannelArray);
                connection.joinChannel(aPrivChannelArray);
            }

        } else {
            Ln.i("privChannels is null");
        }

        sessionData.setIsInChat(true);

        // Add Console
        if (chatroomManager.getChatRooms().size() == 0) {
            chatroomManager.addChatroom(new Chatroom(new Channel(AndFChatApplication.DEBUG_CHANNEL_NAME, Chatroom.ChatroomType.CONSOLE), 50000));
        }
        else {
            for (Chatroom chatroom : chatroomManager.getChatRooms()) {
                // Join all previous channel but not the main one
                if (channels != null) {
                    if (chatroom.isChannel() && !channels.contains(chatroom.getId())) {
                        connection.joinChannel(chatroom.getId());
                    }
                }
            }
        }

        eventManager.fire(ConnectionEventListener.ConnectionEventType.CHAR_CONNECTED);

        // Update notification
        notification.updateNotification(0);
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.IDN};
    }

}
