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
import com.homebrewn.flistchat.core.data.ChatEntry;
import com.homebrewn.flistchat.core.data.ChatEntry.ChatEntryType;
import com.homebrewn.flistchat.core.data.FlistChar;
import com.homebrewn.flistchat.core.data.SessionData;

/**
 * Tracks status changes for user (every user online).
 * @author AndFChat
 */
public class CharInfoHandler extends TokenHandler {

    public CharInfoHandler(SessionData sessionData) {
        super(sessionData);
    }

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        if (token == ServerToken.STA) {
            JSONObject json = new JSONObject(msg);
            String name = json.getString("character");
            String status = json.getString("status");
            String statusmsg = json.getString("statusmsg");

            FlistChar flistChar = sessionData.getCharHandler().findCharacter(name);
            flistChar.setStatus(status, statusmsg);

            if (sessionData.getSessionSettings().showStatusChanges()) {

                String message = "NEW USER STATUS: " + flistChar.getStatus();

                if (statusmsg != null && statusmsg.length() > 0) {
                    message += " MSG: " + flistChar.getStatusMsg();
                }

                ChatEntry chatEntry = new ChatEntry(message, flistChar, new Date(), ChatEntryType.NOTATION_STATUS);
                this.broadcastSystemInfo(chatEntry, flistChar);
            }
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.STA};
    }

}
