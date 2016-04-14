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


package com.andfchat.frontend.menu;

import roboguice.RoboGuice;
import roboguice.util.Ln;

import android.app.Activity;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.frontend.util.FlistAlertDialog;

public class DisconnectAction {

    public static void disconnect(final Activity activity) {

        FlistAlertDialog dialog = new FlistAlertDialog(activity, activity.getResources().getString(R.string.question_disconnect)) {

            @Override
            public void onYes() {
                RoboGuice.getInjector(activity).getInstance(FlistWebSocketConnection.class).closeConnectionLogout(activity);
            }
        };

        dialog.show();
    }
}
