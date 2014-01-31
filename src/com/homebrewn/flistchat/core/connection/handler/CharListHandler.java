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
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.util.Ln;

import com.homebrewn.flistchat.core.connection.FeedbackListner;
import com.homebrewn.flistchat.core.connection.ServerToken;
import com.homebrewn.flistchat.core.data.CharRelation;
import com.homebrewn.flistchat.core.data.ChatEntry;
import com.homebrewn.flistchat.core.data.ChatEntry.ChatEntryType;
import com.homebrewn.flistchat.core.data.FlistChar;

/**
 * Tracks the online status and amount of user. Handles new connects and disconnects.
 * @author AndFChat
 */
public class CharListHandler extends TokenHandler {

    private int count;
    private Long time;

    private static long TIMEOUT = 1000 * 30; // 30 sec

    private final HashMap<String, FlistChar> flistCharacters = new HashMap<String, FlistChar>();

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
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
                Ln.v("Adding character to List: " + character.getString(0) + "/" + character.getString(1) + "/" + character.getString(2));

                FlistChar flistChar = new FlistChar(character.getString(0), character.getString(1), character.getString(2), character.getString(3));

                if (characterManager.getFriendList().isFriend(flistChar.getName())) {
                    flistChar.addRelation(CharRelation.FRIEND);
                }

                flistCharacters.put(character.getString(0), flistChar);
            }

            if (count <= flistCharacters.size()) {
                characterManager.initCharacters(flistCharacters);
            }
        } // New user connected
        else if (token == ServerToken.NLN) {
            JSONObject json = new JSONObject(msg);

            FlistChar flistChar = new FlistChar(json.getString("identity"), json.getString("gender"), json.getString("status"), null);

            if (characterManager.getFriendList().isFriend(flistChar.getName())) {
                flistChar.addRelation(CharRelation.FRIEND);
            }

            Ln.v("Adding character to ChatLog: " + flistChar.toString());
            characterManager.addCharacter(flistChar);

            ChatEntry chatEntry = new ChatEntry("CONNECTED.", flistChar, new Date(), ChatEntryType.NOTATION_CONNECT);
            this.broadcastSystemInfo(chatEntry, flistChar);

        } // Charakter left
        else if (token == ServerToken.FLN) {
            JSONObject json = new JSONObject(msg);

            FlistChar flistChar = characterManager.findCharacter(json.getString("character"));

            ChatEntry chatEntry = new ChatEntry("DISCONNECTED.", flistChar, new Date(), ChatEntryType.NOTATION_DISCONNECT);
            this.broadcastSystemInfo(chatEntry, flistChar);

            characterManager.removeCharacter(json.getString("character"));
            chatroomManager.removeFlistCharFromChat(flistChar);
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.CON, ServerToken.LIS, ServerToken.NLN, ServerToken.FLN};
    }

}
