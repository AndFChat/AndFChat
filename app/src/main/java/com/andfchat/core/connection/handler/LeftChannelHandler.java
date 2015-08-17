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
import org.json.JSONObject;

import com.andfchat.R;
import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.messages.ChatEntry;
import com.andfchat.frontend.events.ChatroomEventListener.ChatroomEventType;
import com.andfchat.frontend.events.UserEventListener.UserEventType;

/**
 * Handles left events on channels.
 * @author AndFChat
 */
public class LeftChannelHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        JSONObject json = new JSONObject(msg);
        String channelId = json.getString("channel");
        String name = json.getString("character");

        Chatroom chatroom = chatroomManager.getChatroom(channelId);

        if (chatroom == null) {
            return;
        }

        if (name.equals(sessionData.getCharacterName())) {
            boolean wasActive = chatroomManager.isActiveChat(chatroom);

            chatroomManager.removeChatroom(chatroom.getChannel());
            eventManager.fire(chatroom, ChatroomEventType.LEFT);

            if (wasActive) {
                List<Chatroom> chatrooms = chatroomManager.getChatRooms();
                chatroomManager.setActiveChat(chatrooms.get(chatrooms.size() - 1));
            }
        } else {
            FCharacter character = characterManager.findCharacter(name);
            if (sessionData.getSessionSettings().showChannelInfo()) {
                ChatEntry entry = entryFactory.getNotation(character, R.string.message_channel_left);
                chatroomManager.addMessage(chatroom, entry);
            }

            chatroom.removeCharacter(character);
            eventManager.fire(character, UserEventType.LEFT, chatroom);
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.LCH};
    }

}
