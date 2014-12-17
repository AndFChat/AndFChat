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

import android.text.Spannable;

import com.andfchat.core.data.messages.ChatEntry;

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
    private final int maxTextLength;

    private List<ChatEntry> chatMessages;
    private final List<FCharacter> characters = new ArrayList<FCharacter>();

    private Spannable description;

    private boolean hasNewMessage = false;

    private String entry;

    public Chatroom(Channel channel, int maxTextLength) {
        this.channel = channel;
        this.maxTextLength = maxTextLength;
        this.chatMessages = new ArrayList<ChatEntry>(channel.getType().maxEntries);
    }

    public Chatroom(Channel channel, FCharacter character, int maxTextLength) {
        this.channel = channel;
        this.characters.add(character);
        this.maxTextLength = maxTextLength;
        this.chatMessages = new ArrayList<ChatEntry>(channel.getType().maxEntries);
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

    /**
     * Maximum entries which are displayed.
     */
    public int getMaxiumEntries() {
        return channel.getType().maxEntries;
    }

    public boolean isPrivateChat() {
        return channel.getType() == ChatroomType.PRIVATE_CHAT;
    }

    /**
     * Chat can be closed by user.
     */
    public boolean isCloseable() {
        return channel.getType().closeable;
    }

    /**
     * Chat should the user list be displayed.
     */
    public boolean showUserList() {
        return channel.getType().showUserList;
    }

    public boolean isChannel() {
        return channel.getType() == ChatroomType.PUBLIC_CHANNEL || channel.getType() == ChatroomType.PRIVATE_CHANNEL;
    }

    public boolean hasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean value) {
        hasNewMessage = value;
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

    protected void addMessage(ChatEntry entry) {
        chatMessages.add(entry);
    }

    /**
     * Get the text input by user.
     */
    public String getEntry() {
        return entry;
    }

    /**
     * Set the text input by user.
     */
    public void setEntry(String entry) {
        this.entry = entry;
    }

    public void addCharacter(FCharacter flistChar) {
        if (!characters.contains(flistChar)) {
            characters.add(flistChar);
        }
    }

    public void removeCharacter(FCharacter flistChar) {
        characters.remove(flistChar);
    }

    public List<FCharacter> getCharacters() {
        return characters;
    }

    public List<ChatEntry> getChatEntriesSince(long time) {
        List<ChatEntry> messages = new ArrayList<ChatEntry>();

        int lastDisplayedMessagePosition = Math.max(0, chatMessages.size() - getMaxiumEntries());

        for (int i = chatMessages.size() - 1; i >= lastDisplayedMessagePosition; i--) {
            if (chatMessages.get(i).getDate().getTime() > time) {
                messages.add(chatMessages.get(i));
            } else {
                break;
            }
        }

        return messages;
    }

    public FCharacter getRecipient() {
        if (characters.size() == 1) {
            return characters.get(0);
        }
        return null;
    }

    public boolean isSystemChat() {
        return this.channel.getType() == ChatroomType.CONSOLE;
    }

    public List<ChatEntry> getChatHistory() {
        return chatMessages;
    }

    public void setChatHistory(List<ChatEntry> chatHistory) {
        chatMessages = chatHistory;
    }

    public ChatroomType getChatroomType() {
        return channel.getType();
    }

    public int getMaxTextLength() {
        return maxTextLength;
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isChannel(Channel channel) {
        return this.channel.equals(channel);
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
