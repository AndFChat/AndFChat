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

/**
 * Chatroom/Privatemessage identification object. Holds the simplest data about a channel.
 * @author AndFChat
 */
public class Channel {

    private final String channelName;
    private final String channelId;

    private int user;

    public Channel(String channelId, String channelName) {
        this.channelName = channelName;
        this.channelId = channelId;
        user = 0;
    }

    public Channel(String channelId, String channelName, int user) {
        this.channelName = channelName;
        this.channelId = channelId;
        this.user = user;
    }

    public int getUserInChannel() {
        return user;
    }

    public void setUserInChannel(int user) {
        this.user = user;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    @Override
    public String toString() {
        return "[" + channelName + " (" + user + "), ID:" + channelId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((channelId == null) ? 0 : channelId.hashCode());
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
        return true;
    }
}
