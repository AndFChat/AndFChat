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


package com.homebrewn.flistchat.frontend.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.homebrewn.flistchat.R;
import com.homebrewn.flistchat.core.data.ChatEntry;

public class ChatEntryListAdapter extends ArrayAdapter<ChatEntry> {
    private Activity activity;

    public ChatEntryListAdapter(Activity activity) {
        super(activity, R.layout.list_item_message, new ArrayList<ChatEntry>());
        this.activity = activity;
    }

    public ChatEntryListAdapter(Activity activity, List<ChatEntry> entries) {
        super(activity, R.layout.list_item_message, entries);
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        LayoutInflater inflater = activity.getLayoutInflater();
        rowView = inflater.inflate(R.layout.list_item_message, null);

        TextView textView = (TextView)rowView.findViewById(R.id.itemText);
        textView.setText(this.getItem(position).getChatMessage(activity));

        // Follow links to browser
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        
        return rowView;
    }

    public long getLastMessageTime() {
        return this.getItem(this.getCount() - 1).getDate().getTime();
    }

}
