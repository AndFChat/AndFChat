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
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.connection.handler.PrivateMessageHandler;
import com.andfchat.core.connection.handler.VariableHandler.Variable;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.messages.ChatEntryFactory;
import com.andfchat.core.util.FlistCharComparator;
import com.andfchat.frontend.util.NameSpannable;
import com.google.inject.Inject;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class FriendListAdapter extends ArrayAdapter<FCharacter> {

    private final static FlistCharComparator COMPARATOR = new FlistCharComparator();

    private static Picasso picasso = null;

    @Inject
    private ChatroomManager chatroomManager;
    @Inject
    private SessionData sessionData;
    @Inject
    private ChatEntryFactory entryFactory;

    private final List<FCharacter> chars;
    private OkHttpClient client = new OkHttpClient();

    ImageView image;

    public FriendListAdapter(final Context context, List<FCharacter> chars) {
        super(context, R.layout.list_item_friend, chars);

        if (chars.size() > 1) {
            this.sort(COMPARATOR);
            sortList();
        }

        this.chars = chars;

        RoboGuice.getInjector(context).injectMembers(this);

        if (picasso == null)  {
            picasso = new Picasso.Builder(getContext())
                    .downloader(new OkHttp3Downloader(client))
                    .build();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FCharacter character = this.getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_friend, null);
        }

        final View rowView = convertView;
        // Set username
        TextView textView = (TextView)rowView.findViewById(R.id.itemText);
        textView.setText(new NameSpannable(character, null, getContext().getResources()));
        textView.setSelected(true);

        // Set icon
        ImageView itemIcon = (ImageView)rowView.findViewById(R.id.itemIcon);

        switch (character.getStatus()) {
            case ONLINE:
                itemIcon.setBackgroundResource(R.drawable.icon_blue);
                break;
            case BUSY:
                itemIcon.setBackgroundResource(R.drawable.icon_orange);
                break;
            case DND:
                itemIcon.setBackgroundResource(R.drawable.icon_red);
                break;
            case LOOKING:
                itemIcon.setBackgroundResource(R.drawable.icon_green);
                break;
            case AWAY:
                itemIcon.setBackgroundResource(R.drawable.icon_grey);
                break;
            default:
                itemIcon.setBackgroundResource(R.drawable.icon_blue);
        }


        //client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));
        image = (ImageView)rowView.findViewById(R.id.ChatroomImage);
        String name = character.getName().toLowerCase().replace(" ", "%20");
        String url = "https://static.f-list.net/images/avatar/" + name + ".png";

        picasso.load(url)
                .placeholder(R.drawable.chat_room_icon)
                .error(R.drawable.chat_room_icon)
                .into(image);

        // Set button
        Button pmButton = (Button)rowView.findViewById(R.id.pmButton);
        pmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Chatroom chatroom;
                if (chatroomManager.hasOpenPrivateConversation(character) == false) {
                    int maxTextLength = sessionData.getIntVariable(Variable.priv_max);
                    chatroom = PrivateMessageHandler.openPrivateChat(chatroomManager, character, maxTextLength, sessionData.getSessionSettings().showAvatarPictures());

                    if (character.getStatusMsg() != null && character.getStatusMsg().length() > 0) {
                        chatroomManager.addMessage(chatroom, entryFactory.getStatusInfo(character));
                    }
                } else {
                    chatroom = chatroomManager.getPrivateChatFor(character);
                }

                chatroomManager.setActiveChat(chatroom);
            }
        });
        sortList();

        return rowView;
    }

    @Override
    public void add(FCharacter flistChar) {
        if (flistChar == null) {
            return;
        }

        boolean added = false;
        for (int i = 0; i < chars.size(); i++) {
            if (COMPARATOR.compare(chars.get(i), flistChar) == 0) {
                chars.add(i, flistChar);
                added = true;
                break;
            }
        }

        if (!added) {
            chars.add(flistChar);
        }
        sortList();

        notifyDataSetChanged();
    }

    public void sortList(){
        this.sort(COMPARATOR);
    }

}
