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


package com.homebrewn.flistchat.core.connection.handler;

import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.homebrewn.flistchat.core.connection.FeedbackListner;
import com.homebrewn.flistchat.core.connection.ServerToken;
import com.homebrewn.flistchat.core.data.Channel;
import com.homebrewn.flistchat.core.data.Chatroom;
import com.homebrewn.flistchat.core.data.ChatroomHandler;
import com.homebrewn.flistchat.core.data.FlistChar;
import com.homebrewn.flistchat.core.data.SessionData;

/**
 * Handles private messages send to user.
 * @author AndFChat
 */
public class PrivateMessageHandler extends TokenHandler {

    public final static String PRIVATE_MESSAGE_TOKEN = "PRIV:::";

    public PrivateMessageHandler(SessionData sessionData) {
        super(sessionData);
    }

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        JSONObject jsonObject = new JSONObject(msg);

        String character = jsonObject.getString("character");
        String message = jsonObject.getString("message");

        Chatroom Chatroom = ChatroomHandler.getChatroom(PRIVATE_MESSAGE_TOKEN + character);
        if (Chatroom == null) {
            Chatroom = openPrivateChat(ChatroomHandler, sessionData.getCharHandler().findCharacter(character));
        }

        Chatroom.addMessage(message, sessionData.getCharHandler().findCharacter(character), new Date());
        Chatroom.setHasNewMessage(true);
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.PRI};
    }

    public static Chatroom openPrivateChat(ChatroomHandler ChatroomHandler, FlistChar character) {
        String channelname = character.getName();

        Chatroom Chatroom = new Chatroom(new Channel(PRIVATE_MESSAGE_TOKEN + channelname, channelname), character);
        ChatroomHandler.addChatroom(Chatroom);

        return Chatroom;
    }
}
