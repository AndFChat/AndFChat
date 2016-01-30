package com.andfchat.frontend.popup;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import okhttp3.Protocol;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
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
        ignores,
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

    public boolean isShowing() {
        if (getDialog() == null) {
            return false;
        }
        return getDialog().isShowing();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sessionData.getDisconnectReason() != null) {
            setError(sessionData.getDisconnectReason());
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
                if (isLoggingIn == false) {
                    isLoggingIn = true;

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            button.setEnabled(false);
                            button.setText("Connecting... please wait!");
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

                            if (loginData.getError() == null || loginData.getError().length() == 0) {
                                Ln.i("Successfully logged in!");
                                addData(loginData);
                                // Connect to chat server
                                connection.connect(true);
                            } else {
                                onError(loginData.getError());
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
                                    button.setText("Login");
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
        sessionData.initSession(loginData.getTicket(), account.getText().toString());
        // Add bookmarks to the RelationManager

        Set<String> bookmarksList = new HashSet<String>();
        for (Iterator<FlistHttpClient.LoginData.Bookmark> iterator = loginData.getBookmarks().iterator(); iterator.hasNext(); ) {
            FlistHttpClient.LoginData.Bookmark bookmark = iterator.next();
            bookmarksList.add(bookmark.getName());
        }
        relationManager.addCharacterToList(CharRelation.BOOKMARKED, bookmarksList);
        Ln.v("Added " + bookmarksList.size() + " bookmarks.");

        // Add friends to the RelationManager
        Set<String> friendList = new HashSet<String>();
        for (Iterator<FlistHttpClient.LoginData.Friend> iterator = loginData.getFriends().iterator(); iterator.hasNext(); ) {
            FlistHttpClient.LoginData.Friend friend = iterator.next();
            friendList.add(friend.getFriend());
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
        prefEditor.apply();

        Collections.sort(loginData.getCharacters());
        sessionData.setCharList(loginData.getCharacters());
        sessionData.setDefaultChar(loginData.getDefaultCharacter());
    }

    public void setError(String message) {
        if (message.contains("Host is unresolved")) {
            errorField.setText(getActivity().getString(R.string.error_disconnected_no_connection));
        }
        else {
            errorField.setText("Disconnected: " + message);
        }
    }
}

