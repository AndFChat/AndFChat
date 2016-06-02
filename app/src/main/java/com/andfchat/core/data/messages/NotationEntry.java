package com.andfchat.core.data.messages;

import android.content.Context;

import com.andfchat.R;
import com.andfchat.core.data.FCharacter;

import java.util.Date;

public class NotationEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;

    public NotationEntry(FCharacter owner, String text) {
        this(owner, text, new Date());
    }

    public NotationEntry(FCharacter owner, String text, Date date) {
        super(owner, MessageType.NOTATION, date);
        this.text = text;

        delimiterBetweenNameAndText = " ";
    }

    @Override
    protected String getText(Context context) {
        return text;
    }

    @Override
    protected Integer getNameColorId() {
        return R.color.name_annotation;
    }
}
