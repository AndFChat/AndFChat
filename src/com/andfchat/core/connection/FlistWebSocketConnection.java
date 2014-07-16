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


package com.andfchat.core.connection;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import roboguice.util.Ln;
import android.content.Context;
import android.content.Intent;

import com.andfchat.core.connection.handler.PrivateMessageHandler;
import com.andfchat.core.data.CharStatus;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.ChatEntryType;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.SessionData;
import com.andfchat.frontend.activities.Login;
import com.andfchat.frontend.application.AndFChatApplication;
import com.andfchat.frontend.events.AndFChatEventManager;
import com.andfchat.frontend.events.ChatroomEventListner.ChatroomEventType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

@Singleton
public class FlistWebSocketConnection {

    private final static String CLIENT_NAME = "AndFChat";
    private final static String CLIENT_VERSION = "0.2.0";
    private final static String SERVER_URL_DEV = "ws://chat.f-list.net:8722/";
    private final static String SERVER_URL_LIVE = "ws://chat.f-list.net:9722/";

    @Inject
    private FlistWebSocketHandler handler;
    @Inject
    private SessionData sessionData;
    @Inject
    private ChatroomManager chatroomManager;
    @Inject
    private CharacterManager characterManager;
    @Inject
    private FlistWebSocketHandler socketHandler;
    @Inject
    private AndFChatEventManager eventManager;

    private final WebSocketConnection connection = new WebSocketConnection();

    public void connect(boolean onLive) {
        try {
            if (onLive) {
                connection.connect(SERVER_URL_LIVE, handler);
            } else {
                connection.connect(SERVER_URL_DEV, handler);
            }
        } catch (WebSocketException e) {
            e.printStackTrace();
            Ln.e("Exception while connecting");
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
            Ln.d("Sending token: " + token.name());
            connection.sendTextMessage(token.name());

            if (sessionData.getSessionSettings().useDebugChannel()) {
                FCharacter systemChar = characterManager.findCharacter(CharacterManager.USER_SYSTEM_OUTPUT);
                ChatEntry entry = new ChatEntry(token.name(), systemChar, ChatEntryType.MESSAGE);
                chatroomManager.addMessage(chatroomManager.getChatroom(AndFChatApplication.DEBUG_CHANNEL_NAME), entry);
            }
        } else {
            Ln.i("Sending message: " + token.name() + " " + data.toString());
            connection.sendTextMessage(token.name() + " " + data.toString());

            if (sessionData.getSessionSettings().useDebugChannel()) {
                FCharacter systemChar = characterManager.findCharacter(CharacterManager.USER_SYSTEM_OUTPUT);
                ChatEntry entry = new ChatEntry(token.name() + " " + data.toString(), systemChar, ChatEntryType.MESSAGE);
                chatroomManager.addMessage(chatroomManager.getChatroom(AndFChatApplication.DEBUG_CHANNEL_NAME), entry);
            }
        }
    }

