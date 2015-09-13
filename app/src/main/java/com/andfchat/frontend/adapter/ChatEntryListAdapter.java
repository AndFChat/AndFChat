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


package com.andfchat.frontend.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.data.messages.AdEntry;
import com.andfchat.core.data.messages.ChatEntry;
import com.andfchat.core.data.messages.ChatEntryFactory;

public class ChatEntryListAdapter extends ArrayAdapter<ChatEntry> {

    private final int colorLine;
    private final int colorOwned;
    private final int colorSystem;
    private final int colorWarning;
    private final int colorAttention;

    private float textSize;

    private ChatEntryFactory.AdClickListener clickListener;

    private boolean showAdText = false;

    public ChatEntryListAdapter(Context context, float textSize) {
        super(context, R.layout.list_item_message, new ArrayList<ChatEntry>());

        this.clickListener = clickListener;

        // Load theme colors
        TypedArray styles = context.getTheme().obtainStyledAttributes(new int[]{
                R.attr.BackgroundChatLine,
                R.attr.BackgroundChatLineSelf,
                R.attr.BackgroundChatSystem,
                R.attr.BackgroundChatWarning,
                R.attr.BackgroundChatAttention});

        colorLine = styles.getColor(0, 0);
        colorOwned = styles.getColor(1, 0);
        colorSystem = styles.getColor(2, 0);
        colorWarning = styles.getColor(3, 0);
        colorAttention = styles.getColor(4, 0);

        this.textSize = textSize;

        styles.recycle();
    }

    public void setShowAdText(boolean showAdText) {
        this.showAdText = showAdText;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_message, null);
        }

        ChatEntry entry = getItem(position);

        TextView textView = (TextView)rowView.findViewById(R.id.itemText);
        textView.setText(entry.getChatMessage(getContext()));
        // Set text size
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        View backgroundView = rowView.findViewById(R.id.messageItem);
        // Adding the right colour
        switch (entry.getMessageType()) {
        case WARNING:
            backgroundView.setBackgroundColor(colorWarning);
            break;
        case ERROR:
            backgroundView.setBackgroundColor(colorAttention);
            break;
        case MESSAGE:
            if (getItem(position).isOwned()) {
                backgroundView.setBackgroundColor(colorOwned);
            }
            else {
                backgroundView.setBackgroundColor(colorLine);
            }
            break;
        case EMOTE:
            backgroundView.setBackgroundColor(colorLine);
            break;
        case AD:
            ((AdEntry)entry).setShowText(showAdText);
            backgroundView.setBackgroundColor(colorSystem);
            break;
        default:
            backgroundView.setBackgroundColor(colorSystem);
            break;
        }

        textView.setText(entry.getChatMessage(getContext()));

        // Follow links to browser
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        ImageView iconImage = (ImageView)rowView.findViewById(R.id.itemIcon);
        if (entry.getIcon() != null) {
            iconImage.setVisibility(View.VISIBLE);
            iconImage.setImageResource(entry.getIcon());
        }
        else {
            iconImage.setVisibility(View.GONE);
        }

        return rowView;
    }

    public void setTextSize(float textSize) {
        boolean changed = textSize != this.textSize;
        this.textSize = textSize;

        if (changed) {
            // Force to redraw
            notifyDataSetChanged();
        }
    }

}
