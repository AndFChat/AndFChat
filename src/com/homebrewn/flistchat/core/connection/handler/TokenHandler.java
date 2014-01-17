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

import java.util.List;

import org.json.JSONException;

import com.homebrewn.flistchat.core.connection.FeedbackListner;
import com.homebrewn.flistchat.core.connection.ServerToken;
import com.homebrewn.flistchat.core.data.ChatEntry;
import com.homebrewn.flistchat.core.data.ChatroomHandler;
import com.homebrewn.flistchat.core.data.FlistChar;
import com.homebrewn.flistchat.core.data.SessionData;

public abstract class TokenHandler {

    protected final ChatroomHandler ChatroomHandler;
    protected final SessionData sessionData;

    public TokenHandler(SessionData sessionData) {
        this.sessionData = sessionData;
        this.ChatroomHandler = sessionData.getChatroomHandler();
    }

    public abstract void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException;

    public abstract ServerToken[] getAcceptableTokens();

    protected void broadcastSystemInfo(ChatEntry chatEntry, FlistChar flistChar) {
        if (flistChar.isFriend() || flistChar.isBookmarked()) {
            ChatroomHandler.getActiveChat().addMessage(chatEntry);

            if (ChatroomHandler.hasOpenPrivateConversation(flistChar)) {
                ChatroomHandler.getPrivateChatFor(flistChar).addMessage(chatEntry);
            }
        }
        else if (ChatroomHandler.hasOpenPrivateConversation(flistChar)) {
            ChatroomHandler.getPrivateChatFor(flistChar).addMessage(chatEntry);
        }
    }

    protected void addChatEntryToActiveChat(ChatEntry chatEntry) {
        ChatroomHandler.getActiveChat().addMessage(chatEntry);
    }

}