    /**
     * Register a feedback called after receiving the ServerToken, the feedback will only be called once than removed.
     */
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
            Ln.w("exception occured while identifying: " + e.getMessage());
        }
    }

    /**
     * Asks the server for permission to enter a channel.
     */
    public void joinChannel(String channel) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", channel);
            sendMessage(ClientToken.JCH, data);
        } catch (JSONException e) {
            Ln.w("exception occured while joining channle: " + e.getMessage());
        }
    }

    /**
     * Asks the server for all public private channel.
     */
    public void askForPrivateChannel() {
        sendMessage(ClientToken.ORS);
    }

    /**
     * Asks the server to leave an channel
     */
    public void leaveChannel(Chatroom chatroom) {
        // TODO: Leave private chat without deleting "log"
        if (!chatroom.isPrivateChat()) {
            JSONObject data = new JSONObject();
            try {
                data.put("channel", chatroom.getChannel().getChannelId());
                sendMessage(ClientToken.LCH, data);
            } catch (JSONException e) {
                Ln.w("exception occured while leaving channle: " + e.getMessage());
            }
        } else {
            // Private chats will just be removed.
            boolean wasActive = chatroomManager.isActiveChat(chatroom);

            chatroomManager.removeChatroom(chatroom.getChannel());
            eventManager.fire(chatroom, ChatroomEventType.LEFT);

            if (wasActive) {
                List<Chatroom> chatrooms = chatroomManager.getChatRooms();
                chatroomManager.setActiveChat(chatrooms.get(chatrooms.size() - 1));
            }
        }
    }

    /**
     * Sends a message to an channel.
     */
    public void sendMessageToChannel(Chatroom chatroom, String msg) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", chatroom.getId());
            data.put("character", sessionData.getCharacterName());
            data.put("message", msg);
            sendMessage(ClientToken.MSG, data);

            ChatEntry entry = new ChatEntry(msg, characterManager.findCharacter(sessionData.getCharacterName()), ChatEntryType.MESSAGE);
            chatroomManager.addMessage(chatroom, entry);

        } catch (JSONException e) {
            Ln.w("exception occured while sending message: " + e.getMessage());
        }
    }

    /**
     * Sends a message to an channel.
     */
    public void sendAdToChannel(Chatroom chatroom, String adMessage) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", chatroom.getId());
            data.put("message", adMessage);
            sendMessage(ClientToken.LRP, data);

            ChatEntry entry = new ChatEntry(adMessage, characterManager.findCharacter(sessionData.getCharacterName()), ChatEntryType.AD);
            chatroomManager.addMessage(chatroom, entry);

        } catch (JSONException e) {
            Ln.w("exception occured while sending message: " + e.getMessage());
        }
    }

    /**
     * Sends a private message.
     */
    public void sendPrivatMessage(String recipient, String msg) {
        JSONObject data = new JSONObject();
        try {
            data.put("recipient", recipient);
            data.put("message", msg);
            sendMessage(ClientToken.PRI, data);

            String channelname = PrivateMessageHandler.PRIVATE_MESSAGE_TOKEN + recipient;
            Chatroom chatroom = chatroomManager.getChatroom(channelname);
            if (chatroom != null) {
                ChatEntry entry = new ChatEntry(msg, characterManager.findCharacter(sessionData.getCharacterName()), ChatEntryType.MESSAGE);
                chatroomManager.addMessage(chatroom, entry);
            }
            else {
                Ln.e("Can't find log for private message recipient " + recipient);
            }

        } catch (JSONException e) {
            Ln.w("exception occured while sending private message: " + e.getMessage());
        }
    }

    /**
     * Set a status for the character.
     */
    public void setStatus(CharStatus status, String msg) {
        JSONObject data = new JSONObject();
        try {
            data.put("status", status.name());
            data.put("statusmsg", msg);
            data.put("character", sessionData.getCharacterName());
            sendMessage(ClientToken.STA, data);
        } catch (JSONException e) {
            Ln.w("exception occured while sending private message: " + e.getMessage());
        }
    }

    /**
     * Asks for character information.
     */
    public void askForInfos(FCharacter flistChar) {
        JSONObject data = new JSONObject();
        try {
            data.put("character", flistChar.getName());
            sendMessage(ClientToken.PRO, data);
        } catch (JSONException e) {
            Ln.w("exception occured while sending private message: " + e.getMessage());
        }
    }

    public void requestOfficialChannels() {
        sendMessage(ClientToken.CHA);
    }

    public void closeConnection(Context context) {
        Ln.d("Disconnect!");

        if (connection.isConnected()) {
            connection.disconnect();
        }

        socketHandler.disconnected();

        sessionData.clear();
        chatroomManager.clear();
        characterManager.clear();
        eventManager.clear();

        Intent intent = new Intent(context, Login.class);
        context.startActivity(intent);
    }

    public void createPrivateChannel(String channelname) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", channelname);
            sendMessage(ClientToken.CCR, data);
        } catch (JSONException e) {
            Ln.w("exception occured while creating a private channel: " + e.getMessage());
        }
    }

    public void bottle(Chatroom activeChat) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", activeChat.getId());
            data.put("dice", "bottle");
            sendMessage(ClientToken.RLL, data);
        } catch (JSONException e) {
            Ln.w("exception occured while botteling: " + e.getMessage());
        }
    }

    public void dice(Chatroom activeChat, String value) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", activeChat.getId());

            if (value == null || value.length() == 0) {
                value = "1d10";
            }

            data.put("dice", value);
            sendMessage(ClientToken.RLL, data);
        } catch (JSONException e) {
            Ln.w("exception occured while botteling: " + e.getMessage());
        }
    }

    public void closeChannel(Chatroom chatroom) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", chatroom.getId());
            data.put("status", "private");
            sendMessage(ClientToken.RST, data);
        } catch (JSONException e) {
            Ln.w("exception occured while sending RST: " + e.getMessage());
        }
    }

    public void openChannel(Chatroom chatroom) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", chatroom.getId());
            data.put("status", "public");
            sendMessage(ClientToken.RST, data);
        } catch (JSONException e) {
            Ln.w("exception occured while sending RST: " + e.getMessage());
        }
    }

    public void unban(String username, Chatroom chatroom) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", chatroom.getId());
            data.put("character", username);
            sendMessage(ClientToken.CUB, data);
        } catch (JSONException e) {
            Ln.w("exception occured while sending CUB: " + e.getMessage());
        }
    }

    public void ban(String username, Chatroom chatroom) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", chatroom.getId());
            data.put("character", username);
            sendMessage(ClientToken.CBU, data);
        } catch (JSONException e) {
            Ln.w("exception occured while sending CBU: " + e.getMessage());
        }
    }

    public void kick(String username, Chatroom chatroom) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", chatroom.getId());
            data.put("character", username);
            sendMessage(ClientToken.CKU, data);
        } catch (JSONException e) {
            Ln.w("exception occured while sending CKU: " + e.getMessage());
        }
    }

    public void invite(String username, Chatroom chatroom) {
        JSONObject data = new JSONObject();
        try {
            data.put("channel", chatroom.getId());
            data.put("character", username);
            sendMessage(ClientToken.CIU, data);
        } catch (JSONException e) {
            Ln.w("exception occured while sending CIU: " + e.getMessage());
        }
    }
}
