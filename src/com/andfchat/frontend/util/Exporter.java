package com.andfchat.frontend.util;

import android.content.Context;

import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.Chatroom;

public class Exporter {

    public static byte[] exportText(Context context, Chatroom chatroom) {

        String text = "";

        for (ChatEntry entry : chatroom.getChatHistory()) {
            text += entry.getChatMessage(context).toString() + "\n";
        }

        return text.getBytes();
    }
}
