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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.homebrewn.flistchat.R;
import com.homebrewn.flistchat.core.connection.FeedbackListner;
import com.homebrewn.flistchat.core.connection.FlistHttpClient;

public class Login extends Activity {

    private enum JsonTokens {
        characters,
        default_character,
        ticket,
        friends,
        bookmarks,
        error
    }

    private static final String TAG = Login.class.getSimpleName();

    private FlistHttpClient httpClient;

    private EditText account;
    private EditText password;

    private CheckBox setToLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        httpClient = new FlistHttpClient();

        account = (EditText)this.findViewById(R.id.accountField);
        password = (EditText)this.findViewById(R.id.passwordField);
        setToLive = (CheckBox)this.findViewById(R.id.isLive);
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

        if (account.length() > 0 && password.length() > 0) {
            if (setToLive.isChecked()) {

                Log.i(TAG, "Using Live data with account: '" + account + "'!");
                prefEditor.putBoolean("isLive", true);
                prefEditor.commit();

                FeedbackListner loginFeedback = new FeedbackListner() {

                    @Override
                    public void onResponse(String response) {
                        if (parseJson(response) == true) {
                            Log.i(TAG, "Succesfully logged in!");
                            MoveToCharakterSelection();
                        } else {
                            this.onError(null);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        String errorText = "Login failed, maybe username or password wrong?";
                        if (ex == null) {
                            Log.i(TAG, "Can't log in!");
                        } else {
                            Log.i(TAG, "Can't log in! " + ex.getMessage());
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

                httpClient.logIn(account, password, loginFeedback);

            } else {
                Log.i(TAG, "Using Mock data!");
                String jsonText = "{\"characters\":[\"Prinzessin Nadia\",\"Nalani Tauperle\",\"Natasha S\",\"Alexander R\",\"Svala the Valkyrie\",\"Tanja Kirsch\",\"Yamka\",\"Rhoa\",\"Mirah\",\"Naoko Aikawa\",\"Alexandra R\",\"Kim Jasper\",\"Calea\",\"Janista\",\"Marie Winters\",\"Chris Walder\",\"Helena Hardwig\",\"Cassy Steppenwolf\",\"Nikki Schellenberger\",\"Asha G\",\"Amarill\",\"Marleen T\",\"Shilana\",\"Emillio\"],\"default_character\":\"Emillio\",\"account_id\":\"51653\",\"ticket\":\"fct_5425fa06350d9506701ca2893dfafac4a1c8541d5a5c440aa00c3f335d2bbb71\",\"friends\":[{\"source_name\":\"Shandal\",\"dest_name\":\"Nalani Tauperle\"},{\"source_name\":\"Kyle Blackmane\",\"dest_name\":\"Rhoa\"},{\"source_name\":\"Greyson McCall\",\"dest_name\":\"Rhoa\"},{\"source_name\":\"Adelina\",\"dest_name\":\"Alexandra R\"},{\"source_name\":\"Pirate Girl\",\"dest_name\":\"Tanja Kirsch\"},{\"source_name\":\"Amanda Powell\",\"dest_name\":\"Tanja Kirsch\"},{\"source_name\":\"Nanako Salestras\",\"dest_name\":\"Kim Jasper\"},{\"source_name\":\"Kaito Yamamoto\",\"dest_name\":\"Marie Winters\"},{\"source_name\":\"Sidney Eden\",\"dest_name\":\"Mirah\"},{\"source_name\":\"Xynari\",\"dest_name\":\"Mirah\"},{\"source_name\":\"Katharina Frosch\",\"dest_name\":\"Chris Walder\"},{\"source_name\":\"Jenny Huge\",\"dest_name\":\"Chris Walder\"},{\"source_name\":\"Kalec\",\"dest_name\":\"Prinzessin Nadia\"},{\"source_name\":\"Gallane\",\"dest_name\":\"Emillio\"},{\"source_name\":\"Latera\",\"dest_name\":\"Emillio\"},{\"source_name\":\"Tioh\",\"dest_name\":\"Emillio\"},{\"source_name\":\"Rhiany\",\"dest_name\":\"Shilana\"},{\"source_name\":\"Elnaria Moonstrike\",\"dest_name\":\"Shilana\"},{\"source_name\":\"Justine Emerald\",\"dest_name\":\"Shilana\"},{\"source_name\":\"Alizani\",\"dest_name\":\"Shilana\"},{\"source_name\":\"Sydni Chamber\",\"dest_name\":\"Shilana\"},{\"source_name\":\"Gabriel Schwarz\",\"dest_name\":\"Marleen T\"},{\"source_name\":\"Shandal\",\"dest_name\":\"Marleen T\"},{\"source_name\":\"drakedarkcon\",\"dest_name\":\"Amarill\"},{\"source_name\":\"Justine Emerald\",\"dest_name\":\"Amarill\"},{\"source_name\":\"Sharlynne\",\"dest_name\":\"Natasha S\"},{\"source_name\":\"basti19\",\"dest_name\":\"Natasha S\"},{\"source_name\":\"Killian Darkwater\",\"dest_name\":\"Natasha S\"},{\"source_name\":\"Viktoria Radvanyi\",\"dest_name\":\"Natasha S\"},{\"source_name\":\"Maria Kern\",\"dest_name\":\"Natasha S\"},{\"source_name\":\"Waleran de Herlorm\",\"dest_name\":\"Natasha S\"},{\"source_name\":\"BradSwimmer\",\"dest_name\":\"Nikki Schellenberger\"},{\"source_name\":\"Alexandra Stevens\",\"dest_name\":\"Nikki Schellenberger\"},{\"source_name\":\"Haro Jen\",\"dest_name\":\"Cassy Steppenwolf\"},{\"source_name\":\"Ajira\",\"dest_name\":\"Cassy Steppenwolf\"},{\"source_name\":\"Monique Steppenwolf\",\"dest_name\":\"Cassy Steppenwolf\"}],\"bookmarks\":[{\"name\":\"Adelina\"},{\"name\":\"Akantha\"},{\"name\":\"Alexandra Stevens\"},{\"name\":\"Alissa Mondraban\"},{\"name\":\"Amanda Powell\"},{\"name\":\"Anyawu\"},{\"name\":\"Cassandra Bellevue\"},{\"name\":\"Clara Lee\"},{\"name\":\"Cynthia Kreet\"},{\"name\":\"David Freck\"},{\"name\":\"Eliara\"},{\"name\":\"Gallane\"},{\"name\":\"Isabelle Soaxs\"},{\"name\":\"Kaito Yamamoto\"},{\"name\":\"Karandra\"},{\"name\":\"Katrina Storms\"},{\"name\":\"Maria Kern\"},{\"name\":\"Mercran Tanner\"},{\"name\":\"Naletha\"},{\"name\":\"Raiken Sandrial\"},{\"name\":\"Samantha Orlea\"},{\"name\":\"Selvara\"},{\"name\":\"Sharla Dunkelmond\"},{\"name\":\"Sihra\"},{\"name\":\"Sjald Olafson\"},{\"name\":\"Syrah\"},{\"name\":\"Triss Merigold\"},{\"name\":\"Vama\"},{\"name\":\"Xialla\"}],\"error\":\"\"}";

                prefEditor.putBoolean("isLive", false);
                prefEditor.commit();

                if (parseJson(jsonText) == true) {
                    Intent intent = new Intent(this, PickChar.class);
                    startActivity(intent);
                    this.finish();
                }
            }
        }
    }

    private boolean parseJson(String jsonText) {
        try {
            Log.i(TAG, jsonText);
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

                prefEditor.putString("account", account.getText().toString());
                prefEditor.putString("characters", charList);
                prefEditor.putString("ticket", jsonDocument.getString(JsonTokens.ticket.name()));
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
