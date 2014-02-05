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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.Channel;
import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FlistChar;
import com.andfchat.core.data.ChatEntry.ChatEntryType;
import com.andfchat.core.data.Chatroom.ChatroomType;
import com.andfchat.R;

/**
 * Handles channel joins, still misses private channel handling.
 * @author AndFChat
 */
public class JoinedChannel extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        JSONObject data = new JSONObject(msg);

        String channelId = data.getString("channel");

        if (token == ServerToken.JCH) {
            JSONObject character = data.getJSONObject("character");
            String channelName = data.getString("title");
            FlistChar flistChar = characterManager.findCharacter(character.getString("identity"));
            getChatroom(channelId, channelName).addCharacter(flistChar);

            if (sessionData.getSessionSettings().showChannelInfos()) {
                ChatEntry chatEntry = new ChatEntry(R.string.message_channel_joined, flistChar, new Date(), ChatEntryType.NOTATION_JOINED);
                chatroomManager.getChatroom(channelId).addMessage(chatEntry);
            }
        }
        else if (token == ServerToken.ICH) {
            JSONArray users = data.getJSONArray("users");

            Channel channel = chatroomManager.getPrivateChannelById(channelId);
            String channelName = channelId;

            if (channel != null) {
                channelName = channel.getChannelId();
            }

            Chatroom Chatroom = getChatroom(channelId, channelName);

            for (int i = 0; i < users.length(); i++) {
                String character = users.getJSONObject(i).getString("identity");
                Log.v("homebrewn.flistchat.JoinedChannle", "Adding Character to Channel('"+channelId+"'): " + character);
                Chatroom.addCharacter(characterManager.findCharacter(character));
            }
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.JCH, ServerToken.ICH};
    }

    public Chatroom getChatroom(String channelId, String channelName) {
        Chatroom chatroom = chatroomManager.getChatroom(channelId);

        if (chatroom == null) {
            Channel channel = null;
            ChatroomType chatroomType = ChatroomType.PUBLIC_CHANNEL;
            if (!channelId.equals(channelName)) {
                chatroomType = ChatroomType.PRIVATE_CHANNEL;
                // Public known channel? use it!
                channel = chatroomManager.getPrivateChannelById(channelId);
            }

            if (channel == null) {
                chatroom = chatroomManager.addChatroom(new Chatroom(new Channel(channelId, channelName), chatroomType));
            } else {
                chatroom = chatroomManager.addChatroom(new Chatroom(channel, chatroomType));
            }
        }

        return chatroom;
    }
}
