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


package com.andfchat.core.data;

import com.andfchat.R;

public enum Gender {
    MALE("Male", R.color.name_male),
    FEMALE("Female", R.color.name_female),
    TRANSGENDER("Transgender", R.color.name_transgender),
    HERM("Herm", R.color.name_herm),
    SHEMALE("Shemale", R.color.name_shemale),
    MALE_HERM("Male-Herm", R.color.name_male_herm),
    CUNT_BOY("Cunt-boy", R.color.name_cunt_boy),
    UNKNOWN("", R.color.name_unknown);

    private String name;
    private int color;

    private Gender(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getColorId() {
        return color;
    }

}
