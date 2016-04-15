package com.andfchat.frontend.events;

public interface ConnectionEventListener {
    enum ConnectionEventType {
        CONNECTED,
        DISCONNECTED,
        CHAR_CONNECTED,
    }

    void onEvent(ConnectionEventType type);
}
