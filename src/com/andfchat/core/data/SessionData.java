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

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.andfchat.R;
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
    private boolean isInChat = false;

    private SessionSettings sessionSettings;

    private final HashMap<Variable, Integer> intVariables = new HashMap<Variable, Integer>();

    @Inject
    public void SessionData(Context context) {

        sessionSettings = new SessionSettings(context);
    }

    public void initSession(String ticket, String account) {
        this.ticket = ticket;
        this.account = account;
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

    public boolean isUser(FCharacter character) {
        return character.getName().equals(characterName);
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

    public boolean isInChat() {
        return isInChat;
    }

    public void setIsInChat(boolean value) {
        isInChat = value;
    }

    public void clear() {
        ticket = null;
        account = null;

        isVisible = true;
        isInChat = false;

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

        public SessionSettings(Context context) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        public boolean useHistory() {
            return preferences.getBoolean(PropertyName.LOG_HISTORY.name().toLowerCase(), true);
        }
        public boolean logChannel() {
            return preferences.getBoolean(PropertyName.LOG_CHANNEL.name().toLowerCase(), true);
        }

        public int getTheme() {
            String theme = preferences.getString(PropertyName.THEME.name().toLowerCase(), "AppTheme.Blue");

            if (theme.equals("AppTheme")) {
                return R.style.AppTheme;
            }
            else if (theme.equals("AppTheme.Blue")) {
                return R.style.AppTheme_Blue;
            }
            else {
                return R.style.AppTheme;
            }
        }
    }
}

