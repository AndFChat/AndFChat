package com.andfchat.frontend.events;

import java.util.ArrayList;
import java.util.List;

import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.messages.ChatEntry;
import com.andfchat.frontend.events.ChatroomEventListener.ChatroomEventType;
import com.andfchat.frontend.events.UserEventListener.UserEventType;
import com.google.inject.Singleton;

@Singleton
public class AndFChatEventManager {

    private final List<ChatroomEventListener> chatroomEventListener = new ArrayList<ChatroomEventListener>();
    private final List<MessageEventListener> messageEventListener = new ArrayList<MessageEventListener>();
    private final List<UserEventListener> userEventListener = new ArrayList<UserEventListener>();
    private final List<ConnectionEventListener> connectionEventListener = new ArrayList<>();

    public synchronized void register(ChatroomEventListener listener) {
        chatroomEventListener.add(listener);
    }

    public synchronized void register(MessageEventListener listener) {
        messageEventListener.add(listener);
    }

    public synchronized void register(ConnectionEventListener listener) {
        connectionEventListener.add(listener);
    }

    public synchronized void register(UserEventListener listener) {
        userEventListener.add(listener);
    }

    public synchronized void fire(Chatroom chatroom, ChatroomEventType type) {
        for (ChatroomEventListener listener : chatroomEventListener) {
            listener.onEvent(chatroom, type);
        }
    }

    public synchronized void fire(ChatEntry entry, Chatroom chatroom) {
        for (MessageEventListener listener : messageEventListener) {
            listener.onEvent(entry, chatroom);
        }
    }

    public synchronized void fire(FCharacter character, UserEventType type, Chatroom chatroom) {
        for (UserEventListener listener : userEventListener) {
            listener.onEvent(character, type, chatroom);
        }
    }

    public synchronized void fire(ConnectionEventListener.ConnectionEventType type) {
        for (ConnectionEventListener listener : connectionEventListener) {
            listener.onEvent(type);
        }
    }

    public void clear() {
        chatroomEventListener.clear();
        messageEventListener.clear();
        userEventListener.clear();
        connectionEventListener.clear();
    }
}
