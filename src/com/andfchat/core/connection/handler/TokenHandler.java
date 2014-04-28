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

import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FlistChar;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.history.HistoryManager;
import com.andfchat.frontend.application.AndFChatApplication;
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

    public abstract void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException;

    public abstract ServerToken[] getAcceptableTokens();

    protected void broadcastSystemInfo(ChatEntry chatEntry, FlistChar flistChar) {
        chatroomManager.getActiveChat().addMessage(chatEntry);
        // Add broadcasted message also to the console.
        if (!chatroomManager.getActiveChat().isSystemChat()) {
            chatroomManager.getChatroom(AndFChatApplication.DEBUG_CHANNEL_NAME).addMessage(chatEntry);
        }

        if (chatroomManager.hasOpenPrivateConversation(flistChar)) {
            chatroomManager.getPrivateChatFor(flistChar).addMessage(chatEntry);
        }
    }

    protected void addChatEntryToActiveChat(ChatEntry chatEntry) {
        chatroomManager.getActiveChat().addMessage(chatEntry);
    }

    protected boolean isInScope(FlistChar flistChar) {
        if (flistChar.isImportant()) {
            return true;
        }

        return chatroomManager.hasOpenPrivateConversation(flistChar);
    }

    public void connected() {};
    public void closed() {};
}
