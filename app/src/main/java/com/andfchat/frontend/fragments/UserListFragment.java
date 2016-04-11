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
import java.util.Collections;
import java.util.List;

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
import com.andfchat.core.data.FCharacter;
import com.andfchat.frontend.adapter.MemberListAdapter;
import com.andfchat.frontend.events.ChatroomEventListener;
import com.andfchat.frontend.events.UserEventListener;
import com.google.inject.Inject;

public class UserListFragment extends RoboFragment implements ChatroomEventListener, UserEventListener {

    @Inject
    private ChatroomManager chatroomManager;

    @InjectView(R.id.userlist)
    private ListView memberListView;

    private MemberListAdapter memberListData;

    private boolean isVisible = true;
    private boolean canBeDisplayed = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        memberListData = new MemberListAdapter(getActivity(), new ArrayList<FCharacter>());
        memberListView.setAdapter(memberListData);
    }

    @Override
    public void onEvent(FCharacter character, UserEventType type, Chatroom chatroom) {
        if (chatroomManager.isActiveChat(chatroom)) {
            Ln.d(character);

            if (type == UserEventType.JOINED) {
                memberListData.add(character);
            }
            else {
                memberListData.remove(character);
            }
        }
    }

    @Override
    public void onEvent(Chatroom chatroom, ChatroomEventType type) {
        if (type == ChatroomEventType.ACTIVE && getView()!= null) {
            canBeDisplayed = chatroom.showUserList();

            if (canBeDisplayed && isVisible) {
                getView().setVisibility(View.VISIBLE);
            } else {
                getView().setVisibility(View.GONE);
            }

            List<FCharacter>  characters = new ArrayList<FCharacter>(chatroom.getCharacters());
            Collections.sort(characters, MemberListAdapter.COMPARATOR);

            memberListData = new MemberListAdapter(getActivity(), characters);
            memberListView.setAdapter(memberListData);
        }
    }

    public boolean toggleVisibility() {
        if (canBeDisplayed && getView() != null) {
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

    public void clear() {
        memberListData.clear();
    }
}
