package com.example.jason.heartratedetection.ui.widget.shape;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;

/**
 * Created by Administrator on 2018/3/12.
 */

public class ShapeHelper {
    public GradientDrawable gradientDrawable;
    public int shape;
    public float radius;
    public float leftTopRadius;
    public float rightTopRadius;
    public float leftBottomRadius;
    public float rightBottomRadius;
    public int solid = Color.WHITE;
    public int alpha = 255;
    public int strokeWidth;
    public int strokeColor = Color.WHITE;
    public float strokeDashGap;
    public float strokeDashWidth;
    public static final int RECTANGLE = 0x11;
    public static final int OVAL = 0x22;
    public View target;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void init(View view) {
        target = view;
        gradientDrawable = new GradientDrawable();
        setAlpha(alpha)
                .setSolid(solid)
                .setLeftTopRadius(leftTopRadius)
                .setLeftBottomRadius(leftBottomRadius)
                .setRightTopRadius(rightTopRadius)
                .setRightBottomRadius(rightBottomRadius)
                .setStrokeColor(strokeColor)
                .setStrokeWidth(strokeWidth)
                .setStrokeDashWidth(strokeDashWidth)
                .setStrokeDashGap(strokeDashGap)
                .setShape(shape);
        if (radius != 0) {
            setRadius(radius);
        }
        target.setBackground(gradientDrawable);
    }

    public ShapeHelper setShape(int shape) {
        this.shape = shape;
        switch (shape) {
            case RECTANGLE:
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                break;

            case OVAL:
                gradientDrawable.setShape(GradientDrawable.OVAL);
                break;
        }
        return this;
    }

    public ShapeHelper setRadius(float radius) {
        this.radius = radius;
        gradientDrawable.setCornerRadius(radius);
        return this;
    }

    public ShapeHelper setRadius(float mLeftTopRadius, float mLeftBottomRadius, float mRightTRadius, float mRightBottomRadius) {
        gradientDrawable.setCornerRadii(new float[]{mLeftTopRadius, mLeftBottomRadius, mRightTRadius, mRightBottomRadius});
        return this;
    }


    public ShapeHelper setLeftTopRadius(float leftTopRadius) {
        this.leftTopRadius = leftTopRadius;
        gradientDrawable.setCornerRadii(new float[]{leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius});
        return this;
    }

    public ShapeHelper setRightTopRadius(float rightTopRadius) {
        this.rightTopRadius = rightTopRadius;
        gradientDrawable.setCornerRadii(new float[]{leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius});
        return this;
    }

    public ShapeHelper setLeftBottomRadius(float leftBottomRadius) {
        this.leftBottomRadius = leftBottomRadius;
        gradientDrawable.setCornerRadii(new float[]{leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius});
        return this;
    }

    public ShapeHelper setRightBottomRadius(float rightBottomRadius) {
        this.rightBottomRadius = rightBottomRadius;
        gradientDrawable.setCornerRadii(new float[]{leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius});
        return this;
    }

    public ShapeHelper setSolid(int solid) {
        this.solid = solid;
        gradientDrawable.setColor(solid);
        return this;
    }

    public ShapeHelper setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        return this;
    }

    public ShapeHelper setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        return this;
    }

    public ShapeHelper setStrokeDashGap(float strokeDashGap) {
        this.strokeDashGap = strokeDashGap;
        gradientDrawable.setStroke(strokeWidth, strokeColor, strokeDashWidth, strokeDashGap);
        return this;
    }

    public ShapeHelper setStrokeDashWidth(float strokeDashWidth) {
        this.strokeDashWidth = strokeDashWidth;
        gradientDrawable.setStroke(strokeWidth, strokeColor, strokeDashWidth, strokeDashGap);
        return this;
    }

    public ShapeHelper setAlpha(int alpha) {
        this.alpha = alpha;
        gradientDrawable.setAlpha(alpha);
        return this;
    }

    public void complete() {
        target.setBackground(gradientDrawable);
    }
}
