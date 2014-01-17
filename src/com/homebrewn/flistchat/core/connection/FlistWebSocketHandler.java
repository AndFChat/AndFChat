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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.util.Log;

import com.homebrewn.flistchat.core.connection.handler.ChannelDescriptionHandler;
import com.homebrewn.flistchat.core.connection.handler.ChannelListHandler;
import com.homebrewn.flistchat.core.connection.handler.CharInfoHandler;
import com.homebrewn.flistchat.core.connection.handler.CharListHandler;
import com.homebrewn.flistchat.core.connection.handler.ErrorMessageHandler;
import com.homebrewn.flistchat.core.connection.handler.FirstConnectionHandler;
import com.homebrewn.flistchat.core.connection.handler.FriendListHandler;
import com.homebrewn.flistchat.core.connection.handler.JoinedChannel;
import com.homebrewn.flistchat.core.connection.handler.LeftChannelHandler;
import com.homebrewn.flistchat.core.connection.handler.MessageHandler;
import com.homebrewn.flistchat.core.connection.handler.PingHandler;
import com.homebrewn.flistchat.core.connection.handler.PrivateMessageHandler;
import com.homebrewn.flistchat.core.connection.handler.TokenHandler;
import com.homebrewn.flistchat.core.data.AppProperties;
import com.homebrewn.flistchat.core.data.CharacterHandler;
import com.homebrewn.flistchat.core.data.ChatEntry;
import com.homebrewn.flistchat.core.data.ChatEntry.ChatEntryType;
import com.homebrewn.flistchat.core.data.FlistChar;
import com.homebrewn.flistchat.core.data.SessionData;

import de.tavendo.autobahn.WebSocketHandler;

/**
 * Handles all input send from server, using the ServerToken to decide which TokenHandler should handle the input.
 * @author AndFChat
 */
public class FlistWebSocketHandler extends WebSocketHandler {

    private final HashMap<ServerToken, TokenHandler> handlerMap = new HashMap<ServerToken, TokenHandler>();
    private final Map<ServerToken, List<FeedbackListner>> feedbackListnerMap = new HashMap<ServerToken, List<FeedbackListner>>();

    private static final String TAG = FlistWebSocketHandler.class.getName();

    private final SessionData sessionData;

    public FlistWebSocketHandler(SessionData sessionData) {

        // Initialize all handler with there tokens, they can handle.
        List<TokenHandler> availableTokenHandler = new ArrayList<TokenHandler>();

        availableTokenHandler.add(new PingHandler(sessionData));
        availableTokenHandler.add(new JoinedChannel(sessionData));
        availableTokenHandler.add(new MessageHandler(sessionData));
        availableTokenHandler.add(new CharListHandler(sessionData));
        availableTokenHandler.add(new CharInfoHandler(sessionData));
        availableTokenHandler.add(new PrivateMessageHandler(sessionData));
        availableTokenHandler.add(new ChannelListHandler(sessionData));
        availableTokenHandler.add(new FirstConnectionHandler(sessionData));
        availableTokenHandler.add(new FriendListHandler(sessionData));
        availableTokenHandler.add(new LeftChannelHandler(sessionData));
        availableTokenHandler.add(new ChannelDescriptionHandler(sessionData));
        availableTokenHandler.add(new ErrorMessageHandler(sessionData));

        for (TokenHandler handler : availableTokenHandler) {
            for (ServerToken token : handler.getAcceptableTokens()) {
                if (!handlerMap.containsKey(token)) {
                    handlerMap.put(token, handler);
                } else {
                    throw new RuntimeException("Can't init to TokenHandler for the same token: '" + token.name() + "'!");
                }
            }
        }

        Log.d(TAG, "Initialized TokenHandler, tokens, listend to: " + handlerMap.keySet().toString());

        this.sessionData = sessionData;
    }

    @Override
    public void onOpen() {
       Log.d(TAG, "Status: Connected");
    }

    @Override
    public void onTextMessage(String payload) {
        Log.v(TAG, "Incoming message: " + payload);

        if (sessionData.getSessionSettings().useDebugChannel()) {
            FlistChar systemChar = sessionData.getCharHandler().findCharacter(CharacterHandler.USER_SYSTEM_INPUT);
            sessionData.getChatroomHandler().getChatroom(AppProperties.DEBUG_CHANNEL_NAME).addMessage(new ChatEntry(payload, systemChar, new Date(), ChatEntryType.MESSAGE));
        }

        ServerToken token = null;
        try {
            token = ServerToken.valueOf(payload.substring(0, 3));
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Can't find token '" + payload.substring(0, 3) + "' in ServerToken-Enum! -> Ignoring Message");
            return;
        }

        Log.d(TAG, "found ServerToken: " + token.name());

        if (handlerMap.containsKey(token)) {
            try {
                // If the message has message, give them to handler without token.
                // FeedbackListner will only be called once and removed.
                if (payload.length() > 3) {
                    handlerMap.get(token).incomingMessage(token, payload.substring(4), feedbackListnerMap.remove(token));
                } else {
                    handlerMap.get(token).incomingMessage(token, "", feedbackListnerMap.remove(token));
                }
            } catch (JSONException ex) {
                Log.e(TAG, "Can't parse json: " + payload);
            }
        } else {
            Log.e(TAG, "Can't find handler for token '" + token + "' -> Ignoring Message");
        }

    }

    @Override
    public void onClose(int code, String reason) {
       Log.d(TAG, "Status: Connection closed: " + reason);
    }

    public void addFeedbackListner(ServerToken serverToken, FeedbackListner feedbackListner) {
        if (feedbackListnerMap.containsKey(serverToken)) {
            feedbackListnerMap.get(serverToken).add(feedbackListner);
        } else {
            List<FeedbackListner> listnerList = new ArrayList<FeedbackListner>();
            listnerList.add(feedbackListner);
            feedbackListnerMap.put(serverToken, listnerList);
        }
    }

}
