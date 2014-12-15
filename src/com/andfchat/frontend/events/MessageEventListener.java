package com.andfchat.frontend.events;

import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.messages.ChatEntry;

public interface MessageEventListener {
    public void onEvent(ChatEntry entry, Chatroom chatroom);
}
