package com.andfchat.frontend.events;

import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.Chatroom;

public interface MessageEventListener {
    public void onEvent(ChatEntry entry, Chatroom chatroom);
}
