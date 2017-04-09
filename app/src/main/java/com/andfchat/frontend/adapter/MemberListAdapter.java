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


package com.andfchat.frontend.adapter;

import java.util.ArrayList;
import java.util.List;

import net.sourcerer.quickaction.ActionItem;
import net.sourcerer.quickaction.PopUpAlignment;
import net.sourcerer.quickaction.QuickActionBar;
import net.sourcerer.quickaction.QuickActionOnClickListener;
import net.sourcerer.quickaction.QuickActionOnOpenListener;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import roboguice.RoboGuice;
import roboguice.util.Ln;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.connection.FlistHttpClient;
import com.andfchat.core.connection.handler.PrivateMessageHandler;
import com.andfchat.core.connection.handler.VariableHandler.Variable;
import com.andfchat.core.data.CharRelation;
import com.andfchat.core.data.Chatroom;
import com.andfchat.core.data.ChatroomManager;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.RelationManager;
import com.andfchat.core.data.SessionData;
import com.andfchat.core.data.messages.ChatEntryFactory;
import com.andfchat.core.util.FlistCharComparatorGender;
import com.andfchat.frontend.util.NameSpannable;
import com.google.inject.Inject;
import okhttp3.OkHttpClient;

public class MemberListAdapter extends ArrayAdapter<FCharacter> {

    public final static FlistCharComparatorGender COMPARATOR = new FlistCharComparatorGender();

    @Inject
    private ChatroomManager chatroomManager;
    @Inject
    private SessionData sessionData;
    @Inject
    private RelationManager relationManager;
    @Inject
    private ChatEntryFactory entryFactory;

    private final List<FCharacter> chars;
    private final QuickActionBar quickActionBar;
    private FCharacter activeCharacter;
    private View activeCharacterView;

