package com.andfchat.frontend.events;

import com.andfchat.core.data.Chatroom;

public interface ChatroomEventListner {

    public enum ChatroomEventType {
        ACTIVE,
        NEW,
        LEFT,
        NEW_MESSAGE,
    }

    public void onEvent(Chatroom chatroom, ChatroomEventType type);
}
