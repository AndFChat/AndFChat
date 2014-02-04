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


package com.homebrewn.flistchat.frontend.fragments;

import java.util.ArrayList;
import java.util.List;

import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.inject.Inject;
import com.homebrewn.flistchat.R;
import com.homebrewn.flistchat.core.data.Chatroom;
import com.homebrewn.flistchat.core.data.ChatroomManager;
import com.homebrewn.flistchat.frontend.adapter.ChatroomListAdapter;

public class ChannelListFragment extends RoboFragment {

    @Inject
    private ChatroomManager chatroomManager;

    @InjectView(R.id.channelList)
    private ListView chatroomList;

    private ChatroomListAdapter chatroomListAdapter;

    private final List<Chatroom> openChatrooms = new ArrayList<Chatroom>();

    private boolean isVisible = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_channels, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatroomListAdapter = new ChatroomListAdapter(getActivity(), openChatrooms);
        chatroomList.setAdapter(chatroomListAdapter);
    }

    protected void setActiveChat(@Observes Chatroom chatroom) {
        Ln.v("Active chat set event is called!");
        chatroomListAdapter.notifyDataSetChanged();
    }

    public void refreshChannels() {
        boolean isChanged = false;
        // Remove Chatrooms left chatrooms from list
        for (Chatroom room : chatroomManager.getRemovedRooms()) {
            chatroomListAdapter.removeChatroom(room);
            isChanged = true;
        }
        chatroomManager.clearRemovedRooms();

        // Add new Chatrooms if there are new one
        for (Chatroom room : chatroomManager.getNewRooms()) {
            Ln.d("Found new channel '" + room.getName() + "'");
            chatroomListAdapter.addChatroom(room);
            isChanged = true;
        }
        chatroomManager.clearNewRooms();

        // Refresh new message alert
        for (int i = 0; i < openChatrooms.size(); i++) {
            Chatroom room = openChatrooms.get(i);

            if (room.hasNewMessage() && room.getId() != chatroomManager.getActiveChat().getId()) {
                isChanged = true;
                // refresh is done, no need for more notifies.
                break;
            }
        }

        if (isChanged) {
            Ln.v("Redraw channel list");
            chatroomListAdapter.notifyDataSetChanged();
        }
    }

    public boolean toggleVisibility() {
        if (isVisible) {
            getView().setVisibility(View.GONE);
            isVisible = false;
        } else {
            getView().setVisibility(View.VISIBLE);
            isVisible = true;
        }
        return isVisible;
    }
}
