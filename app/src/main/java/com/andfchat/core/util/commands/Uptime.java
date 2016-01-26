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

public class Uptime extends TextCommand {

    public Uptime() {
        allowedIn = new Chatroom.ChatroomType[]{Chatroom.ChatroomType.CONSOLE};
    }

    @Override
    public String getDescription() {
        return "*  /uptime | Reports how long the server has been running.";
    }

    @Override
    public boolean fitToCommand(String token) {
        return token.equals("/uptime");
    }

    @Override
    public void runCommand(String token, String text) {
        connection.uptime();
    }
}
