package com.example.jason.heartratedetection.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.jason.heartratedetection.R;

/**
 * Created by Administrator on 2018/4/13.
 */

public class ItemTabView extends View {
    public static final int NONE = 0x11;
    public static final int TRIANGLE = 0x22;
    public static final int SQUARE = 0x33;
    public static final int PENTAGON = 0x44;
    public static final int HEXAGON = 0x55;
    public static final int CIRCLE = 0x66;
    public static final int DIAMOND = 0x77;
    public static final int TOP_TRAPEZOIDAL = 0x88;
    public static final int BOTTOM_TRAPEZOIDAL = 0x99;
    public static final int OCTAGON = 0x111;
    public static final int PENTAGRAM = 0x112;
    public static final int CROSS = 0x113;

    private int width;
    private int height;
    private int borderLength;
    private int iconId;
    private String text;
    private int shape;
    private int textColor;
    private int borderColor;
    private int iconColor;
    private int fillColor;
    private float textSize;
    private float borderSize;
    private int contentHeight;
    private int contentWidth;
    private float offset;
    private boolean fill;

    private Rect textRect = new Rect();
    private Bitmap bitmap;
    private Paint borderPaint;
    private Paint textPaint;
    private Paint fillPaint;
    private Paint bitmapPaint;
    private Context context;

    public ItemTabView(Context context) {
        this(context, null);
    }

