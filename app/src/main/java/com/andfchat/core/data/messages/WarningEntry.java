package com.andfchat.core.data.messages;

import android.content.Context;

import com.andfchat.core.data.FCharacter;

import java.util.Date;

public class WarningEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;

    public WarningEntry(FCharacter owner, String text, Date date) {
        super(owner, MessageType.WARNING, date);
        this.text = text;
    }

    @Override
    protected String getText(Context context) {
        return text;
    }
}
