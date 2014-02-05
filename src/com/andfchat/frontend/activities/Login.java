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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.connection.FeedbackListner;
import com.andfchat.core.connection.FlistHttpClient;
import com.andfchat.core.data.SessionData;
import com.google.inject.Inject;

public class Login extends RoboActivity {

    private static String SAVE_ACCOUNT_NAME = "SAVE_ACCOUNT_NAME";
    private static String ACCOUNT_NAME = "ACCOUNT_NAME";

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
    @InjectView(R.id.loginErrorField)
    private TextView errorField;
    @InjectView(R.id.rememberAccount)
    private CheckBox rememberAccount;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = this.getPreferences(0);
        rememberAccount.setChecked(preferences.getBoolean(SAVE_ACCOUNT_NAME, false));

        if (rememberAccount.isChecked()) {
            account.setText(preferences.getString(ACCOUNT_NAME, account.getText().toString()));
            password.requestFocus();
        }
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

        final Intent intent = new Intent(getBaseContext(), PickChar.class);


        FeedbackListner loginFeedback = new FeedbackListner() {

            @Override
            public void onResponse(String response) {
                if (parseJson(response, intent) == true) {
                    Ln.i("Succesfully logged in!");
                    startActivity(intent);
                    finish();
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
                sessionData.initSession(jsonDocument.getString(JsonTokens.ticket.name()), account.getText().toString(), this);

                JSONArray bookmarks = jsonDocument.getJSONArray(JsonTokens.bookmarks.name());
                String bookmarksList = "";
                for (int i = 0; i < bookmarks.length(); i++) {
                    bookmarksList += bookmarks.getString(i);
                    if (i + 1 < bookmarks.length()) {
                        bookmarksList += ",";
                    }
                }

                Editor prefEditor = preferences.edit();
                prefEditor.putBoolean(SAVE_ACCOUNT_NAME, rememberAccount.isChecked());
                if (rememberAccount.isChecked()) {
                    prefEditor.putString(ACCOUNT_NAME, account.getText().toString());
                } else {
                    prefEditor.remove(ACCOUNT_NAME);
                }
                prefEditor.commit();

                intent.putExtra("characters", charList);
                intent.putExtra("default_char", jsonDocument.getString(JsonTokens.default_character.name()));
                intent.putExtra("bookmarks", bookmarksList);

                return true;
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return false;
    }

}
