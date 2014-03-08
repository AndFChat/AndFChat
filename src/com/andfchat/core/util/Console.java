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


package com.andfchat.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import roboguice.RoboGuice;
import android.content.Context;

import com.andfchat.R;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.ChatEntryType;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FlistChar;
import com.andfchat.core.util.commands.Ban;
import com.andfchat.core.util.commands.Bottle;
import com.andfchat.core.util.commands.CloseChannelToPublic;
import com.andfchat.core.util.commands.CloseChatroom;
import com.andfchat.core.util.commands.CreateChannel;
import com.andfchat.core.util.commands.Dice;
import com.andfchat.core.util.commands.InviteToChannel;
import com.andfchat.core.util.commands.Kick;
import com.andfchat.core.util.commands.OpenChannelToPublic;
import com.andfchat.core.util.commands.PMUser;
import com.andfchat.core.util.commands.StatusChange;
import com.andfchat.core.util.commands.TextCommand;
import com.andfchat.core.util.commands.Unban;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class Console {

    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected CharacterManager characterManager;

    public List<TextCommand> availableCommands = new ArrayList<TextCommand>();

    @Inject
    public Console(Context context) {
        availableCommands.add(new StatusChange());
        availableCommands.add(new Bottle());
        availableCommands.add(new Dice());
        availableCommands.add(new PMUser());
        availableCommands.add(new CloseChatroom());

        // Channel OP commands
        availableCommands.add(new CreateChannel());
        availableCommands.add(new InviteToChannel());
        availableCommands.add(new OpenChannelToPublic());
        availableCommands.add(new CloseChannelToPublic());
        availableCommands.add(new Kick());
        availableCommands.add(new Ban());
        availableCommands.add(new Unban());

        Injector injector = RoboGuice.getInjector(context);
        for (TextCommand command : availableCommands) {
            injector.injectMembers(command);
        }
    }

    public boolean checkForCommands(String message) {
        if (message == null || message.length() == 0) {
            return false;
        }

        if (message.startsWith("/")) {
            // Ignore /me commands
            if (message.startsWith("/me")) {
                return false;
            }

            String token = message;
            if (message.indexOf(' ') != -1) {
                token = token.substring(0, message.indexOf(' '));
            }

            for (TextCommand command : availableCommands) {
                if (command.fitToCommand(token)) {
                    if (command.isAllowedIn(chatroomManager.getActiveChat().getChatroomType())) {
                        command.runCommand(token, message.replace(token, "").trim());
                        return true;
                    } else {
                        FlistChar systemChar = characterManager.findCharacter(CharacterManager.USER_SYSTEM);
                        ChatEntry chatEntry = new ChatEntry(R.string.error_command_not_allowed, systemChar, new Date(), ChatEntryType.ERROR);
                        chatroomManager.getActiveChat().addMessage(chatEntry);
                        break;
                    }
                }
            }
            // No fitting command found, show help.
            showHelp();
            return true;
        } else {
            return false;
        }
    }

    private void showHelp() {
        String message = "Help menu shows all usable command:";

        for (TextCommand command : availableCommands) {
            message += "\n" + command.getDescription();
        }

        FlistChar systemChar = characterManager.findCharacter(CharacterManager.USER_SYSTEM);
        ChatEntry chatEntry = new ChatEntry(message, systemChar, new Date(), ChatEntryType.NOTATION_SYSTEM);
        chatroomManager.getActiveChat().addMessage(chatEntry);
    }
}
