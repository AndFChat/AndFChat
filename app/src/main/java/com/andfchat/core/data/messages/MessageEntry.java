package com.andfchat.core.data.messages;

import android.content.Context;

import com.andfchat.core.data.FCharacter;

import java.util.Date;

public class MessageEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;

    public MessageEntry(FCharacter owner, String text) {
        this(owner, text, new Date());
    }

    public MessageEntry(FCharacter owner, String text, Date date) {
        super(owner, MessageType.MESSAGE, date);
        this.text = text;
    }

    @Override
    protected String getText(Context context) {
        return text;
    }
}
