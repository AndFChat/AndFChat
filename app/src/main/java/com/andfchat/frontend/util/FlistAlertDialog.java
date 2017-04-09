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


package com.andfchat.frontend.util;

import android.support.v7.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;

import com.andfchat.R;

public abstract class FlistAlertDialog extends DialogFragment {

    private final AlertDialog.Builder dialog;

    public FlistAlertDialog(Context context, String message) {
        dialog = new AlertDialog.Builder(context);

        dialog.setMessage(message);

        dialog.setPositiveButton(context.getResources().getText(R.string.yes),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    onYes();
                }
            }
        );

        dialog.setNegativeButton(context.getResources().getText(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                onNo();
            }
        });
    }

    public void show() {
        dialog.create().show();
    }

    public abstract void onYes();
    public void onNo() {
        //do nothing.
    }
}
