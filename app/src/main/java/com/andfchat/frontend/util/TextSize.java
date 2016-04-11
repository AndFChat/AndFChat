package com.andfchat.frontend.util;

import com.andfchat.R;

public enum TextSize {
    very_small(R.dimen.text_size_button_very_small),
    small(R.dimen.text_size_button_small),
    medium(R.dimen.text_size_button_medium),
    big(R.dimen.text_size_button_big);

    private int textSizeId;

    TextSize(int id) {
        textSizeId = id;
    }

    public int getTextSizeId() {
        return textSizeId;
    }
}
