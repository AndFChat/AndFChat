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

import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.messages.ChatEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import roboguice.util.Ln;

/**
 * Displays add messages in channels.
 * @author AndFChat
 */
public class ModsHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        if (token == ServerToken.ADL) {

            List<String> opList = new ArrayList<String>();

            JSONObject json = new JSONObject(msg);
            JSONArray ops = json.getJSONArray("ops");
            for (int i = 0; i < ops.length(); i++) {
                opList.add(ops.getString(i));
                Ln.d("Add new global mod: " + ops.getString(i));
            }

            characterManager.setGlobalMods(opList);
        }
        else if (token == ServerToken.COL) {
            JSONObject json = new JSONObject(msg);

            Chatroom chatroom = chatroomManager.getChatroom(json.getString("channel"));

            List<String> opList = new ArrayList<String>();
            JSONArray ops = json.getJSONArray("oplist");
            for (int i = 0; i < ops.length(); i++) {
                opList.add(ops.getString(i));
                Ln.d("Add new channel mod: " + ops.getString(i));
            }

            Ln.d("Set channel mods for: '" + chatroom.getName() + "'");
            chatroom.setChannelMods(opList);
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.ADL, ServerToken.COL};
    }

}
