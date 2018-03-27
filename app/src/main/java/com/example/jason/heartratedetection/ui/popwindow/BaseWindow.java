package com.example.jason.heartratedetection.ui.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/6.
 */

public abstract class BaseWindow extends PopupWindow {

    protected Context context;

    protected View contentView;

    public BaseWindow(Context context, int width, int height) {
        super(width, height);
        this.context = context;
        contentView = LayoutInflater.from(context).inflate(layoutId(), null);
        ButterKnife.bind(this, contentView);
        init();
    }

    public void init() {
        setContentView(contentView);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOutsideTouchable(true);
        if (animStyle() != -1)
            setAnimationStyle(animStyle());
    }

    public abstract int layoutId();

    protected int animStyle() {
        return -1;
    }
}
