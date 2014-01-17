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


package com.homebrewn.flistchat.core.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.homebrewn.flistchat.core.util.BBCodeReader;
import com.homebrewn.flistchat.core.util.SmileyReader;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

public class ChatEntry {
    
    public enum ChatEntryType {
        MESSAGE,
        EMOTE(null, Typeface.ITALIC, ""),
        ERROR(Color.RED, Typeface.BOLD, " "),
        NOTATION_CONNECT(Color.GRAY),
        NOTATION_DISCONNECT(Color.GRAY),
        NOTATION_LEFT(Color.GRAY),
        NOTATION_JOINED(Color.GRAY),
        NOTATION_SYSTEM(Color.GRAY),
        NOTATION_STATUS(Color.GRAY);
        
        private Integer color = null;
        private Integer typeFace = null;
        private String delimeter = ": ";
        
        private ChatEntryType(int color) {
            this.color = color;   
        }
        
        private ChatEntryType(Integer color, int typeFace, String delimeter) {
            this.typeFace = typeFace;
            this.color = color;  
            this.delimeter = delimeter;
        }
        
        private ChatEntryType() {
            this.color = null;
        }
        
        public Integer getColor() {
            return color;
        }
        
        public Integer getTypeFace() {
            return typeFace;
        }
        
        public String getDelimeter() {
            return delimeter;
        }
    }
    
    private final static int DATE_CHAR_LENGTH = 10;
    
    private final Date date;
    private final Spannable text;
    private final FlistChar owner;
    private final ChatEntryType messageType;

    private static DateFormat df = new SimpleDateFormat("[KK:mm aa]", Locale.US);

    public ChatEntry(String text, FlistChar owner, Date date, ChatEntryType messageType) {
        this.date = date;        
        this.owner = owner;
        
        if (messageType == ChatEntryType.MESSAGE) {
            if (text.startsWith("/me")) {
                messageType = ChatEntryType.EMOTE;
                text = text.substring(3);
                if (!text.startsWith("'")) {
                    text = " " + text;
                }
            }
        }
        
        this.messageType = messageType;
        this.text = BBCodeReader.createSpannableWithBBCode(text);
    }

    public Date getDate() {
        return date;
    }

    public FlistChar getOwner() {
        return owner;
    }

    public Spannable getChatMessage(Context context) {
        String dateText = df.format(date);
        Spannable dateSpan = new SpannableString(dateText);
        dateSpan.setSpan(new ForegroundColorSpan(Color.GRAY), 0, dateText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dateSpan.setSpan(new RelativeSizeSpan(0.75f), 0, dateText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);       
        // Replace smileys in text
        Spannable withSmileys = SmileyReader.addSmileys(context, this.text);
        // Time and username
        SpannableStringBuilder finishedText = new SpannableStringBuilder(dateSpan).append(" ").append(owner.toFormattedText(messageType.getColor()));
        // Delimeter
        finishedText.append(messageType.getDelimeter());
        // Message
        finishedText.append(withSmileys);
        // Add overall styles
        if (messageType.typeFace != null) {
            finishedText.setSpan(new StyleSpan(messageType.typeFace), DATE_CHAR_LENGTH, finishedText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        
        return finishedText;
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
