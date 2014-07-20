/*******************************************************************************
 *     This file is part of AndFChat.
 *
 *     AndFChat is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     AndFChat is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with AndFChat.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


package com.andfchat.core.data;

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
import com.andfchat.core.util.BBCodeReader;
import com.andfchat.core.util.SmileyReader;
import com.andfchat.frontend.util.NameSpannable;

public class ChatEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static int DATE_CHAR_LENGTH = 10;
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("[KK:mm aa]", Locale.ENGLISH);
    private static DateFormat DATE_FORMAT_OLD = new SimpleDateFormat("[dd/MM/yy]", Locale.ENGLISH);

    private final Date date;
    private final FCharacter owner;
    private final ChatEntryType messageType;

    private final String text;
    private Integer stringId = null;
    private Object[] values = null;

    private boolean isOwned = false;

    private transient Spannable spannedText;
    private transient boolean isCreated = false;

    public ChatEntry(int stringId, FCharacter owner, ChatEntryType messageType) {
        this(null, owner, messageType);

        this.stringId = stringId;
    }

    public ChatEntry(int stringId, Object[] values, FCharacter owner,ChatEntryType messageType) {
        this(null, owner, messageType);

        this.stringId = stringId;
        this.values = values;
    }

    public ChatEntry(String text, FCharacter owner, ChatEntryType messageType) {
        this.date = new Date();
        this.owner = owner;

        if (text != null && text.startsWith("/warn")) {
            messageType = ChatEntryType.WARNING;
            text = text.substring(5);
        }

        if (messageType == ChatEntryType.MESSAGE) {
            if (text.startsWith("/me")) {
                messageType = ChatEntryType.EMOTE;
                text = text.substring(3);
            }
        }

        this.messageType = messageType;
        this.text = text;
    }

    public void setOwned(boolean isOwned) {
        this.isOwned = isOwned;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public Date getDate() {
        return date;
    }

    public FCharacter getOwner() {
        return owner;
    }

    public ChatEntryType getMessageType() {
        return messageType;
    }

    public Spannable getChatMessage(Context context) {
        if (!isCreated) {
            String dateText = DATE_FORMAT.format(date);
            // older than 24h
            if (date.before(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))) {
                dateText = DATE_FORMAT_OLD.format(date);
            }
            Spannable dateSpan = new SpannableString(dateText);
            dateSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_timestomp_color)), 0, dateText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            dateSpan.setSpan(new RelativeSizeSpan(0.70f), 0, dateText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            // Replace smiles in text
            Spannable withSmileys = SmileyReader.addSmileys(context, getText(context));
            // Add time and username
            SpannableStringBuilder finishedText = new SpannableStringBuilder(dateSpan);
            finishedText.append(messageType.getDelimiterFirstGap());
            finishedText.append(new NameSpannable(owner, messageType.getColorId(), context.getResources()));
            // Delimiter
            finishedText.append(messageType.getDelimeter());
            // Emotes without a /me's should have a space.
            if (messageType == ChatEntryType.EMOTE) {
                if (text.charAt(0) != '\'') {
                    finishedText.append(" ");
                }
            }

            // Message
            finishedText.append(withSmileys);
            // Add overall styles
            if (messageType.getTypeFace() != null) {
                finishedText.setSpan(new StyleSpan(messageType.getTypeFace()), DATE_CHAR_LENGTH, finishedText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }

            spannedText = finishedText;
            isCreated = true;
        }

        return spannedText;
    }

    private Spannable getText(Context context) {
        if (text != null) {
            return BBCodeReader.createSpannableWithBBCode(text, context);
        } else if (stringId != null){
            // Load string only once, than save it at the text variable.
            if (values == null) {
                return BBCodeReader.createSpannableWithBBCode(context.getString(stringId), context);
            } else {
                String unformattedText = context.getString(stringId);
                unformattedText = String.format(unformattedText,  values);

                return BBCodeReader.createSpannableWithBBCode(unformattedText, context);
            }
        }

        return new SpannableString("");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        return result;
    }

    /**
     * If owner and date is equals the message must be the same.
     */
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
        return true;
    }

}
