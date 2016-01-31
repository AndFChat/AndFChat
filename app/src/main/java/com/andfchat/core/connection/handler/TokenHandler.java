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

import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.history.HistoryManager;
import com.andfchat.core.data.messages.ChatEntry;
import com.andfchat.core.data.messages.ChatEntryFactory;
import com.andfchat.frontend.application.AndFChatApplication;
import com.andfchat.frontend.events.AndFChatEventManager;
import com.google.inject.Inject;

public abstract class TokenHandler {

    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected CharacterManager characterManager;
    @Inject
    protected SessionData sessionData;
    @Inject
    protected HistoryManager historyManager;
    @Inject
    protected AndFChatEventManager eventManager;
    @Inject
    protected ChatEntryFactory entryFactory;

    public abstract void incomingMessage(ServerToken token, String msg, List<FeedbackListener> feedbackListener) throws JSONException;

    public abstract ServerToken[] getAcceptableTokens();

    protected void broadcastSystemInfo(ChatEntry chatEntry, FCharacter flistChar) {
        //chatroomManager.addMessage(chatroomManager.getActiveChat(), chatEntry); //This can get annoying, especially if you have lots of bookmarks or friends.
        // Add broadcast message also to the console.
        chatroomManager.addMessage(chatroomManager.getChatroom(AndFChatApplication.DEBUG_CHANNEL_NAME), chatEntry);

        if (chatroomManager.hasOpenPrivateConversation(flistChar)) {
            // If private conversation is already the active chat, do not post it again.
            if (chatroomManager.isActiveChat(chatroomManager.getPrivateChatFor(flistChar)) == false) {
                chatroomManager.addMessage(chatroomManager.getPrivateChatFor(flistChar), chatEntry);
            }
        }
    }

    protected void broadcastInviteInfo(ChatEntry chatEntry, FCharacter flistChar) {
        chatroomManager.addMessage(chatroomManager.getActiveChat(), chatEntry); //This gets extremely annoying, especially if you have lots of bookmarks or friends.
        // Add broadcast message also to the console.
        if (!chatroomManager.getActiveChat().isSystemChat()) {
            chatroomManager.addMessage(chatroomManager.getChatroom(AndFChatApplication.DEBUG_CHANNEL_NAME), chatEntry);
        }

        if (chatroomManager.hasOpenPrivateConversation(flistChar)) {
            // If private conversation is already the active chat, do not post it again.
            if (chatroomManager.isActiveChat(chatroomManager.getPrivateChatFor(flistChar)) == false) {
                chatroomManager.addMessage(chatroomManager.getPrivateChatFor(flistChar), chatEntry);
            }
        }
    }

    protected void addChatEntryToActiveChat(ChatEntry chatEntry) {
        chatroomManager.addMessage(chatroomManager.getActiveChat(), chatEntry);
    }

    protected boolean isInScope(FCharacter flistChar) {
        return flistChar.isImportant() || chatroomManager.hasOpenPrivateConversation(flistChar);
    }

    public void connected() {}

    public void closed() {}
}
