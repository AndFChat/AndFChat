package com.andfchat.core.data.messages;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.messages.ChatEntryFactory.AdClickListner;
import com.andfchat.core.util.BBCodeReader;
import com.andfchat.core.util.SmileyReader;

public class AdEntry extends ChatEntry {

    private static final long serialVersionUID = 1L;

    private final String text;
    private final String displayText;

    private final transient AdClickListner adClickListner;

    public AdEntry(FCharacter owner, String text, String displayText, AdClickListner adClickListner) {
        super(owner, MessageType.AD);
        this.text = text;
        this.displayText = displayText;
        this.adClickListner = adClickListner;

        delimeterBetweenDateAndName = " ";
        delimeterBetweenNameAndText = " ";
    }

    @Override
    protected String getText(Context context) {
        return text;
    }

    @Override
    protected Spannable createText(Context context) {
        String text = getText(context);
        text = BBCodeReader.modifieUrls(text, "http://");
        text = BBCodeReader.modifieUrls(text, "https://");

        final Spannable textSpan = SmileyReader.addSmileys(context, BBCodeReader.createSpannableWithBBCode(text, context));

        if (adClickListner != null) {
            // Create display text
            Spannable displayedSpan = new SpannableString(displayText);

            ClickableSpan clickable = new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    adClickListner.openAd(textSpan);
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
