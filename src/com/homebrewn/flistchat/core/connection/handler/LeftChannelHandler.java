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
import com.homebrewn.flistchat.core.data.AppProperties.PropertyName;
import com.homebrewn.flistchat.core.data.ChatEntry;
import com.homebrewn.flistchat.core.data.ChatEntry.ChatEntryType;
import com.homebrewn.flistchat.core.data.FlistChar;

/**
 * Handles left events on channels.
 * @author AndFChat
 */
public class LeftChannelHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) {

        try {
            JSONObject json = new JSONObject(msg);
            String channel = json.getString("channel");
            String character = json.getString("character");
            if (character.equals(sessionData.getCharacterName())) {
                chatroomManager.removeChatroom(channel);
            } else {
                FlistChar flistChar = characterManager.findCharacter(character);
                if (sessionData.getProperties().getBooleanValue(PropertyName.SHOW_Chatroom_INFOS)) {
                    ChatEntry chatEntry = new ChatEntry("LEFT THE CHANNEL.", flistChar, new Date(), ChatEntryType.NOTATION_LEFT);
                    chatroomManager.getChatroom(channel).addMessage(chatEntry);
                }

                chatroomManager.getChatroom(channel).removeCharacter(flistChar);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.LCH};
    }

}
