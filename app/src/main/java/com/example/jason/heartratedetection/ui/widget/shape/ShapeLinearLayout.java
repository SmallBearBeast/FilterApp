package com.example.jason.heartratedetection.ui.widget.shape;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jason.heartratedetection.R;

/**
 * Created by Administrator on 2018/3/25.
 */

public class ShapeLinearLayout extends LinearLayout implements View.OnClickListener {
    private boolean isCheck = false;
    private ShapeHelper shapeHelper;
    public ShapeLinearLayout(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public ShapeLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        shapeHelper = new ShapeHelper();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ShapeView);
        shapeHelper.shape = array.getInt(R.styleable.ShapeView_sv_shape, GradientDrawable.RECTANGLE);
        shapeHelper.alpha = array.getInt(R.styleable.ShapeView_sv_alpha, 255);
        shapeHelper.radius = array.getDimensionPixelSize(R.styleable.ShapeView_sv_radius, 0);
        shapeHelper.leftTopRadius = array.getDimension(R.styleable.ShapeView_sv_left_top_radius, 0);
        shapeHelper.leftBottomRadius = array.getDimension(R.styleable.ShapeView_sv_left_bottom_radius, 0);
        shapeHelper.rightTopRadius = array.getDimension(R.styleable.ShapeView_sv_right_top_radius, 0);
        shapeHelper.rightBottomRadius = array.getDimension(R.styleable.ShapeView_sv_right_bottom_radius, 0);
        shapeHelper.solid = array.getColor(R.styleable.ShapeView_sv_solid, Color.WHITE);
        shapeHelper.strokeColor = array.getColor(R.styleable.ShapeView_sv_stroke_color, Color.WHITE);
        shapeHelper.strokeWidth = (int) array.getDimension(R.styleable.ShapeView_sv_stroke_width, 0);
        shapeHelper.strokeDashGap = array.getDimension(R.styleable.ShapeView_sv_stroke_gap, 0);
        shapeHelper.strokeDashWidth = array.getDimension(R.styleable.ShapeView_sv_stroke_gap_width, 0);
        array.recycle();
        shapeHelper.init(this);
    }

    @Override
    public void onClick(View v) {
        if (mCheckListener == null)
            return;
        if (!isCheck) {
            mCheckListener.check();
        } else {
            mCheckListener.unCheck();
        }
        isCheck = !isCheck;
    }

    public interface OnCheckListener {
        void check();

        void unCheck();
    }

    private ShapeTextView.OnCheckListener mCheckListener;

    public void setCheck(boolean check) {
        isCheck = !check;
        onClick(null);
    }

    public void setCheckListener(ShapeTextView.OnCheckListener mCheckListener) {
        this.mCheckListener = mCheckListener;
    }

    public ShapeHelper getShapeHelper() {
        return shapeHelper;
    }
}
