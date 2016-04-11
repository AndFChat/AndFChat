package com.andfchat.frontend.events;

import com.andfchat.core.data.Chatroom;

public interface ConnectionEventListener {
    enum ConnectionEventType {
        CONNECTED,
        DISCONNECTED,
        CHAR_CONNECTED,
    }

    void onEvent(ConnectionEventType type);
}
