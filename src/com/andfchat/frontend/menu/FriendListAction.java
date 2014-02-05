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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.andfchat.core.data.CharacterManager;
import com.andfchat.core.data.FlistChar;
import com.andfchat.frontend.adapter.MemberListAdapter;
import com.andfchat.frontend.popup.FListPopupWindow;
import com.andfchat.R;

public class FriendListAction {

    public static void open(Activity activity, View parent, int height) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.popup_friendlist, null);

        int width = (int)(parent.getWidth() * 0.8f);
        height = (int)(height * 0.7f);
        // Max width
        if (width > 600) {
            width = 600;
        }

        final PopupWindow popupWindow = new FListPopupWindow(layout, width, height);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        final ListView friendList = (ListView)layout.findViewById(R.id.channlesToJoin);

        List<FlistChar> friendsData = new ArrayList<FlistChar>(RoboGuice.getInjector(activity).getInstance(CharacterManager.class).getFriendList().getOnlineFriends());
        MemberListAdapter memberListData = new MemberListAdapter(activity, friendsData);
        friendList.setAdapter(memberListData);
    }
}
