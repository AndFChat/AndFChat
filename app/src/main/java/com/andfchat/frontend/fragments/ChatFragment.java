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
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.messages.ChatEntry;
import com.andfchat.frontend.adapter.ChatEntryListAdapter;
import com.andfchat.frontend.events.AndFChatEventManager;
import com.andfchat.frontend.events.ChatroomEventListener;
import com.andfchat.frontend.events.MessageEventListener;
import com.google.inject.Inject;

public class ChatFragment extends RoboFragment implements ChatroomEventListener, MessageEventListener {

    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected FlistWebSocketConnection connection;
    @Inject
    protected AndFChatEventManager eventManager;
    @Inject
    protected SessionData sessionData;

    @InjectView(R.id.chat)
    private ListView chatListView;

    private ChatEntryListAdapter chatListData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        float textSize = getResources().getDimension(sessionData.getSessionSettings().getChatTextSize().getTextSizeId());
        chatListData.setTextSize(textSize);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        { // Chat window setup
            float textSize = getResources().getDimension(sessionData.getSessionSettings().getChatTextSize().getTextSizeId());
            chatListData = new ChatEntryListAdapter(getActivity(), textSize);
            chatListView.setAdapter(chatListData);
            // Autoscroll to bottom
            chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
            // Stack chat from bottom to top
            chatListView.setStackFromBottom(true);
        }
    }

    @Override
    public void onEvent(final ChatEntry entry, Chatroom chatroom) {
        if (chatroom.equals(chatroomManager.getActiveChat())) {
            synchronized(this) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        chatListData.add(entry);
                    }
                });
            }
        }
    }

    @Override
    public void onEvent(Chatroom chatroom, ChatroomEventType type) {
        if (type == ChatroomEventType.ACTIVE) {
            List<ChatEntry> messages = new ArrayList<ChatEntry>();

            if (chatroom != null) {
                messages = chatroom.getLastMessages(chatroom.getMaxiumEntries());
            }

            // Set messages
            chatListData.clear();
            chatListData.addAll(messages);
            // Scroll to last message
            if (messages.size() > 0) {
                chatListView.setSelection(messages.size() - 1);
            }
        }
    }

    public void clear() {
        chatListData.clear();
    }

}