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

import roboguice.activity.RoboPreferenceActivity;
import android.app.NotificationManager;
import android.os.Bundle;

import com.andfchat.R;
import com.andfchat.frontend.application.AndFChatApplication;
import com.google.inject.Inject;

public class Settings extends RoboPreferenceActivity {

    @Inject
    protected NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(AndFChatApplication.LED_NOTIFICATION_ID);
    }
}
