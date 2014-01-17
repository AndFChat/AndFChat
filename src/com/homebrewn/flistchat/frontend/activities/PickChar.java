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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.homebrewn.flistchat.R;
import com.homebrewn.flistchat.backend.data.StaticDataContainer;
import com.homebrewn.flistchat.core.connection.FlistWebSocketConnection;
import com.homebrewn.flistchat.core.data.SessionData;

public class PickChar extends Activity {

    private Spinner charSelector;
    private String[] characters;

    private String ticket;
    private String account;
    private FlistWebSocketConnection connection;

    private static final String TAG = "homebrewn.flistchat.PickChar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_char);

        charSelector = (Spinner) findViewById(R.id.charField);

        SharedPreferences preferences = this.getSharedPreferences("account", 0);
        characters = preferences.getString("characters", "").split(",");

        charSelector.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, characters));

        ticket = preferences.getString("ticket", null);
        account = preferences.getString("account", null);
        StaticDataContainer.sessionData = new SessionData(ticket, account, this);

        if (preferences.getBoolean("isLive", false)) {
            connection = new FlistWebSocketConnection(account, ticket, StaticDataContainer.sessionData);
        } else {
//            connection = new FlistWebSocketConnectionMock(account, ticket, "TestCharakter", StaticDataContainer.sessionData);
            throw new IllegalArgumentException("Running ServerMock is still not supported!");
        }

        StaticDataContainer.sessionData.setConnection(connection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_pick_char, menu);
        return true;
    }

    public void logIn(View v) {

        String characterName = characters[charSelector.getSelectedItemPosition()];

        StaticDataContainer.sessionData.setCharname(characterName);

        if (StaticDataContainer.sessionData.getConnection().isConnected()) {
            Log.i(TAG, "Connected to WebSocket!");
            StaticDataContainer.sessionData.getConnection().identify();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    while(StaticDataContainer.sessionData.isConnected() == false) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
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
            Log.i(TAG, "Can't connect to WebSocket!");
        }

    }
}
