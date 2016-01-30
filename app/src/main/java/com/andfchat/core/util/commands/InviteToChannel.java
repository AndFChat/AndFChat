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
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.SessionData;
import com.google.inject.Inject;


public class InviteToChannel extends TextCommand {

    public InviteToChannel() {
        allowedIn = new ChatroomType[]{ChatroomType.PRIVATE_CHANNEL, ChatroomType.PUBLIC_CHANNEL};
    }

    @Inject
    protected SessionData sessionData;

    @Override
    public String getDescription() {
        return "*  /invite [USER] | USUALLY USED FOR CLOSED PRIVATE ROOMS, THIS WILL INVITE ANOTHER CHARACTER TO THE ROOM, ESSENTIALLY GIVING THEM PERMISSION TO ENTER IT. DESPITE DOCUMENTATION TO THE CONTRARY, ELSEWHERE, THIS COMMAND WILL BE EFFECTIVE FOR THE ROOM IN WHICH IT IS TYPED (PROVIDED YOUR CURRENT CHARACTER IS A CHAN-OP OR ROOM OWNER), AND NOT JUST THE MOST RECENT ROOM YOU CREATED.";
    }

    @Override
    public boolean fitToCommand(String token) {
        return token.equals("/invite");
    }

    @Override
    public void runCommand(String token, String text) {
        if (text != null) {
            FCharacter flistChar = characterManager.findCharacter(text.trim(), false);
            if (flistChar != null){
                connection.invite(flistChar.getName(), chatroomManager.getActiveChat());
            }
        }
    }
}
