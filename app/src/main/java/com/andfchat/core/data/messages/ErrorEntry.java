package com.andfchat.core.data.messages;

import android.content.Context;
import android.graphics.Typeface;

import com.andfchat.R;
import com.andfchat.core.data.FCharacter;

import java.util.Date;

public class ErrorEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;

    public ErrorEntry(FCharacter owner, String text) {
        super(owner, MessageType.ERROR, new Date());
        this.text = text;
    }

    @Override
    protected String getText(Context context) {
        return text;
    }

    @Override
    protected Integer getNameColorId() {
        return R.color.name_error;
    }

    @Override
    protected Integer getTypeFace() {
        return Typeface.BOLD;
    }
}
