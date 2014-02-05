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



public class OpenChatroom extends TextCommand {

    @Override
    public String getDescription() {
        return "*  /makeroom [name] | OPEN A NEW PRIVATE ROOM NAMED [name].";
    }

    @Override
    public boolean fitToCommand(String token) {
        return token.equals("/makeroom");
    }

    @Override
    public void runCommand(String token, String text) {
        if (text != null) {
            text = text.trim();
            if (text.length() > 0) {
                connection.createPrivateChannel(text);
            }
        }
    }

}
