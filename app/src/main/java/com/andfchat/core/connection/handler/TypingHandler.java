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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TypingHandler extends TokenHandler{

    public final static String PRIVATE_MESSAGE_TOKEN = "PRIV:::";

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        if(token == ServerToken.TPN) {
            JSONObject jsonObject = new JSONObject(msg);

            String character = jsonObject.getString("character");
            String status = jsonObject.getString("status");

            Chatroom chatroom = chatroomManager.getChatroom(PRIVATE_MESSAGE_TOKEN + character);
            if (chatroom != null) {
                chatroomManager.addTyping(chatroom, status);
            }
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.TPN};
    }
}
