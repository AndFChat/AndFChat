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


package com.andfchat.frontend.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import roboguice.RoboGuice;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.connection.ServerToken;
import com.andfchat.core.data.Channel;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.frontend.popup.FListPopupWindow;
import com.google.inject.Inject;

public class JoinChannelAction {

    public static void open(Activity activity, View parent) {

        final ChatroomManager chatroomManager = RoboGuice.getInjector(activity).getInstance(ChatroomManager.class);
        final FlistWebSocketConnection connection = RoboGuice.getInjector(activity).getInstance(FlistWebSocketConnection.class);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.popup_add_channels, null);

        Display display = activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int height = (int)(size.y * 0.8f);
        int width = (int)(size.x * 0.8f);

        final PopupWindow popupWindow = new FListPopupWindow(layout, width, height);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

        final ListView channelList = (ListView)layout.findViewById(R.id.channlesToJoin);

        final CheckboxAdapter adapter = new CheckboxAdapter(activity, chatroomManager.getOfficialChannels());
        channelList.setAdapter(adapter);

        Button showPublicChannel = (Button)layout.findViewById(R.id.publicChannelButton);
        showPublicChannel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                adapter.clear();
                adapter.replaceChannels(chatroomManager.getOfficialChannels());
                adapter.setPrivate(false);
            }
        });

        Button showPrivateChannel = (Button)layout.findViewById(R.id.privateChannelButton);
        showPrivateChannel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                adapter.clear();
                FeedbackListener feedbackListener = new FeedbackListener() {

                    @Override
                    public void onResponse(String response) {
                        adapter.replaceChannels(chatroomManager.getPrivateChannelNames());
                        adapter.setPrivate(true);
                    }

                    @Override
                    public void onError(Exception ex) {}
                };

                connection.registerFeedbackListner(ServerToken.ORS, feedbackListener);
                connection.askForPrivateChannel();
            }
        });
    }

    public static class CheckboxAdapter extends ArrayAdapter<String> {

        @Inject
        private ChatroomManager chatroomManager;
        @Inject
        private FlistWebSocketConnection connection;

        private List<String> channelNames = new ArrayList<String>();
        private boolean isPrivat = false;

        public CheckboxAdapter(Context context, Set<String> content) {
            super(context, R.layout.list_item_checkbox);
            this.replaceChannels(content);

            RoboGuice.getInjector(context).injectMembers(this);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final String channelName = channelNames.get(position);

            View rowView = convertView;

            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_checkbox, null);

            TextView textView = (TextView)rowView.findViewById(R.id.itemText);
            textView.setText(this.getItem(position));

            final CheckBox checkbox = (CheckBox)rowView.findViewById(R.id.checkbox);
            checkbox.setChecked(chatroomManager.getChatroom(channelName) != null);
            checkbox.setClickable(false);

            rowView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!checkbox.isChecked()) {
                        if (isPrivat) {
                            Channel channel = chatroomManager.getPrivateChannelByName(channelName);
                            if (channel != null) {
                                connection.joinChannel(channel.getChannelId());
                            }
                        } else {
                            connection.joinChannel(channelName);
                        }
                        checkbox.setChecked(true);
                    }
                }
            });

            return rowView;
        }

        public List<String> getCheckedItems() {
            return channelNames;
        }

        public void setPrivate(boolean isPrivate) {
            this.isPrivat = isPrivate;
        }

        public void replaceChannels(Set<String> content) {
            List<String> sortedContent = new ArrayList<String>(content);
            Collections.sort(sortedContent);

            channelNames = sortedContent;

            for (String text : sortedContent) {
                this.add(text);
            }
        }
    }
}
