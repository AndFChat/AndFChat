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

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Spannable;

import com.homebrewn.flistchat.core.connection.FeedbackListner;
import com.homebrewn.flistchat.core.connection.ServerToken;
import com.homebrewn.flistchat.core.data.Chatroom;
import com.homebrewn.flistchat.core.util.BBCodeReader;

/**
 * Sets and changes channel-description for official and unofficial channel.
 * @author AndFChat
 */
public class ChannelDescriptionHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        if (token == ServerToken.CDS) {
            JSONObject json = new JSONObject(msg);
            String channelId = json.getString("channel");
            String description = json.getString("description");

            Spannable bbCodedDescription = BBCodeReader.createSpannableWithBBCode(description);

            Chatroom Chatroom = chatroomManager.getChatroom(channelId);
            if (Chatroom != null) {
                Chatroom.setDescription(bbCodedDescription);
            }
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.CDS};
    }

}
