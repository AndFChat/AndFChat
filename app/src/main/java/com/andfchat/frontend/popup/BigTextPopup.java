package com.andfchat.frontend.popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.andfchat.R;

public class BigTextPopup extends DialogFragment {

    private Spannable content;

    private View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.popup_description, null);


        TextView descriptionText = (TextView)view.findViewById(R.id.descriptionText);
        descriptionText.setText(content);
        // Enable touching/clicking links in text
        descriptionText.setMovementMethod(LinkMovementMethod.getInstance());

        builder.setView(view);
        builder.setPositiveButton(getString(R.string.dismiss), null);
        builder.setCancelable(true);

        return builder.create();
    }

    public void setText(Spannable content) {
        this.content = content;
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    @Override
    public void onStart()
    {
        super.onStart();    //Call show on default first so we can override the handlers

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog == null) {
            return;
        }
        // Can close dialog
        dialog.setCanceledOnTouchOutside(true);
    }
}

