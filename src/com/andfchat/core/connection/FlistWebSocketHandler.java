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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import roboguice.RoboGuice;
import roboguice.util.Ln;
import android.content.Context;

import com.andfchat.core.connection.handler.AdHandler;
import com.andfchat.core.connection.handler.ChannelDescriptionHandler;
import com.andfchat.core.connection.handler.ChannelListHandler;
import com.andfchat.core.connection.handler.CharInfoHandler;
import com.andfchat.core.connection.handler.CharListHandler;
import com.andfchat.core.connection.handler.DiceBottleHandler;
import com.andfchat.core.connection.handler.ErrorMessageHandler;
import com.andfchat.core.connection.handler.FirstConnectionHandler;
import com.andfchat.core.connection.handler.FriendListHandler;
import com.andfchat.core.connection.handler.JoinedChannel;
import com.andfchat.core.connection.handler.LeftChannelHandler;
import com.andfchat.core.connection.handler.MessageHandler;
import com.andfchat.core.connection.handler.PingHandler;
import com.andfchat.core.connection.handler.PrivateMessageHandler;
import com.andfchat.core.connection.handler.TokenHandler;
import com.andfchat.core.data.AppProperties;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.ChatEntry.ChatEntryType;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FlistChar;
import com.andfchat.core.data.SessionData;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import de.tavendo.autobahn.WebSocketHandler;

/**
 * Handles all input send from server, using the ServerToken to decide which TokenHandler should handle the input.
 * @author AndFChat
 */
@Singleton
public class FlistWebSocketHandler extends WebSocketHandler {

    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected CharacterManager characterManager;
    @Inject
    protected SessionData sessionData;

    private final HashMap<ServerToken, TokenHandler> handlerMap = new HashMap<ServerToken, TokenHandler>();
    private final Map<ServerToken, List<FeedbackListner>> feedbackListnerMap = new HashMap<ServerToken, List<FeedbackListner>>();

    @Inject
    public FlistWebSocketHandler(Context context) {

        // Initialize all handler with there tokens, they can handle.
        List<TokenHandler> availableTokenHandler = new ArrayList<TokenHandler>();

        availableTokenHandler.add(new PingHandler());
        availableTokenHandler.add(new JoinedChannel());
        availableTokenHandler.add(new MessageHandler());
        availableTokenHandler.add(new CharListHandler());
        availableTokenHandler.add(new CharInfoHandler());
        availableTokenHandler.add(new PrivateMessageHandler());
        availableTokenHandler.add(new ChannelListHandler());
        availableTokenHandler.add(new FirstConnectionHandler());
        availableTokenHandler.add(new FriendListHandler());
        availableTokenHandler.add(new LeftChannelHandler());
        availableTokenHandler.add(new ChannelDescriptionHandler());
        availableTokenHandler.add(new ErrorMessageHandler());
        availableTokenHandler.add(new DiceBottleHandler());
        availableTokenHandler.add(new AdHandler());

        Injector injector = RoboGuice.getInjector(context);

        for (TokenHandler handler : availableTokenHandler) {
            injector.injectMembers(handler);
            for (ServerToken token : handler.getAcceptableTokens()) {
                if (!handlerMap.containsKey(token)) {
                    handlerMap.put(token, handler);
                } else {
                    throw new RuntimeException("Can't init to TokenHandler for the same token: '" + token.name() + "'!");
                }
            }
        }

        Ln.d("Initialized TokenHandler, tokens, listend to: " + handlerMap.keySet().toString());
    }

    @Override
    public void onOpen() {
       Ln.d("Status: Connected");
    }

    @Override
    public void onTextMessage(String payload) {
        Ln.v("Incoming message: " + payload);

        if (sessionData.getSessionSettings().useDebugChannel()) {
            FlistChar systemChar = characterManager.findCharacter(CharacterManager.USER_SYSTEM_INPUT);
            ChatEntry entry = new ChatEntry(payload, systemChar, new Date(), ChatEntryType.MESSAGE);
            chatroomManager.getChatroom(AppProperties.DEBUG_CHANNEL_NAME).addMessage(entry);
        }

        ServerToken token = null;
        try {
            token = ServerToken.valueOf(payload.substring(0, 3));
        } catch (IllegalArgumentException e) {
            Ln.w("Can't find token '" + payload.substring(0, 3) + "' in ServerToken-Enum! -> Ignoring Message");
            return;
        }

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
                Ln.e("Can't parse json: " + payload);
            }
        } else {
            Ln.e("Can't find handler for token '" + token + "' -> Ignoring Message");
        }

    }

    @Override
    public void onClose(int code, String reason) {
        Ln.d("Status: Connection closed: " + reason);
    }

    /**
     * Register a feedback called after receiving the ServerToken, the feedback will only be called once than removed.
     */
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
