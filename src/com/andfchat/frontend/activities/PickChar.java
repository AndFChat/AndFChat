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
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
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

    @InjectView(R.id.charField)
    private Spinner charSelector;

    private String[] characters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_char);

        characters = getIntent().getStringExtra("characters").split(",");
        charSelector.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, characters));

        // Connect websockets
        if (connection.isConnected() == false) {
            connection.connect(getIntent().getBooleanExtra("isLive", false));
        }
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
        }

    }

    public void openChat() {
        Intent intent = new Intent(getBaseContext(), ChatScreen.class);
        startActivity(intent);
        finish();
    }
}
