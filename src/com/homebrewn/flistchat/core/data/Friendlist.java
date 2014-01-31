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

import java.util.HashSet;
import java.util.Set;

public class Friendlist {

    private final CharacterManager characterManager;

    public Friendlist(CharacterManager characterManager) {
        this.characterManager = characterManager;
    }

    private final Set<String> friendList = new HashSet<String>();

    public Set<FlistChar> getOnlineFriends() {
        Set<FlistChar> onlineFriends = new HashSet<FlistChar>();
        for (String username : friendList) {
            FlistChar flistChar = characterManager.findCharacter(username, false);
            if (flistChar != null) {
                onlineFriends.add(flistChar);
            }
        }

        return onlineFriends;
    }

    public void addFriend(String charname) {
        friendList.add(charname);
    }

    public boolean isFriend(String name) {
        return friendList.contains(name);
    }

}
