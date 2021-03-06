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

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.util.Console;
import com.andfchat.frontend.events.ChatroomEventListener;
import com.google.inject.Inject;

public class ChatInputFragment extends RoboFragment implements ChatroomEventListener {

    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected FlistWebSocketConnection connection;
    @Inject
    protected Console commands;
    @Inject
    private InputMethodManager inputManager;

    @InjectView(R.id.chatMessage)
    private EditText inputText;
    @InjectView(R.id.sendButton)
    private Button sendButton;

    private long lastMessage = System.currentTimeMillis();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_input, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        { // Setup send button
            sendButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                }
            });
        }
    }

    private void sendMessage() {
        Chatroom activeChat = chatroomManager.getActiveChat();

        // Cant send a message without active chat
        if (activeChat == null) {
            return;
        }

        // Ignore empty messages
        if (inputText.getText().toString().trim().length() == 0) {
            cleanInput(activeChat);
            return;
        }
        // Console commands and text commands like /help /open and /close shouldn't be sent to the server.
        if (commands.checkForCommands(inputText.getText().toString()) || activeChat.isSystemChat()) {
            cleanInput(activeChat);
            return;
        }
        else if ((System.currentTimeMillis() - lastMessage > 1000)) {
            if (activeChat.isPrivateChat()) {
                connection.sendPrivateMessage(activeChat.getRecipient().getName(), inputText.getText().toString().trim());
            } else {
                connection.sendMessageToChannel(activeChat, inputText.getText().toString().trim());
            }

            lastMessage = System.currentTimeMillis();
        }
        else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.fast_message, Snackbar.LENGTH_SHORT).show();
        }

        // Reset input
        cleanInput(activeChat);
    }

    private void cleanInput(Chatroom activeChat) {
        // Reset input
        inputText.setText("");
        activeChat.setEntry("");
    }

    public void sendTextAsAd() {
        // Ignore empty messages
        if (inputText.getText().toString().trim().length() == 0 ) {
            return;
        }

        connection.sendAdToChannel(chatroomManager.getActiveChat(), inputText.getText().toString().trim());
        cleanInput(chatroomManager.getActiveChat());
    }

    public void hideKeyboard() {
        inputManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
    }

    @Override
    public void onEvent(Chatroom chatroom, ChatroomEventType type) {
        if (type == ChatroomEventType.ACTIVE && inputText != null) {
            // Set maximum text length
            inputText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(chatroom.getMaxTextLength())});
            // Set last text
            inputText.setText(chatroom.getEntry());
        }
    }

    public void saveEntry() {
        if (chatroomManager.getActiveChat() != null) {
            chatroomManager.getActiveChat().setEntry(inputText.getEditableText().toString());
        }
    }
}
