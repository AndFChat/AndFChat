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


package com.andfchat.frontend.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sourcerer.android.ActionItem;
import net.sourcerer.android.QuickActionBar;
import net.sourcerer.android.QuickActionClickListner;
import net.sourcerer.android.QuickActionPreOpenListner;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.ChatEntry;
import com.andfchat.core.data.ChatEntryType;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.history.HistoryManager;
import com.andfchat.core.util.SmileyReader;
import com.andfchat.frontend.application.AndFChatApplication;
import com.andfchat.frontend.events.AndFChatEventManager;
import com.andfchat.frontend.events.ChatroomEventListner;
import com.andfchat.frontend.events.MessageEventListner;
import com.andfchat.frontend.events.UserEventListner;
import com.andfchat.frontend.fragments.ChannelListFragment;
import com.andfchat.frontend.fragments.ChatFragment;
import com.andfchat.frontend.fragments.ChatInputFragment;
import com.andfchat.frontend.fragments.UserListFragment;
import com.andfchat.frontend.menu.AboutAction;
import com.andfchat.frontend.menu.DisconnectAction;
import com.andfchat.frontend.menu.JoinChannelAction;
import com.andfchat.frontend.popup.FListPopupWindow;
import com.andfchat.frontend.util.Exporter;
import com.google.inject.Inject;

public class ChatScreen extends RoboFragmentActivity implements ChatroomEventListner {

    @Inject
    protected CharacterManager charManager;
    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected FlistWebSocketConnection connection;
    @Inject
    protected SessionData sessionData;
    @Inject
    private AndFChatEventManager eventManager;
    @Inject
    protected NotificationManager notificationManager;
    @Inject
    protected HistoryManager historyManager;


    @InjectView(R.id.toggleSidebarLeft)
    private Button toggleSidebarLeft;
    @InjectView(R.id.toggleSidebarRight)
    private Button toggleSidebarRight;
    @InjectView(R.id.chatMessage)
    private EditText inputText;
    @InjectView(R.id.sendButton)
    private Button sendButton;
    @InjectView(R.id.actionButton)
    private Button actionButton;

    // Fragments
    private ChatFragment chat;
    private UserListFragment userList;
    private ChannelListFragment channelList;
    private ChatInputFragment inputFragment;

    // Display height
    private int height;

    private QuickActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        eventManager.clear();
        eventManager.register(this);

        chat = (ChatFragment)getSupportFragmentManager().findFragmentById(R.id.chatFragment);
        userList = (UserListFragment)getSupportFragmentManager().findFragmentById(R.id.userListFragment);
        channelList = (ChannelListFragment)getSupportFragmentManager().findFragmentById(R.id.channelListFragment);
        inputFragment = (ChatInputFragment)getSupportFragmentManager().findFragmentById(R.id.chatInputFragment);
        // Register fragments
        eventManager.register((ChatroomEventListner)chat);
        eventManager.register((MessageEventListner)chat);
        eventManager.register((ChatroomEventListner)userList);
        eventManager.register((UserEventListner)userList);
        eventManager.register(channelList);
        eventManager.register(inputFragment);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;


        actionBar = new QuickActionBar(this);
        actionBar.setOrientation(QuickActionBar.BOTTOM);

        //
        // Show description
        //
        ActionItem showDescprition = new ActionItem(getString(R.string.channel_description), null);
        showDescprition.setQuickActionClickListner(new QuickActionClickListner() {

            @Override
            public void onClick(ActionItem item, View view) {
                showDescription();
            }
        });

        showDescprition.setQuickActionPreOpenListner(new QuickActionPreOpenListner() {

            @Override
            public void onPreOpen(ActionItem item) {
                Chatroom chat = chatroomManager.getActiveChat();

                if (chat.isPrivateChat() || chat.isSystemChat()) {
                    item.setVisibility(View.GONE);
                }
                else {
                    item.setVisibility(View.VISIBLE);
                }
            }
        });
        actionBar.addActionItem(showDescprition);

        //
        // Export active chat
        //
        ActionItem exportActiveChat = new ActionItem(getString(R.string.export_text), null);
        exportActiveChat.setQuickActionClickListner(new QuickActionClickListner() {

            @Override
            public void onClick(ActionItem item, View view) {
                exportChat();
            }
        });

        exportActiveChat.setQuickActionPreOpenListner(new QuickActionPreOpenListner() {

            @Override
            public void onPreOpen(ActionItem item) {
                Chatroom chat = chatroomManager.getActiveChat();
                item.setEnabled(chat.isSystemChat() == false);
            }
        });
        actionBar.addActionItem(exportActiveChat);


        //
        // Leave active chat
        //
        ActionItem leaveActiveChat = new ActionItem(getString(R.string.leave_channel), null);
        leaveActiveChat.setQuickActionClickListner(new QuickActionClickListner() {

            @Override
            public void onClick(ActionItem item, View view) {
                leaveActiveChat();
            }
        });

