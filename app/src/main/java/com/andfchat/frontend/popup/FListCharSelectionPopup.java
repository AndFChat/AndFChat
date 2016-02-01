package com.andfchat.frontend.popup;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.history.HistoryManager;
import com.google.inject.Inject;

import roboguice.util.Ln;

public class FListCharSelectionPopup extends DialogFragment {

    private static String SAVE_ACCOUNT_NAME = "SAVE_ACCOUNT_NAME";
    private static String ACCOUNT_NAME = "ACCOUNT_NAME";

    @Inject
    protected SessionData sessionData;
    @Inject
    protected FlistWebSocketConnection connection;
    @Inject
    protected HistoryManager historyManager;
    @Inject
    protected ChatroomManager chatroomManager;
    @Inject
    protected CharacterManager characterManager;

    private View view;

    private Spinner charSelector;
    private DialogInterface.OnDismissListener dismissListener;

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.popup_pick_char, null);

        charSelector = (Spinner)view.findViewById(R.id.charField);
        ArrayAdapter<String> charListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sessionData.getCharList());
        charSelector.setAdapter(charListAdapter);

        String defaultChar = sessionData.getDefaultChar();
        if (sessionData.getCharacterName() != null) {
            defaultChar = sessionData.getCharacterName();
        }

        for (int i = 0; i < sessionData.getCharList().size(); i++) {
            if (sessionData.getCharList().get(i).equals(defaultChar)) {
                charSelector.setSelection(i);
                break;
            }
        }

        builder.setView(view);
        builder.setPositiveButton(R.string.login, null);

        builder.setCancelable(false);

        return builder.create();
    }

    public boolean isShowing() {
        if (getDialog() == null) {
            return false;
        }
        return getDialog().isShowing();
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        ArrayAdapter<String> charListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sessionData.getCharList());
        charSelector.setAdapter(charListAdapter);

        return super.show(transaction, tag);
    }

    @Override
    public void onStart() {
        super.onStart();    //Call show on default first so we can override the handlers

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog == null) {
            return;
        }

        final Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String characterName = ((ArrayAdapter<String>)charSelector.getAdapter()).getItem(charSelector.getSelectedItemPosition());

                sessionData.clear();

                // Reset data only when new character connects
                if (sessionData.getCharacterName() == null || sessionData.getCharacterName().equals(characterName) == false) {
                    chatroomManager.clear();
                    characterManager.clear();

                    sessionData.setCharname(characterName);
                }
                // Websocket is connected?
                if (connection.isConnected()) {

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            button.setEnabled(false);
                            button.setText(R.string.connecting);
                        }
                    };

                    Ln.i("Connected to WebSocket!");
                    Ln.d("loading logs");
                    historyManager.loadHistory();
                    // Identify the character
                    connection.identify();
                }
            }
        });

        // Cant close dialog
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        dismissListener.onDismiss(dialog);
    }
}

