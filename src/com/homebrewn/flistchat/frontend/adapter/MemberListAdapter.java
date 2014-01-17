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


package com.homebrewn.flistchat.frontend.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.homebrewn.flistchat.R;
import com.homebrewn.flistchat.core.data.FlistChar;
import com.homebrewn.flistchat.core.util.FlistCharComparator;
import com.homebrewn.flistchat.frontend.actions.CanOpenUserDetails;

public class MemberListAdapter extends ArrayAdapter<FlistChar> {

    private FlistCharComparator comparator = new FlistCharComparator();
    private Context context;
    private final CanOpenUserDetails target;

    public MemberListAdapter(Context context, CanOpenUserDetails target) {
        super(context, R.layout.list_item_member, new ArrayList<FlistChar>());
        this.context = context;
        this.target = target;
    }

    public MemberListAdapter(Context context, CanOpenUserDetails target, List<FlistChar> chars) {
        super(context, R.layout.list_item_member, chars);
        if (chars.size() > 1) {
            this.sort(comparator);
        }
        this.context = context;
        this.target = target;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.list_item_member, null);
        FlistChar character = this.getItem(position);
        TextView textView = (TextView)rowView.findViewById(R.id.itemText);
        textView.setText(character.toFormattedText());
        ImageView itemIcon = (ImageView)rowView.findViewById(R.id.itemIcon);

        switch (character.getStatus()) {
            case online:
                itemIcon.setBackgroundResource(R.drawable.icon_blue);
                break;
            case busy:
                itemIcon.setBackgroundResource(R.drawable.icon_orange);
                break;
            case dnd:
                itemIcon.setBackgroundResource(R.drawable.icon_red);
                break;
            case looking:
                itemIcon.setBackgroundResource(R.drawable.icon_green);
                break;
            case away:
                itemIcon.setBackgroundResource(R.drawable.icon_grey);
                break;
            default:
                itemIcon.setBackgroundResource(R.drawable.icon_blue);
        }


        if (target != null) {
            rowView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    target.openUserDetails(getItem(position));
                }
            });
        }
        return rowView;
    }

    @Override
    public void add(FlistChar object) {
        if (object == null) {
            return;
        }

        super.add(object);
        if (this.getCount() > 1) {
            this.sort(comparator);
        }
    }

}