    public MemberListAdapter(final Context context, List<FCharacter> chars) {
        super(context, R.layout.list_item_user, chars);

        RoboGuice.getInjector(context).injectMembers(this);

        if (chars.size() > 1) {
            COMPARATOR.setChatroom(chatroomManager.getActiveChat());
            sortList();
        }

        this.chars = new ArrayList<FCharacter>();
        for (int i=0; i<chars.size(); i++) {
            if (!chars.get(i).isIgnored()) {
                this.chars.add(chars.get(i));
            }
        }

        quickActionBar = new QuickActionBar(context);
        quickActionBar.setAlignment(PopUpAlignment.LEFT);

        // Add PM user
        ActionItem pmUser = new ActionItem(context.getResources().getString(R.string.pm_user), context.getResources().getDrawable(R.drawable.ic_pm));
        pmUser.setQuickActionClickListener(new QuickActionOnClickListener() {

            @Override
            public void onClick(ActionItem item, View view) {
                Chatroom chatroom;
                if (!chatroomManager.hasOpenPrivateConversation(activeCharacter)) {
                    int maxTextLength = sessionData.getIntVariable(Variable.priv_max);
                    chatroom = PrivateMessageHandler.openPrivateChat(chatroomManager, activeCharacter, maxTextLength, sessionData.getSessionSettings().showAvatarPictures());

                    if (activeCharacter.getStatusMsg() != null && activeCharacter.getStatusMsg().length() > 0) {
                        chatroomManager.addMessage(chatroom, entryFactory.getStatusInfo(activeCharacter));
                    }

                } else {
                    chatroom = chatroomManager.getPrivateChatFor(activeCharacter);
                }

                activeCharacter = null;
                chatroomManager.setActiveChat(chatroom);
                notifyDataSetChanged();
            }
        });
        quickActionBar.addActionItem(pmUser);

        final String bookmarkText = context.getResources().getString(R.string.bookmark_user);
        final String unbookmarkText = context.getResources().getString(R.string.unbookmark_user);

        // Add Bookmark user
        final ActionItem bookmark = new ActionItem(bookmarkText, context.getResources().getDrawable(R.drawable.ic_bookmark));
        bookmark.setQuickActionClickListener(new QuickActionOnClickListener() {

            @Override
            public void onClick(ActionItem item, View view) {

                OkHttpClient client = new OkHttpClient();
                //client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));

                Retrofit restAdapter = new Retrofit.Builder()
                        .baseUrl("https://www.f-list.net")
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                FlistHttpClient httpClient = restAdapter.create(FlistHttpClient.class);


                retrofit2.Callback<FlistHttpClient.LoginData> ticketgetterremove = new retrofit2.Callback<FlistHttpClient.LoginData>() {
                    @Override
                    public void onResponse(retrofit2.Response<FlistHttpClient.LoginData> response) {
                        FlistHttpClient.LoginData loginData = response.body();
                        Ln.i("Successfully got a ticket: " + loginData.getTicket());
                        sessionData.setTicket(loginData.getTicket());
                        removeBookmark();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Ln.i("Problem with getting ticket: " + t.getMessage());
                    }
                };

                retrofit2.Callback<FlistHttpClient.LoginData> ticketgetteradd = new retrofit2.Callback<FlistHttpClient.LoginData>() {
                    @Override
                    public void onResponse(retrofit2.Response<FlistHttpClient.LoginData> response) {
                        FlistHttpClient.LoginData loginData = response.body();
                        Ln.i("Successfully got a ticket: " + loginData.getTicket());
                        sessionData.setTicket(loginData.getTicket());
                        addBookmark();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Ln.i("Problem with getting ticket: " + t.getMessage());
                    }
                };



                if (item.isSelected()) {
                    Call<FlistHttpClient.LoginData> relog = httpClient.logIn(sessionData.getAccount(), sessionData.getPassword());
                    relog.enqueue(ticketgetterremove);

                }
                else {
                    Call<FlistHttpClient.LoginData> relog = httpClient.logIn(sessionData.getAccount(), sessionData.getPassword());
                    relog.enqueue(ticketgetteradd);
                }
            }
        });

        bookmark.setQuickActionOnOpenListener(new QuickActionOnOpenListener() {

            @Override
            public void onOpen(ActionItem item) {
                if (activeCharacter.isBookmarked()) {
                    item.setSelected(true);
                    item.setTitle(unbookmarkText);
                    item.setIcon(context.getResources().getDrawable(R.drawable.ic_bookmarked));
                }
                else {
                    item.setSelected(false);
                    item.setTitle(bookmarkText);
                    item.setIcon(context.getResources().getDrawable(R.drawable.ic_bookmark));
                }
            }
        });

        quickActionBar.addActionItem(bookmark);

        // Add show details
        ActionItem showDetails = new ActionItem(context.getResources().getString(R.string.show_profile), context.getResources().getDrawable(R.drawable.ic_info));
        showDetails.setQuickActionClickListener(new QuickActionOnClickListener() {

            @Override
            public void onClick(ActionItem item, View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.f-list.net/c/" + activeCharacter.getName())); //was https
                if (android.os.Build.VERSION.SDK_INT >= 18) {
                    final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";
                    Bundle extras = new Bundle();
                    extras.putBinder(EXTRA_CUSTOM_TABS_SESSION, null);
                    final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
                    browserIntent.putExtra(EXTRA_CUSTOM_TABS_TOOLBAR_COLOR, R.color.primary_color);
                    browserIntent.putExtras(extras);
                }
                getContext().startActivity(browserIntent);
            }
        });
        quickActionBar.addActionItem(showDetails);

        quickActionBar.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                if (activeCharacterView != null) {
                    activeCharacterView.setSelected(false);
                }
            }
        });
    }

    private class UserViewHolder{
        public TextView textView;
        public ImageView itemIcon;
        public ImageView itemIconOverlay;
        public ImageView itemIconOverlay2;
        View userLabel;

        public UserViewHolder(TextView textView, ImageView itemIcon, ImageView itemIconOverlay, ImageView itemIconOverlay2, View userLabel){
            this.textView = textView;
            this.itemIcon = itemIcon;
            this.itemIconOverlay = itemIconOverlay;
            this.itemIconOverlay2 = itemIconOverlay2;
            this.userLabel = userLabel;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FCharacter character = this.getItem(position);

        TextView textView;
        ImageView itemIcon;
        ImageView itemIconOverlay;
        ImageView itemIconOverlay2;
        View userLabel;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_user, null);

            textView = (TextView) convertView.findViewById(R.id.itemText);
            itemIcon = (ImageView)convertView.findViewById(R.id.itemIcon);
            itemIconOverlay = (ImageView)convertView.findViewById(R.id.itemIconOverlay);
            itemIconOverlay2 = (ImageView)convertView.findViewById(R.id.itemIconOverlay2);
            userLabel = convertView.findViewById(R.id.userlabel);
            convertView.setTag(new UserViewHolder(textView, itemIcon, itemIconOverlay, itemIconOverlay2, userLabel));
        } else {
            UserViewHolder holder = (UserViewHolder) convertView.getTag();
            textView = holder.textView;
            itemIcon = holder.itemIcon;
            itemIconOverlay = holder.itemIconOverlay;
            itemIconOverlay2 = holder.itemIconOverlay2;
            userLabel = holder.userLabel;
        }

        final View rowView = convertView;
        rowView.setSelected(false);

        // Set username
        textView.setText(new NameSpannable(character, null, getContext().getResources()));

        // Set icon
        switch (character.getStatus()) {
            case ONLINE:
                itemIcon.setBackgroundResource(R.drawable.icon_blue);
                break;
            case BUSY:
                itemIcon.setBackgroundResource(R.drawable.icon_orange);
                break;
            case DND:
                itemIcon.setBackgroundResource(R.drawable.icon_red);
                break;
            case LOOKING:
                itemIcon.setBackgroundResource(R.drawable.icon_green);
                break;
            case AWAY:
                itemIcon.setBackgroundResource(R.drawable.icon_grey);
                break;
            case IDLE:
                itemIcon.setBackgroundResource(R.drawable.icon_grey2);
                break;
            case CROWN:
                itemIcon.setBackgroundResource(R.drawable.icon_gold);
                break;
            default:
                itemIcon.setBackgroundResource(R.drawable.icon_blue);
        }

        // Set icon
        if (character.isGlobalOperator()) {
            itemIconOverlay.setVisibility(View.VISIBLE);
            itemIconOverlay2.setVisibility(View.GONE);
        }
        else if (chatroomManager.getActiveChat().isChannelMod(character)) {
            itemIconOverlay.setVisibility(View.GONE);
            itemIconOverlay2.setVisibility(View.VISIBLE);
        }
        else {
            itemIconOverlay.setVisibility(View.GONE);
            itemIconOverlay2.setVisibility(View.GONE);
        }

        userLabel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sessionData.getCharacterName().equals(character.getName())) {
                    activeCharacterView = rowView;
                    activeCharacterView.setSelected(true);
                    activeCharacter = character;

                    quickActionBar.show(v);
                }
            }
        });

        if (position == 1) {
            Ln.d("Redrawn");
        }

        return rowView;
    }

    @Override
    public void add(FCharacter fCharacter) {
        if (fCharacter == null || chars.contains(fCharacter)) {
            return;
        }

        COMPARATOR.setChatroom(chatroomManager.getActiveChat());

        boolean added = false;
        for (int i = 0; i < chars.size(); i++) {
            if (COMPARATOR.compare(chars.get(i), fCharacter) >= 0 && !fCharacter.isIgnored()) {
                chars.add(i, fCharacter);
                added = true;
                break;
            }
        }

        if (!added && !fCharacter.isIgnored()) {
            chars.add(fCharacter);
        }
        sortList();

        notifyDataSetChanged();
    }

    public void removeBookmark() {
        OkHttpClient client = new OkHttpClient();
        //client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl("https://www.f-list.net")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FlistHttpClient httpClient = restAdapter.create(FlistHttpClient.class);

        // HTTP call need to be a post and post wants a callback, that is not needed -> ignore
        retrofit2.Callback<Object> callback = new retrofit2.Callback<Object>() {
            @Override
            public void onResponse(Response<Object> response) {
                if (response.body() != null) {
                    relationManager.removeFromList(CharRelation.BOOKMARKED, activeCharacter);
                    sortList();
                } else {
                    onError("null response.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                onError(t.getMessage());
            }

            private void onError(final String message) {
                Ln.i("Bookmarking failed: " + message);
            }
        };
        Call<Object> call = httpClient.removeBookmark(sessionData.getAccount(), sessionData.getTicket(), activeCharacter.getName());
        Ln.i("Removing " + activeCharacter.getName() + " from bookmarks");
        call.enqueue(callback);
    }

    public void addBookmark() {
        OkHttpClient client = new OkHttpClient();
        //client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl("https://www.f-list.net")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FlistHttpClient httpClient = restAdapter.create(FlistHttpClient.class);

        // HTTP call need to be a post and post wants a callback, that is not needed -> ignore
        retrofit2.Callback<Object> callback = new retrofit2.Callback<Object>() {
            @Override
            public void onResponse(Response<Object> response) {
                if (response.body() != null) {
                    relationManager.addOnList(CharRelation.BOOKMARKED, activeCharacter);
                    sortList();
                } else {
                    onError("null response.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                onError(t.getMessage());
            }

            private void onError(final String message) {
                Ln.i("Bookmarking failed: " + message);
            }
        };
        Call<Object> call = httpClient.addBookmark(sessionData.getAccount(), sessionData.getTicket(), activeCharacter.getName());
        Ln.i("Adding " + activeCharacter.getName() + " to bookmarks");
        call.enqueue(callback);
    }

    public void sortList(){
        this.sort(COMPARATOR);
    }

}
