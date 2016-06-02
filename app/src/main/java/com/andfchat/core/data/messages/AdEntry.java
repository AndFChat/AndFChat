package com.andfchat.core.data.messages;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.messages.ChatEntryFactory.AdClickListener;
import com.andfchat.core.util.BBCodeReader;
import com.andfchat.core.util.SmileyReader;

import java.util.Date;

public class AdEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;
    private final String displayText;

    protected static AdClickListener adClickListener;

    private transient boolean showText = false;

    public AdEntry(FCharacter owner, String text, String displayText) {
        this(owner, text, displayText, new Date());
    }

    public AdEntry(FCharacter owner, String text, String displayText, Date date) {
        super(owner, MessageType.AD, date);
        this.text = text;
        this.displayText = displayText;

        delimiterBetweenDateAndName = " ";
        delimiterBetweenNameAndText = " ";
    }

    @Override
    protected String getText(Context context) {
        return text;
    }

    public void setShowText(boolean value) {
        if( showText != value) {
            showText = value;
            spannedText = null;
        }
    }

    public static void setAdClickListener(AdClickListener adClickListener) {
        AdEntry.adClickListener = adClickListener;
    }

    public Spannable createText(Context context) {
        String text = getText(context);
        text = BBCodeReader.modifyUrls(text, "http://");
        //text = BBCodeReader.modifyUrls(text, "https://");

        final Spannable textSpan = SmileyReader.addSmileys(context, BBCodeReader.createSpannableWithBBCode(text, context));

        if (adClickListener != null && !showText) {
            // Create display text
            Spannable displayedSpan = new SpannableString(displayText);

            ClickableSpan clickable = new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                adClickListener.openAd(textSpan);
                }
            };

            displayedSpan.setSpan(clickable, 0, displayedSpan.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            return displayedSpan;
        }
        else {
            return textSpan;
        }
    }

}
