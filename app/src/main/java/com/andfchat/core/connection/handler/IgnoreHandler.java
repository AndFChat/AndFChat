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

import android.content.Context;
import android.text.Spannable;

import com.andfchat.R;
import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.CharRelation;
import com.andfchat.core.data.RelationManager;
import com.andfchat.core.data.messages.ChatEntry;
import com.google.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roboguice.util.Ln;

/**
 * Handles server feedback for ignoring a character or characters.
 * @author AndFChat-Pandora
 */
public class IgnoreHandler extends TokenHandler {

    @Inject
    private Context context;
    @Inject
    protected RelationManager relationManager;

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        if (token == ServerToken.IGN) {
            JSONObject json = new JSONObject(msg);
            String action = json.getString("action");
            String character = json.optString("character");
            String characters = json.optString("characters");

            if (action.equals("init")) {
                //Initial ignore list
                String[] splitCharacters = characters.split(",");

                // Add ignores to the RelationManager
                Set<String> ignoresList = new HashSet<String>();
                for(int i=0; i< splitCharacters.length; i++) {
                    ignoresList.add(splitCharacters[i].trim());
                }
                relationManager.addCharacterToList(CharRelation.IGNORE, ignoresList);
                Ln.v("Added " + ignoresList.size() + " ignores.");
            } else if (action.equals("add")) {
                //Added character to ignore list
                ChatEntry entry = entryFactory.getNotation(characterManager.findCharacter(character), R.string.handler_message_ignored);
                this.addChatEntryToActiveChat(entry);
                Ln.v("Added " + character + " to the ignore list.");
            } else if (action.equals("delete")) {
                //Removed character from ignore list
                ChatEntry entry = entryFactory.getNotation(characterManager.findCharacter(character), R.string.handler_message_unignored);
                this.addChatEntryToActiveChat(entry);
                Ln.v("Removed " + character + " from the ignore list.");
            } else {
                //This shouldn't happen, ever.
            }

        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.IGN};
    }
}
