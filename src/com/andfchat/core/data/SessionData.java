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
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.andfchat.core.connection.handler.VariableHandler.Variable;
import com.andfchat.frontend.application.AndFChatApplication;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SessionData {

    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected NotificationManager notificationManager;

    private String ticket;
    private String account;
    private String characterName;

    private boolean isVisible = false;

    private SessionSettings sessionSettings;

    private final HashMap<Variable, Integer> intVariables = new HashMap<Variable, Integer>();

    public void initSession(String ticket, String account, Activity activity) {
        this.ticket = ticket;
        this.account = account;

        sessionSettings = new SessionSettings(activity);
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

    public void setIsVisible(boolean value) {
        isVisible = value;
        if (isVisible) {
            notificationManager.cancel(AndFChatApplication.LED_NOTIFICATION_ID);
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void clear() {
        ticket = null;
        account = null;
        characterName = null;

        isVisible = true;

        intVariables.clear();
    }

    public int getIntVariable(Variable variable) {
        return intVariables.get(variable);
    }

    public void setVariable(Variable variable, int value) {
        intVariables.put(variable, value);
    }

    public class SessionSettings {
        private final SharedPreferences preferences;

        public SessionSettings(Activity activity) {
            preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }
        public boolean useDebugChannel() {
            return preferences.getBoolean(PropertyName.USE_DEBUG_CHANNEL.name().toLowerCase(), false);
        }

        public boolean showStatusChanges() {
            return preferences.getBoolean(PropertyName.SHOW_USER_STATUS_CHANGES.name().toLowerCase(), false);
        }
        public boolean showChannelInfos() {
            return preferences.getBoolean(PropertyName.SHOW_CHANNEL_INFOS.name().toLowerCase(), false);
        }

        public boolean vibrationFeedback() {
            return preferences.getBoolean(PropertyName.VIBRATION_FEEDBACK.name().toLowerCase(), false);
        }

        public boolean ledFeedback() {
            return preferences.getBoolean(PropertyName.LED_FEEDBACK.name().toLowerCase(), false);
        }

        public String getInitialChannel() {
            return preferences.getString(PropertyName.INITIAL_CHANNEL.name().toLowerCase(), null);
        }
    }
}

