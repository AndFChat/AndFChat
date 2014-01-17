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
import com.homebrewn.flistchat.core.data.CharacterHandler;
import com.homebrewn.flistchat.core.data.ChatEntry;
import com.homebrewn.flistchat.core.data.ChatEntry.ChatEntryType;
import com.homebrewn.flistchat.core.data.FlistChar;
import com.homebrewn.flistchat.core.data.SessionData;

/**
 * Displays error messages send from server at the active chat.
 * @author AndFChat
 */
public class ErrorMessageHandler extends TokenHandler {

    public ErrorMessageHandler(SessionData sessionData) {
        super(sessionData);
    }

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        if (token == ServerToken.ERR) {
            JSONObject json = new JSONObject(msg);
            FlistChar systemChar = sessionData.getCharHandler().findCharacter(CharacterHandler.USER_SYSTEM);
            ChatEntry chatEntry = new ChatEntry(json.getString("message"), systemChar, new Date(), ChatEntryType.ERROR);
            this.addChatEntryToActiveChat(chatEntry);
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.ERR};
    }

}
