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

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.history.HistoryManager;
import com.google.inject.Inject;

public class PickChar extends RoboActivity {

    @Inject
    protected FlistWebSocketConnection connection;
    @Inject
    protected SessionData sessionData;
    @Inject
    protected HistoryManager historyManager;
    @Inject
    protected CharacterManager characterManager;

    @InjectView(R.id.charField)
    private Spinner charSelector;

    private String[] characters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(sessionData.getSessionSettings().getTheme());
        setContentView(R.layout.activity_pick_char);

        if (characters == null) {
            characters = getIntent().getStringExtra("characters").split(",");
        }

        charSelector.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, characters));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Ln.d("on resume");

        // On return via back button the connection occurs to be still disconnecting, so wait a second.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
             @Override
            public void run() {
                 Ln.d("Checking connection");
                 // Connect websocket
                 if (connection.isConnected() == false) {
                     Ln.d("Connecting...");
                     connection.connect(getIntent().getBooleanExtra("isLive", true));
                 }
             }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_pick_char, menu);
        return true;
    }

    public void logIn(View v) {

        String characterName = characters[charSelector.getSelectedItemPosition()];

        sessionData.setCharname(characterName);
        // Websocket is connected?
        if (connection.isConnected()) {
            Ln.i("Connected to WebSocket!");
            Ln.d("loading logs");
            historyManager.loadHistory();
            // Identify the character
            connection.identify();
            openChat();
        } else {
            Ln.i("Can't connect to WebSocket!");
            Ln.d("Connecting...");
            connection.connect(getIntent().getBooleanExtra("isLive", true));
        }

    }

    public void openChat() {
        Intent intent = new Intent(getBaseContext(), ChatScreen.class);
        startActivity(intent);
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
