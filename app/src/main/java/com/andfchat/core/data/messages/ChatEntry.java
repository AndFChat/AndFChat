package com.andfchat.core.data.messages;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.andfchat.R;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.util.BBCodeReader;
import com.andfchat.core.util.SmileyReader;
import com.andfchat.frontend.util.NameSpannable;

public abstract class ChatEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum MessageType {
        MESSAGE,
        WARNING,
        EMOTE,
        ERROR,
        NOTATION,
        AD
    }

    protected final static int DATE_CHAR_LENGTH = 10;

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("[KK:mm aa]", Locale.ENGLISH);
    protected static final DateFormat DATE_FORMAT_OLD = new SimpleDateFormat("[dd/MM/yy]", Locale.ENGLISH);

    protected final Date date;
    protected final MessageType type;
    protected final FCharacter owner;

    protected boolean isOwned = false;

    protected String delimeterBetweenDateAndName = " ";
    protected String delimeterBetweenNameAndText = ": ";

    private transient Spannable spannedText = null;

    public Integer iconId = null;

    public ChatEntry(FCharacter owner, MessageType type) {
        this.owner = owner;
        this.type = type;

        this.date = new Date();
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned(boolean isOwned) {
        this.isOwned = isOwned;
    }

    public FCharacter getOwner() {
        return owner;
    }

    public Date getDate() {
        return date;
    }

    public MessageType getMessageType() {
        return type;
    }

    public void setIcon(Integer iconId) {
        this.iconId = iconId;
    }

    public Integer getIcon() {
        return iconId;
    }

    public final Spannable getChatMessage(Context context) {
        if (spannedText == null) {
            Spannable dateSpan = createDateSpannable(context);
            Spannable textSpan = createText(context);

            // Time
            SpannableStringBuilder finishedText = new SpannableStringBuilder(dateSpan);
            // Delimiter
            finishedText.append(delimeterBetweenDateAndName);
            // Name
            finishedText.append(new NameSpannable(owner, getNameColorId(), context.getResources()));
            // Delimiter
            finishedText.append(delimeterBetweenNameAndText);
            // Message
            finishedText.append(textSpan);

            // Add overall styles
            if (getTypeFace() != null) {
                finishedText.setSpan(new StyleSpan(getTypeFace()), DATE_CHAR_LENGTH, finishedText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }

            spannedText = finishedText;
        }
        return spannedText;
    }

    protected Spannable createDateSpannable(Context context) {
        String dateText = DATE_FORMAT.format(date);
        // older than 24h
        if (date.before(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))) {
            dateText = DATE_FORMAT_OLD.format(date);
        }
        Spannable dateSpan = new SpannableString(dateText);
        dateSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_timestomp_color)), 0, dateText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dateSpan.setSpan(new RelativeSizeSpan(0.70f), 0, dateText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return dateSpan;
    }

    protected Spannable createText(Context context) {
        String text = getText(context);
        text = BBCodeReader.modifieUrls(text, "http://");
        text = BBCodeReader.modifieUrls(text, "https://");

        Spannable textSpan = BBCodeReader.createSpannableWithBBCode(text, context);
        // Replace smiles in text
        return SmileyReader.addSmileys(context, textSpan);
    }

    protected abstract String getText(Context context);

    protected Integer getTypeFace() {
        return null;
    }

    protected Integer getNameColorId() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChatEntry other = (ChatEntry) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (owner == null) {
            if (other.owner != null)
                return false;
        } else if (!owner.equals(other.owner))
            return false;
        return type == other.type;
    }
}
