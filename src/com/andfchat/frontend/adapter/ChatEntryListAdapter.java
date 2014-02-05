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

import java.util.List;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andfchat.core.data.ChatEntry;
import com.andfchat.R;

public class ChatEntryListAdapter extends ArrayAdapter<ChatEntry> {

    public ChatEntryListAdapter(Context context, List<ChatEntry> entries) {
        super(context, R.layout.list_item_message, entries);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.list_item_message, null);

        TextView textView = (TextView)rowView.findViewById(R.id.itemText);
        textView.setText(this.getItem(position).getChatMessage(getContext()));

        // Follow links to browser
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        return rowView;
    }

    public long getLastMessageTime() {
        if (this.getCount() > 0) {
            return this.getItem(this.getCount() - 1).getDate().getTime();
        } else {
            return 0;
        }
    }

}
