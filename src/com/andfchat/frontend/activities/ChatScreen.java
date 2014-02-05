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

import roboguice.activity.RoboFragmentActivity;
import roboguice.event.Observes;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.util.SmileyReader;
import com.andfchat.frontend.fragments.ChannelListFragment;
import com.andfchat.frontend.fragments.ChatFragment;
import com.andfchat.frontend.fragments.UserListFragment;
import com.andfchat.frontend.menu.AboutAction;
import com.andfchat.frontend.menu.DisconnectAction;
import com.andfchat.frontend.menu.FriendListAction;
import com.andfchat.frontend.menu.JoinChannelAction;
import com.andfchat.frontend.popup.FListPopupWindow;
import com.google.inject.Inject;

public class ChatScreen extends RoboFragmentActivity {

    // Sleep time between two loops
    private static final long TICK_TIME = 250;

    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected FlistWebSocketConnection connection;
    @Inject
    protected SessionData sessionData;
    @Inject
    private InputMethodManager inputManager;

    @InjectView(R.id.toggleSidebarLeft)
    private Button toggleSidebarLeft;
    @InjectView(R.id.toggleSidebarRight)
    private Button toggleSidebarRight;

    // Fragments
    private ChatFragment chat;
    private UserListFragment userList;
    private ChannelListFragment channelList;

    private boolean paused = true;
    // Display size
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        chat = (ChatFragment)getSupportFragmentManager().findFragmentById(R.id.chatFragment);
        userList = (UserListFragment)getSupportFragmentManager().findFragmentById(R.id.userListFragment);
        channelList = (ChannelListFragment)getSupportFragmentManager().findFragmentById(R.id.channelListFragment);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    public void leaveActiveChat(View v) {
        Chatroom activeChat = chatroomManager.getActiveChat();
        if (activeChat != null) {
            connection.leaveChannel(activeChat);
        }
    }

    public void toggleSidebarRight(View v) {
        if (userList.toggleVisibility()) {
            toggleSidebarRight.setText(R.string.hide);
        } else {
            toggleSidebarRight.setText(R.string.show);
        }
    }

    public void toggleSidebarLeft(View v) {
        if (channelList.toggleVisibility()) {
            toggleSidebarLeft.setText(R.string.hide);
        } else {
            toggleSidebarLeft.setText(R.string.show);
        }
    }

    public void showDescription(View v) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.popup_description, null);
        final PopupWindow descriptionPopup = new FListPopupWindow(layout, chat.getView(), 0.8f);
        descriptionPopup.showAtLocation(chat.getView(), Gravity.CENTER, 0, 0);

        final TextView descriptionText = (TextView)layout.findViewById(R.id.descriptionText);
        descriptionText.setText(SmileyReader.addSmileys(this, chatroomManager.getActiveChat().getDescription()));
        // Enable touching/clicking links in text
        descriptionText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Listen to setActiveChat-Events
     */
    protected void setActiveChat(@Observes Chatroom chatroom) {
        Ln.v("Active chat set event is called!");

        if (chatroom.isSystemChat()) {
            toggleSidebarRight.setVisibility(View.GONE);
            setVisibilityForLeaveChannelButton(View.GONE);
            setVisibilityForChannelDescriptionButton(View.GONE);
        } else {
            toggleSidebarRight.setVisibility(View.VISIBLE);
            setVisibilityForLeaveChannelButton(View.VISIBLE);

            if (chatroom.isPrivateChat()) {
                setVisibilityForChannelDescriptionButton(View.GONE);
            } else {
                setVisibilityForChannelDescriptionButton(View.VISIBLE);
            }
        }

        if (chatroom.isPrivateChat() && chatroom.getRecipient().getStatusMsg() != null) {
            setChannelTitle(chatroom.getName() + " - " + chatroom.getRecipient().getStatusMsg());
        } else {
            setChannelTitle(chatroom.getName());
        }
    }

    private void setVisibilityForLeaveChannelButton(int visibility) {
        ((Button)this.findViewById(R.id.leaveButton)).setVisibility(visibility);
    }

    private void setVisibilityForChannelDescriptionButton(int visibility) {
        ((Button)this.findViewById(R.id.showDescription)).setVisibility(visibility);
    }

    private void setChannelTitle(String name) {
        this.setTitle(name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.paused = false;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ChatroomManager chatrooms = chatroomManager;

                while(!paused) {
                    try {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                channelList.refreshChannels();
                            }
                        });

                        // If no Chatroom is open, try to open the first one
                        if (chatrooms.getActiveChat() == null) {
                            for (String key : chatrooms.getChatroomKeys()) {
                                final Chatroom chatroom = chatrooms.getChatroom(key);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Ln.d("Set new active chat!");
                                        chatroomManager.setActiveChat(chatroom);
                                        eventManager.fire(chatroom);
                                    }
                                });

                                break;
                            }
                        }

                        // If an chat is open do some tasks
                        if (chatrooms.getActiveChat() != null) {

                            // Reseting "new messages blink"
                            chatrooms.getActiveChat().setHasNewMessage(false);
                            // Refresh chat log and user list
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chat.refreshChat();
                                    userList.refreshList();
                                }
                            });
                        }
                        Thread.sleep(TICK_TIME);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };

        new Thread(runnable).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
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
                FriendListAction.open(this, chat.getView(), height);
                return true;
            case R.id.action_disconnect:
                DisconnectAction.disconnect(this);
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
                chat.hideKeyboard();
            }
        }

        return ret;
    }
}
