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

public enum ServerToken {
    CBU, // Removes a user from a channel, and prevents them from re-entering.
    CKU, // Kicks a user from a channel.
    HLO, // Server hello command. Tells which server version is running and who wrote it.
    RTB, // Real-time bridge. Indicates the user received a note or message, right at the very moment this is received.
    SYS, // An informative auto generated message from the server. This is also the way the server responds to some commands, such as RST, CIU, CBL, COL, and CUB. The server will sometimes send this in concert with a response command, such as with COA and COR.
    TPN, // A user informs you of his typing status.
    VAR, // Variables the server sends to inform the client about server variables.

    // All tokens which have a fitting handler.
    ADL, // Listen all global operator
    BRO, // Incoming admin broadcast. -> MessageHandler
    CDS, // Sends channel description -> ChannelDescriptionHandler
    CHA, // Sends the client a list of all public channels. -> ChannelListHandler
    CIU, // Invites a user to a channel. -> ChannelInviteHandler
    COA, // Promotes a user to channel operator -> PromotionHandler
    COL, // Listen all operator for a channel.
    CON, // After connecting and identifying you will receive a CON command, giving the number of connected users to the network. -> CharListHandler
    COR, // Demotes a user from a channel operator to a normal user. -> DemotionHandler
    CTU, // Temporarily bans a character from a channel. -> TimeoutHandler
    ERR, // Indicates that the given error has occurred. -> ErrorMessageHandler
    FLN, // Sent by the server to inform the client a given character went offline. -> CharListHandler
    FRL, // Initial friends list. -> FriendListHandler
    ICH, // Initial channel data. Received in response to JCH, along with CDS. -> JoinedChannel
    IDN, // Used to inform the client their identification is successful, and handily sends their character name along with it. -> FirstConnectionHandler
    IGN, // Handles the ignore list. -> IgnoreHandler
    JCH, // Indicates the given user has joined the given channel. This may also be the client's character. -> JoinedChannel
    LCH, // An indicator that the given character has left the channel. This may also be the client's character. -> LeftChannelHandler
    LIS, // Sends an array of all the online characters and their gender, status, and status message. -> CharListHandler
    LRP, // A role play ad is received from a user in a channel. -> AdHandler
    MSG, // A message is received from a user in a channel. -> MessageHandler
    NLN, // A user connected. -> CharListHandler
    ORS, // Gives a list of open private rooms. -> ChannelListHandler
    PIN, // Ping command from the server, requiring a response, to keep the connection alive. -> PingHandler
    PRI, // A private message is received from another user. -> PrivateMessageHandler
    RLL, // Rolls dice or spins the bottle. -> DiceBottleHandler
    RMO, // Change room mode to accept chats, ads, or both. ->RoomModeHandler
    STA, // A user changed their status -> CharInfoHandler
    UPT, // Informs the client of the server's self-tracked online time, and a few other bits of information -> UptimeHandle
}
