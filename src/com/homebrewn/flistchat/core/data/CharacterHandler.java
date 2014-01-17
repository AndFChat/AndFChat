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

import java.util.HashMap;

public class CharacterHandler {
    private final HashMap<String, FlistChar> knownCharacters = new HashMap<String, FlistChar>();

    // Username for system accounts
    public static final String USER_SYSTEM = "System";
    public static final String USER_SYSTEM_OUTPUT = "Output";
    public static final String USER_SYSTEM_INPUT = "Input";

    public CharacterHandler() {
        this.addCharacter(new FlistChar(USER_SYSTEM));
        this.addCharacter(new FlistChar(USER_SYSTEM_OUTPUT, Gender.MALE));
        this.addCharacter(new FlistChar(USER_SYSTEM_INPUT, Gender.FEMALE));
    }

    public FlistChar findCharacter(String name, boolean create) {
        synchronized(this) {
            if (knownCharacters.containsKey(name) == false && create) {
                if (create) {
                    knownCharacters.put(name, new FlistChar(name));
                }
            }
            return knownCharacters.get(name);
        }
    }

    public FlistChar findCharacter(String character) {
        return findCharacter(character, true);
    }

    public void addCharacter(FlistChar character) {
        synchronized(this) {
            if (knownCharacters.containsKey(character.getName()) == true) {
                knownCharacters.get(character.getName()).setInfos(character);
            } else {
                knownCharacters.put(character.getName(), character);
            }
        }
    }

    public void initCharacters(HashMap<String, FlistChar> characterList) {
        synchronized(this) {
            knownCharacters.putAll(characterList);
        }
    }

    public void removeCharacter(String name) {
        synchronized(this) {
            knownCharacters.remove(name);
        }
    }

    public void clear() {
        knownCharacters.clear();
    }
}
