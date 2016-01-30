/*******************************************************************************
 *     This file is part of AndFChat.
 *
 *     AndFChat is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     AndFChat is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with AndFChat.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


package com.andfchat.core.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roboguice.util.Ln;

import com.andfchat.core.data.Chatroom.ChatroomType;
import com.andfchat.core.data.history.HistoryManager;
import com.andfchat.core.data.messages.ChatEntry;
import com.andfchat.frontend.application.AndFChatApplication;
import com.andfchat.frontend.events.AndFChatEventManager;
import com.andfchat.frontend.events.ChatroomEventListener.ChatroomEventType;
import com.andfchat.frontend.events.UserEventListener.UserEventType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ChatroomManager {

    @Inject
    protected AndFChatEventManager eventManager;
    @Inject
    private HistoryManager historyManager;
    @Inject
    private SessionData sessionData;

    private final ArrayList<Chatroom> chats = new ArrayList<Chatroom>();
    private Chatroom activeChat;

    // List of channels
    private final Set<String> officialChannelSet = new HashSet<String>();
    private final Set<Channel> privateChannelSet = new HashSet<Channel>();

    // Adds a chat message to every channel
    public void addBroadcast(ChatEntry entry) {
        synchronized(this) {
            for (Chatroom chat : chats) {
                chat.addMessage(entry);
            }
        }

        if (activeChat != null) {
            eventManager.fire(entry, activeChat);
        }
    }

    // Returns a existing chat or null.
    public Chatroom getChatroom(String channelId) {
        for (Chatroom chat : chats) {
            if (chat.getId().equals(channelId)) {
                return chat;
            }
        }
        return null;
    }

    public Chatroom addChatroom(Chatroom chatroom) {
        synchronized(this) {
            Ln.d("Add chatroom '" + chatroom.getName() + "'");
            // Only load history for channel/pm's
            if (chatroom.isSystemChat() == false) {
                // HistoryManager loads data via the "channel" key.
                chatroom.setChatHistory(historyManager.loadHistory(chatroom.getChannel()));
            }
            chats.add(chatroom);
            if (chats.size() == 1) {
                setActiveChat(chatroom);
            }
        }
        return chatroom;
    }

    public void removeChatroom(Channel channel) {
        synchronized(this) {
            for (int i = chats.size() - 1; i >= 0; i--) {
                Chatroom chatRoom = chats.get(i);
                if  (chatRoom.isChannel(channel)) {
                    chats.remove(i);
                    break;
                }
            }
            // if active chat is remove we have to them to null again.
            if (activeChat != null && activeChat.isChannel(channel)) {
                activeChat = null;
            }

        }
    }

    public Chatroom getActiveChat() {
        synchronized(this) {
            return activeChat;
        }
    }

    public void setActiveChat(Chatroom chatroom) {
        synchronized(this) {
            Ln.d("Set active chat to '" + chatroom.getName() + "'");
            // old active chat has no new messages
            if (activeChat != null) {
                activeChat.setHasNewMessage(false);
            }

            activeChat = chatroom;
            activeChat.setHasNewMessage(false);
            // Inform about active chat change
            eventManager.fire(chatroom, ChatroomEventType.ACTIVE);
        }
    }

    public void addMessage(Chatroom chatroom, ChatEntry entry) {
        if (chatroom == null) {
            Ln.e("Cant find chatroom, is null");
            return;
        }

        chatroom.addMessage(entry);
        eventManager.fire(entry, chatroom);

        entry.setOwned(sessionData.isUser(entry.getOwner()));

        if (chatroom.hasNewMessage() == false && isActiveChat(chatroom) == false) {
            chatroom.setHasNewMessage(true);
            eventManager.fire(chatroom, ChatroomEventType.NEW_MESSAGE);
        }
    }

    public boolean hasOpenPrivateConversation(FCharacter flistChar) {
        for (Chatroom chat : chats) {
            if (chat.getRecipient() != null && chat.getRecipient().equals(flistChar)) {
                return true;
            }
        }

        return false;
    }

    public Chatroom getPrivateChatFor(FCharacter flistChar) {
        for (Chatroom chat : chats) {
            if (chat.getRecipient() != null && chat.getRecipient().equals(flistChar)) {
                return chat;
            }
        }

        return null;
    }

    public void removeFlistCharFromChat(FCharacter character) {
        for (Chatroom chatroom : chats) {
            if (!chatroom.isPrivateChat() && chatroom.getCharacters().contains(character)) {
                chatroom.removeCharacter(character);
                eventManager.fire(character, UserEventType.LEFT, chatroom);
            }
        }
    }

    public void addOfficialChannel(String name) {
        if (officialChannelSet.contains(name) == false) {
            officialChannelSet.add(name);
        }
    }

    // Gives the saved list of official channels.
    public Set<String> getOfficialChannels() {
        return officialChannelSet;
    }

    public void clearPrivateChannels() {
        privateChannelSet.clear();
    }

    public void addPrivateChannel(Channel channel) {
        privateChannelSet.add(channel);
    }

    public Set<String> getPrivateChannelNames() {
        Set<String> names = new HashSet<String>();

        for (Channel channel : privateChannelSet) {
            names.add(channel.getChannelName());
        }
        return names;
    }

    public Channel getPrivateChannelByName(String channelName) {
        for (Channel channel : privateChannelSet) {
            if (channel.getChannelName().equals(channelName)) {
                return channel;
            }
        }

        return null;
    }

    public Channel getPrivateChannelById(String channelId) {
        for (Channel channel : privateChannelSet) {
            if (channel.getChannelId().equals(channelId)) {
                return channel;
            }
        }

        return null;
    }

    public void clear() {
        this.activeChat = null;
        this.chats.clear();
        this.officialChannelSet.clear();
        this.privateChannelSet.clear();
    }

    public List<Chatroom> getChatRooms() {
        return chats;
    }

    public boolean isActiveChat(Chatroom chatroom) {
        return activeChat != null && chatroom.getId().equals(activeChat.getId());
    }
}
