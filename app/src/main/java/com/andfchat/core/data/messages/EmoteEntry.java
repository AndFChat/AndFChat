package com.andfchat.core.data.messages;

import android.content.Context;
import android.graphics.Typeface;

import com.andfchat.core.data.FCharacter;

import java.util.Date;

public class EmoteEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;

    public EmoteEntry(FCharacter owner, String text, Date date) {
        super(owner, MessageType.EMOTE, date);
        this.text = text;

        delimiterBetweenDateAndName = " * ";

        if (text.charAt(0) == '\'') {
            delimiterBetweenNameAndText = "";
        }
        else {
            delimiterBetweenNameAndText = " ";
        }
    }

    @Override
    protected String getText(Context context) {
        return text;
    }

    @Override
    protected Integer getTypeFace() {
        return Typeface.ITALIC;
    }
}
