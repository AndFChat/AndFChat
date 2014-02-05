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


package com.andfchat.frontend.util;

import android.text.style.ClickableSpan;
import android.view.View;

import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.google.inject.Inject;

public class OpenChatroomSpan extends ClickableSpan {

    @Inject
    private ChatroomManager chatroomManager;
    @Inject
    private FlistWebSocketConnection connection;

    private final String roomId;

    public OpenChatroomSpan(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public void onClick(View widget) {
        Chatroom chatroom = chatroomManager.getChatroom(roomId);
        if (chatroom != null) {
            chatroomManager.setActiveChat(chatroom);
        } else {
            connection.joinChannel(roomId);
        }
    }
}
