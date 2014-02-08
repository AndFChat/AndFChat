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

import java.util.HashMap;

import android.app.Activity;

import com.andfchat.core.connection.handler.VariableHandler.Variable;
import com.andfchat.core.data.AppProperties.PropertyName;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SessionData {

    @Inject
    protected ChatroomManager chatroomManager;

    private String ticket;
    private String account;
    private String characterName;

    private AppProperties appProperties;
    private SessionSettings sessionSettings;

    private final HashMap<Variable, Integer> intVariables = new HashMap<Variable, Integer>();

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

    public void clear() {
        ticket = null;
        account = null;
        characterName = null;

        intVariables.clear();
    }

    public int getIntVariable(Variable variable) {
        return intVariables.get(variable);
    }

    public void setVariable(Variable variable, int value) {
        intVariables.put(variable, value);
    }

    public class SessionSettings {
        private boolean useDebugChannel = false;
        private boolean showStatusChanges = true;
        private boolean showChannelInfo = false;

        public SessionSettings(AppProperties appProperties) {
            if (appProperties.getBooleanValue(PropertyName.IS_INITIATED)) {
                this.setUseDebugChannel(appProperties.getBooleanValue(PropertyName.USE_DEBUG_CHANNEL));
                this.setShowStatusChanges(appProperties.getBooleanValue(PropertyName.SHOW_USER_STATUS_CHANGES));
                this.setShowChannelInfo(appProperties.getBooleanValue(PropertyName.SHOW_CHANNEL_INFOS));
            } else {
                this.setUseDebugChannel(false);
                this.setShowChannelInfo(false);
                this.setShowStatusChanges(true);

                appProperties.setBooleanValue(PropertyName.IS_INITIATED, true);
            }
        }

        public void setUseDebugChannel(boolean value) {
            useDebugChannel = value;
            appProperties.setBooleanValue(PropertyName.USE_DEBUG_CHANNEL, value);
        }

        public boolean useDebugChannel() {
            return useDebugChannel;
        }

        public boolean showStatusChanges() {
            return showStatusChanges;
        }

        public void setShowStatusChanges(boolean value) {
            showStatusChanges = value;
            appProperties.setBooleanValue(PropertyName.SHOW_USER_STATUS_CHANGES, value);
        }

        public boolean showChannelInfos() {
            return showChannelInfo;
        }

        public void setShowChannelInfo(boolean value) {
            showChannelInfo = value;
            appProperties.setBooleanValue(PropertyName.SHOW_CHANNEL_INFOS, value);
        }
    }
}

