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

import android.app.Activity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.homebrewn.flistchat.core.data.AppProperties.PropertyName;
import com.homebrewn.flistchat.core.data.Chatroom.ChatroomType;

@Singleton
public class SessionData {

    @Inject
    protected ChatroomManager chatroomManager;

    private String ticket;
    private String account;
    private String characterName;

    private AppProperties appProperties;
    private SessionSettings sessionSettings;

    public void initSession(String ticket, String account, Activity activity) {
        this.ticket = ticket;
        this.account = account;

        appProperties = new AppProperties(activity);
        sessionSettings = new SessionSettings(appProperties);
    }

    public void setCharname(String name) {
        characterName = name;
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

    public SessionSettings getSessionSettings() {
        return sessionSettings;
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
                if (chatroomManager.getChatroom(AppProperties.DEBUG_CHANNEL_NAME) == null) {
                    chatroomManager.addChatroom(new Chatroom(new Channel(AppProperties.DEBUG_CHANNEL_NAME, AppProperties.DEBUG_CHANNEL_NAME), ChatroomType.SYSTEM));
                }
            } else {
                chatroomManager.removeChatroom(AppProperties.DEBUG_CHANNEL_NAME);
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
}

