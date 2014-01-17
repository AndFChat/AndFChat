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


package com.homebrewn.flistchat.core.util;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.webkit.URLUtil;

public class BBCodeReader {
    
    public enum BBCodeType {
        BOLD("b", new StyleSpan(Typeface.BOLD), new SimpleTextMatcher()),
        ITALICS("i", new StyleSpan(Typeface.ITALIC), new SimpleTextMatcher()),
        UNDERLINE("u", new UnderlineSpan(), new SimpleTextMatcher()),
        STRIKETHROUGH("s", new StrikethroughSpan(), new SimpleTextMatcher()),
        SUPERSCRIPT("sup", new SuperscriptSpan(), new SimpleTextMatcher()),
        SUBSCRIPT("sub", new SubscriptSpan(), new SimpleTextMatcher()),
        COLOR("color", null, new VariableTextMatcher()),
        LINK("url", null, new VariableTextMatcher());
        
        public final String bbCode;
        public final Object spannableType;        
        private final Matcher matcher;
        
        BBCodeType(String bbCode, Object spannableType, Matcher matcher) {
            this.bbCode = bbCode;
            this.spannableType = spannableType;
            this.matcher = matcher;
        }
        
        public boolean isStart(String text) {
            return matcher.isStart(text, this);
        }
        
        public boolean isEnd(String text) {
            return matcher.isEnd(text, this);
        }
        
        /**
         * Not Nullsave!
         */
        public String getVariable(String text) {
            if (matcher instanceof VariableTextMatcher) {
                return ((VariableTextMatcher)matcher).getVariable(text, this);
            } else {
                return null;
            }
        }
    }
    
    public static Spannable createSpannableWithBBCode(String text) {
        // Position in text 
        int pointer = 0;
        
        List<Span> spans = new ArrayList<Span>();
        
        while (pointer < text.length()) {
            int start = text.indexOf("[", pointer);
            int end = text.indexOf("]", start);
            
            // If no [ or ] found, break loop, no BBCode 
            if (start == -1 || end == -1) {
                break;
            } else {
                // Getting the ] at the end of the token.
                end++;
            }
            
            String token = text.subSequence(start, end).toString();
            Log.v("BBC", "Found: '" + token + "'");
            
            boolean found = false;
            // Test each BBCodeType for matching start/end
            for (BBCodeType bbCodeType : BBCodeType.values()) {
                
                if (bbCodeType.isStart(token)) {
                    spans.add(new Span(start, bbCodeType, token));                    
                    found = true;
                }
                else if (bbCodeType.isEnd(token)) {
                    // Stacked so from top to bottom
                    for (int i = spans.size() - 1; i >= 0; i--) {
                        Span span = spans.get(i);
                        if (span.bbCodeType == bbCodeType && !span.closed()) {
                            // End = Start of the token!
                            span.setEnd(start);
                            found = true;
                            break;
                        }
                    }
                }
                
                if (found) {
                    break;
                }
            }
            
            if (found) {
                // Remove BBCode
                text = text.substring(0, start) + text.substring(end);                
            }
            
            // Move pointer
            pointer = start + 1;
        }
        
        Spannable textSpan = new SpannableString(text);
        for (Span span : spans) {
            if (span.closed()) {
                Log.v("BBC", "ADD span: " + span.toString());
                span.addToText(textSpan);
            }
        }
        
        // Add all unclosed tags again
        SpannableStringBuilder polishedText = new SpannableStringBuilder(textSpan);
        for (int i = spans.size() - 1; i >= 0; i--) {
            Span span = spans.get(i);
            if (!span.closed()) {
                polishedText.insert(span.start, "[" + span.bbCodeType.bbCode + "]");
            }
        }
        
        return polishedText;
    }

    public static class Span {
        
        public final int start;
        public final BBCodeType bbCodeType;        
        private final String token;
        
        private Integer end = null;
        
        public Span(int start, BBCodeType type, String token) {
            this.start = start;
            this.bbCodeType = type;
            this.token = token;
        }
        
        public void setEnd(int end) {
            this.end = end;
        }
        
        public boolean closed() {
            return end != null;
        }
        
        public void addToText(Spannable text) {
            if (bbCodeType == BBCodeType.COLOR) {
                String colorText = bbCodeType.getVariable(token);
                if (colorText != null) {
                    try {
                        int color = Color.parseColor(colorText);
                        text.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    } 
                    catch (IllegalArgumentException ex) {
                        // If color can't be parsed, return without adding span.
                        Log.v("BBC", "Can't parse color from: '" + colorText +"'");
                        return;
                    };
                }
            }
            else if (bbCodeType == BBCodeType.LINK) {
                String link = bbCodeType.getVariable(token);
                // IF no url is given, check String between [url]here[/url]
                if (link == null) {
                    link = URLUtil.guessUrl(text.subSequence(start, end).toString());
                }
                
                if (link != null && URLUtil.isValidUrl(link)) {
                    text.setSpan(new URLSpan(link), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
            else {
                text.setSpan(bbCodeType.spannableType, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        
        public String toString() {
            return "[" + bbCodeType.name() + " from: " + start + " to: " + end + "]";
        }
    }
    
    public interface Matcher {
        public boolean isStart(String text, BBCodeType type);
        public boolean isEnd(String text, BBCodeType type);
    }
    
    public static class SimpleTextMatcher implements Matcher {

        @Override
        public boolean isStart(String text, BBCodeType type) {
            return text.equals("[" + type.bbCode + "]");
        }

        @Override
        public boolean isEnd(String text, BBCodeType type) {
            return text.equals("[/" + type.bbCode + "]");
        }
    }
    
    public static class VariableTextMatcher implements Matcher {

        @Override
        public boolean isStart(String text, BBCodeType type) {
            return text.startsWith("[" + type.bbCode) && text.endsWith("]");
        }

        @Override
        public boolean isEnd(String text, BBCodeType type) {
            return text.equals("[/" + type.bbCode + "]");
        }
        
        public String getVariable(String text, BBCodeType type) {
            if (isStart(text, type) && text.contains("=")) {
                String newText = text.replace("[" + type.bbCode + "=", "");
                return newText.substring(0, newText.length() - 1);
            } else {
                return null;
            }
        }
    }
    
}
