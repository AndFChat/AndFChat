package com.andfchat.core.data.messages;

import android.content.Context;

import com.andfchat.core.data.FCharacter;

public class MessageEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;

    public MessageEntry(FCharacter owner, String text) {
        super(owner, MessageType.MESSAGE);
        this.text = text;
    }

    @Override
    protected String getText(Context context) {
        return text;
    }
}
