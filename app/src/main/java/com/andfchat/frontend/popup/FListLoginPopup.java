package com.andfchat.frontend.popup;

import android.support.v7.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.connection.FlistWebSocketConnection;
import com.andfchat.core.connection.FlistHttpClient;
import com.andfchat.core.data.CharRelation;
import com.andfchat.core.data.RelationManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.frontend.events.AndFChatEventManager;
import com.google.inject.Inject;
import okhttp3.OkHttpClient;

import java.util.Collections;
import java.util.HashSet;
//import java.util.Iterator;
import java.util.Set;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import roboguice.util.Ln;

public class FListLoginPopup extends DialogFragment {

    private static String SAVE_ACCOUNT_NAME = "SAVE_ACCOUNT_NAME";
    private static String SAVE_PASSWORD = "SAVE_PASSWORD";
    private static String ACCOUNT_NAME = "ACCOUNT_NAME";
    private static String PASSWORD = "PASSWORD";
    private static String HOST = "HOST";

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
        ignores,
        error,
        source_name,
        name
    }

    private View view;

    private EditText account, password, host;
    private TextView errorField;
    private CheckBox rememberAccount, rememberPassword;

    private boolean isLoggingIn = false;

    private SharedPreferences preferences;
    private DialogInterface.OnDismissListener dismissListener;

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        preferences = this.getActivity().getPreferences(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.popup_login, null);

        account = (EditText)view.findViewById(R.id.accountField);

        password = (EditText)view.findViewById(R.id.passwordField);
        host = (EditText)view.findViewById(R.id.hostField);
        errorField = (TextView)view.findViewById(R.id.loginErrorField);

        rememberAccount = (CheckBox)view.findViewById(R.id.rememberAccount);
        rememberAccount.setChecked(preferences.getBoolean(SAVE_ACCOUNT_NAME, false));
        rememberAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rememberPassword.setEnabled(isChecked);
            }
        });
        rememberPassword = (CheckBox)view.findViewById(R.id.rememberPassword);
        rememberPassword.setEnabled(rememberAccount.isChecked());

        CheckBox showAdvanced = (CheckBox) view.findViewById(R.id.showAdvanced);
        showAdvanced.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                view.findViewById(R.id.advanced).setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        if (rememberAccount.isChecked()) {
            account.setText(preferences.getString(ACCOUNT_NAME, account.getText().toString()));
            rememberPassword.setChecked(preferences.getBoolean(SAVE_PASSWORD, false));
            if(rememberPassword.isChecked()) {
                password.setText(preferences.getString(PASSWORD, ""));
            } else password.requestFocus();
        }
        host.setText(preferences.getString(HOST, "ws://chat.f-list.net:9722/"));

        builder.setView(view);
        builder.setPositiveButton(R.string.login, null);

        builder.setCancelable(false);

        return builder.create();
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    @Override
    public void onResume() {
        super.onResume();

        try{
            setError(sessionData.getDisconnectReason());
        } catch (NullPointerException e) {
            Ln.i("No disconnect reason");
        }
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
                if (!isLoggingIn) {
                    isLoggingIn = true;

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            button.setEnabled(false);
                            button.setText(R.string.connecting);
                        }
                    };

                    new Handler(Looper.getMainLooper()).post(runnable);

                    String account = FListLoginPopup.this.account.getText().toString().trim();
                    String password = FListLoginPopup.this.password.getText().toString().trim();

                    retrofit2.Callback<FlistHttpClient.LoginData> callback = new retrofit2.Callback<FlistHttpClient.LoginData>() {

                        @Override
                        public void onResponse(retrofit2.Response<FlistHttpClient.LoginData> response) {
                            isLoggingIn = false;
                            FlistHttpClient.LoginData loginData = response.body();

                            if (loginData != null) {
                                if (loginData.getError() == null || loginData.getError().length() == 0) {
                                    Ln.i("Successfully logged in!");
                                    addData(loginData);
                                    // Connect to chat server
                                    connection.connect();
                                } else {
                                    onError(loginData.getError());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            isLoggingIn = false;
                            onError(t.getMessage());
                        }

                        private void onError(final String message) {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    setError(message);
                                    button.setEnabled(true);
                                    button.setText(R.string.login);
                                }
                            };

                            new Handler(Looper.getMainLooper()).post(runnable);
                        }
                    };

                    OkHttpClient client = new OkHttpClient();
                    //client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));

                    Retrofit restAdapter = new Retrofit.Builder()
                            .baseUrl("https://www.f-list.net")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .build();

                    FlistHttpClient httpClient = restAdapter.create(FlistHttpClient.class);
                    Call<FlistHttpClient.LoginData> call = httpClient.logIn(account, password);
                    call.enqueue(callback);
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

    private void addData(FlistHttpClient.LoginData loginData) {
        // Init session
        sessionData.initSession(loginData.getTicket(), account.getText().toString(), password.getText().toString(), host.getText().toString());
        // Add bookmarks to the RelationManager

        Set<String> bookmarksList = new HashSet<String>();
        for (FlistHttpClient.LoginData.Bookmark bookmark : loginData.getBookmarks()) {
            bookmarksList.add(bookmark.getName());
        }
        relationManager.addCharacterToList(CharRelation.BOOKMARKED, bookmarksList);
        Ln.v("Added " + bookmarksList.size() + " bookmarks.");

        // Add friends to the RelationManager
        Set<String> friendList = new HashSet<String>();
        for (FlistHttpClient.LoginData.Friend friend : loginData.getFriends()) {
            friendList.add(friend.getFriend());
        }
        relationManager.addCharacterToList(CharRelation.FRIEND, friendList);
        Ln.v("Added " + friendList.size() + " friends.");

        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putBoolean(SAVE_ACCOUNT_NAME, rememberAccount.isChecked());
        prefEditor.putBoolean(SAVE_PASSWORD, rememberPassword.isChecked());
        if (rememberAccount.isChecked()) {
            prefEditor.putString(ACCOUNT_NAME, account.getText().toString());
        } else {
            prefEditor.remove(ACCOUNT_NAME);
        }
        if(rememberAccount.isChecked() && rememberPassword.isChecked()) {
            prefEditor.putString(PASSWORD, password.getText().toString());
        } else {
            prefEditor.remove(PASSWORD);
        }
        prefEditor.putString(HOST, host.getText().toString());
        prefEditor.apply();

        Collections.sort(loginData.getCharacters());
        sessionData.setCharList(loginData.getCharacters());
        sessionData.setDefaultChar(loginData.getDefaultCharacter());
    }

    public void setError(String message) {
        if (message.contains("Host is unresolved")) {
            errorField.setText(String.format(getActivity().getString(R.string.error_disconnected), R.string.error_disconnected_no_connection));
        } else if (!message.isEmpty()) {
            errorField.setText(getActivity().getString(R.string.error_disconnected, message));
        }
    }
}

