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

import java.util.Locale;

/**
 * Do not change the enum, used for comparing with strings.
 * @author AndFChat
 */
public enum CharStatus {
    ONLINE,
    LOOKING,
    BUSY,
    DND,
    AWAY,
    IDLE(false),
    CROWN(false);

    private boolean allowedToSet = true;

    CharStatus() {}

    CharStatus(boolean isAllowedToSet) {
        allowedToSet = isAllowedToSet;
    }

    public boolean isAllowedToSet() {
        return allowedToSet;
    }

    public static CharStatus findStatus(String statusText) {
        return CharStatus.valueOf(statusText.toUpperCase(Locale.ENGLISH).trim());
    }

}
