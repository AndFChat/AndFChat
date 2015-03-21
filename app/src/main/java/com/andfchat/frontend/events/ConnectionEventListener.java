package com.andfchat.frontend.events;

import com.andfchat.core.data.Chatroom;

public interface ConnectionEventListener {
    public enum ConnectionEventType {
        CONNECTED,
        DISCONNECTED,
        CHAR_CONNECTED,
    }

    public void onEvent(ConnectionEventType type);
}
