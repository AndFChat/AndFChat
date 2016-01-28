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

import android.content.Context;

import com.andfchat.R;
import com.andfchat.core.data.Chatroom.ChatroomType;
import com.google.inject.Inject;


public class SendAd extends TextCommand {

    public SendAd() {
        allowedIn = new ChatroomType[]{ChatroomType.PRIVATE_CHANNEL, ChatroomType.PUBLIC_CHANNEL};
    }

    @Inject
    protected Context context;

    @Override
    public String getDescription() {
        return "*  /ad " + context.getString(R.string.command_description_ad);
    }

    @Override
    public boolean fitToCommand(String token) {
        return token.equals("/ad");
    }

    @Override
    public void runCommand(String token, String text) {
        connection.sendAdToChannel(chatroomManager.getActiveChat(), text.trim());
    }
}
