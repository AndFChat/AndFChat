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


package com.andfchat.core.connection.handler;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import roboguice.util.Ln;

import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.ServerToken;

/**
 * Displays add messages in channels.
 * @author AndFChat
 */
public class VariableHandler extends TokenHandler {

    public enum Variable {
        chat_max(Integer.class),
        priv_max(Integer.class),
        lfrp_max(Integer.class),
        lfrp_flood(Float.class),
        msg_flood(Float.class),
        permission(Integer.class);

        private final Class type;

        private Variable(Class type) {
            this.type = type;
        }

        public Class getType() {
            return type;
        }
    }

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        if (token == ServerToken.VAR) {
            JSONObject data = new JSONObject(msg);
            String variableName = data.getString("variable");

            Variable variable;
            try {
                variable = Variable.valueOf(variableName);
            }
            catch(IllegalArgumentException e) {
                Ln.e("Can't parse variable enum for '"+variableName+"' - skipping!");
                return;
            }

            if (variable.getType() == Integer.class) {
                sessionData.setVariable(variable, data.getInt("value"));
            }
        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.VAR};
    }

}
