package com.homebrewn.flistchat.frontend.util;

import android.text.style.ClickableSpan;
import android.view.View;

import com.google.inject.Inject;
import com.homebrewn.flistchat.core.connection.FlistWebSocketConnection;
import com.homebrewn.flistchat.core.data.Chatroom;
import com.homebrewn.flistchat.core.data.ChatroomManager;

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
