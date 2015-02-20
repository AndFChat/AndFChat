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

import com.andfchat.R;
import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.messages.ChatEntry;

public class ChannelInviteHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        if (token == ServerToken.CIU) {
            JSONObject json = new JSONObject(msg);
            String channelId = json.getString("title");
            String channelName = json.getString("name");
            String username = json.getString("sender");

            FCharacter flistChar = characterManager.findCharacter(username, false);
            if (flistChar != null) {
                ChatEntry entry = entryFactory.getNotation(flistChar, R.string.message_invite_to_channel, new Object[]{channelId, channelName});
                broadcastSystemInfo(entry, flistChar);
            }

        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.CIU};
    }
}
