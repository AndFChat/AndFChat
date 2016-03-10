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

import java.util.ArrayList;
import java.util.List;

import roboguice.RoboGuice;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.andfchat.R;
import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.FCharacter;
import com.andfchat.core.data.SessionData;
import com.andfchat.frontend.adapter.FriendListAdapter;
import com.andfchat.frontend.popup.FListPopupWindow;

public class FriendListAction {

    private static FriendListAdapter adapter;

    public static void open(Activity activity, View parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.popup_friendlist, null);

        Display display = activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int height = (int)(size.y * 0.8f);
        int width = (int)(size.x * 0.8f);

        final PopupWindow popupWindow = new FListPopupWindow(layout, width, height);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

        final ListView shownList = (ListView)layout.findViewById(R.id.userlist);

        boolean separateFriends = RoboGuice.getInjector(activity).getInstance(SessionData.class).getSessionSettings().separateFriends();

        final List<FCharacter> friendsData = new ArrayList<FCharacter>(RoboGuice.getInjector(activity).getInstance(CharacterManager.class).getFriendCharacters());
        final List<FCharacter> bookmarksData = new ArrayList<FCharacter>(RoboGuice.getInjector(activity).getInstance(CharacterManager.class).getBookmarkedCharacters());

        adapter = new FriendListAdapter(activity, new ArrayList<FCharacter>());
        shownList.setAdapter(adapter);

        if (separateFriends) {
            final Button showFriends = (Button) layout.findViewById(R.id.friendsButton);
            final Button showBookmarks = (Button) layout.findViewById(R.id.bookmarksButton);
            showFriends.setEnabled(false);

            showFriends.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    adapter.clear();
                    showFriends.setEnabled(false);
                    showBookmarks.setEnabled(true);
                    adapter.addAll(friendsData);
                    adapter.sortList();
                }
            });

            showBookmarks.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    adapter.clear();
                    showFriends.setEnabled(true);
                    showBookmarks.setEnabled(false);
                    adapter.addAll(bookmarksData);
                    adapter.sortList();
                }
            });

            // Set friends active first
            adapter.addAll(friendsData);
            adapter.sortList();
        }
        else {
            adapter.addAll(friendsData);
            // Add all bookmarks not already added (friends can be bookmarked too)
            for (FCharacter character : bookmarksData) {
                if (!character.isFriend()) {
                    adapter.add(character);
                }
            }
            adapter.sortList();
            Button showFriends = (Button) layout.findViewById(R.id.friendsButton);
            showFriends.setVisibility(View.GONE);
            Button showBookmarks = (Button) layout.findViewById(R.id.bookmarksButton);
            showBookmarks.setVisibility(View.GONE);
        }
    }

    public void clearList() {
        adapter.clear();
    }
}
