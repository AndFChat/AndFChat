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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.FlistHttpClient;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.Channel;
import com.andfchat.core.data.CharRelation;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.Chatroom.ChatroomType;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.RelationManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.history.HistoryManager;
import com.andfchat.frontend.application.AndFChatApplication;
import com.andfchat.frontend.events.AndFChatEventManager;
import com.google.inject.Inject;

public class Login extends RoboActivity {

    private static String SAVE_ACCOUNT_NAME = "SAVE_ACCOUNT_NAME";
    private static String ACCOUNT_NAME = "ACCOUNT_NAME";

    public enum Server {
        DEV_SERVER,
        LIVE_SERVER;
    }

    private enum JsonTokens {
        characters,
        default_character,
        ticket,
        friends,
        bookmarks,
        error,
        source_name,
        name
    }

    @Inject
    protected SessionData sessionData;
    @Inject
    protected RelationManager relationManager;
    @Inject
    protected CharacterManager charManager;
    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected FlistWebSocketConnection connection;
    @Inject
    private AndFChatEventManager eventManager;
    @Inject
    protected NotificationManager notificationManager;
    @Inject
    protected HistoryManager historyManager;

    @InjectView(R.id.accountField)
    private EditText account;
    @InjectView(R.id.passwordField)
    private EditText password;
    @InjectView(R.id.loginErrorField)
    private TextView errorField;
    @InjectView(R.id.rememberAccount)
    private CheckBox rememberAccount;
    @InjectView(R.id.serverSelection)
    private Spinner serverSelection;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(sessionData.getSessionSettings().getTheme());
        setContentView(R.layout.activity_login);

        chatroomManager.addChatroom(new Chatroom(new Channel(AndFChatApplication.DEBUG_CHANNEL_NAME, ChatroomType.CONSOLE), 50000));

        preferences = this.getPreferences(0);
        rememberAccount.setChecked(preferences.getBoolean(SAVE_ACCOUNT_NAME, false));

        if (rememberAccount.isChecked()) {
            account.setText(preferences.getString(ACCOUNT_NAME, account.getText().toString()));
            password.requestFocus();
        }

        List<String> list = new ArrayList<String>();
        list.add(Server.LIVE_SERVER.name());
        list.add(Server.DEV_SERVER.name());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serverSelection.setAdapter(dataAdapter);
        serverSelection.setSelection(0);

        if (AndFChatApplication.DEBUGGING_MODE != true) {
            serverSelection.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();

        Ln.d("Resume login");

        notificationManager.cancel(AndFChatApplication.LED_NOTIFICATION_ID);
        eventManager.clear();
        Ln.i("Disconnecting!");
        if (connection.isConnected()) {
            connection.closeConnection(this);
        }
        else {
            sessionData.clearAll();
            chatroomManager.clear();
            charManager.clear();
            eventManager.clear();
        }
    }

    public void logIn(View v) {
        String account = this.account.getText().toString();
        String password = this.password.getText().toString();

        final Intent intent = new Intent(getBaseContext(), PickChar.class);


        FeedbackListner loginFeedback = new FeedbackListner() {

            @Override
            public void onResponse(String response) {
                if (parseJson(response, intent) == true) {
                    Ln.i("Succesfully logged in!");
                    startActivity(intent);
                } else {
                    this.onError(null);
                }
            }

            @Override
            public void onError(Exception ex) {
                if (ex == null) {
                    Ln.i("Can't log in!");
                } else {
                    Ln.i("Can't log in! " + ex.getMessage());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorField.setText(R.string.error_login);
                    }
                });
            }
        };

        FlistHttpClient.logIn(account, password, loginFeedback);
    }

    private boolean parseJson(String jsonText, Intent intent) {
        try {
            Ln.v(jsonText);
            final JSONObject jsonDocument = new JSONObject(jsonText);

            if (jsonDocument.getString(JsonTokens.error.name()).length() != 0) {
                // TODO: Proper error handling
                return false;
            } else {
                JSONArray chars = jsonDocument.getJSONArray(JsonTokens.characters.name());
                String charList = "";
                for (int i = 0; i < chars.length(); i++) {
                    charList += chars.getString(i);
                    if (i + 1 < chars.length()) {
                        charList += ",";
                    }
                }

                // Init session
                sessionData.initSession(jsonDocument.getString(JsonTokens.ticket.name()), account.getText().toString());
                // Add bookmarks to the RelationManager
                JSONArray bookmarks = jsonDocument.getJSONArray(JsonTokens.bookmarks.name());
                Set<String> bookmarksList = new HashSet<String>();
                for (int i = 0; i < bookmarks.length(); i++) {
                    bookmarksList.add(bookmarks.getJSONObject(i).getString(JsonTokens.name.name()));
                }
                relationManager.addCharacterToList(CharRelation.BOOKMARKED, bookmarksList);
                Ln.v("Added " + bookmarksList.size() + " bookmarks.");

                // Add friends to the RelationManager
                JSONArray friends = jsonDocument.getJSONArray(JsonTokens.friends.name());
                Set<String> friendList = new HashSet<String>();
                for (int i = 0; i < friends.length(); i++) {
                    friendList.add(friends.getJSONObject(i).getString(JsonTokens.source_name.name()));
                }
                relationManager.addCharacterToList(CharRelation.FRIEND, friendList);
                Ln.v("Added " + friendList.size() + " friends.");

                Editor prefEditor = preferences.edit();
                prefEditor.putBoolean(SAVE_ACCOUNT_NAME, rememberAccount.isChecked());
                if (rememberAccount.isChecked()) {
                    prefEditor.putString(ACCOUNT_NAME, account.getText().toString());
                } else {
                    prefEditor.remove(ACCOUNT_NAME);
                }
                prefEditor.commit();


                intent.putExtra("isLive", serverSelection.getSelectedItemPosition() == 0);
                intent.putExtra("characters", charList);
                intent.putExtra("default_char", jsonDocument.getString(JsonTokens.default_character.name()));

                return true;
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        // Do smaller chat height on displayed keyboard the height is determined by display size.
        switch (item.getItemId()) {
        case R.id.action_open_settings:
            startActivity(new Intent(this, Settings.class));
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
