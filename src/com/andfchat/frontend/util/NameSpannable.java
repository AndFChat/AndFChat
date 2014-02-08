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


package com.andfchat.frontend.util;

import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.andfchat.R;
import com.andfchat.core.data.FlistChar;

public class NameSpannable extends SpannableString {

    public NameSpannable(FlistChar flistChar, Integer colorId, Resources resources) {
        super(flistChar.getName());

        int color;

        if (colorId == null) {
            color = resources.getColor(flistChar.getGender().getColorId());

            if (flistChar.isBookmarked()) {
                color = resources.getColor(R.color.name_bookmark);
            } else if (flistChar.isFriend()) {
                color = resources.getColor(R.color.name_friend);
            }
        } else {
            color = resources.getColor(colorId);
        }

        setSpan(new ForegroundColorSpan(color), 0, length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
