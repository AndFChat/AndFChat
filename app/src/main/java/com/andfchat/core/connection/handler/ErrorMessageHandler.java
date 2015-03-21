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

import roboguice.util.Ln;

import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.messages.ChatEntry;

/**
 * Displays error messages send from server at the active chat.
 * @author AndFChat
 */
public class ErrorMessageHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        if (token == ServerToken.ERR) {
            JSONObject json = new JSONObject(msg);

            Ln.i("ERROR: " + json.getString("message"));

            FCharacter systemChar = characterManager.findCharacter(CharacterManager.USER_SYSTEM);
            ChatEntry entry = entryFactory.getError(systemChar, json.getString("message"));
            this.addChatEntryToActiveChat(entry);
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.ERR};
    }

}
