package com.andfchat.frontend.events;

import java.util.ArrayList;
import java.util.List;

import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FCharacter;
import com.andfchat.frontend.events.ChatroomEventListner.ChatroomEventType;
import com.google.inject.Singleton;

@Singleton
public class AndFChatEventManager {

    private final List<ChatroomEventListner> chatroomEventListner = new ArrayList<ChatroomEventListner>();
    private final List<MessageEventListner> messageEventListner = new ArrayList<MessageEventListner>();
    private final List<UserEventListner> userEventListner = new ArrayList<UserEventListner>();

    public synchronized void register(ChatroomEventListner listner) {
        chatroomEventListner.add(listner);
    }

    public synchronized void register(MessageEventListner listner) {
        messageEventListner.add(listner);
    }

    public synchronized void register(UserEventListner listner) {
        userEventListner.add(listner);
    }

    public synchronized void fire(Chatroom chatroom, ChatroomEventType type) {
        for (ChatroomEventListner listner : chatroomEventListner) {
            listner.onEvent(chatroom, type);
        }
    }

    public synchronized void fire(ChatEntry entry, Chatroom chatroom) {
        for (MessageEventListner listner : messageEventListner) {
            listner.onEvent(entry, chatroom);
        }
    }

    public synchronized void fire(FCharacter character, Chatroom chatroom) {
        for (UserEventListner listner : userEventListner) {
            listner.onEvent(character, chatroom);
        }
    }

    public void clear() {
        chatroomEventListner.clear();
        messageEventListner.clear();
        userEventListner.clear();
    }

}
