package com.andfchat.core.data.history;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import roboguice.util.Ln;
import android.content.Context;

import com.andfchat.core.data.Channel;
import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.Chatroom.ChatroomType;
import com.andfchat.core.data.SessionData;
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

            if (sessionData.getSessionSettings().logChannel() == false) {
                HashMap<Channel, List<ChatEntry>> newHistories = new HashMap<Channel, List<ChatEntry>>();
                for (Channel channel : histories.keySet()) {
                    if (channel.getType() != ChatroomType.PUBLIC_CHANNEL) {
                        newHistories.put(channel, histories.get(channel));
                    }
                }
            }

            Ln.d("Save history to disk!");
            String filename = sessionData.getCharacterName() + ".hist";

            if (context.deleteFile(filename)) {
                Ln.d("Old file named: " + filename + " deleted");
            }

            try {
                FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
                ObjectOutputStream outputStream = new ObjectOutputStream(fos);

                // If public channel shouldn't be saved
                if (sessionData.getSessionSettings().logChannel() == false) {
                    HashMap<Channel, List<ChatEntry>> filteredHistories = new HashMap<Channel, List<ChatEntry>>();
                    for (Channel channel : histories.keySet()) {
                        if (channel.getType() != ChatroomType.PUBLIC_CHANNEL) {
                            filteredHistories.put(channel, histories.get(channel));
                        }
                    }
                    outputStream.writeObject(filteredHistories);
                }
                else {
                    outputStream.writeObject(histories);
                }

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
