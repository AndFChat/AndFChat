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


package com.andfchat.core.connection;

public enum ClientToken {
    CBU, // Ban
    CCR, // Create a private, invite-only channel.
    CDS, // Sets the description of the room.
    CHA, // Request a list of all public channels.
    CIU, // Invite User to channel.
    CKU, // Kick
    COA, // Request a character be promoted to channel op (moderator)
    COL, // Requests the list of channel ops (channel moderators).
    COR, // Demotes a channel op (moderator) to a normal user.
    CSO, // Changes the channel owner to a specified character.
    CTU, // Give a user a timeout for a designated number of minutes.
    CUB, // Unban
    FKS, // Search for characters fitting the user's selections. Kinks is required, all other parameters are optional.
    IDN, // This command is used to identify with the server.
    IGN, // A multi-faceted command to handle actions related to the ignore list. The server does not actually handle much of the ignore process, as it is the client's responsibility to block out messages it receives from the server if that character is on the user's ignore list.
    JCH, // Send a channel join request.
    LCH, // Request to leave a channel.
    LRP, // Send RP-Ad {channel:'ADH', message:'text'}
    MSG, // Sends a message to all other users in a channel.
    ORS, // Request a list of open private rooms.
    PIN, // Sends a ping response to the server. Timeout detection, and activity to keep the connection alive.
    PRI, // Sends a private message to another user.
    PRO, // Requests some of the profile tags on a character, such as Top/Bottom position and Language Preference.
    RLL, // Roll dice or spin the bottle.
    RMO, // Change room mode to accept chats, ads, or both.
    RST, // Sets a private room's status to closed or open. (private, public)
    STA, // Request a new status be set for your character.
    TPN, // User x is typing/stopped typing/has entered text" for private messages.
    UPT, // Request info about how long the server has been running, and some stats about usage.
    WHS, // The client can send \x00WSH\xff to skip websocket negotiation. A little convenience thing for third party desktop chat clients.
}
