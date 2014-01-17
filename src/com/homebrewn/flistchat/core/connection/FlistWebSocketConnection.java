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


package com.homebrewn.flistchat.core.connection;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.homebrewn.flistchat.core.connection.handler.PrivateMessageHandler;
import com.homebrewn.flistchat.core.data.AppProperties;
import com.homebrewn.flistchat.core.data.CharStatus;
import com.homebrewn.flistchat.core.data.CharacterHandler;
import com.homebrewn.flistchat.core.data.ChatEntry;
import com.homebrewn.flistchat.core.data.ChatEntry.ChatEntryType;
import com.homebrewn.flistchat.core.data.Chatroom;
import com.homebrewn.flistchat.core.data.FlistChar;
import com.homebrewn.flistchat.core.data.SessionData;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

public class FlistWebSocketConnection {

    protected final WebSocketConnection connection = new WebSocketConnection();
    protected final FlistWebSocketHandler handler;

    private final SessionData sessionData;

    private final static String CLIENT_NAME = "AndFChat";
    private final static String CLIENT_VERSION = "alpha01";
    private final static String SERVER_URL = "ws://chat.f-list.net:8722/";

    private final static String TAG = "flistchat.client.actions";

    public FlistWebSocketConnection(String account, String ticket, SessionData sessionData) {
        this.sessionData = sessionData;
        handler = new FlistWebSocketHandler(sessionData);
        try {
            connection.connect(SERVER_URL, handler);
        } catch (WebSocketException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception while connecting");
        }
    }

    public void sendMessage(ClientToken token) {
        sendMessage(token, null);
    }

    public boolean isConnected() {
        return connection.isConnected();
    }

    public void sendMessage(ClientToken token, JSONObject data) {
        if (data == null) {
            Log.i(TAG, "Sending token: " + token.name());
            connection.sendTextMessage(token.name());

            if (sessionData.getSessionSettings().useDebugChannel()) {
                FlistChar systemChar = sessionData.getCharHandler().findCharacter(CharacterHandler.USER_SYSTEM_OUTPUT);
                sessionData.getChatroomHandler().getChatroom(AppProperties.DEBUG_CHANNEL_NAME).addMessage(new ChatEntry(token.name(), systemChar, new Date(), ChatEntryType.MESSAGE));
            }
        } else {
            Log.i(TAG, "Sending message: " + token.name() + " " + data.toString());
            connection.sendTextMessage(token.name() + " " + data.toString());

            if (sessionData.getSessionSettings().useDebugChannel()) {
                FlistChar systemChar = sessionData.getCharHandler().findCharacter(CharacterHandler.USER_SYSTEM_OUTPUT);
                sessionData.getChatroomHandler().getChatroom(AppProperties.DEBUG_CHANNEL_NAME).addMessage(new ChatEntry(token.name() + " " + data.toString(), systemChar, new Date(), ChatEntryType.MESSAGE));
            }
        }
    }

    public void registerFeedbackListner(ServerToken serverToken, FeedbackListner feedbackListner) {
        handler.addFeedbackListner(serverToken, feedbackListner);
    }

    /**
     * Identify character with the server
     */
    public void identify() {
        JSONObject data = new JSONObject();
        try {
            data.put("ticket", sessionData.getTicket());
            data.put("method", "ticket");
            data.put("cname", CLIENT_NAME);
            data.put("cversion", CLIENT_VERSION);
            data.put("account", sessionData.getAccount());
            data.put("character", sessionData.getCharacterName());
            sendMessage(ClientToken.IDN, data);
        } catch (JSONException e) {
            Log.w(TAG, "exception occured while identifying: " + e.getMessage());
        }
    }

    /**
     * Asks the server for permission to enter a channel.
     * @param channel
     */
    public void joinChannel(String channel) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", channel);
            sendMessage(ClientToken.JCH, data);
        } catch (JSONException e) {
            Log.w(TAG, "exception occured while joining channle: " + e.getMessage());
        }
    }

    /**
     * Asks the server for all public private channel.
     * @param channel
     */
    public void askForPrivateChannel() {
        sendMessage(ClientToken.ORS);
    }

    /**
     * Asks the server to leave an channel
     * @param channel
     */
    public void leaveChannel(Chatroom Chatroom) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", Chatroom.getId());
            sendMessage(ClientToken.LCH, data);
        } catch (JSONException e) {
            Log.w(TAG, "exception occured while leaving channle: " + e.getMessage());
        }
    }

    /**
     * Sends a message to an channel.
     * @param channel
     * @param msg
     */
    public void sendMessageToChannel(Chatroom Chatroom, String msg) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", Chatroom.getId());
            data.put("character", sessionData.getCharacterName());
            data.put("message", msg);
            sendMessage(ClientToken.MSG, data);

            Chatroom.addMessage(msg, sessionData.getCharHandler().findCharacter(sessionData.getCharacterName()), new Date());

        } catch (JSONException e) {
            Log.w(TAG, "exception occured while sending message: " + e.getMessage());
        }
    }

    /**
     * Sends a private message.
     * @param recipient
     * @param msg
     */
    public void sendPrivatMessage(String recipient, String msg) {
        JSONObject data = new JSONObject();
        try {
            data.put("recipient", recipient);
            data.put("message", msg);
            sendMessage(ClientToken.PRI, data);

            String channelname = PrivateMessageHandler.PRIVATE_MESSAGE_TOKEN + recipient;
            Chatroom log = sessionData.getChatroomHandler().getChatroom(channelname);
            log.addMessage(msg, sessionData.getCharHandler().findCharacter(sessionData.getCharacterName()), new Date());

        } catch (JSONException e) {
            Log.w(TAG, "exception occured while sending private message: " + e.getMessage());
        }
    }

    /**
     * Set a status for the character.
     * @param status
     * @param msg
     */
    public void setStatus(CharStatus status, String msg) {
        JSONObject data = new JSONObject();
        try {
            data.put("status", status.name());
            data.put("statusmsg", msg);
            data.put("character", sessionData.getCharacterName());
            sendMessage(ClientToken.STA, data);
        } catch (JSONException e) {
            Log.w(TAG, "exception occured while sending private message: " + e.getMessage());
        }
    }

    public void requestOfficialChannels() {
        sendMessage(ClientToken.CHA);
    }

    public void closeConnection() {
        connection.disconnect();
    }
}
