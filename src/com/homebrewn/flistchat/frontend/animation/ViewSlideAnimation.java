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


package com.homebrewn.flistchat.frontend.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

public class ViewSlideAnimation implements AnimationListener {

    private int margin;
    private View view;

    public ViewSlideAnimation (View view, int margin) {
        this.margin = margin;
        this.view = view;
    }

    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationRepeat(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(view.getWidth(), view.getHeight());
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);
        layoutParams.setMargins(0, view.getLeft() + margin, 0, 0);
        view.setLayoutParams(layoutParams);
    }
}
