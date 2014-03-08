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
import java.util.Date;
import java.util.List;

import roboguice.util.Ln;
import android.text.Spannable;

public class Chatroom {

    public enum ChatroomType {
        PUBLIC_CHANNEL(true, true, 80),
        PRIVATE_CHANNEL(true, true, 60),
        PRIVATE_CHAT(true, false, 40),
        CONSOLE(false, false, 100);

        public final boolean closeable;
        public final boolean showUserList;
        public final int maxEntries;

        ChatroomType(boolean closeable, boolean showUserList, int maxEntries) {
            this.closeable = closeable;
            this.showUserList = showUserList;
            this.maxEntries = maxEntries;
        }
    }

    private final Channel channel;
    private final ChatroomType chatroomType;
    private final int maxTextLength;

    private List<ChatEntry> chatMessages;
    private final List<FlistChar> characters = new ArrayList<FlistChar>();

    private Spannable description;

    private boolean hasNewMessage = false;
    private boolean hasChangedUser = false;
    private boolean hasChanged = false;


    public Chatroom(Channel channel, ChatroomType type, int maxTextLength) {
        this.channel = channel;
        this.chatroomType = type;
        this.maxTextLength = maxTextLength;

        this.chatMessages = new ArrayList<ChatEntry>(chatroomType.maxEntries);
    }

    public Chatroom(Channel channel, FlistChar character, int maxTextLength) {
        this.chatroomType = ChatroomType.PRIVATE_CHAT;
        this.channel = channel;
        this.characters.add(character);
        this.maxTextLength = maxTextLength;

        this.chatMessages = new ArrayList<ChatEntry>(chatroomType.maxEntries);
    }

    public void setDescription(Spannable description) {
        this.description = description;
    }

    public Spannable getDescription() {
        return description;
    }

    public String getName() {
        return channel.getChannelName();
    }

    public String getId() {
        return channel.getChannelId();
    }

    public int getMaxiumEntries() {
        return chatroomType.maxEntries;
    }

    public boolean isPrivateChat() {
        return chatroomType == ChatroomType.PRIVATE_CHAT;
    }

    public boolean isCloseable() {
        return chatroomType.closeable;
    }

    public boolean showUserList() {
        return chatroomType.showUserList;
    }

    public boolean hasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean value) {
        hasNewMessage = value;
        if (value) {
            hasChanged = true;
        } else {
            hasChanged = true;
        }
    }

    public boolean hasChanged() {
        if (hasChanged) {
            hasChanged = false;
            return true;
        }
        else {
            return false;
        }
    }

    public List<ChatEntry> getLastMessages(int amount) {
        List<ChatEntry> lastMessages = new ArrayList<ChatEntry>(amount);

        int startPosition = 0;
        if (chatMessages.size() > amount) {
            startPosition = chatMessages.size() - amount;
        }

        for (int i = startPosition; i < chatMessages.size(); i++) {
            lastMessages.add(chatMessages.get(i));
        }

        return lastMessages;
    }

    public boolean chatChangedSince(Date date) {
        return chatMessages.get(chatMessages.size() - 1).getDate().after(date);
    }

    public void addMessage(ChatEntry entry) {
        if (chatMessages.size() < chatroomType.maxEntries) {
            chatMessages.add(entry);
        } else {
            chatMessages.remove(0);
            chatMessages.add(entry);
        }
    }

    public void addCharacter(FlistChar flistChar) {
        if (!characters.contains(flistChar)) {
            characters.add(flistChar);
            hasChangedUser = true;
        }
    }

    public void removeCharacter(FlistChar flistChar) {
        if (characters.remove(flistChar) == true) {
            hasChangedUser = true;
        }
    }

    public List<FlistChar> getCharacters() {
        return characters;
    }

    public void addMessage(String message, FlistChar character, Date date) {
        Ln.d("NEW MESSAGE: " + message + " FROM " + character.getName());
        this.addMessage(new ChatEntry(message, character, date, ChatEntryType.MESSAGE));
    }

    public List<ChatEntry> getChatEntries() {
        return chatMessages;
    }

    public List<ChatEntry> getChatEntriesSince(long time) {
        List<ChatEntry> messages = new ArrayList<ChatEntry>();

        for (int i = chatMessages.size() - 1; i >= 0; i--) {
            if (chatMessages.get(i).getDate().getTime() > time) {
                messages.add(chatMessages.get(i));
            } else {
                break;
            }
        }

        return messages;
    }

    public FlistChar getRecipient() {
        if (characters.size() == 1) {
            return characters.get(0);
        }
        return null;
    }

    public boolean isSystemChat() {
        return this.chatroomType == ChatroomType.CONSOLE;
    }

    public List<ChatEntry> getChatHistory() {
        return chatMessages;
    }

    public void setChatHistory(List<ChatEntry> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public boolean hasChangedUser() {
        if (hasChangedUser) {
            hasChangedUser = false;
            return true;
        } else {
            return false;
        }
    }

    public ChatroomType getChatroomType() {
        return chatroomType;
    }

    public int getMaxTextLength() {
        return maxTextLength;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Chatroom other = (Chatroom) obj;
        if (channel == null) {
            if (other.channel != null)
                return false;
        } else if (!channel.equals(other.channel))
            return false;
        return true;
    }
}
