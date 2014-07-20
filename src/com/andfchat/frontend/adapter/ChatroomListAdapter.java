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

import roboguice.RoboGuice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.google.inject.Inject;

public class ChatroomListAdapter extends ArrayAdapter<Chatroom> {

    @Inject
    private ChatroomManager chatroomManager;

    private final int activeColor;
    private final int attentionColor;
    private final int color;

    public ChatroomListAdapter(Context context, List<Chatroom> entries) {
        super(context, R.layout.list_item_chat, entries);

        RoboGuice.getInjector(context).injectMembers(this);

        activeColor = context.getResources().getColor(R.color.background_chat_tab_active);
        attentionColor = context.getResources().getColor(R.color.background_chat_tab_attention);
        color = context.getResources().getColor(R.color.background_chat_tab);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        final Chatroom chatroom = getItem(position);

        if (chatroom == null) {
            return rowView;
        }

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.list_item_chat, null);

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chatroomManager.setActiveChat(chatroom);
            }
        });

        TextView title = (TextView)rowView.findViewById(R.id.ChatroomName);
        title.setText("#" + chatroom.getName());

        if (chatroomManager.getActiveChat() != null && chatroomManager.getActiveChat().getId().equals(chatroom.getId())) {
            rowView.setBackgroundColor(activeColor);
        }
        else if (chatroom.hasNewMessage() && chatroom.isSystemChat() == false) {
            rowView.setBackgroundColor(attentionColor);
        }
        else {
            rowView.setBackgroundColor(color);
        }

        return rowView;
    }
}
