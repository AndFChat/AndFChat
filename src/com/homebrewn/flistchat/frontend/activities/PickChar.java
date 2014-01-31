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

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.inject.Inject;
import com.homebrewn.flistchat.R;
import com.homebrewn.flistchat.core.connection.FlistWebSocketConnection;
import com.homebrewn.flistchat.core.data.SessionData;

public class PickChar extends RoboActivity {

    @Inject
    protected FlistWebSocketConnection connection;
    @Inject
    protected SessionData sessionData;

    @InjectView(R.id.charField)
    private Spinner charSelector;

    private String[] characters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_char);

        SharedPreferences preferences = this.getSharedPreferences("account", 0);
        characters = preferences.getString("characters", "").split(",");

        charSelector.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, characters));

        // Connect websockets
        if (connection.isConnected() == false) {
            connection.connect();
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
            // Identify the character
            connection.identify();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    while(connection.isConnected() == false) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent intent = new Intent(PickChar.this, MainScreen.class);
                    startActivity(intent);
                    PickChar.this.finish();
                }
            };

            new Thread(runnable).start();
        } else {
            Ln.i("Can't connect to WebSocket!");
        }

    }
}
