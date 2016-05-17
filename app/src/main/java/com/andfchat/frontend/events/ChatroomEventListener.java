package com.andfchat.frontend.events;

import com.andfchat.core.data.Chatroom;

public interface ChatroomEventListener {

    enum ChatroomEventType {
        ACTIVE,
        NEW,
        LEFT,
        NEW_MESSAGE,
        NEW_STATUS,
        NEW_TYPING_STATUS,
    }

    void onEvent(Chatroom chatroom, ChatroomEventType type);
}
