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


package com.andfchat.core.data;

import java.io.Serializable;

import com.andfchat.core.data.Chatroom.ChatroomType;

/**
 * Chatroom/Privatemessage identification object. Holds the simplest data about a channel.
 * @author AndFChat
 */
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String channelName;
    private final String channelId;

    private final ChatroomType type;

    public Channel(String channelId, String channelName, ChatroomType type) {
        this.channelName = channelName;
        this.channelId = channelId;
        this.type = type;
    }

    public Channel(String channelId, ChatroomType type) {
        this.channelName = channelId;
        this.channelId = channelId;
        this.type = type;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public ChatroomType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "[" + channelName +  " / ID:" + channelId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((channelId == null) ? 0 : channelId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Channel other = (Channel) obj;
        if (channelId == null) {
            if (other.channelId != null)
                return false;
        } else if (!channelId.equals(other.channelId))
            return false;
        return type == other.type;
    }
}