    public ItemTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ItemTabView);
        iconId = array.getResourceId(R.styleable.ItemTabView_icon, 0);
        text = array.getString(R.styleable.ItemTabView_text);
        shape = array.getInteger(R.styleable.ItemTabView_shape, NONE);
        textColor = array.getColor(R.styleable.ItemTabView_textColor, Color.BLACK);
        borderColor = array.getColor(R.styleable.ItemTabView_borderColor, Color.BLACK);
        iconColor = array.getColor(R.styleable.ItemTabView_iconColor, Integer.MAX_VALUE);
        fillColor = array.getColor(R.styleable.ItemTabView_fiilColor, Color.BLACK);
        textSize = array.getDimension(R.styleable.ItemTabView_textSize, 30);
        borderSize = array.getDimension(R.styleable.ItemTabView_borderSize, 0);
        offset = array.getDimension(R.styleable.ItemTabView_offset, 10);
        fill = array.getBoolean(R.styleable.ItemTabView_fill, false);
        array.recycle();
        init();
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        borderPaint.setStrokeWidth(borderSize);
        borderPaint.setStyle(Paint.Style.STROKE);

        fillPaint = new Paint();
        fillPaint.setColor(fillColor);
        fillPaint.setAntiAlias(true);
        fillPaint.setDither(true);
        fillPaint.setStrokeWidth(borderSize);
        fillPaint.setStyle(Paint.Style.FILL);

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setDither(true);
        bitmapPaint.setColor(iconColor);
        bitmapPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
        if (text != null)
            textPaint.getTextBounds(text, 0, text.length(), textRect);
        bitmap = BitmapFactory.decodeResource(context.getResources(), iconId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY)
            width = MeasureSpec.getSize(widthMeasureSpec);
        else
            width = 100;
        mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY)
            height = MeasureSpec.getSize(heightMeasureSpec);
        else
            height = 100;
        setMeasuredDimension(width, height);

        if (width != 0 && height != 0) {
            if (bitmap == null) {
                contentHeight = textRect.height();
                contentWidth = textRect.width();
            } else {
                borderLength = Math.min(width, height) * 2 / 3;
                contentHeight = borderLength + textRect.height();
                contentWidth = borderLength;
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate((width - contentWidth) / 2, (height - contentHeight) / 2);
        drawFill(canvas);
        drawBorder(canvas);
        drawColorBitmap(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (text == null)
            return;
        canvas.drawText(text, (contentWidth - textRect.width()) / 2, contentHeight + offset, textPaint);
    }


    private void drawColorBitmap(Canvas canvas) {
        if (bitmap == null)
            return;
        if (iconColor == Integer.MAX_VALUE) {
            drawBitmap(canvas);
        } else {
            RectF rectf = new RectF(0, 0, borderLength, borderLength);
            int sc = canvas.saveLayer(rectf, bitmapPaint, Canvas.ALL_SAVE_FLAG);
            drawBitmap(canvas);
            bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawRect(rectf, bitmapPaint);
            canvas.drawColor(iconColor, PorterDuff.Mode.DST_IN);
            bitmapPaint.setXfermode(null);
            canvas.restoreToCount(sc);
        }
    }

    private void drawBitmap(Canvas canvas) {
        Matrix matrix = new Matrix();
        matrix.postTranslate((borderLength - bitmap.getWidth()) / 2, (borderLength - bitmap.getHeight()) / 2);
        float sw = borderLength / 2f / bitmap.getWidth();
        float sh = borderLength / 2f / bitmap.getHeight();
        float scale = Math.min(sh, sw);
        matrix.postScale(scale, scale, borderLength / 2, borderLength / 2);
        canvas.drawBitmap(bitmap, matrix, bitmapPaint);
    }

    private void drawFill(Canvas canvas) {
        if(shape == NONE)
            return;
        if (fill) {
            Path path = createShapePath();
            if (path == null) {
                canvas.drawCircle(borderLength / 2, borderLength / 2, borderLength / 2, fillPaint);
            } else {
                canvas.drawPath(path, fillPaint);
            }
        }
    }

    private void drawBorder(Canvas canvas) {
        if(shape == NONE)
            return;
        Path path = createShapePath();
        if (path == null)
            canvas.drawCircle(borderLength / 2, borderLength / 2, borderLength / 2, borderPaint);
        else
            canvas.drawPath(path, borderPaint);
    }

    private Path createShapePath() {
        Path path = new Path();
        if (shape == TRIANGLE) {
            path.moveTo(borderLength / 2, 0);
            path.lineTo(borderLength, borderLength);
            path.lineTo(0, borderLength);
            path.lineTo(borderLength / 2, 0);
        } else if (shape == SQUARE) {
            path.moveTo(0, 0);
            path.lineTo(borderLength, 0);
            path.lineTo(borderLength, borderLength);
            path.lineTo(0, borderLength);
            path.lineTo(0, 0);
        } else if (shape == CIRCLE) {
            path = null;
        } else if (shape == PENTAGON) {
            path.moveTo(borderLength / 2, 0);
            path.lineTo(borderLength, borderLength / 2);
            path.lineTo(borderLength * 3 / 4, borderLength);
            path.lineTo(borderLength / 4, borderLength);
            path.lineTo(0, borderLength / 2);
            path.lineTo(borderLength / 2, 0);
        } else if (shape == HEXAGON) {
            path.moveTo(borderLength / 4, 0);
            path.lineTo(borderLength * 3 / 4, 0);
            path.lineTo(borderLength, borderLength / 2);
            path.lineTo(borderLength * 3 / 4, borderLength);
            path.lineTo(borderLength / 4, borderLength);
            path.lineTo(0, borderLength / 2);
            path.lineTo(borderLength / 4, 0);
        } else if (shape == DIAMOND) {
            path.moveTo(borderLength / 2, 0);
            path.lineTo(borderLength, borderLength / 2);
            path.lineTo(borderLength / 2, borderLength);
            path.lineTo(0, borderLength / 2);
            path.lineTo(borderLength / 2, 0);
        } else if (shape == BOTTOM_TRAPEZOIDAL) {
            path.moveTo(0, 0);
            path.lineTo(borderLength, 0);
            path.lineTo(borderLength * 3 / 4, borderLength);
            path.lineTo(borderLength / 4, borderLength);
            path.lineTo(0, 0);
        } else if (shape == TOP_TRAPEZOIDAL) {
            path.moveTo(borderLength / 4, 0);
            path.lineTo(borderLength * 3 / 4, 0);
            path.lineTo(borderLength, borderLength);
            path.lineTo(0, borderLength);
            path.lineTo(borderLength / 4, 0);
        } else if (shape == OCTAGON) {
            path.moveTo(borderLength / 3, 0);
            path.lineTo(borderLength * 2 / 3, 0);
            path.lineTo(borderLength, borderLength / 3);
            path.lineTo(borderLength, borderLength * 2 / 3);
            path.lineTo(borderLength * 2 / 3, borderLength);
            path.lineTo(borderLength / 3, borderLength);
            path.lineTo(0, borderLength * 2 / 3);
            path.lineTo(0, borderLength / 3);
            path.lineTo(borderLength / 3, 0);
        } else if (shape == PENTAGRAM) {
            path.moveTo(borderLength / 2, 0);
            path.lineTo(borderLength / 4, borderLength);
            path.lineTo(borderLength, borderLength / 2);
            path.lineTo(0, borderLength / 2);
            path.lineTo(borderLength * 3 / 4, borderLength);
            path.lineTo(borderLength / 2, 0);
        } else if (shape == CROSS) {
            path.moveTo(borderLength / 3, 0);
            path.lineTo(borderLength * 2 / 3, 0);
            path.lineTo(borderLength * 2 / 3, borderLength / 3);
            path.lineTo(borderLength, borderLength / 3);
            path.lineTo(borderLength, borderLength * 2 / 3);
            path.lineTo(borderLength * 2 / 3, borderLength * 2 / 3);
            path.lineTo(borderLength * 2 / 3, borderLength);
            path.lineTo(borderLength / 3, borderLength);
            path.lineTo(borderLength / 3, borderLength * 2 / 3);
            path.lineTo(0, borderLength * 2 / 3);
            path.lineTo(0, borderLength / 3);
            path.lineTo(borderLength / 3, borderLength / 3);
            path.lineTo(borderLength / 3, 0);
        }
        if (path != null)
            path.close();
        return path;
    }
}
