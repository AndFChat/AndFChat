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

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.andfchat.R;
import com.andfchat.core.util.BBCodeReader;
import com.andfchat.frontend.popup.FListPopupWindow;

public class AboutAction {

    public static void open(Activity activity, View parent, String aboutText) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.popup_about, null);

        Display display = activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int height = (int)(size.y * 0.8f);
        int width = (int)(size.x * 0.8f);

        TextView text = (TextView)layout.findViewById(R.id.aboutText);
        // Make links clickable.
        text.setMovementMethod(LinkMovementMethod.getInstance());
        // Replace text with bb encoded text.
        text.setText(BBCodeReader.createSpannableWithBBCode(aboutText, activity));

        final PopupWindow popupWindow = new FListPopupWindow(layout, width, height);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }
}
