package com.andfchat.frontend.events;

import java.util.ArrayList;
import java.util.List;

import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FCharacter;
import com.andfchat.frontend.events.ChatroomEventListener.ChatroomEventType;
import com.andfchat.frontend.events.UserEventListener.UserEventType;
import com.google.inject.Singleton;

@Singleton
public class AndFChatEventManager {

    private final List<ChatroomEventListener> chatroomEventListner = new ArrayList<ChatroomEventListener>();
    private final List<MessageEventListener> messageEventListner = new ArrayList<MessageEventListener>();
    private final List<UserEventListener> userEventListner = new ArrayList<UserEventListener>();

    public synchronized void register(ChatroomEventListener listner) {
        chatroomEventListner.add(listner);
    }

    public synchronized void register(MessageEventListener listner) {
        messageEventListner.add(listner);
    }

    public synchronized void register(UserEventListener listner) {
        userEventListner.add(listner);
    }

    public synchronized void fire(Chatroom chatroom, ChatroomEventType type) {
        for (ChatroomEventListener listner : chatroomEventListner) {
            listner.onEvent(chatroom, type);
        }
    }

    public synchronized void fire(ChatEntry entry, Chatroom chatroom) {
        for (MessageEventListener listner : messageEventListner) {
            listner.onEvent(entry, chatroom);
        }
    }

    public synchronized void fire(FCharacter character, UserEventType type, Chatroom chatroom) {
        for (UserEventListener listner : userEventListner) {
            listner.onEvent(character, type, chatroom);
        }
    }

    public void clear() {
        chatroomEventListner.clear();
        messageEventListner.clear();
        userEventListner.clear();
    }

}
