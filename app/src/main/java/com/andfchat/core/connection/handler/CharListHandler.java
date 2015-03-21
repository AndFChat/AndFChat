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

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.util.Ln;

import com.andfchat.R;
import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.RelationManager;
import com.andfchat.core.data.messages.ChatEntry;
import com.google.inject.Inject;

/**
 * Tracks the online status and amount of user. Handles new connects and disconnects.
 * @author AndFChat
 */
public class CharListHandler extends TokenHandler {

    @Inject
    protected RelationManager relationManager;

    private int count;
    private Long time;

    private final HashMap<String, FCharacter> flistCharacters = new HashMap<String, FCharacter>();

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        long TIMEOUT = 1000 * 30;

        if (token == ServerToken.CON) {
            JSONObject json = new JSONObject(msg);
            count = json.getInt("count");
            time = System.currentTimeMillis();

        } // Initial characters online.
        else if (token == ServerToken.LIS && (System.currentTimeMillis() - time) < TIMEOUT) {
            JSONObject json = new JSONObject(msg);
            JSONArray characters = json.getJSONArray("characters");
            for (int i = 0; i < characters.length(); i++) {
                JSONArray character = characters.getJSONArray(i);
                FCharacter fCharacter = new FCharacter(character.getString(0), character.getString(1), character.getString(2), character.getString(3));
                flistCharacters.put(character.getString(0), fCharacter);
            }

            if (count <= flistCharacters.size()) {
                characterManager.initCharacters(flistCharacters);
            }
        } // New user connected
        else if (token == ServerToken.NLN) {
            JSONObject json = new JSONObject(msg);

            FCharacter fCharacter = new FCharacter(json.getString("identity"), json.getString("gender"), json.getString("status"), null);

            Ln.v("Adding character to ChatLog: " + fCharacter.toString());
            characterManager.addCharacter(fCharacter);

            if (fCharacter.isImportant()) {
                ChatEntry entry = entryFactory.getNotation(fCharacter, R.string.message_connected);
                broadcastSystemInfo(entry, fCharacter);
            }
            else if (chatroomManager.hasOpenPrivateConversation(fCharacter)) {
                ChatEntry entry = entryFactory.getNotation(fCharacter, R.string.message_connected);
                chatroomManager.addMessage(chatroomManager.getPrivateChatFor(fCharacter), entry);
            }

        } // Character left
        else if (token == ServerToken.FLN) {
            JSONObject json = new JSONObject(msg);

            FCharacter fCharacter = characterManager.findCharacter(json.getString("character"));

            if (fCharacter.isImportant()) {
                ChatEntry entry = entryFactory.getNotation(fCharacter, R.string.message_disconnected);
                broadcastSystemInfo(entry, fCharacter);
            }
            else if (chatroomManager.hasOpenPrivateConversation(fCharacter)) {
                ChatEntry entry = entryFactory.getNotation(fCharacter, R.string.message_disconnected);
                chatroomManager.addMessage(chatroomManager.getPrivateChatFor(fCharacter), entry);
            }

            characterManager.removeCharacter(fCharacter);
            chatroomManager.removeFlistCharFromChat(fCharacter);
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.CON, ServerToken.LIS, ServerToken.NLN, ServerToken.FLN};
    }

}
