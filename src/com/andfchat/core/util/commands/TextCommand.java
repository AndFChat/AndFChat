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


package com.andfchat.core.util.commands;

import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.ChatEntryType;
import com.andfchat.core.data.Chatroom.ChatroomType;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FCharacter;
import com.google.inject.Inject;

public abstract class TextCommand {

    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected CharacterManager characterManager;
    @Inject
    protected FlistWebSocketConnection connection;

    protected ChatroomType[] allowedIn;

    public abstract String getDescription();
    public abstract boolean fitToCommand(String token);
    public abstract void runCommand(String token, String text);

    public boolean isAllowedIn(ChatroomType type) {
        for (ChatroomType otherType : allowedIn) {
            if (otherType == type) {
                return true;
            }
        }
        return false;
    }

    protected void showMessage(String message, ChatEntryType type) {
        FCharacter systemChar = characterManager.findCharacter(CharacterManager.USER_SYSTEM);
        ChatEntry chatEntry = new ChatEntry(message, systemChar, type);
        chatroomManager.addMessage(chatroomManager.getActiveChat(), chatEntry);
    }

}
