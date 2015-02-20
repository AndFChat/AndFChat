package com.andfchat.core.data.messages;

import android.content.Context;
import android.graphics.Typeface;

import com.andfchat.core.data.FCharacter;

public class EmoteEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;

    public EmoteEntry(FCharacter owner, String text) {
        super(owner, MessageType.EMOTE);
        this.text = text;

        delimeterBetweenDateAndName = " * ";

        if (text.charAt(0) == '\'') {
            delimeterBetweenNameAndText = "";
        }
        else {
            delimeterBetweenNameAndText = " ";
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
