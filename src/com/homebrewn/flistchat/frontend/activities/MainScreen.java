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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.homebrewn.flistchat.R;
import com.homebrewn.flistchat.backend.data.StaticDataContainer;
import com.homebrewn.flistchat.core.connection.FeedbackListner;
import com.homebrewn.flistchat.core.connection.FlistHttpClient;
import com.homebrewn.flistchat.core.connection.handler.PrivateMessageHandler;
import com.homebrewn.flistchat.core.data.CharRelation;
import com.homebrewn.flistchat.core.data.ChatEntry;
import com.homebrewn.flistchat.core.data.Chatroom;
import com.homebrewn.flistchat.core.data.ChatroomHandler;
import com.homebrewn.flistchat.core.data.FlistChar;
import com.homebrewn.flistchat.core.util.SmileyReader;
import com.homebrewn.flistchat.frontend.actions.ActionEvent;
import com.homebrewn.flistchat.frontend.actions.CanOpenUserDetails;
import com.homebrewn.flistchat.frontend.actions.SplitScreenWishListner;
import com.homebrewn.flistchat.frontend.adapter.ChatEntryListAdapter;
import com.homebrewn.flistchat.frontend.adapter.ChatroomListAdapter;
import com.homebrewn.flistchat.frontend.adapter.MemberListAdapter;
import com.homebrewn.flistchat.frontend.menu.DisconnectAction;
import com.homebrewn.flistchat.frontend.menu.FriendListAction;
import com.homebrewn.flistchat.frontend.menu.JoinChannelAction;
import com.homebrewn.flistchat.frontend.popup.FListPopupWindow;

public class MainScreen extends Activity implements CanOpenUserDetails {

    // Sleep time between two loops
    private static final long TICK_TIME = 250;
    private static final String DETAIL_URL = "http://www.f-list.net/c/";

    private boolean paused = true;

    private ListView memberListView;
    private ListView chatListView;

    private ArrayAdapter<FlistChar> memberListData;
    private ChatEntryListAdapter chatListData;

    private EditText inputText;

    private long lastMessage = System.currentTimeMillis();

    private View memberListBar;
    private boolean memberListIsOpen = true;

    private ListView ChatroomList;
    private ChatroomListAdapter ChatroomListAdapter;
    private boolean shortCutListIsOpen = true;

    private PopupWindow pwindo = null;

    private final List<Chatroom> openChatrooms = new ArrayList<Chatroom>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        inputText = (EditText)findViewById(R.id.chatMessage);

        memberListView = (ListView)this.findViewById(R.id.chatmember);
        memberListData = new MemberListAdapter(this, this);
        memberListView.setAdapter(memberListData);
        memberListBar = this.findViewById(R.id.memberSidebar);

        { // Chat Room bar on the left side.
            ChatroomList = (ListView)this.findViewById(R.id.sidebareLeft);
            ChatroomListAdapter = new ChatroomListAdapter(this, openChatrooms);
            ChatroomList.setAdapter(ChatroomListAdapter);
        }

