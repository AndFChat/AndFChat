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

/**
 * Do not change the enum, used for comparing with strings.
 * @author AndFChat
 */
public enum CharStatus {
    online,
    looking,
    busy,
    dnd,
    away,
    idle(false),
    crown(false);

    private boolean allowedToSet = true;

    private CharStatus() {}

    private CharStatus(boolean isAllowedToSet) {
        allowedToSet = isAllowedToSet;
    }

    public boolean isAllowedToSet() {
        return allowedToSet;
    }
}
