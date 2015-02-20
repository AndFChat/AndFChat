package com.andfchat.core.data.messages;

import android.content.Context;

import com.andfchat.R;
import com.andfchat.core.data.FCharacter;

public class NotationEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;

    public NotationEntry(FCharacter owner, String text) {
        super(owner, MessageType.NOTATION);
        this.text = text;

        delimeterBetweenNameAndText = " ";
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
