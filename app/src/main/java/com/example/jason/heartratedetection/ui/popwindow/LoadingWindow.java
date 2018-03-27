package com.example.jason.heartratedetection.ui.popwindow;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jason.heartratedetection.R;

import butterknife.BindView;
import butterknife.BindViews;

/**
 * Created by Administrator on 2018/3/25.
 */

public class LoadingWindow extends BaseWindow {
    @BindView(R.id.iv_deleting)
    ImageView ivDeleting;
    @BindView(R.id.tv_text)
    TextView tvText;
    ObjectAnimator loadingOa;

    public LoadingWindow(Context context, int width, int height) {
        super(context, width, height);
        loadingOa = ObjectAnimator.ofFloat(ivDeleting, "rotation", 0, 360 * 10);
        loadingOa.setInterpolator(new LinearInterpolator());
        loadingOa.setDuration(500 * 10);
        loadingOa.setRepeatCount(ObjectAnimator.INFINITE);
        loadingOa.start();
    }

    public void setText(String text) {
        tvText.setText(text);
    }

    @Override
    public int layoutId() {
        return R.layout.item_detele_ing;
    }

    @Override
    protected int animStyle() {
        return R.style.alpha_in_out;
    }
}
