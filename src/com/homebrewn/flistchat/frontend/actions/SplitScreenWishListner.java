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


package com.homebrewn.flistchat.frontend.actions;


import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SplitScreenWishListner implements OnTouchListener {

	private final static float MIN_X_MOVEMENT= 60;
	private final static float MAX_Y_MOVEMENT = 150;

	private float positionX = 0;
	private float positionY = 0;

	private final ActionEvent actionLeft;
	private final ActionEvent actionRight;

	public SplitScreenWishListner(ActionEvent actionLeft, ActionEvent actionRight) {
	    this.actionLeft = actionLeft;
	    this.actionRight = actionRight;
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			positionX = motionEvent.getX();
			positionY = motionEvent.getY();
			return true;
	    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
			if (motionEvent.getX() - positionX > MIN_X_MOVEMENT && Math.abs(motionEvent.getY() - positionY) < MAX_Y_MOVEMENT) {
			    int maxiumSize = Math.abs(view.getLeft() - view.getRight());
			    if (positionX < maxiumSize / 2) {
			        actionLeft.onAction(true);
			    } else {
			        actionRight.onAction(false);
			    }
				return true;
			}
			else if (motionEvent.getX() - positionX < -MIN_X_MOVEMENT && Math.abs(motionEvent.getY() - positionY) < MAX_Y_MOVEMENT) {
			    int maxiumSize = Math.abs(view.getLeft() - view.getRight());
                if (positionX < maxiumSize / 2) {
                    actionLeft.onAction(false);
                } else {
                    actionRight.onAction(true);
                }
				return true;
			}
	    }
		return false;
	}

}
