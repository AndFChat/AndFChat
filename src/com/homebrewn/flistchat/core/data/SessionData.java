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


package com.homebrewn.flistchat.core.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;

import com.homebrewn.flistchat.core.connection.FlistWebSocketConnection;
import com.homebrewn.flistchat.core.data.AppProperties.PropertyName;
import com.homebrewn.flistchat.core.data.Chatroom.ChatroomType;

public class SessionData {

    private final String ticket;
    private final String account;

    private String characterName;

    private FlistWebSocketConnection connection;

    private final CharacterHandler characterHandler = new CharacterHandler();
    private final ChatroomHandler ChatroomHandler = new ChatroomHandler();

    private boolean isConnected = false;

    private final Set<String> officialChannelSet = new HashSet<String>();
    private final Set<Channel> privateChannelSet = new HashSet<Channel>();

    private final Set<String> friendList = new HashSet<String>();

    private final AppProperties appProperties;

    private final SessionSettings sessionSettings;

    public SessionData(String ticket, String account, Activity activity) {
        this.ticket = ticket;
        this.account = account;
        appProperties = new AppProperties(activity);

        sessionSettings = new SessionSettings(appProperties);
    }

    public void setCharname(String name) {
        characterName = name;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public CharacterHandler getCharHandler() {
        return characterHandler;
    }

    public ChatroomHandler getChatroomHandler() {
        return ChatroomHandler;
    }

    public String getAccount() {
        return account;
    }

    public String getTicket() {
        return ticket;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setConnection(FlistWebSocketConnection connection) {
        this.connection = connection;
    }

    public FlistWebSocketConnection getConnection() {
        return connection;
    }

    public void addOfficialChannel(String name) {
        if (officialChannelSet.contains(name) == false) {
            officialChannelSet.add(name);
        }
    }

    public Set<String> getOfficialChannels() {
        return officialChannelSet;
    }

    public void disconnect() {
        connection.closeConnection();
    }

    public SessionSettings getSessionSettings() {
        return sessionSettings;
    }

    public Set<FlistChar> getOnlineFriends() {
        Set<FlistChar> onlineFriends = new HashSet<FlistChar>();
        for (String username : friendList) {
            FlistChar flistChar = characterHandler.findCharacter(username, false);
            if (flistChar != null) {
                onlineFriends.add(flistChar);
            }
        }

        return onlineFriends;
    }

    public AppProperties getProperties() {
        return appProperties;
    }

    public class SessionSettings {
        private boolean useDebugChannel = true;
        private boolean showStatusChanges = true;
        private final boolean showOnlineOfflineChanges = true;

        public SessionSettings(AppProperties appProperties) {
            //this.setUseDebugChannel(appProperties.getBooleanValue(PropertyName.USE_DEBUG_CHANNEL));
            this.setUseDebugChannel(true);
            this.showStatuschanges(true);
        }

        public void setUseDebugChannel(boolean value) {
            if (value) {
                if (ChatroomHandler.getChatroom(AppProperties.DEBUG_CHANNEL_NAME) == null) {
                    ChatroomHandler.addChatroom(new Chatroom(new Channel(AppProperties.DEBUG_CHANNEL_NAME, AppProperties.DEBUG_CHANNEL_NAME), ChatroomType.SYSTEM));
                }
            } else {
                ChatroomHandler.removeChatroom(AppProperties.DEBUG_CHANNEL_NAME);
            }

            useDebugChannel = value;
            appProperties.setBooleanValue(PropertyName.USE_DEBUG_CHANNEL, value);
        }

        public boolean useDebugChannel() {
            return useDebugChannel;
        }

        public boolean showStatusChanges() {
            return showStatusChanges;
        }

        public void showStatuschanges(boolean value) {
            showStatusChanges = value;
            appProperties.setBooleanValue(PropertyName.SHOW_USER_STATUS_CHANGES, value);
        }

        public boolean showOnlineOfflineChanges() {
            return showOnlineOfflineChanges;
        }
    }

    public void addFriend(String charname) {
        friendList.add(charname);
    }

    public Set<String> getFriendList() {
        return Collections.unmodifiableSet(friendList);
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
}

