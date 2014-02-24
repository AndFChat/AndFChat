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
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.andfchat.core.connection.FlistWebSocketConnection;

public class DisconnectAction {

    public static void disconnect(final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("REALLY WANT TO DISCONNECT?")
               .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                   @Override
                public void onClick(DialogInterface dialog, int id) {
                       RoboGuice.getInjector(activity).getInstance(FlistWebSocketConnection.class).closeConnection(activity);
                       activity.finish();
                   }
               })
               .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                   @Override
                public void onClick(DialogInterface dialog, int id) {}
               });
        // Create the AlertDialog object and return it
        builder.create().show();
    }
}
