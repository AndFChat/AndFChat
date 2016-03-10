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
import android.os.Vibrator;

import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.connection.handler.VariableHandler.Variable;
import com.andfchat.core.data.Channel;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.Chatroom.ChatroomType;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.messages.ChatEntry;
import com.andfchat.core.data.messages.ChatEntryFactory;
import com.andfchat.core.data.messages.MessageEntry;
import com.andfchat.core.data.messages.NotationEntry;
import com.andfchat.frontend.application.AndFChatNotification;
import com.andfchat.frontend.events.ChatroomEventListener.ChatroomEventType;
import com.google.inject.Inject;

/**
 * Handles private messages send to user.
 * @author AndFChat
 */
public class PrivateMessageHandler extends TokenHandler {

    public final static String PRIVATE_MESSAGE_TOKEN = "PRIV:::";

    @Inject
    protected Vibrator vibrator;
    @Inject
    protected AndFChatNotification notification;

    private final static int VIBRATING_TIME = 300;

    private int messages = 0;

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException {
        JSONObject jsonObject = new JSONObject(msg);

        String character = jsonObject.getString("character");
        String message = jsonObject.getString("message");

        if (!characterManager.findCharacter(character).isIgnored()) {

            Chatroom chatroom = chatroomManager.getChatroom(PRIVATE_MESSAGE_TOKEN + character);
            if (chatroom == null) {
                int maxTextLength = sessionData.getIntVariable(Variable.priv_max);
                FCharacter friend = characterManager.findCharacter(character);
                chatroom = openPrivateChat(chatroomManager, friend, maxTextLength, sessionData.getSessionSettings().showAvatarPictures());

                if (friend.getStatusMsg() != null && friend.getStatusMsg().length() > 0) {
                    chatroomManager.addMessage(chatroom, entryFactory.getStatusInfo(friend));
                }

                eventManager.fire(chatroom, ChatroomEventType.NEW);
            }

            // If vibration is allowed, do it on new messages!
            if (sessionData.getSessionSettings().vibrationFeedback()) {
                // Vibrate if the active channel is not the same as the "messaged" one or the app is not visible and the chatroom isn't already set to "hasNewMessage".
                if ((!chatroomManager.getActiveChat().equals(chatroom) || !sessionData.isVisible()) && !chatroom.hasNewMessage()) {
                    Ln.d("New Message Vibration on!");
                    vibrator.vibrate(VIBRATING_TIME);
                }
            }

            // Update notification
            if (!sessionData.isVisible()) {
                notification.updateNotification(sessionData.addMessage());
            }

            ChatEntry entry = entryFactory.getMessage(characterManager.findCharacter(character), message);
            chatroomManager.addMessage(chatroom, entry);
        } else {
            Ln.d("Blocked a private message from an ignored character.");
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[]{ServerToken.PRI};
    }

    public static Chatroom openPrivateChat(ChatroomManager chatroomManager, FCharacter character, int maxTextLength, boolean showAvatar) {
        if (!character.isIgnored()) {
            String channelname = PrivateMessageHandler.PRIVATE_MESSAGE_TOKEN + character.getName();

            Chatroom chatroom = new Chatroom(new Channel(channelname, character.getName(), ChatroomType.PRIVATE_CHAT), character, maxTextLength, showAvatar);
            chatroomManager.addChatroom(chatroom);

            return chatroom;
        }
        return null;
    }
}
