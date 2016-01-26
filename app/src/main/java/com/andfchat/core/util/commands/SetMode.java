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

import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.SessionData;
import com.google.inject.Inject;

public class SetMode extends TextCommand {

    public SetMode() {
        allowedIn = new Chatroom.ChatroomType[]{Chatroom.ChatroomType.PRIVATE_CHANNEL, Chatroom.ChatroomType.PUBLIC_CHANNEL};
    }

    @Inject
    protected SessionData sessionData;

    @Override
    public String getDescription() {
        return "*  /setmode [mode] | This determines the type of content that can be sent in a private room. [mode] can be 'ads', 'chat', or 'both' (Default: both)";
    }

    @Override
    public boolean fitToCommand(String token) {
        return token.equals("/setmode");
    }

    @Override
    public void runCommand(String token, String text) {
        Chatroom chatroom = chatroomManager.getActiveChat();
        if (chatroom != null && text != null) {
            connection.setMode(chatroom, text.trim());
        }
    }
}
