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
