package com.andfchat.frontend.events;

import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FCharacter;

public interface UserEventListener {
    enum UserEventType {
        JOINED,
        LEFT
    }

    void onEvent(FCharacter character, UserEventType type, Chatroom chatroom);
}
