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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;
import com.homebrewn.flistchat.R;
import com.homebrewn.flistchat.core.connection.FeedbackListner;
import com.homebrewn.flistchat.core.connection.FlistHttpClient;
import com.homebrewn.flistchat.core.data.SessionData;

public class Login extends RoboActivity {

    private enum JsonTokens {
        characters,
        default_character,
        ticket,
        friends,
        bookmarks,
        error
    }

    @Inject
    protected SessionData sessionData;

    @InjectView(R.id.accountField)
    private EditText account;
    @InjectView(R.id.passwordField)
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }

    public void logIn(View v) {
        String account = this.account.getText().toString();
        String password = this.password.getText().toString();

        Editor prefEditor = Login.this.getSharedPreferences("account", 0).edit();

        Ln.i("Using Live data with account: '" + account + "'!");
        prefEditor.putBoolean("isLive", true);
        prefEditor.commit();

        FeedbackListner loginFeedback = new FeedbackListner() {

            @Override
            public void onResponse(String response) {
                if (parseJson(response) == true) {
                    Ln.i("Succesfully logged in!");
                    MoveToCharakterSelection();
                } else {
                    this.onError(null);
                }
            }

            @Override
            public void onError(Exception ex) {
                String errorText = "Login failed, maybe username or password wrong?";
                if (ex == null) {
                    Ln.i("Can't log in!");
                } else {
                    Ln.i("Can't log in! " + ex.getMessage());
                    errorText += " " + ex.getMessage();
                }

                // This really sucks, help, pls!
                final String finalErrorText = errorText;
                final TextView loginErrorField = (TextView)Login.this.findViewById(R.id.loginErrorField);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginErrorField.setText(finalErrorText);
                    }
                });
            }
        };

        FlistHttpClient.logIn(account, password, loginFeedback);
    }

    private boolean parseJson(String jsonText) {
        try {
            Ln.v(jsonText);
            JSONObject jsonDocument = new JSONObject(jsonText);

            if (jsonDocument.getString(JsonTokens.error.name()).length() != 0) {

            } else {
                Editor prefEditor = this.getSharedPreferences("account", 0).edit();

                JSONArray chars = jsonDocument.getJSONArray(JsonTokens.characters.name());
                String charList = "";
                for (int i = 0; i < chars.length(); i++) {
                    charList += chars.getString(i);
                    if (i + 1 < chars.length()) {
                        charList += ",";
                    }
                }

                // Init session
                sessionData.initSession(jsonDocument.getString(JsonTokens.ticket.name()), account.getText().toString(), this);

                prefEditor.putString("characters", charList);
                prefEditor.putString("default_char", jsonDocument.getString(JsonTokens.default_character.name()));

                JSONArray bookmarks = jsonDocument.getJSONArray(JsonTokens.bookmarks.name());
                String bookmarksList = "";
                for (int i = 0; i < bookmarks.length(); i++) {
                    bookmarksList += bookmarks.getString(i);
                    if (i + 1 < bookmarks.length()) {
                        bookmarksList += ",";
                    }
                }
                prefEditor.putString("bookmarks", bookmarksList);
                prefEditor.commit();
                return true;
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return false;
    }

    private void MoveToCharakterSelection() {
        Intent intent = new Intent(this, PickChar.class);
        startActivity(intent);
    }

}
