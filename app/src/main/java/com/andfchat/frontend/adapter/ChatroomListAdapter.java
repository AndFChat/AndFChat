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

import java.util.Collections;
import java.util.List;

import roboguice.RoboGuice;
import roboguice.util.Ln;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.SessionData;
import com.google.inject.Inject;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class ChatroomListAdapter extends ArrayAdapter<Chatroom> {

    @Inject
    private ChatroomManager chatroomManager;
    @Inject
    private SessionData sessionData;

    Picasso picasso;

    private final int activeColor;
    private final int attentionColor;
    private final int standardColor;
    private final int statusColor;

    public ChatroomListAdapter(Context context, List<Chatroom> entries) {
        super(context, R.layout.list_item_chat, entries);

        RoboGuice.getInjector(context).injectMembers(this);

        TypedArray colorArray = context.getTheme().obtainStyledAttributes(new int[]{
                R.attr.BackgroundChatTab,
                R.attr.BackgroundChatTabActive,
                R.attr.BackgroundChatTabAttention,
                R.attr.BackgroundChatTabStatus});

        standardColor = colorArray.getColor(0, 0);
        activeColor = colorArray.getColor(1, 0);
        attentionColor = colorArray.getColor(2, 0);
        statusColor = colorArray.getColor(3, 0);

        colorArray.recycle();

        OkHttpClient client = new OkHttpClient();
        //client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));

        picasso = new Picasso.Builder(getContext())
                .downloader(new OkHttp3Downloader(client))
                .build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        final Chatroom chatroom = getItem(position);

        if (chatroom == null) {
            return rowView;
        }

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_chat, null);
        }

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chatroomManager.setActiveChat(chatroom);
            }
        });

        TextView title = (TextView)rowView.findViewById(R.id.ChatroomName);
        title.setText(chatroom.getName()); //# was unneeded.

        if (chatroomManager.isActiveChat(chatroom)) {
            rowView.setBackgroundColor(activeColor);
            title.setSelected(true);
        }
        else if (chatroom.hasNewMessage() && !chatroom.isSystemChat()) {
            rowView.setBackgroundColor(attentionColor);
            title.setSelected(false);
        }
        else if (chatroom.hasNewStatus() && !chatroom.isSystemChat()) {
            rowView.setBackgroundColor(statusColor);
            title.setSelected(false);
        }
        else {
            rowView.setBackgroundColor(standardColor);
            title.setSelected(false);
        }

        ImageView image = (ImageView)rowView.findViewById(R.id.ChatroomImage);
        // sessionData.getSessionSettings().showAvatarPictures()
        if (chatroom.isPrivateChat() && chatroom.getShowAvatar()) {
            String name = chatroom.getCharacters().get(0).getName().toLowerCase().replace(" ", "%20");
            String url = "https://static.f-list.net/images/avatar/" + name + ".png";

            picasso.load(url)
                    .placeholder(R.drawable.chat_priv_icon)
                    .error(R.drawable.chat_priv_icon)
                    .into(image);
        }
        else if (chatroom.isSystemChat()) {
            image.setImageResource(R.drawable.chat_sys_icon);
        }
        else {
            image.setImageResource(R.drawable.chat_room_icon);
        }

        return rowView;
    }
}
