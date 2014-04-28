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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FlistChar implements Serializable {

    private final String name;
    private Gender gender;

    private final Set<CharRelation> charRelations = new HashSet<CharRelation>();

    private CharStatus status = CharStatus.online;
    private String statusMsg = null;

    public FlistChar(String name) {
        this.name = name;
        this.gender = Gender.UNKNOWN;
    }

    public FlistChar(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }

    public FlistChar(String name, String gender, String status, String statusMsg, CharRelation... charRelations) {
        this.name = name;
        this.status = CharStatus.valueOf(status);

        if (statusMsg != null && statusMsg.length() == 0) {
            this.statusMsg = null;
        } else {
            this.statusMsg = statusMsg;
        }

        for (Gender genderPos : Gender.values()) {
            if (genderPos.getName().equals(gender)) {
                this.gender = genderPos;
            }
        }
        if (this.gender == null) {
            this.gender = Gender.UNKNOWN;
        }

        Collections.addAll(this.charRelations, charRelations);
    }

    public CharStatus getStatus() {
        return status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatus(String status, String statusMsg) {
        this.status = CharStatus.valueOf(status);
        this.statusMsg = statusMsg;

        if (this.statusMsg != null && this.statusMsg.length() == 0) {
            this.statusMsg = null;
        }
    }

    public void setStatus(CharStatus newStatus) {
        this.status = newStatus;
    }

    public void addRelation(CharRelation relation) {
        charRelations.add(relation);
    }

    public void removeRelation(CharRelation relation) {
        charRelations.remove(relation);
    }

    public String getName() {
        return name;
    }

    public boolean isFriend() {
        return charRelations.contains(CharRelation.FRIEND);
    }

    public boolean isBookmarked() {
        return charRelations.contains(CharRelation.BOOKMARKED);
    }

    @Override
    public String toString() {
        return "[" + name + " " + gender.name() + " " + status.name() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FlistChar other = (FlistChar) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public Gender getGender() {
        return gender;
    }

    public void setInfos(FlistChar character) {
        this.gender = character.getGender();
    }

    public boolean isImportant() {
        return (isBookmarked() || isFriend());
    }
}
