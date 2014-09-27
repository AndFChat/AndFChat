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

import net.sourcerer.quickaction.ActionItem;
import net.sourcerer.quickaction.QuickActionBar;
import net.sourcerer.quickaction.QuickActionOnClickListener;
import net.sourcerer.quickaction.QuickActionOnOpenListener;
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
import com.andfchat.frontend.events.AndFChatEventManager;
import com.andfchat.frontend.events.ChatroomEventListener;
import com.andfchat.frontend.events.MessageEventListener;
import com.andfchat.frontend.events.UserEventListener;
import com.andfchat.frontend.fragments.ChannelListFragment;
import com.andfchat.frontend.fragments.ChatFragment;
import com.andfchat.frontend.fragments.ChatInputFragment;
import com.andfchat.frontend.fragments.UserListFragment;
import com.andfchat.frontend.menu.AboutAction;
import com.andfchat.frontend.menu.DisconnectAction;
import com.andfchat.frontend.menu.FriendListAction;
import com.andfchat.frontend.menu.JoinChannelAction;
import com.andfchat.frontend.popup.FListPopupWindow;
import com.andfchat.frontend.util.Exporter;
import com.andfchat.frontend.util.FlistAlertDialog;
import com.google.inject.Inject;

public class ChatScreen extends RoboFragmentActivity implements ChatroomEventListener {

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
    private int width;

    private QuickActionBar actionBar;

    // Saving stuff


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(sessionData.getSessionSettings().getTheme());
        setContentView(R.layout.activity_chat_screen);

        toggleSidebarRight.setSelected(true);

        eventManager.clear();
        eventManager.register(this);

        // Fetch fragments
        chat = (ChatFragment)getSupportFragmentManager().findFragmentById(R.id.chatFragment);
        userList = (UserListFragment)getSupportFragmentManager().findFragmentById(R.id.userListFragment);
        channelList = (ChannelListFragment)getSupportFragmentManager().findFragmentById(R.id.channelListFragment);
        inputFragment = (ChatInputFragment)getSupportFragmentManager().findFragmentById(R.id.chatInputFragment);

        // Register fragments
        eventManager.register((ChatroomEventListener)chat);
        eventManager.register((MessageEventListener)chat);
        eventManager.register((ChatroomEventListener)userList);
        eventManager.register((UserEventListener)userList);
        eventManager.register(channelList);
        eventManager.register(inputFragment);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;
        width = size.x;

        actionBar = new QuickActionBar(this);
        actionBar.setOrientation(QuickActionBar.BOTTOM);

        //
        // Show description
        //
        ActionItem showDescprition = new ActionItem(getString(R.string.channel_description), getResources().getDrawable(R.drawable.ic_description));
        showDescprition.setQuickActionClickListener(new QuickActionOnClickListener() {

            @Override
            public void onClick(ActionItem item, View view) {
                showDescription();
            }
        });

        showDescprition.setQuickActionOnOpenListener(new QuickActionOnOpenListener() {

            @Override
            public void onOpen(ActionItem item) {
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
        ActionItem exportActiveChat = new ActionItem(getString(R.string.export_text), getResources().getDrawable(R.drawable.ic_export));
        exportActiveChat.setQuickActionClickListener(new QuickActionOnClickListener() {

            @Override
            public void onClick(ActionItem item, View view) {
                exportChat();
            }
        });

        exportActiveChat.setQuickActionOnOpenListener(new QuickActionOnOpenListener() {

            @Override
            public void onOpen(ActionItem item) {
                Chatroom chat = chatroomManager.getActiveChat();
                item.setEnabled(chat.isSystemChat() == false);
            }
        });
        actionBar.addActionItem(exportActiveChat);


        //
        // Leave active chat
        //
        ActionItem leaveActiveChat = new ActionItem(getString(R.string.leave_channel), getResources().getDrawable(R.drawable.ic_leave));
        leaveActiveChat.setQuickActionClickListener(new QuickActionOnClickListener() {

            @Override
            public void onClick(ActionItem item, View view) {
                leaveActiveChat();
            }
        });

        leaveActiveChat.setQuickActionOnOpenListener(new QuickActionOnOpenListener() {

            @Override
            public void onOpen(ActionItem item) {
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
        toggleSidebarRight.setSelected(userList.toggleVisibility());
    }

    public void toggleSidebarLeft(View v) {
        toggleSidebarLeft.setSelected(!channelList.toggleVisibility());
    }

    public void showDescription() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.popup_description, null);

        int height = (int)(this.height * 0.8f);
        int width = (int)(this.width * 0.8f);

        final PopupWindow descriptionPopup = new FListPopupWindow(layout, width, height);
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
                JoinChannelAction.open(this, chat.getView());
                return true;
            case R.id.action_open_friendlist:
                FriendListAction.open(this, chat.getView());
                return true;
            case R.id.action_disconnect:
                DisconnectAction.disconnect(this);
                return true;
            case R.id.action_open_settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            case R.id.action_about:
                AboutAction.open(this, chat.getView());
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
                inputFragment.saveEntry();
            }
        }

        return ret;
    }

    @Override
    public void onBackPressed() {
        FlistAlertDialog dialog = new FlistAlertDialog(this, getResources().getString(R.string.question_back)) {

            @Override
            public void onYes() {
                connection.closeConnection(ChatScreen.this, false);
                goBackToCharSelection();
            }

            @Override
            public void onNo() {}
        };

        dialog.show();
    }

    private void goBackToCharSelection() {
        Ln.d("Back to char Selection");
        finish();
        super.onBackPressed();
    }


}
