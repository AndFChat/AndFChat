package com.andfchat.frontend.util;

import android.content.Context;

import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.messages.ChatEntry;

public class Exporter {

    public static byte[] exportText(Context context, Chatroom chatroom) {

        String text = "";

        for (ChatEntry entry : chatroom.getExportableChatHistory()) {
            text += entry.getChatMessage(context).toString() + "\n";
        }

        return text.getBytes();
    }
}
