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

import android.graphics.Typeface;

import com.andfchat.R;

/**
 * This enum is all about the different stiles of chat messages that can be displayed.
 * Every type can have his own color/style and delimeter.
 * @author AndFChat
 */
public enum ChatEntryType {
    MESSAGE,
    WARNING,
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
