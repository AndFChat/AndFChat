package com.andfchat.frontend.events;

import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FCharacter;

public interface UserEventListner {
    public void onEvent(FCharacter character, Chatroom chatroom);
}
