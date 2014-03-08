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

import java.util.ArrayList;

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
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FlistChar;
import com.andfchat.frontend.adapter.MemberListAdapter;
import com.google.inject.Inject;

public class UserListFragment extends RoboFragment {

    @Inject
    private ChatroomManager chatroomManager;
    @Inject
    private CharacterManager characterManager;

    @InjectView(R.id.userlist)
    private ListView memberListView;

    private MemberListAdapter memberListData;

    private boolean isVisible = true;
    private boolean canBeDisplayed = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_user_list, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        memberListData = new MemberListAdapter(getActivity(), new ArrayList<FlistChar>());
        memberListView.setAdapter(memberListData);
    }

    protected void setActiveChat(@Observes Chatroom chatroom) {
        Ln.v("Active chat set event is called!");
        canBeDisplayed = chatroom.showUserList();

        if (canBeDisplayed && isVisible) {
            getView().setVisibility(View.VISIBLE);
        } else {
            getView().setVisibility(View.GONE);
        }

        memberListData = new MemberListAdapter(getActivity(), chatroom.getCharacters());
        memberListView.setAdapter(memberListData);
    }

    public void refreshList() {
        Chatroom activeChat = chatroomManager.getActiveChat();

        if (activeChat != null) {
            if (activeChat.hasChangedUser() || characterManager.isStatusChanged()) {
                memberListData.notifyDataSetChanged();
            }
        }
    }

    public boolean toggleVisibility() {
        if (canBeDisplayed) {
            if (isVisible) {
                getView().setVisibility(View.GONE);
                isVisible = false;
            } else {
                getView().setVisibility(View.VISIBLE);
                isVisible = true;
            }
        }
        return isVisible;
    }
}
