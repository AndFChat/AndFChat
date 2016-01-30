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

import com.andfchat.core.data.Chatroom.ChatroomType;



public class Dice extends TextCommand {

    public Dice() {
        allowedIn = new ChatroomType[]{ChatroomType.PRIVATE_CHANNEL, ChatroomType.PUBLIC_CHANNEL, ChatroomType.PRIVATE_CHAT};
    }

    @Override
    public String getDescription() {
        return "*  /roll [#d##] | This command rolls a number of dice, all with the same number of sides. If Bob wanted to roll two dice with six sides each, he'd type: /roll 2d6";
    }

    @Override
    public boolean fitToCommand(String token) {
        return token.equals("/roll");
    }

    @Override
    public void runCommand(String token, String text) {
        connection.dice(chatroomManager.getActiveChat(), text.trim());
    }
}
