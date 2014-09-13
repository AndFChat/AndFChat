package com.andfchat.frontend.events;

import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FCharacter;

public interface UserEventListener {
    public enum UserEventType {
        JOINED,
        LEFT
    }

    public void onEvent(FCharacter character, UserEventType type, Chatroom chatroom);
}
