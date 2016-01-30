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

import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.messages.ChatEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import roboguice.util.Ln;

public class DemotionHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        if (token == ServerToken.COR) {
            JSONObject json = new JSONObject(msg);
            String channel = json.getString("channel");
            String character = json.getString("character");

            Chatroom chatroom = chatroomManager.getChatroom(channel);

            if (chatroom != null) {
                ChatEntry entry = entryFactory.getNotation(characterManager.findCharacter(character), " has been demoted in " + chatroom.getName() + ".");
                this.addChatEntryToActiveChat(entry);
            }
            else {
                Ln.e("Demotion is for a unknown channel: " + channel);
            }
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.COR};
    }
}