        leaveActiveChat.setQuickActionPreOpenListner(new QuickActionPreOpenListner() {

            @Override
            public void onPreOpen(ActionItem item) {
                Chatroom chat = chatroomManager.getActiveChat();
                if (chat.isCloseable()) {
                    item.setVisibility(View.VISIBLE);
                }
                else {
                    item.setVisibility(View.GONE);
                }
            }
        });
        actionBar.addActionItem(leaveActiveChat);

        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.show(actionButton);
            }
        });
    }

    public void leaveActiveChat() {
        Chatroom activeChat = chatroomManager.getActiveChat();
        if (activeChat != null) {
            connection.leaveChannel(activeChat);
        }
    }

    public void toggleSidebarRight(View v) {
        if (userList.toggleVisibility()) {
            toggleSidebarRight.setText(R.string.arrows_right);
        } else {
            toggleSidebarRight.setText(R.string.arrows_left);
        }
    }

    public void toggleSidebarLeft(View v) {
        if (channelList.toggleVisibility()) {
            toggleSidebarLeft.setText(R.string.arrows_left);
        } else {
            toggleSidebarLeft.setText(R.string.arrows_right);
        }
    }

    public void showDescription() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.popup_description, null);

        int width = (int)(chat.getView().getWidth() * 0.8f);
        int scaledHeight = (int)(height * 0.7f);

        final PopupWindow descriptionPopup = new FListPopupWindow(layout, width, scaledHeight);
        descriptionPopup.showAtLocation(chat.getView(), Gravity.CENTER, 0, 0);

        final TextView descriptionText = (TextView)layout.findViewById(R.id.descriptionText);
        descriptionText.setText(SmileyReader.addSmileys(this, chatroomManager.getActiveChat().getDescription()));
        // Enable touching/clicking links in text
        descriptionText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onEvent(Chatroom chatroom, ChatroomEventType type) {
        if (type == ChatroomEventType.ACTIVE) {
            if (chatroom.isSystemChat()) {
                toggleSidebarRight.setVisibility(View.GONE);
            } else {
                toggleSidebarRight.setVisibility(View.VISIBLE);

                if (chatroom.isPrivateChat()) {
                    toggleSidebarRight.setVisibility(View.GONE);
                }
            }

            if (chatroom.isPrivateChat() && chatroom.getRecipient().getStatusMsg() != null) {
                setChannelTitle(chatroom.getName() + " - " + chatroom.getRecipient().getStatusMsg());
            } else {
                setChannelTitle(chatroom.getName());
            }
        }
    }

    private void setChannelTitle(String name) {
        this.setTitle(name);
    }

    public void exportChat() {
        String filename =  "FListLog-" + chatroomManager.getActiveChat().getName() + "-" + System.currentTimeMillis() + ".txt";
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);

        try {
            path.mkdirs();

            OutputStream os = new FileOutputStream(file);
            os.write(Exporter.exportText(this, chatroomManager.getActiveChat()));
            os.close();

            ChatEntry entry = new ChatEntry("Sucessfully exported to the download dictory, filename: " + filename, charManager.findCharacter(CharacterManager.USER_SYSTEM), ChatEntryType.MESSAGE);
            chatroomManager.addMessage(chatroomManager.getActiveChat(), entry);

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } catch (IOException e) {
            Ln.w("ExternalStorage", "Error writing " + file, e);
            // TODO: String!
            ChatEntry entry = new ChatEntry("Can't write output, download directory doesn't exist!", charManager.findCharacter(CharacterManager.USER_SYSTEM), ChatEntryType.ERROR);
            chatroomManager.addMessage(chatroomManager.getActiveChat(), entry);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionData.setIsVisible(true);

        // Reload chat
        if (chatroomManager.getActiveChat() != null) {
            chatroomManager.setActiveChat(chatroomManager.getActiveChat());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sessionData.setIsVisible(false);
        historyManager.saveHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(AndFChatApplication.LED_NOTIFICATION_ID);
        eventManager.clear();
        Ln.i("Disconnecting!");
        if (connection.isConnected()) {
            connection.closeConnection(this);
        }
        else {
            sessionData.clear();
            chatroomManager.clear();
            charManager.clear();
            eventManager.clear();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        // Do smaller chat height on displayed keyboard the height is determined by display size.
        switch (item.getItemId()) {
            case R.id.action_add_chat:
                JoinChannelAction.open(this, chat.getView(), height);
                return true;
            case R.id.action_open_friendlist:
                //FriendListAction.open(this, chat.getView(), height);
                return true;
            case R.id.action_disconnect:
                DisconnectAction.disconnect(this);
                return true;
            case R.id.action_open_settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            case R.id.action_about:
                AboutAction.open(this, chat.getView(), height);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Hides the soft keyboard if user is touching anywhere else.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {
                inputFragment.hideKeyboard();
            }
        }

        return ret;
    }
}
