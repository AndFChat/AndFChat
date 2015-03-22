package com.andfchat.frontend.popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
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

    private View view;

    private Spinner charSelector;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.popup_pick_char, null);

        charSelector = (Spinner)view.findViewById(R.id.charField);
        final ArrayAdapter<String> charListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sessionData.getCharList());
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
        builder.setPositiveButton("Login", null);

        builder.setCancelable(false);

        return builder.create();
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

                sessionData.setCharname(characterName);
                // Websocket is connected?
                if (connection.isConnected()) {

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            button.setEnabled(false);
                            button.setText("Connecting... please wait!");
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
    }
}

