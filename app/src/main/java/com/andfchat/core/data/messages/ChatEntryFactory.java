package com.andfchat.core.data.messages;


import android.content.Context;
import android.text.Spannable;
import android.util.Log;

import com.andfchat.R;
import com.andfchat.core.data.FCharacter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Date;
import java.util.MissingFormatArgumentException;

@Singleton
public class ChatEntryFactory {

    private final Context context;


    @Inject
    public ChatEntryFactory(Context context) {
        this.context = context;
    }

    public ChatEntry getMessage(FCharacter owner, String text) {
        return getMessage(owner, text, new Date());
    }

    public ChatEntry getMessage(FCharacter owner, String text, Date date) {
        if (text.startsWith("/me")) {
            text = text.substring(3);
            if(!text.trim().isEmpty()) {
                return new EmoteEntry(owner, text.trim(), date);
            }
            else {
                return null;
            }
        }
        else if (text.startsWith("/warn ")) {
            text = text.substring(6);
            if(!text.trim().isEmpty()) {
                return new WarningEntry(owner, text.trim(), date);
            }
            else {
                return null;
            }
        }
        else {
            return new MessageEntry(owner, text.trim(), date);
        }
    }

    public ChatEntry getNotation(FCharacter owner, int stringId) {
        return getNotation(owner, stringId, null);
    }

    public ChatEntry getNotation(FCharacter owner, int stringId, Object[] textParts) {
        return getNotation(owner, getText(stringId, textParts), new Date());
    }

    public ChatEntry getNotation(FCharacter owner, String text, Date time) {
        ChatEntry entry = new NotationEntry(owner, text, time);
        entry.setIcon(R.drawable.ic_info_dark);
        return entry;
    }

    public ChatEntry getHelp(FCharacter owner, String text) {
        AdEntry entry = new AdEntry(owner, text, context.getString(R.string.command_help_menu));
        entry.setIcon(R.drawable.ic_info_dark);
        return entry;
    }

    public ChatEntry getAd(FCharacter owner, String text) {
        return getAd(owner, text, new Date());
    }

    public ChatEntry getAd(FCharacter owner, String text, Date time) {
        AdEntry entry = new AdEntry(owner, text, context.getString(R.string.ad_clickable_advertisement), time);
        entry.setIcon(R.drawable.ic_ad);
        return entry;
    }

    public ChatEntry getError(FCharacter owner, int stringId) {
        return getError(owner, stringId, null);
    }

    public ChatEntry getError(FCharacter owner, int stringId, Object[] textParts) {
        return getError(owner, getText(stringId, textParts));
    }

    public ChatEntry getError(FCharacter owner, String text) {
        ChatEntry entry = new ErrorEntry(owner, text);
        entry.setIcon(R.drawable.ic_error);
        return entry;
    }

    public ChatEntry getStatusInfo(FCharacter owner) {
        ChatEntry entry = new NotationEntry(owner, context.getString(R.string.status) + " " + owner.getStatusMsg());
        entry.setIcon(R.drawable.ic_info_dark);
        return entry;
    }

    private String getText(int stringId, Object[] textParts) {
        String text = context.getString(stringId);
        if (textParts != null) {
            try {
                text = String.format(text, textParts);
            }
            catch (MissingFormatArgumentException exception) {
                Log.e(this.getClass().getSimpleName(), "Tried to format text: " + text);
                exception.printStackTrace();
            }
        }

        return text;
    }

    public void setShowAdText(boolean value) {

    }

    public interface AdClickListener {
        void openAd(Spannable text);
    }
}
