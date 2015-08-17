package com.andfchat.core.data.messages;


import android.content.Context;
import android.text.Spannable;

import com.andfchat.R;
import com.andfchat.core.data.FCharacter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ChatEntryFactory {

    private final Context context;

    private AdClickListner adClickListner;

    @Inject
    public ChatEntryFactory(Context context) {
        this.context = context;
    }

    public void setAdClickListner(AdClickListner adClickListner) {
        this.adClickListner = adClickListner;
    }

    public ChatEntry getMessage(FCharacter owner, String text) {
        if (text.startsWith("/me")) {
            text = text.substring(3);
            return new EmoteEntry(owner, text);
        }
        else if (text.startsWith("/warn ")) {
            text = text.substring(6);
            return new WarningEntry(owner, text);
        }
        else {
            return new MessageEntry(owner, text);
        }
    }

    public ChatEntry getNotation(FCharacter owner, int stringId) {
        return getNotation(owner, stringId, null);
    }

    public ChatEntry getNotation(FCharacter owner, int stringId, Object[] textParts) {
        return getNotation(owner, getText(stringId, textParts));
    }

    public ChatEntry getNotation(FCharacter owner, String text) {
        ChatEntry entry = new NotationEntry(owner, text);
        entry.setIcon(R.drawable.ic_info_dark);
        return entry;
    }

    public ChatEntry getAd(FCharacter owner, String text) {
        AdEntry entry = new AdEntry(owner, text, context.getString(R.string.ad_clickable_advertisement), adClickListner);
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

    private String getText(int stringId, Object[] textParts) {
        String text = context.getString(stringId);
        if (textParts != null) {
            text = String.format(text,  textParts);
        }

        return text;
    }

    public interface AdClickListner {
        public void openAd(Spannable text);
    }
}
