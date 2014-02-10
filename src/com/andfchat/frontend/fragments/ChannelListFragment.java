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


package com.andfchat.frontend.fragments;

import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.andfchat.R;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.frontend.adapter.ChatroomListAdapter;
import com.google.inject.Inject;

public class ChannelListFragment extends RoboFragment {

    @Inject
    private ChatroomManager chatroomManager;

    @InjectView(R.id.channelList)
    private ListView chatroomList;

    private ChatroomListAdapter chatroomListAdapter;

    private boolean isVisible = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_channels, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatroomListAdapter = new ChatroomListAdapter(getActivity(), chatroomManager.getChatRooms());
        chatroomList.setAdapter(chatroomListAdapter);
    }

    protected void setActiveChat(@Observes Chatroom chatroom) {
        Ln.v("Active chat set event is called!");
        chatroomListAdapter.notifyDataSetChanged();
    }

    public void refreshChannels() {
        boolean isChanged = chatroomManager.isChanged();

        // Refresh new message alert
        for (Chatroom room : chatroomManager.getChatRooms()) {
            if (room.hasChanged() && (chatroomManager.getActiveChat() == null || room.getId() != chatroomManager.getActiveChat().getId())) {
                isChanged = true;
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
