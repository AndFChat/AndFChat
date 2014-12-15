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
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.andfchat.R;
import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.messages.ChatEntry;

/**
 * Tracks status changes for user (every user online).
 * @author AndFChat
 */
public class CharInfoHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        if (token == ServerToken.STA) {
            JSONObject json = new JSONObject(msg);
            String name = json.getString("character");
            String status = json.getString("status");
            String statusmsg = json.getString("statusmsg");

            FCharacter flistChar = characterManager.changeStatus(name, status, statusmsg);

            if (sessionData.getSessionSettings().showStatusChanges() && flistChar.isImportant()) {
                ChatEntry entry;

                status = String.valueOf(status.charAt(0)).toUpperCase(Locale.getDefault()) + status.substring(1);
                if (statusmsg != null && statusmsg.length() > 0) {
                    entry = entryFactory.getNotation(flistChar, R.string.message_status_changed_with_message, new Object[]{status, statusmsg});
                } else {
                    entry = entryFactory.getNotation(flistChar, R.string.message_status_changed, new Object[]{status});
                }

                broadcastSystemInfo(entry, flistChar);
            }
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.STA};
    }

}
