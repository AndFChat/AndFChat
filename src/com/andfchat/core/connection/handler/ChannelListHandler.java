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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.util.Ln;

import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.Channel;
import com.andfchat.core.data.Chatroom.ChatroomType;

/**
 * Reads and saves official channels delivered by CHA-Token.
 * @author AndFChat
 */
public class ChannelListHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        if (token == ServerToken.CHA) {
            JSONObject json = new JSONObject(msg);
            JSONArray jsonArray = json.getJSONArray("channels");
            for (int i = 0; i < jsonArray.length(); i++) {
                String channelName = jsonArray.getJSONObject(i).getString("name");
                Ln.i("found channel: " + channelName);
                chatroomManager.addOfficialChannel(channelName);
            }
        }
        else if (token == ServerToken.ORS) {
            JSONObject json = new JSONObject(msg);
            JSONArray jsonArray = json.getJSONArray("channels");

            for (int i = 0; i < jsonArray.length(); i++) {
                String channelId = jsonArray.getJSONObject(i).getString("name");
                String channelName = jsonArray.getJSONObject(i).getString("title");

                Channel channel = new Channel(channelId, channelName, ChatroomType.PRIVATE_CHANNEL);
                Ln.i("Found channel: " + channel.toString());
                chatroomManager.addPrivateChannel(channel);
            }

            // Feedback, private channel list
            if (feedbackListner != null) {
                for (FeedbackListner listner : feedbackListner) {
                    listner.onResponse(null);
                }
            }
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.CHA, ServerToken.ORS};
    }

}
