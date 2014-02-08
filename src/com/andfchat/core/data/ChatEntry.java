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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
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

public class ChatEntry {

    public enum ChatEntryType {
        MESSAGE,
        EMOTE(null, Typeface.ITALIC, "", " * "),
        ERROR(R.color.name_error, Typeface.BOLD, ":"),
        NOTATION_CONNECT(R.color.name_annotation, " "),
        NOTATION_DISCONNECT(R.color.name_annotation, " "),
        NOTATION_LEFT(R.color.name_annotation, " "),
        NOTATION_JOINED(R.color.name_annotation, " "),
        NOTATION_SYSTEM(R.color.name_annotation),
        NOTATION_STATUS(R.color.name_annotation, " "),
        NOTATION_DICE(R.color.name_annotation, " "),
        AD(R.color.name_ad);

        private Integer colorId = null;
        private Integer typeFace = null;
        private String delimiter = ": ";
        private String delimiterFirstGap = " ";

        private ChatEntryType(int color) {
            this.colorId = color;
        }

        private ChatEntryType(Integer colorId, String delimeter) {
            this.colorId = colorId;
            this.delimiter = delimeter;
        }

        private ChatEntryType(Integer color, int typeFace, String delimiter) {
            this.typeFace = typeFace;
            this.colorId = color;
            this.delimiter = delimiter;
        }

        private ChatEntryType(Integer color, int typeFace, String delimiter, String delimiterFirstGap) {
            this.typeFace = typeFace;
            this.colorId = color;
            this.delimiter = delimiter;
            this.delimiterFirstGap = delimiterFirstGap;
        }

        private ChatEntryType() {
            this.colorId = null;
        }

        public Integer getColorId() {
            return colorId;
        }

        public Integer getTypeFace() {
            return typeFace;
        }

        public String getDelimeter() {
            return delimiter;
        }

        public String getDelimiterFirstGap() {
            return delimiterFirstGap;
        }
    }

    private final static int DATE_CHAR_LENGTH = 10;
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("[KK:mm aa]", Locale.US);

    private final Date date;
    private final FlistChar owner;
    private final ChatEntryType messageType;

    private final String text;
    private Integer stringId = null;
    private Object[] values = null;

    private Spannable spannedText;
    private boolean isCreated = false;


    public ChatEntry(int stringId, FlistChar owner, Date date, ChatEntryType messageType) {
        this(null, owner, date, messageType);

        this.stringId = stringId;
    }

    public ChatEntry(int stringId, Object[] values, FlistChar owner, Date date, ChatEntryType messageType) {
        this(null, owner, date, messageType);

        this.stringId = stringId;
        this.values = values;
    }

    public ChatEntry(String text, FlistChar owner, Date date, ChatEntryType messageType) {
        this.date = date;
        this.owner = owner;

        if (messageType == ChatEntryType.MESSAGE) {
            if (text.startsWith("/me")) {
                messageType = ChatEntryType.EMOTE;
                text = text.substring(3);
            }
        }

        this.messageType = messageType;
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public FlistChar getOwner() {
        return owner;
    }

    public Spannable getChatMessage(Context context) {
        if (!isCreated) {
            String dateText = DATE_FORMAT.format(date);
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
            // Message
            finishedText.append(withSmileys);
            // Add overall styles
            if (messageType.typeFace != null) {
                finishedText.setSpan(new StyleSpan(messageType.typeFace), DATE_CHAR_LENGTH, finishedText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
