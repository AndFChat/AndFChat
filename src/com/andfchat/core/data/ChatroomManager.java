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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roboguice.util.Ln;

import com.andfchat.core.connection.handler.PrivateMessageHandler;
import com.andfchat.core.data.Chatroom.ChatroomType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ChatroomManager {

    private final HashMap<String, Chatroom> chats = new HashMap<String, Chatroom>();

    private Chatroom activeChat;

    private final List<Chatroom> newRooms = new ArrayList<Chatroom>();
    private final List<Chatroom> removedRooms = new ArrayList<Chatroom>();

    private final HashMap<String, List<ChatEntry>> chatRoomHistory = new HashMap<String, List<ChatEntry>>();

    // List of channels
    private final Set<String> officialChannelSet = new HashSet<String>();
    private final Set<Channel> privateChannelSet = new HashSet<Channel>();

    @Inject
    public ChatroomManager() {
        initChats();
    }

    private void initChats() {
        this.addChatroom(new Chatroom(new Channel(ChatroomType.CONSOLE.name(), "Console"), ChatroomType.CONSOLE));
    }

    public void addChatEntry(String Chatroom, ChatEntry entry) {
        chats.get(Chatroom).addMessage(entry);
    }

    public void addBroadcast(ChatEntry entry) {
        synchronized(this) {
            for (String key : chats.keySet()) {
                chats.get(key).addMessage(entry);
            }
        }
    }

    public List<String> getChatroomKeys() {
        return new ArrayList<String>(chats.keySet());
    }

    public Chatroom getChatroom(String channelId) {
        return chats.get(channelId);
    }

    public Chatroom addChatroom(Chatroom chatroom) {
        synchronized(this) {
            if (!chats.containsKey(chatroom.getId())) {
                chats.put(chatroom.getId(), chatroom);
                newRooms.add(chatroom);
                removedRooms.remove(chatroom);
            }
        }

        // Handle chat history
        if (chatRoomHistory.containsKey(chatroom.getId())) {
            chatroom.setChatHistory(chatRoomHistory.get(chatroom.getId()));
        } else {
            chatRoomHistory.put(chatroom.getId(), chatroom.getChatHistory());
        }

        return chats.get(chatroom.getId());
    }

    public void removeChatroom(String channelId) {
        synchronized(this) {
            Chatroom Chatroom = chats.remove(channelId);
            if (Chatroom != null) {
                removedRooms.add(Chatroom);
                newRooms.remove(Chatroom);
            }

            if (activeChat != null && activeChat.getId().equals(channelId)) {
                activeChat = null;
            }
        }
    }

    public List<Chatroom> getNewRooms() {
        return newRooms;
    }

    public void clearNewRooms() {
        newRooms.clear();
    }

    public List<Chatroom> getRemovedRooms() {
        return removedRooms;
    }

    public void clearRemovedRooms() {
        removedRooms.clear();
    }

    public Chatroom getActiveChat() {
        return activeChat;
    }

    public void setActiveChat(Chatroom chatroom) {
        Ln.d("Set active chat to '" + chatroom.getName() + "'");
        activeChat = chatroom;
    }

    public boolean hasOpenPrivateConversation(FlistChar flistChar) {
        return chats.containsKey(PrivateMessageHandler.PRIVATE_MESSAGE_TOKEN + flistChar.getName());
    }

    public Chatroom getPrivateChatFor(FlistChar flistChar) {
        return chats.get(PrivateMessageHandler.PRIVATE_MESSAGE_TOKEN + flistChar.getName());
    }

    public void removeFlistCharFromChat(FlistChar flistChar) {
        for (Chatroom Chatroom : chats.values()) {
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
        this.chatRoomHistory.clear();
        this.chats.clear();
        this.newRooms.clear();
        this.removedRooms.clear();
        this.officialChannelSet.clear();
        this.privateChannelSet.clear();

        initChats();
    }
}
