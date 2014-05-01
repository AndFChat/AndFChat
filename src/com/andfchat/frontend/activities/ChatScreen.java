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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import roboguice.activity.RoboFragmentActivity;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.history.HistoryManager;
import com.andfchat.core.util.SmileyReader;
import com.andfchat.frontend.adapter.ChatActionListAdapter;
import com.andfchat.frontend.application.AndFChatApplication;
import com.andfchat.frontend.fragments.ChannelListFragment;
import com.andfchat.frontend.fragments.ChatFragment;
import com.andfchat.frontend.fragments.ChatInputFragment;
import com.andfchat.frontend.fragments.UserListFragment;
import com.andfchat.frontend.menu.AboutAction;
import com.andfchat.frontend.menu.DisconnectAction;
import com.andfchat.frontend.menu.FriendListAction;
import com.andfchat.frontend.menu.JoinChannelAction;
import com.andfchat.frontend.popup.FListPopupWindow;
import com.google.inject.Inject;

public class ChatScreen extends RoboFragmentActivity {

    public enum ChatAction {
        DEFAULT(R.string.actions, 0),
        DESCRIPTION(R.string.channel_description, 1),
        LEAVE(R.string.leave_channel, 10);

        int id;
        Integer weight;

        private ChatAction(int id, Integer weight) {
            this.id = id;
            this.weight = weight;
        }

        public int getId() {
            return id;
        }

        public Integer getWeight() {
            return weight;
        }
    }

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
    @Inject
    private EventManager eventManager;
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
    @InjectView(R.id.actionSpinner)
    private Spinner actionSpinner;

    // Fragments
    private ChatFragment chat;
    private UserListFragment userList;
    private ChannelListFragment channelList;
    private ChatInputFragment inputFragment;

    private boolean paused = true;
    // Display size
    private int width;
    private int height;

    private ChatActionListAdapter chatActionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        chat = (ChatFragment)getSupportFragmentManager().findFragmentById(R.id.chatFragment);
        userList = (UserListFragment)getSupportFragmentManager().findFragmentById(R.id.userListFragment);
        channelList = (ChannelListFragment)getSupportFragmentManager().findFragmentById(R.id.channelListFragment);
        inputFragment = (ChatInputFragment)getSupportFragmentManager().findFragmentById(R.id.chatInputFragment);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        List<ChatAction> actionList = new ArrayList<ChatAction>();

        actionList.add(ChatAction.DEFAULT);
        actionList.add(ChatAction.DESCRIPTION);
        actionList.add(ChatAction.LEAVE);

        Collections.sort(actionList, new ChatActionsSorter());

        chatActionAdapter = new ChatActionListAdapter(this, actionList);

        actionSpinner.setAdapter(chatActionAdapter);

        actionSpinner.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    switch (chatActionAdapter.getItem(i)) {
                        case DESCRIPTION:
                            showDescription();
                            break;
                        case LEAVE:
                            leaveActiveChat();
                            break;
                        case DEFAULT:
                            break;
                    }

                    actionSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                actionSpinner.setSelection(0);
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

    /**
     * Listen to setActiveChat-Events
     */
    protected void setActiveChat(@Observes Chatroom chatroom) {
        Ln.v("Active chat set event is called!");

        List<ChatAction> actionList = new ArrayList<ChatAction>();

        actionList.add(ChatAction.DEFAULT);

        if (chatroom.isSystemChat()) {
            toggleSidebarRight.setVisibility(View.GONE);
        } else {
            toggleSidebarRight.setVisibility(View.VISIBLE);
            actionList.add(ChatAction.LEAVE);

            if (chatroom.isPrivateChat()) {
                toggleSidebarRight.setVisibility(View.GONE);
            } else {
                actionList.add(ChatAction.DESCRIPTION);
            }
        }

        chatActionAdapter.clear();
        Collections.sort(actionList, new ChatActionsSorter());
        chatActionAdapter.addAll(actionList);

        if (chatroom.isPrivateChat() && chatroom.getRecipient().getStatusMsg() != null) {
            setChannelTitle(chatroom.getName() + " - " + chatroom.getRecipient().getStatusMsg());
        } else {
            setChannelTitle(chatroom.getName());
        }
    }

    private void setChannelTitle(String name) {
        this.setTitle(name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionData.setIsVisible(true);
        this.paused = false;

        if (chatroomManager.getActiveChat() != null) {
            eventManager.fire(chatroomManager.getActiveChat());
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                while(!paused) {
                    try {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                channelList.refreshChannels();
                            }
                        });

                        // If no Chatroom is open, try to open the first one
                        if (chatroomManager.getActiveChat() == null) {
                            for (final Chatroom chatroom : chatroomManager.getChatRooms()) {

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
                        if (chatroomManager.getActiveChat() != null) {
                            // Active chat don't need new messages
                            chatroomManager.getActiveChat().setHasNewMessage(false);
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
    protected void onStop() {
        super.onStop();
        sessionData.setIsVisible(false);
        historyManager.saveHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(AndFChatApplication.LED_NOTIFICATION_ID);
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

    public class ChatActionsSorter implements Comparator<ChatAction> {

        @Override
        public int compare(ChatAction lhs, ChatAction rhs) {
            return lhs.getWeight().compareTo(rhs.getWeight());
        }

    }
}
