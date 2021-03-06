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

import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.messages.ChatEntry;

/**
 * Adds Dice and Bottle messages to channels and private messages.
 * @author AndFChat
 */
public class DiceBottleHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        if (token == ServerToken.RLL) {
            JSONObject json = new JSONObject(msg);
            String channelId = json.optString("channel");
            String recipient = json.optString("recipient");
            String message = json.getString("message");
            String character = json.getString("character");
            Date time = json.has("time") ? parseDate(json.getLong("time")) : new Date();

            FCharacter owner = characterManager.findCharacter(character);

            Chatroom chatroom;
            if(!channelId.isEmpty() && recipient.isEmpty()) {
                chatroom = chatroomManager.getChatroom(channelId);
            } else {
                if (character.equals(sessionData.getCharacterName())) {
                    chatroom = chatroomManager.getPrivateChatFor(recipient);
                } else {
                    chatroom = chatroomManager.getPrivateChatFor(character);
                }
            }

            if (chatroom != null) {
                // Remove the first name, is already displayed by the ChatEntry.
                message = message.substring(message.indexOf("[/user]") + "[/user]".length());
                ChatEntry entry = entryFactory.getNotation(owner, message, time);
                chatroomManager.addChat(chatroom, entry);
            }
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.RLL};
    }

}
