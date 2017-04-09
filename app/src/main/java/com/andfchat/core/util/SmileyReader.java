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


package com.andfchat.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.andfchat.R;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ImageSpan;

public class SmileyReader {
    
    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();
    
    static {
        emoticons.put(Pattern.compile(Pattern.quote(":heart:")), R.drawable.emo_heart);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-angry:")), R.drawable.emo_angry);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-blush:")), R.drawable.emo_blush);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-cry:")), R.drawable.emo_cry);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-evil:")), R.drawable.emo_evil);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-gasp:")), R.drawable.emo_gasp);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-happy:")), R.drawable.emo_happy);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-meh:")), R.drawable.emo_meh);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-neutral:")), R.drawable.emo_neutral);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-ooh:")), R.drawable.emo_ooh);        
        emoticons.put(Pattern.compile(Pattern.quote(":lif-purr:")), R.drawable.emo_purr);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-roll:")), R.drawable.emo_roll);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-sad:")), R.drawable.emo_sad);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-sick:")), R.drawable.emo_sick);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-smile:")), R.drawable.emo_smile);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-whee:")), R.drawable.emo_whee);        
        emoticons.put(Pattern.compile(Pattern.quote(":lif-wink:")), R.drawable.emo_wink);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-wtf:")), R.drawable.emo_wtf);
        emoticons.put(Pattern.compile(Pattern.quote(":lif-yawn:")), R.drawable.emo_yawn);
        emoticons.put(Pattern.compile(Pattern.quote(":cake:")), R.drawable.emo_cake);
    }
    
    public static Spannable addSmileys(Context context, Spannable text) {
        for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(text);
            while (matcher.find()) {
                text.setSpan(new ImageSpan(context, entry.getValue()), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
                
        return text;        
    }

}
