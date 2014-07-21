package com.andfchat.frontend.util.quickaction;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class QuickActionWindow {

    protected Context context;
    protected PopupWindow popupWindow;
    protected WindowManager windowManager;
    protected View content;

    public QuickActionWindow(Context context) {
        this.context = context;

        popupWindow = new PopupWindow(context);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                }
                return false;
            }
        });

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     *
     * @return Returns the size as android point.
     */
    protected Point getScreenSize() {
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);

        return size;
    }

    public void renderWindow() {
        if (content == null) {
            throw new RuntimeException("Can't open a popup without content!");
        }


        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setContentView(content);
    }

    public void setContentView(View content) {
        content.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        this.content = content;
    }

    public View setContentView(int layoutResID) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflator.inflate(layoutResID, null);
        setContentView(content);

        return content;
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        popupWindow.setOnDismissListener(listener);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

}
