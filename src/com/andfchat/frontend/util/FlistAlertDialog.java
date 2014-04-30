package com.andfchat.frontend.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.andfchat.R;

public abstract class FlistAlertDialog {

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
    public abstract void onNo();
}
