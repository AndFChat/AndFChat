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


package com.andfchat.core.data.history;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import roboguice.util.Ln;
import android.content.Context;

import com.andfchat.core.data.Channel;
import com.andfchat.core.data.Chatroom.ChatroomType;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.messages.ChatEntry;
import com.andfchat.core.data.messages.ChatEntry.MessageType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

// Manages all history from 0 AD.
@Singleton
public class HistoryManager {

    @Inject
    private SessionData sessionData;

    private final Context context;

    @Inject
    public HistoryManager(Context context) {
        this.context = context;
    }

    private HashMap<Channel, List<ChatEntry>> histories = new HashMap<Channel, List<ChatEntry>>();

    public List<ChatEntry> loadHistory(Channel channel) {
        Ln.d("Load history for " + channel);
        List<ChatEntry> history = histories.get(channel);
        if (history == null) {
            Ln.d("No history found");
            history = new ArrayList<ChatEntry>();

            Ln.d("put history for" + channel);
            histories.put(channel, history);
        }

        return history;
    }

    public void loadHistory() {
        if (sessionData.getSessionSettings().useHistory()) {
            Ln.d("Load history from disk!");
            String filename = sessionData.getCharacterName() + ".hist";

                FileInputStream fis;
                try {
                    Ln.d("loading file: " + filename);
                    fis = context.openFileInput(filename);
                    ObjectInputStream is = new ObjectInputStream(fis);

                    histories = (HashMap<Channel, List<ChatEntry>>) is.readObject();
                    Ln.d("loading successfully! channels: " + histories.size());
                    is.close();

                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    Ln.e("loading failed!");

                }

            histories = new HashMap<Channel, List<ChatEntry>>();
        }
    }

    public void saveHistory() {
        if (sessionData.getSessionSettings().useHistory()) {
            Ln.d("Save history to disk!");
            String filename = sessionData.getCharacterName() + ".hist";

            if (context.deleteFile(filename)) {
                Ln.d("Old file named: " + filename + " deleted");
            }

            try {
                FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
                ObjectOutputStream outputStream = new ObjectOutputStream(fos);

                // Filter history, do not save notations
                HashMap<Channel, List<ChatEntry>> filteredHistories = new HashMap<Channel, List<ChatEntry>>();
                for (Channel channel : histories.keySet()) {
                    // Care about the channel filter
                    if (sessionData.getSessionSettings().logChannel() || channel.getType() != ChatroomType.PUBLIC_CHANNEL) {
                        List<ChatEntry> channelMessages = new ArrayList<ChatEntry>(histories.get(channel));

                        // Clean up chat history by removing everything than messages and emotes.
                        ListIterator<ChatEntry> iterator = channelMessages.listIterator();
                        while (iterator.hasNext()) {
                            ChatEntry entry = iterator.next();
                            if (entry.getMessageType() != MessageType.MESSAGE && entry.getMessageType() != MessageType.EMOTE) {
                                iterator.remove();
                            }
                        }

                        filteredHistories.put(channel, channelMessages);
                    }
                }

                // Write serialized output
                outputStream.writeObject(filteredHistories);
                outputStream.flush();
                outputStream.close();
                Ln.d("Saving complete:" + histories.size());
            } catch (Exception e) {
                e.printStackTrace();
                Ln.e("Saving failed!");
            }
        }
    }

    public void clearHistory(boolean andSave) {
        for (List<ChatEntry> chatEntries : histories.values()) {
            chatEntries.clear();
        }

        histories.clear();

        for (String filename : context.fileList()) {
            if (filename.endsWith(".hist")) {
                Ln.d("Deleted file: " + filename);
                context.deleteFile(filename);
            }
        }
    }


}
