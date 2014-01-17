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

import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.homebrewn.flistchat.R;
import com.homebrewn.flistchat.backend.data.StaticDataContainer;
import com.homebrewn.flistchat.core.data.Chatroom;
import com.homebrewn.flistchat.frontend.activities.MainScreen;

public class ChatroomListAdapter extends ArrayAdapter<Chatroom> {
    private MainScreen activity;

    public ChatroomListAdapter(MainScreen activity, List<Chatroom> entries) {
        super(activity, R.layout.list_item_chat, entries);
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        
        final Chatroom Chatroom = getItem(position);
        
        LayoutInflater inflater = activity.getLayoutInflater();
        rowView = inflater.inflate(R.layout.list_item_chat, null);        
        
        rowView.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                activity.setActiveChat(Chatroom);
                notifyDataSetChanged();
            }
        });
        
        TextView title = (TextView)rowView.findViewById(R.id.ChatroomName);
        title.setText("#" + Chatroom.getName());
        
        if (StaticDataContainer.sessionData.getChatroomHandler().getActiveChat().getId().equals(Chatroom.getId())) {
            rowView.setBackgroundColor(Color.BLUE);
        } 
        else if (Chatroom.hasNewMessage()) {
            rowView.setBackgroundColor(Color.RED);
        }
        
        
        return rowView;
    }

    public void removeChatroom(Chatroom Chatroom) {
        this.remove(Chatroom);
        notifyDataSetChanged();
    }

    public void addChatroom(Chatroom Chatroom) {
        this.add(Chatroom);
        notifyDataSetChanged();
    }
    
}
