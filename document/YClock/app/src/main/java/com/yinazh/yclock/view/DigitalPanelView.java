package com.yinazh.yclock.view;

import android.content.Context;
import android.util.AttributeSet;

import com.yinazh.yclock.font.FontManager;

/**
 * 数字表盘
 * */
public class DigitalPanelView extends androidx.appcompat.widget.AppCompatTextView {

    public DigitalPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        setTypeface(FontManager.getInstance(context).getFont());
    }
}
