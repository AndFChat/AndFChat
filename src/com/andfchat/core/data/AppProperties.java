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

import android.app.Activity;
import android.content.SharedPreferences;

public class AppProperties {
    private final SharedPreferences settings;

    public static final String DEBUG_CHANNEL_NAME = "Debug Channel";

    public enum PropertyName {
        SHOW_CHATROOM_INFOS,
        SHOW_USER_STATUS_CHANGES,
        USE_DEBUG_CHANNEL
    }

    public AppProperties(Activity activity) {
        settings = activity.getPreferences(0);
    }

    public boolean getBooleanValue(PropertyName property) {
        return settings.getBoolean(property.name(), false);
    }

    public void setBooleanValue(PropertyName property, boolean value) {
        settings.edit().putBoolean(property.name(), value).commit();
    }
}