        { // Chat window setup
            chatListView = (ListView)findViewById(R.id.chat);

            // Adding touch (wish) listner to hide/show the options and memberlist.
            chatListView.setOnTouchListener(
                new SplitScreenWishListner(
                    new ActionEvent() {
                        @Override
                        public void onAction(boolean value) {
                            toggleShortCutList(value);
                    }
                },
                    new ActionEvent() {
                        @Override
                        public void onAction(boolean value) {
                            toggleMemberList(value);
                        }
                    }
                )
            );

            chatListData = new ChatEntryListAdapter(this, new ArrayList<ChatEntry>());
            chatListView.setAdapter(chatListData);
            // Autoscroll to bottom
            chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
            // Stack chat from bottom to top
            chatListView.setStackFromBottom(true);
        }
    }


    public void sendMessage(View v) {
        if (inputText.getText().toString().length() > 0 && (System.currentTimeMillis() - lastMessage > 2000)) {
            Chatroom activeChat = StaticDataContainer.sessionData.getChatroomHandler().getActiveChat();

            if (activeChat.isPrivateChat()) {
                StaticDataContainer.sessionData.getConnection().sendPrivatMessage(activeChat.getRecipient().getName(), inputText.getText().toString());
            } else {
                StaticDataContainer.sessionData.getConnection().sendMessageToChannel(activeChat, inputText.getText().toString());
            }
            // Reset input
            inputText.setText("");
            lastMessage = System.currentTimeMillis();
        }
        else {
            //TODO: Show error message (input to fast).
        }
    }

    public void leaveActiveChat(View v) {
        Chatroom activeChat = StaticDataContainer.sessionData.getChatroomHandler().getActiveChat();
        // TODO: Leave private chat without deleting "log"
        if (!activeChat.isPrivateChat()) {
            StaticDataContainer.sessionData.getConnection().leaveChannel(activeChat);
        } else {
            StaticDataContainer.sessionData.getChatroomHandler().removeChatroom(activeChat.getId());
        }
    }

    public void showDescription(View v) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.popup_description, null);
        final PopupWindow descriptionPopup = new FListPopupWindow(layout, chatListView, 0.8f);
        descriptionPopup.showAtLocation(this.chatListView, Gravity.CENTER, 0, 0);

        final TextView descriptionText = (TextView)layout.findViewById(R.id.descriptionText);
        descriptionText.setText(SmileyReader.addSmileys(this, StaticDataContainer.sessionData.getChatroomHandler().getActiveChat().getDescription()));
        // enable touching/clicking links in text
        descriptionText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void removeChatroom(Chatroom Chatroom) {
        Log.d("com.homebrewn.flistchat.frontend.activities.MainScreen", "Removing Chatroom: " + Chatroom.getName());
        ChatroomListAdapter.remove(Chatroom);
    }

    public void setActiveChat(Chatroom Chatroom) {
        if (Chatroom.showUserList() && memberListIsOpen) {
            memberListBar.setVisibility(View.VISIBLE);
        } else {
            memberListBar.setVisibility(View.GONE);
        }

        StaticDataContainer.sessionData.getChatroomHandler().setActiveChat(Chatroom);

        memberListData = new MemberListAdapter(this, this, Chatroom.getCharacters());
        memberListView.setAdapter(memberListData);

        List<ChatEntry> messages = new ArrayList<ChatEntry>();

        if (Chatroom != null) {
            messages = Chatroom.getLastMessages(Chatroom.getMaxiumEntries());
        }

        if (Chatroom.isSystemChat()) {
            inputText.setEnabled(false);
            setVisibilityForLeaveChannelButton(View.GONE);
            setVisibilityForChannelDescriptionButton(View.GONE);
        } else {
            inputText.setEnabled(true);
            setVisibilityForLeaveChannelButton(View.VISIBLE);

            if (Chatroom.isPrivateChat()) {
                setVisibilityForChannelDescriptionButton(View.GONE);
            } else {
                setVisibilityForChannelDescriptionButton(View.VISIBLE);
            }
        }

        if (Chatroom.isPrivateChat() && Chatroom.getRecipient().getStatusMsg() != null) {
            setChannelTitle(Chatroom.getName()  + " - " + Chatroom.getRecipient().getStatusMsg());
        } else {
            setChannelTitle(Chatroom.getName());
        }

        chatListData = new ChatEntryListAdapter(this, messages);
        chatListView.setAdapter(chatListData);
    }

    private void setVisibilityForLeaveChannelButton(int visibility) {
        ((Button)this.findViewById(R.id.leaveButton)).setVisibility(visibility);
    }

    private void setVisibilityForChannelDescriptionButton(int visibility) {
        ((Button)this.findViewById(R.id.showDescription)).setVisibility(visibility);
    }

    private void setChannelTitle(String name) {
        ((TextView)this.findViewById(R.id.channelName)).setText(name);
    }


    public void addChat(final Chatroom chat) {

        ChatroomListAdapter.addChatroom(chat);

        if (StaticDataContainer.sessionData.getChatroomHandler().getActiveChat() == null) {
            this.setActiveChat(chat);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.paused = false;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ChatroomHandler Chatrooms = StaticDataContainer.sessionData.getChatroomHandler();

                while(!paused) {
                    try {

                        //
                        // Remove Chatrooms if there are one
                        //
                        for (final Chatroom room : Chatrooms.getRemovedRooms()) {
                            //found an Chatroom which isn't added to tabList
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    removeChatroom(room);
                                }
                            });
                        }

                        Chatrooms.clearRemovedRooms();

                        // If no Chatroom is open, try to open the first one
                        if (Chatrooms.getActiveChat() == null) {
                            for (String key : Chatrooms.getChatroomKeys()) {
                                final Chatroom Chatroom = Chatrooms.getChatroom(key);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        chatListData.clear();
                                        setActiveChat(Chatroom);
                                    }
                                });

                                break;
                            }
                        }

                        //if an chat is open do some tasks
                        if (Chatrooms.getActiveChat() != null) {
                            List<ChatEntry> list;

                            //reseting "new messages blink"
                            Chatrooms.getActiveChat().setHasNewMessage(false);

                            //
                            // refresh chat log
                            //
                            if (chatListData.getCount() == 0) {
                                list = Chatrooms.getActiveChat().getChatEntries();
                            } else {
                                list = Chatrooms.getActiveChat().getChatEntriesSince(chatListData.getLastMessageTime());
                            }

                            if (list.size() > 0) {
                                for (final ChatEntry entry : list) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatListData.add(entry);
                                        }
                                    });

                                }
                            }

                            if (Chatrooms.getActiveChat().getLeftChars().size() != 0 || Chatrooms.getActiveChat().getJoinedChars().size() != 0) {
                                //
                                // Remove left chars
                                //
                                List<FlistChar> leftMembers = Chatrooms.getActiveChat().getLeftChars();
                                if (leftMembers.size() > 0) {
                                    for (final FlistChar entry : leftMembers) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                memberListData.remove(entry);
                                                memberListData.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }

                                //
                                // Add joined chars
                                //
                                List<FlistChar> joinedMembers = Chatrooms.getActiveChat().getJoinedChars();
                                if (joinedMembers.size() > 0) {
                                    for (final FlistChar entry : joinedMembers) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                memberListData.add(entry);
                                                memberListData.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            }
                            else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        memberListData.notifyDataSetChanged();
                                    }
                                });
                            }

                            for (int i = 0; i < openChatrooms.size(); i++) {
                                Chatroom room = openChatrooms.get(i);

                                if (room.hasNewMessage() && room.getId() != StaticDataContainer.sessionData.getChatroomHandler().getActiveChat().getId()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ChatroomListAdapter.notifyDataSetChanged();
                                        }
                                    });

                                    break;
                                }
                            }

                            //
                            // Add new Chatrooms if there are new one
                            //
                            for (final Chatroom room : Chatrooms.getNewRooms()) {
                                //found an Chatroom which isn't added to tabList
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addChat(room);
                                    }
                                });
                            }

                            Chatrooms.clearNewRooms();
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

    public void toggleMemberList(boolean value) {
        if (StaticDataContainer.sessionData.getChatroomHandler().getActiveChat().showUserList()) {
            if (memberListIsOpen && value == false) {
                //close
                memberListBar.setVisibility(View.GONE);
                memberListIsOpen = false;
            } else if (memberListIsOpen == false && value) {
                //open
                memberListBar.setVisibility(View.VISIBLE);
                memberListIsOpen = true;
            }
        }
    }

    public void toggleShortCutList(boolean value) {
        if (shortCutListIsOpen && value == false) {
            //close
            ChatroomList.setVisibility(View.GONE);
            shortCutListIsOpen = false;
        } else if (shortCutListIsOpen == false && value) {
            //open
            ChatroomList.setVisibility(View.VISIBLE);
            shortCutListIsOpen = true;
        }
    }

    @Override
    public void openUserDetails(final FlistChar character) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.popup_profile_info, null);
        pwindo = new PopupWindow(layout);
        pwindo.setFocusable(true);

        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        pwindo.setHeight(layout.getMeasuredHeight());
        pwindo.setWidth(layout.getMeasuredWidth());
        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

        TextView nameText = (TextView)layout.findViewById(R.id.textName);
        nameText.setText(character.toFormattedText());

        TextView genderText = (TextView)layout.findViewById(R.id.textGender);
        genderText.setText(character.getGender().getName());

        Button buttonCloseInfo = (Button)layout.findViewById(R.id.buttonCloseInfo);
        buttonCloseInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeUserDetails();
            }
        });

        Button buttonPM = (Button)layout.findViewById(R.id.buttonPM);
        buttonPM.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Chatroom Chatroom = PrivateMessageHandler.openPrivateChat(StaticDataContainer.sessionData.getChatroomHandler(), character);
                closeUserDetails();
                setActiveChat(Chatroom);
            }
        });

        Button buttonBookmark = (Button)layout.findViewById(R.id.buttonBookmark);
        if (character.isFriend() || character.isBookmarked()) {
            buttonBookmark.setText("UNBOOKMARK");
            buttonBookmark.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    FeedbackListner removeBookmarkFeedback = new FeedbackListner() {

                        @Override
                        public void onResponse(String response) {
                            character.removeRelation(CharRelation.BOOKMARKED);
                        }

                        @Override
                        public void onError(Exception ex) {}

                    };

                    String account = StaticDataContainer.sessionData.getAccount();
                    String ticketId = StaticDataContainer.sessionData.getTicket();
                    String charName = character.getName();

                    new FlistHttpClient().removeBookmark(account, ticketId, charName, removeBookmarkFeedback);
                    closeUserDetails();
                }
            });
        } else {
            buttonBookmark.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    FeedbackListner addBookmarkFeedback = new FeedbackListner() {

                        @Override
                        public void onResponse(String response) {
                            character.addRelation(CharRelation.BOOKMARKED);
                        }

                        @Override
                        public void onError(Exception ex) {}

                    };

                    String account = StaticDataContainer.sessionData.getAccount();
                    String ticketId = StaticDataContainer.sessionData.getTicket();
                    String charName = character.getName();

                    new FlistHttpClient().addBookmark(account, ticketId, charName, addBookmarkFeedback);
                    closeUserDetails();
                }
            });
        }

        Button buttonDetails = (Button)layout.findViewById(R.id.buttonDetails);
        buttonDetails.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(DETAIL_URL + character.getName());
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }

        });
    }

    @Override
    public void closeUserDetails() {
        pwindo.dismiss();
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
                FriendListAction.open(this, this);
                return true;
            case R.id.action_disconnect:
                DisconnectAction.disconnect(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
