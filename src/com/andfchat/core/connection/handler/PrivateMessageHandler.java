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

import roboguice.util.Ln;
import android.os.Vibrator;

import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.connection.handler.VariableHandler.Variable;
import com.andfchat.core.data.Channel;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FlistChar;
import com.google.inject.Inject;

/**
 * Handles private messages send to user.
 * @author AndFChat
 */
public class PrivateMessageHandler extends TokenHandler {

    public final static String PRIVATE_MESSAGE_TOKEN = "PRIV:::";

    @Inject
    protected Vibrator vibrator;

    private final static int VIBRATING_TIME = 300;

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        JSONObject jsonObject = new JSONObject(msg);

        String character = jsonObject.getString("character");
        String message = jsonObject.getString("message");

        Chatroom chatroom = chatroomManager.getChatroom(PRIVATE_MESSAGE_TOKEN + character);
        if (chatroom == null) {
            int maxTextLength = sessionData.getIntVariable(Variable.priv_max);
            chatroom = openPrivateChat(chatroomManager, characterManager.findCharacter(character), maxTextLength);
        }

        // If vibration is allowed, do it on new messages!
        if (sessionData.getSessionSettings().vibrationFeedback()) {
            if (chatroomManager.getActiveChat().equals(chatroom) == false && chatroom.hasNewMessage() == false) {
                Ln.d("New Message Vibration on!");
                vibrator.vibrate(VIBRATING_TIME);
            }
        }

        chatroom.addMessage(message, characterManager.findCharacter(character), new Date());
        chatroom.setHasNewMessage(true);
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.PRI};
    }

    public static Chatroom openPrivateChat(ChatroomManager chatroomManager, FlistChar character, int maxTextLength) {
        String channelname = character.getName();

        Chatroom chatroom = new Chatroom(new Channel(PRIVATE_MESSAGE_TOKEN + channelname, channelname), character, maxTextLength);
        chatroomManager.addChatroom(chatroom);

        return chatroom;
    }
}
