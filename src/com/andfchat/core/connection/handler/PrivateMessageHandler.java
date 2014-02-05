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

import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.Channel;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FlistChar;

/**
 * Handles private messages send to user.
 * @author AndFChat
 */
public class PrivateMessageHandler extends TokenHandler {

    public final static String PRIVATE_MESSAGE_TOKEN = "PRIV:::";

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        JSONObject jsonObject = new JSONObject(msg);

        String character = jsonObject.getString("character");
        String message = jsonObject.getString("message");

        Chatroom chatroom = chatroomManager.getChatroom(PRIVATE_MESSAGE_TOKEN + character);
        if (chatroom == null) {
            chatroom = openPrivateChat(chatroomManager, characterManager.findCharacter(character));
        }

        chatroom.addMessage(message, characterManager.findCharacter(character), new Date());
        chatroom.setHasNewMessage(true);
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.PRI};
    }

    public static Chatroom openPrivateChat(ChatroomManager chatroomManager, FlistChar character) {
        String channelname = character.getName();

        Chatroom chatroom = new Chatroom(new Channel(PRIVATE_MESSAGE_TOKEN + channelname, channelname), character);
        chatroomManager.addChatroom(chatroom);

        return chatroom;
    }
}
