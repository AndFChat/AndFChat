package com.andfchat.core.data.messages;

import android.content.Context;

import com.andfchat.core.data.FCharacter;

public class WarningEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;

    public WarningEntry(FCharacter owner, String text) {
        super(owner, MessageType.WARNING);
        this.text = text;
    }

    @Override
    protected String getText(Context context) {
        return text;
    }
}
