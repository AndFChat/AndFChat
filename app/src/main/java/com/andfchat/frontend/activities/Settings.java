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
import java.util.Arrays;
import java.util.Collections;

import roboguice.activity.RoboPreferenceActivity;
import roboguice.util.Ln;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.history.HistoryManager;
import com.andfchat.frontend.application.AndFChatApplication;
import com.andfchat.frontend.util.FlistAlertDialog;
import com.google.inject.Inject;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class Settings extends RoboPreferenceActivity {

    @Inject
    protected NotificationManager notificationManager;
    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected SessionData sessionData;
    @Inject
    protected HistoryManager historyManager;
    @Inject
    private FlistWebSocketConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(R.color.primary_color_dark));

        final MultiSelectListPreference initialChannelList = (MultiSelectListPreference)findPreference("initial_channels");
        ArrayList<String> channels = new ArrayList<String>(chatroomManager.getOfficialChannels());
        Collections.sort(channels);
        String[] defaults = {"Frontpage"};
        ArrayList<String> defaultChannels = new ArrayList<String>(Arrays.asList(defaults));

        initialChannelList.setEntries(channels.toArray(new String[channels.size()]));
        initialChannelList.setEntryValues(channels.toArray(new String[channels.size()]));
        initialChannelList.setDefaultValue(defaultChannels.toArray());

        /*initialChannelList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String title = getString(R.string.title_initial_channel);
                title += ": " + newValue.toString();

                initialChannelList.setTitle(title);
                return true;
            }
        });*/

        if(sessionData.getSessionSettings().getInitialChannel() != null) {
            initialChannelList.setValues(sessionData.getSessionSettings().getInitialChannel());
        }

        String title = getString(R.string.title_initial_channel);
        //title += ": " + sessionData.getSessionSettings().getInitialChannel();
        initialChannelList.setTitle(title);

        final MultiSelectListPreference initialPrivChannelList = (MultiSelectListPreference)findPreference("initial_private_channels");
        ArrayList<String> privChannels = new ArrayList<String>(chatroomManager.getPrivateChannelNames());
        Collections.sort(privChannels);
        ArrayList<String> privChannelIDs = new ArrayList<>();
        for (int i=0; i<privChannels.size(); i++) {
            String channelID = chatroomManager.getPrivateChannelByName(privChannels.get(i)).getChannelId();
            privChannelIDs.add(channelID);
        }
        String[] privDefaults = {"ADH-e16df7b19f4a38938ee0"};
        ArrayList<String> defaultPrivChannels = new ArrayList<String>(Arrays.asList(privDefaults));

        initialPrivChannelList.setEntries(privChannels.toArray(new String[privChannels.size()]));
        initialPrivChannelList.setEntryValues(privChannelIDs.toArray(new String[privChannelIDs.size()]));
        initialPrivChannelList.setDefaultValue(defaultPrivChannels.toArray());

        if(sessionData.getSessionSettings().getInitialPrivateChannel() != null) {
            initialPrivChannelList.setValues(sessionData.getSessionSettings().getInitialPrivateChannel());
        }

        String privTitle = getString(R.string.title_initial_private_channel);
        initialPrivChannelList.setTitle(privTitle);

        /*final ListPreference textSizeList = (ListPreference)findPreference("chat_text_size");

        textSizeList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String title = getString(R.string.title_chat_text_size);
                // Find the right entry fitting to the value
                CharSequence[] values = textSizeList.getEntryValues();
                for (int i = 0; i < values.length; i++) {
                    if (values[i].equals(newValue)) {
                        title += ": " + textSizeList.getEntries()[i];
                    }
                }
                textSizeList.setTitle(title);
                return true;
            }
        });

        title = getString(R.string.title_chat_text_size);
        title += ": " + textSizeList.getEntry();
        textSizeList.setTitle(title);*/

        Preference button = findPreference("button");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference arg0) {

                            @SuppressLint("ValidFragment") FlistAlertDialog dialog = new FlistAlertDialog(Settings.this, Settings.this.getResources().getString(R.string.question_delete_log)) {

                                @Override
                                public void onYes() {
                                    Ln.d("Clear history!");
                                    historyManager.clearHistory(true);
                                }
                            };

                            dialog.show();

                            return true;
                        }
                    });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(AndFChatApplication.LED_NOTIFICATION_ID);
    }
}
