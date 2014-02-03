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


package com.homebrewn.flistchat.core.util.commands;

import roboguice.event.EventManager;

import com.google.inject.Inject;
import com.homebrewn.flistchat.core.connection.handler.PrivateMessageHandler;
import com.homebrewn.flistchat.core.data.ChatEntry.ChatEntryType;
import com.homebrewn.flistchat.core.data.Chatroom;
import com.homebrewn.flistchat.core.data.FlistChar;

public class PMUser extends TextCommand {

    @Inject
    protected EventManager eventManager;

    @Override
    public String getDescription() {
        return "*  /pm [USER] | OPEN A CONVERSATION WITH GIVEN USER.";
    }

    @Override
    public boolean fitToCommand(String token) {
        return token.equals("/pm");
    }

    @Override
    public void runCommand(String token, String text) {
        if (text == null || text.length() == 0) {
            showMessage("PLEASE GIVE A USERNAME AS PARAMETER!", ChatEntryType.ERROR);
            return;
        }

        FlistChar flistChar = characterManager.findCharacter(text, false);

        if (flistChar == null) {
            showMessage("NO USER WITH NAME '" + text + "' FOUND!", ChatEntryType.ERROR);
        } else {
            Chatroom chatroom;
            if (chatroomManager.hasOpenPrivateConversation(flistChar) == false) {
                chatroom = PrivateMessageHandler.openPrivateChat(chatroomManager, flistChar);
            } else {
                chatroom = chatroomManager.getPrivateChatFor(flistChar);
            }

            chatroomManager.setActiveChat(chatroom);
            eventManager.fire(chatroom);
        }
    }

}
