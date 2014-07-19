package com.andfchat.frontend.util.quickaction;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andfchat.R;

/**
 * Button class for QuickActionBar, will be added to a bar by addActionItem(final ActionItem action).
 * ActionItem can change there depended View object by using the setters. Be careful this also means
 * an ActionItem can only be connected to one QuickActionBar.
 *
 * @author Sourcerer
 *
 */
public class ActionItem {

    private Drawable icon;
    private String title;

    private boolean selected = false;
    private boolean sticky = false;

    private QuickActionClickListner onClickListner;
    private QuickActionPreOpenListner onPreOpenListner;

    private View button;
    private ImageView imageView;
    private TextView textView;

    public ActionItem(String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
    }

    protected void init(View button) {
        this.button = button;

        imageView = (ImageView) button.findViewById(R.id.qa_icon);
        textView = (TextView) button.findViewById(R.id.qa_title);

        setTitle(title);
        setIcon(icon);

        button.setSelected(selected);
        button.setFocusable(true);
        button.setClickable(true);
    }

    public void setTitle(String title) {
        this.title = title;

        if (textView != null) {
            textView.setText(title);

            if (title == null) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    public String getTitle() {
        return this.title;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;

        if (imageView != null) {
            imageView.setImageDrawable(icon);

            if (icon == null) {
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        if (button != null) {
            button.setSelected(selected);
        }
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setQuickActionClickListner(QuickActionClickListner onClickListner) {
        this.onClickListner = onClickListner;
    }

    public QuickActionClickListner getQuickActionClickListner() {
        return onClickListner;
    }

    public boolean hasOnClickListner() {
        return onClickListner != null;
    }

    public void setQuickActionPreOpenListner(QuickActionPreOpenListner onPreOpenListner) {
        this.onPreOpenListner = onPreOpenListner;
    }

    public QuickActionPreOpenListner getQuickActionPreOpenListner() {
        return onPreOpenListner;
    }

    public boolean hasOnPreOpenListner() {
        return onPreOpenListner != null;
    }

    /**
     * Can only return isEnabled after beeing added to a QuickActionBar
     */
    public void setEnabled(boolean value) {
        button.setEnabled(value);
    }

    /**
     * Can only return isEnabled after beeing added to a QuickActionBar
     * @return
     */
    public boolean isEnabled() {
        return button.isEnabled();
    }

    /**
     * Can only return isEnabled after beeing added to a QuickActionBar
     */
    public void setVisibility(int value) {
        button.setVisibility(value);
    }

}
