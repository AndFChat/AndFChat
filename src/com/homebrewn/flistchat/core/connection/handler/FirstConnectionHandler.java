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


package com.homebrewn.flistchat.core.connection.handler;

import java.util.List;

import org.json.JSONException;

import com.google.inject.Inject;
import com.homebrewn.flistchat.core.connection.FeedbackListner;
import com.homebrewn.flistchat.core.connection.FlistWebSocketConnection;
import com.homebrewn.flistchat.core.connection.ServerToken;

/**
 * After Identification this handler is called first.
 * @author AndFChat
 */
public class FirstConnectionHandler extends TokenHandler {

    @Inject
    private FlistWebSocketConnection connection;

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        connection.joinChannel("Frontpage");
        connection.requestOfficialChannels();
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.IDN};
    }

}
