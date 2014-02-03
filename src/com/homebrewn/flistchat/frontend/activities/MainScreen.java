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


package com.homebrewn.flistchat.frontend.activities;

import roboguice.activity.RoboFragmentActivity;
import roboguice.event.Observes;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.inject.Inject;
import com.homebrewn.flistchat.R;
import com.homebrewn.flistchat.core.connection.FlistWebSocketConnection;
import com.homebrewn.flistchat.core.data.Chatroom;
import com.homebrewn.flistchat.core.data.ChatroomManager;
import com.homebrewn.flistchat.core.data.SessionData;
import com.homebrewn.flistchat.core.util.SmileyReader;
import com.homebrewn.flistchat.frontend.fragments.ChannelListFragment;
import com.homebrewn.flistchat.frontend.fragments.ChatFragment;
import com.homebrewn.flistchat.frontend.fragments.UserListFragment;
import com.homebrewn.flistchat.frontend.menu.DisconnectAction;
import com.homebrewn.flistchat.frontend.menu.FriendListAction;
import com.homebrewn.flistchat.frontend.menu.JoinChannelAction;
import com.homebrewn.flistchat.frontend.popup.FListPopupWindow;

public class MainScreen extends RoboFragmentActivity {

    // Sleep time between two loops
    private static final long TICK_TIME = 250;

    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected FlistWebSocketConnection connection;
    @Inject
    protected SessionData sessionData;

    @InjectView(R.id.toggleSidebarLeft)
    private Button toggleSidebarLeft;
    @InjectView(R.id.toggleSidebarRight)
    private Button toggleSidebarRight;

    // Fragments
    private ChatFragment chat;
    private UserListFragment userList;
    private ChannelListFragment channelList;

    private boolean paused = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        chat = (ChatFragment)getSupportFragmentManager().findFragmentById(R.id.chatFragment);
        userList = (UserListFragment)getSupportFragmentManager().findFragmentById(R.id.userListFragment);
        channelList = (ChannelListFragment)getSupportFragmentManager().findFragmentById(R.id.channelListFragment);
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
        // enable touching/clicking links in text
        descriptionText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Listen to setActiveChat-Events
     */
    protected void setActiveChat(@Observes Chatroom chatroom) {
        Ln.v("Active chat set event is called!");

        if (chatroom.isSystemChat()) {
            setVisibilityForLeaveChannelButton(View.GONE);
            setVisibilityForChannelDescriptionButton(View.GONE);
        } else {
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
        switch (item.getItemId()) {
            case R.id.action_add_chat:
                JoinChannelAction.open(this);
                return true;
            case R.id.action_open_friendlist:
                FriendListAction.open(this);
                return true;
            case R.id.action_disconnect:
                DisconnectAction.disconnect(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
