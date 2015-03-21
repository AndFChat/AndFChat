package com.andfchat.frontend.popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.connection.FeedbackListener;
import com.andfchat.core.connection.FlistHttpClient;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.data.CharRelation;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.RelationManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.history.HistoryManager;
import com.andfchat.frontend.events.AndFChatEventManager;
import com.andfchat.frontend.events.ConnectionEventListener;
import com.google.inject.Inject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roboguice.util.Ln;

public class FListLoginPopup extends DialogFragment {

    private static String SAVE_ACCOUNT_NAME = "SAVE_ACCOUNT_NAME";
    private static String ACCOUNT_NAME = "ACCOUNT_NAME";

    @Inject
    protected SessionData sessionData;
    @Inject
    protected RelationManager relationManager;
    @Inject
    protected FlistWebSocketConnection connection;
    @Inject
    private AndFChatEventManager eventManager;

    private enum JsonTokens {
        characters,
        default_character,
        ticket,
        friends,
        bookmarks,
        error,
        source_name,
        name
    }

    private View view;

    private EditText account;
    private EditText password;
    private TextView errorField;
    private CheckBox rememberAccount;

    private boolean isLoggingIn = false;

    private SharedPreferences preferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        preferences = this.getActivity().getPreferences(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.activity_login, null);

        account = (EditText)view.findViewById(R.id.accountField);

        password = (EditText)view.findViewById(R.id.passwordField);
        errorField = (TextView)view.findViewById(R.id.loginErrorField);

        rememberAccount = (CheckBox)view.findViewById(R.id.rememberAccount);
        rememberAccount.setChecked(preferences.getBoolean(SAVE_ACCOUNT_NAME, false));

        if (rememberAccount.isChecked()) {
            account.setText(preferences.getString(ACCOUNT_NAME, account.getText().toString()));
            password.requestFocus();
        }

        builder.setView(view);
        builder.setPositiveButton("Login", null);

        builder.setCancelable(false);

        return builder.create();
    }

    @Override
    public void onStart()
    {
        super.onStart();    //Call show on default first so we can override the handlers

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog == null) {
            return;
        }

        final Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isLoggingIn==false) {
                    isLoggingIn = true;

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            button.setEnabled(false);
                            button.setText("Connecting... please wait!");
                        }
                    };

                    new Handler(Looper.getMainLooper()).post(runnable);

                    String account = FListLoginPopup.this.account.getText().toString();
                    String password = FListLoginPopup.this.password.getText().toString();

                    FeedbackListener feedbackListener = new FeedbackListener() {
                        @Override
                        public void onResponse(String response) {
                            isLoggingIn = false;

                            if (parseJson(response) == true) {
                                Ln.i("Successfully logged in!");
                                // Connect to chat server
                                connection.connect(true);
                            } else {
                                onError(null);
                            }
                        }

                        @Override
                        public void onError(Exception ex) {
                            isLoggingIn = false;

                            if (ex == null) {
                                Ln.i("Can't log in!");
                            } else {
                                Ln.i("Can't log in! " + ex.getMessage());
                            }

                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    errorField.setText(getActivity().getString(R.string.error_login));
                                    button.setEnabled(true);
                                    button.setText("Login");
                                }
                            };

                            new Handler(Looper.getMainLooper()).post(runnable);
                        }
                    };

                    FlistHttpClient.logIn(account, password, feedbackListener);
                }
            }
        });
    }

        private boolean parseJson(String jsonText) {
        try {
            Ln.v(jsonText);
            final JSONObject jsonDocument = new JSONObject(jsonText);

            if (jsonDocument.getString(JsonTokens.error.name()).length() != 0) {
                // TODO: Proper error handling
                return false;
            } else {
                JSONArray chars = jsonDocument.getJSONArray(JsonTokens.characters.name());
                List<String> charList = new ArrayList<String>();
                for (int i = 0; i < chars.length(); i++) {
                    charList.add(chars.getString(i));
                }

                // Init session
                sessionData.initSession(jsonDocument.getString(JsonTokens.ticket.name()), account.getText().toString());
                // Add bookmarks to the RelationManager
                JSONArray bookmarks = jsonDocument.getJSONArray(JsonTokens.bookmarks.name());
                Set<String> bookmarksList = new HashSet<String>();
                for (int i = 0; i < bookmarks.length(); i++) {
                    bookmarksList.add(bookmarks.getJSONObject(i).getString(JsonTokens.name.name()));
                }
                relationManager.addCharacterToList(CharRelation.BOOKMARKED, bookmarksList);
                Ln.v("Added " + bookmarksList.size() + " bookmarks.");

                // Add friends to the RelationManager
                JSONArray friends = jsonDocument.getJSONArray(JsonTokens.friends.name());
                Set<String> friendList = new HashSet<String>();
                for (int i = 0; i < friends.length(); i++) {
                    friendList.add(friends.getJSONObject(i).getString(JsonTokens.source_name.name()));
                }
                relationManager.addCharacterToList(CharRelation.FRIEND, friendList);
                Ln.v("Added " + friendList.size() + " friends.");

                SharedPreferences.Editor prefEditor = preferences.edit();
                prefEditor.putBoolean(SAVE_ACCOUNT_NAME, rememberAccount.isChecked());
                if (rememberAccount.isChecked()) {
                    prefEditor.putString(ACCOUNT_NAME, account.getText().toString());
                } else {
                    prefEditor.remove(ACCOUNT_NAME);
                }
                prefEditor.commit();

                String defaultCharacter = jsonDocument.getString(JsonTokens.default_character.name());

                sessionData.setCharList(charList);
                sessionData.setDefaultChar(defaultCharacter);

                return true;
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return false;
    }
}

