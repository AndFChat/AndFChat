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

import roboguice.util.Ln;

import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.messages.ChatEntry;

/**
 * Handles incoming messages for channel and Broadcasts
 * @author AndFChat
 *
 */
public class MessageHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        JSONObject jsonObject = new JSONObject(msg);
        if(token == ServerToken.MSG) {
            String character = jsonObject.getString("character");
            String message = jsonObject.getString("message");
            String channel = jsonObject.getString("channel");

            Chatroom chatroom = chatroomManager.getChatroom(channel);

            if (chatroom != null) {
                ChatEntry entry = entryFactory.getMessage(characterManager.findCharacter(character), message);
                chatroomManager.addMessage(chatroom, entry);
            }
            else {
                Ln.e("Incoming message is for a unknown channel: " + channel);
            }
        }
        else if(token == ServerToken.BRO) {
            String message = jsonObject.getString("message");
            ChatEntry entry = entryFactory.getMessage(characterManager.findCharacter(CharacterManager.USER_SYSTEM), message);
            chatroomManager.addBroadcast(entry);
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.MSG, ServerToken.BRO};
    }

}
