package com.andfchat.core.connection.handler;

import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.andfchat.R;
import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.ChatEntry.ChatEntryType;
import com.andfchat.core.data.FlistChar;

public class ChannelInviteHandler extends TokenHandler {

    @Override
    public void incomingMessage(ServerToken token, String msg, List<FeedbackListner> feedbackListner) throws JSONException {
        if (token == ServerToken.CIU) {
            JSONObject json = new JSONObject(msg);
            String channelId = json.getString("title");
            String channelName = json.getString("name");
            String username = json.getString("sender");

            FlistChar flistChar = characterManager.findCharacter(username, false);
            if (flistChar != null) {
                ChatEntry chatEntry = new ChatEntry(R.string.message_invite_to_channel, new Object[]{channelId, channelName},flistChar, new Date(), ChatEntryType.NOTATION_STATUS);
                broadcastSystemInfo(chatEntry, flistChar);
            }

        }
    }

    @Override
    public ServerToken[] getAcceptableTokens() {
        return new ServerToken[] {ServerToken.CIU};
    }
}
