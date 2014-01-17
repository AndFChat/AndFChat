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


package com.homebrewn.flistchat.core.util;

import java.util.Comparator;

import com.homebrewn.flistchat.core.data.FlistChar;


public class FlistCharComparator implements Comparator<FlistChar> {

    @Override
    public int compare(FlistChar lhs, FlistChar rhs) {
        int compareInt;

        if (lhs == null) {
            return 1;
        }
        if (rhs == null) {
            return -1;
        }

        //compare bookmared/friend vs not bookmarked/friend
        if ((lhs.isBookmarked() || lhs.isFriend()) && (!rhs.isBookmarked() && !rhs.isFriend())) {
            return -1;
        }
        else if ((!lhs.isBookmarked() || !lhs.isFriend()) && (rhs.isBookmarked() && rhs.isFriend())) {
            return 1;
        }

        //compare gender
        else if ((compareInt = lhs.getGender().getName().compareTo(rhs.getGender().getName())) != 0) {
            return compareInt;
        }
        //compare name
        else {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

}
