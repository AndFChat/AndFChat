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
import com.andfchat.frontend.application.AndFChatApplication;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ChatroomManager {

    private final ArrayList<Chatroom> chats = new ArrayList<Chatroom>();
    private final ArrayList<Chatroom> removedChats = new ArrayList<Chatroom>();

    private boolean isChanged = false;
    private Chatroom activeChat;

    // List of channels
    private final Set<String> officialChannelSet = new HashSet<String>();
    private final Set<Channel> privateChannelSet = new HashSet<Channel>();

    @Inject
    public ChatroomManager() {
        initChats();
    }

    private void initChats() {
        addChatroom(new Chatroom(new Channel(AndFChatApplication.DEBUG_CHANNEL_NAME, AndFChatApplication.DEBUG_CHANNEL_NAME), ChatroomType.CONSOLE, 50000));
    }

    public void addBroadcast(ChatEntry entry) {
        synchronized(this) {
            for (Chatroom chat : chats) {
                chat.addMessage(entry);
            }
        }
        isChanged = true;
    }

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
            for (int i = 0; i < removedChats.size(); i++) {
                if (removedChats.get(i).equals(chatroom)) {
                    // Add the chat history to the rejoined chatroom
                    chatroom.setChatHistory(removedChats.remove(i).getChatEntries());
                }
            }
            chats.add(chatroom);
        }
        isChanged = true;
        return chatroom;
    }

    public void removeChatroom(String channelId) {
        synchronized(this) {
            for (int i = 0; i < chats.size(); i++) {
                if (chats.get(i).getId().equals(channelId)) {
                    removedChats.add(chats.get(i));
                    chats.remove(i);
                }
            }

            if (activeChat != null && activeChat.getId().equals(channelId)) {
                activeChat = null;
            }
        }
        isChanged = true;
    }

    public Chatroom getActiveChat() {
        synchronized(this) {
            return activeChat;
        }
    }

    public void setActiveChat(Chatroom chatroom) {
        synchronized(this) {
            Ln.d("Set active chat to '" + chatroom.getName() + "'");
            activeChat = chatroom;
            isChanged = true;
        }
    }

    public boolean hasOpenPrivateConversation(FlistChar flistChar) {
        for (Chatroom chat : chats) {
            if (chat.getRecipient() != null && chat.getRecipient().equals(flistChar)) {
                return true;
            }
        }

        return false;
    }

    public Chatroom getPrivateChatFor(FlistChar flistChar) {
        for (Chatroom chat : chats) {
            if (chat.getRecipient() != null && chat.getRecipient().equals(flistChar)) {
                return chat;
            }
        }

        return null;
    }

    public void removeFlistCharFromChat(FlistChar flistChar) {
        for (Chatroom Chatroom : chats) {
            if (!Chatroom.isPrivateChat()) {
                Chatroom.removeCharacter(flistChar);
            }
        }
    }

    public void addOfficialChannel(String name) {
        if (officialChannelSet.contains(name) == false) {
            officialChannelSet.add(name);
        }
    }

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
        this.removedChats.clear();
        this.chats.clear();
        this.officialChannelSet.clear();
        this.privateChannelSet.clear();

        initChats();
    }

    public List<Chatroom> getChatRooms() {
        return chats;
    }

    public boolean isChanged() {
        if (isChanged) {
            isChanged = false;
            return true;
        } else {
            return false;
        }
    }
}
