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
import java.util.List;
import java.util.Set;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.andfchat.R;
import com.andfchat.core.connection.handler.VariableHandler.Variable;
import com.andfchat.core.util.Version;
import com.andfchat.frontend.application.AndFChatNotification;
import com.andfchat.frontend.util.TextSize;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SessionData {

    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected NotificationManager notificationManager;
    @Inject
    protected AndFChatNotification notification;

    private String ticket;
    private String account;
    private String characterName;
    private String password;

    private List<String> charList;
    private String defaultChar;

    private boolean isVisible = false;
    private boolean isInChat = false;

    private int messages = 0;

    private final SessionSettings sessionSettings;

    private final HashMap<Variable, Integer> intVariables = new HashMap<Variable, Integer>();
    private String disconnectReason;
    private boolean disconnected;

    @Inject
    public SessionData(Context context) {
        sessionSettings = new SessionSettings(context);
    }

    public void initSession(String ticket, String account, String password) {
        this.ticket = ticket;
        this.account = account;
        this.password = password;
    }

    public void setCharname(String name) {
        characterName = name;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getCharacterName() {
        return characterName;
    }

    public List<String> getCharList() {
        return charList;
    }

    public void setCharList(List<String> charList) {
        this.charList = charList;
    }

    public String getDefaultChar() {
        return defaultChar;
    }

    public void setDefaultChar(String defaultChar) {
        this.defaultChar = defaultChar;
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
            messages = 0;
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
        isVisible = true;
        isInChat = false;

        intVariables.clear();
    }

    public void clearAll() {
        clear();

        ticket = null;
        account = null;
    }

    public int getIntVariable(Variable variable) {
        return intVariables.get(variable);
    }

    public void setVariable(Variable variable, int value) {
        intVariables.put(variable, value);
    }

    public void setDisconnectReason(String disconnectReason) {
        this.disconnectReason = disconnectReason;
    }

    public String getDisconnectReason() {
        return this.disconnectReason;
    }

    public int addMessage() {
        return ++messages;
    }

    public class SessionSettings {
        private final SharedPreferences preferences;

        public SessionSettings(Context context) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        public boolean useDebugChannel() {
            return preferences.getBoolean(PropertyName.USE_DEBUG_CHANNEL.name().toLowerCase(), false);
        }

        public boolean showAvatarPictures() {
            return preferences.getBoolean(PropertyName.SHOW_AVATAR_PICTURES.name().toLowerCase(), true);
        }

        public boolean showStatusChanges() {
            return preferences.getBoolean(PropertyName.SHOW_USER_STATUS_CHANGES.name().toLowerCase(), false);
        }
        public boolean showChannelInfo() {
            return preferences.getBoolean(PropertyName.SHOW_CHANNEL_INFO.name().toLowerCase(), false);
        }

        public boolean vibrationFeedback() {
            return preferences.getBoolean(PropertyName.VIBRATION_FEEDBACK.name().toLowerCase(), false);
        }

        public boolean audioFeedback() {
            return preferences.getBoolean(PropertyName.AUDIO_FEEDBACK.name().toLowerCase(), false);
        }

        public Set<String> getInitialChannel() {
            return preferences.getStringSet(PropertyName.INITIAL_CHANNELS.name().toLowerCase(), null);
        }

        public Set<String> getInitialPrivateChannel() {
            return preferences.getStringSet(PropertyName.INITIAL_PRIVATE_CHANNELS.name().toLowerCase(), null);
        }

        public boolean useHistory() {
            return preferences.getBoolean(PropertyName.LOG_HISTORY.name().toLowerCase(), true);
        }

        public boolean logChannel() {
            return preferences.getBoolean(PropertyName.LOG_CHANNEL.name().toLowerCase(), true);
        }

        public boolean showNotifications() {
            return preferences.getBoolean(PropertyName.SHOW_NOTIFICATIONS.name().toLowerCase(), true);
        }

        public boolean separateFriends() {
            return preferences.getBoolean(PropertyName.SEPARATE_FRIENDS.name().toLowerCase(), true);
        }

        public int getTheme() {
            String theme = preferences.getString(PropertyName.THEME.name().toLowerCase(), "AppTheme");

            switch (theme) {
                case "AppTheme":
                    return R.style.AppTheme;
                case "AppTheme.Blue":
                    return R.style.AppTheme_Blue;
                case "AppTheme.Light":
                    return R.style.AppTheme_Light;
                default:
                    return R.style.AppTheme;
            }
        }

        public TextSize getChatTextSize() {
            return TextSize.valueOf(preferences.getString(PropertyName.CHAT_TEXT_SIZE.name().toLowerCase(), "medium"));
        }

        public Version getVersion() {
            return new Version(preferences.getString(PropertyName.VERSION.name().toLowerCase(), "0.6.0"));
        }

        public void setVersion(String version) {
            preferences.edit().putString(PropertyName.VERSION.name().toLowerCase(), version).apply();
        }
    }
}

