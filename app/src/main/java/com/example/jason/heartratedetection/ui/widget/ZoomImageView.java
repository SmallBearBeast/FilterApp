package com.example.jason.heartratedetection.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Scroller;

import com.example.jason.heartratedetection.R;


/**
 * Created by Administrator on 2017/10/31.
 */

public class ZoomImageView extends View {
    private static final String TAG = "ZoomImageView";
    private static final int Threshold = 5;
    private float zoomMax = 10;
    private float ZOOM_MIN = 0.5f;

    //the src id of picture
    private int srcId;
    //the width of view
    private int width;
    //the height of view
    private int height;
    private float lastDis;
    private float totalRate = 1.0f;
    private float zoomRate = 1.0f;
    private float lastX;
    private float lastY;

    private float[] center = new float[2];
    private float[] bmBoundary = new float[4];

    private boolean isFirstTouch = true;

    private Matrix matrix;
    private Bitmap bitmap;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ZoomImageView);
        srcId = array.getResourceId(R.styleable.ZoomImageView_src, -1);
        array.recycle();

        matrix = new Matrix();
        mZoomScroller = new Scroller(context);
        mTranScroller = new Scroller(context);

//        setOnTouchListener(touchListener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldw != w) {
            width = w;
        }
        if (oldh != h) {
            height = h;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isFirstTouch) {
            isFirstTouch = false;
            if (bitmap == null && srcId != -1)
                bitmap = initBitmap(BitmapFactory.decodeResource(getResources(), srcId));
            if (bitmap != null)
                matrix.preTranslate((width - bitmap.getWidth()) / 2, (height - bitmap.getHeight()) / 2);
        }
        if (bitmap != null)
            canvas.drawBitmap(bitmap, matrix, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    lastDis = calculateFingerDis(event);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    updateMatrixByMove(event);
                } else if (event.getPointerCount() == 2) {
                    updateMatrixByZoom(event);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    Log.e(TAG, "event.getActionIndex() =  " + event.getActionIndex());
                    lastX = event.getX(1 - event.getActionIndex());
                    lastY = event.getY(1 - event.getActionIndex());
                    Log.e(TAG, "lastX = " + lastX + "  " + "lastY = " + lastY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isDoTwiceTouch(event)) {
                    return true;
                }
                if (totalRate < 1.0f) {
                    zoomToMin((int) (totalRate * 100));
                } else if (totalRate > zoomMax) {
                    zoomToMax((int) (totalRate * 100));
                } else
                    zoomToNow();
                break;
        }
        super.onTouchEvent(event);
        return true;
    }


    private void zoomToNow() {
        mTranX = 0;
        mTranY = 0;
        int endX = 0;
        int endY = 0;
        if (bmBoundary[3] - bmBoundary[2] <= width) {
            endX = width / 2 - (int) ((bmBoundary[2] + bmBoundary[3]) / 2);
        } else {
            if (bmBoundary[2] > 0 && bmBoundary[3] > width) {
                endX = -(int) bmBoundary[2];
            } else if (bmBoundary[2] < 0 && bmBoundary[3] < width) {
                endX = (int) (width - bmBoundary[3]);
            }
        }
        if (bmBoundary[1] - bmBoundary[0] <= height) {
            endY = height / 2 - (int) ((bmBoundary[0] + bmBoundary[1]) / 2);
        } else {
            if (bmBoundary[0] > 0 && bmBoundary[1] > height) {
                endY = -(int) bmBoundary[0];
            } else if (bmBoundary[0] < 0 && bmBoundary[1] < height) {
                endY = (int) (height - bmBoundary[1]);
            }
        }
        if (endX != 0 || endY != 0) {
            mTranScroller.startScroll(0, 0, endX, endY, 300);
            invalidate();
        }
        //also use animation to replace scroller
    }

    /**
     * update matrix when a touch is moving
     *
     * @param event
     */
    private void updateMatrixByMove(MotionEvent event) {
        float curX = event.getX();
        float curY = event.getY();
        float disX = curX - lastX;
        float disY = curY - lastY;
        if (Math.max(Math.abs(disX), Math.abs(disY)) > 10) {
            lastX = curX;
            lastY = curY;
            if (disX > 0) {
                if (bmBoundary[2] < 0) {
                    if (disX > -bmBoundary[2]) {
                        disX = -bmBoundary[2];
                    }
                } else {
                    disX = 0;
                }
            } else {
                if (bmBoundary[3] > width) {
                    if (-disX > bmBoundary[3] - width) {
                        disX = width - bmBoundary[3];
                    }
                } else {
                    disX = 0;
                }
            }

            if (disY > 0) {
                if (bmBoundary[0] < 0) {
                    if (disY > -bmBoundary[0]) {
                        disY = -bmBoundary[0];
                    }
                } else {
                    disY = 0;
                }
            } else {
                if (bmBoundary[1] > height) {
                    if (-disY > bmBoundary[1] - height) {
                        disY = height - bmBoundary[1];
                    }
                } else {
                    disY = 0;
                }
            }

            bmBoundary[2] = bmBoundary[2] + disX;
            bmBoundary[3] = bmBoundary[3] + disX;
            bmBoundary[0] = bmBoundary[0] + disY;
            bmBoundary[1] = bmBoundary[1] + disY;
            Log.e(TAG, "bmBoundary[2] = " + bmBoundary[2]);
            Log.e(TAG, "bmBoundary[3] = " + bmBoundary[3]);
            matrix.postTranslate(disX, disY);
            invalidate();
        }
    }

    /**
     * update matrix when a touch is zooming
     *
     * @param event
     */
    private void updateMatrixByZoom(MotionEvent event) {
        float newDis = calculateFingerDis(event);
        if (Math.abs(newDis - lastDis) > 10) {
            calculateFingerCenter(event);
            mTwiceZoomType = 2;
            zoomRate = newDis / lastDis;
            totalRate = totalRate * zoomRate;
            if (totalRate > zoomMax + 1) {
                zoomRate = (zoomMax + 1) / (totalRate / zoomRate);
                totalRate = zoomMax + 1;
            }
            if (totalRate < ZOOM_MIN) {
                zoomRate = ZOOM_MIN / (totalRate / zoomRate);
                totalRate = ZOOM_MIN;
            }
            lastDis = newDis;
            modifyBoundary();
            invalidate();
        }
    }

    /**
     * calculate the distance of two finger touch
     *
     * @param event
     * @return
     */
    private float calculateFingerDis(MotionEvent event) {
        float disX = event.getX(0) - event.getX(1);
        float disY = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * calculate the center point of two finger touch
     *
     * @param event
     */
    private void calculateFingerCenter(MotionEvent event) {
        center[0] = (event.getX(0) + event.getX(1)) / 2;
        center[1] = (event.getY(0) + event.getY(1)) / 2;

        if (bmBoundary[2] > 0 && bmBoundary[3] < width) {
            center[0] = width / 2;
        }
        if (bmBoundary[0] > 0 && bmBoundary[1] < height) {
            center[1] = height / 2;
        }
    }

    public void setBitmap(final Bitmap bm) {
        if (width != 0 && height != 0) {
            bitmap = initBitmap(bm);
            matrix.preTranslate((width - bitmap.getWidth()) / 2, (height - bitmap.getHeight()) / 2);
            invalidate();
        } else {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (bitmap == null) {
                        if (width == 0 || height == 0) {
                            width = getWidth();
                            height = getHeight();
                            return;
                        }
                        bitmap = initBitmap(bm);
                        invalidate();
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    public void setSrcId(int mSrcId) {
        this.srcId = mSrcId;
        if (width != 0 && height != 0) {
            bitmap = initBitmap(BitmapFactory.decodeResource(getResources(), mSrcId));
            matrix.preTranslate((width - bitmap.getWidth()) / 2, (height - bitmap.getHeight()) / 2);
            invalidate();
        }
    }


    /**
     * judge a touch is twice touch
     *
     * @return
     */
    private boolean isTwiceTouch() {
        long now = System.currentTimeMillis();
        if (now - mLastTouchTime < 300) {
            return true;
        }
        mLastTouchTime = now;
        return false;
    }


    private boolean isDoTwiceTouch(MotionEvent event) {
        if (isTwiceTouch()) {
            if (mTwiceZoomType == 1) {
                zoomUp(event);
            } else if (mTwiceZoomType == 2) {
                zoomToMin((int) (totalRate * 100));
            }
            invalidate();
            return true;
        }
        return false;
    }

    private long mLastTouchTime;
    private int mTwiceZoomType = 1;
    private Scroller mZoomScroller;
    private Scroller mTranScroller;

    private int mTranX = 0;
    private int mTranY = 0;
    private float mScale = 0.0f;

    @Override
    public void computeScroll() {
        if (mZoomScroller.computeScrollOffset()) {
            mScale = mZoomScroller.getCurrX() * 1.0f / 100;
            zoomRate = mScale / zoomRate;
            totalRate = totalRate * zoomRate;
            modifyBoundary();
            zoomRate = mScale;
            invalidate();
        }
        if (mTranScroller.computeScrollOffset()) {
            int curX = mTranScroller.getCurrX();
            int curY = mTranScroller.getCurrY();
            matrix.postTranslate(curX - mTranX, curY - mTranY);
            bmBoundary[2] = bmBoundary[2] + curX - mTranX;
            bmBoundary[3] = bmBoundary[3] + curX - mTranX;
            bmBoundary[0] = bmBoundary[0] + curY - mTranY;
            bmBoundary[1] = bmBoundary[1] + curY - mTranY;
            center[0] = (bmBoundary[2] + bmBoundary[3]) / 2;
            center[1] = (bmBoundary[0] + bmBoundary[1]) / 2;
            mTranX = curX;
            mTranY = curY;
            invalidate();
        }
    }


    private void zoomToMax(int zoom) {
        mTwiceZoomType = 2;
        zoomRate = totalRate;
        mZoomScroller.startScroll(zoom, 0, (int) -(zoom - zoomMax * 100), 0, 300);
        invalidate();
    }

    private void zoomToMin(int zoom) {
        center[0] = (bmBoundary[2] + bmBoundary[3]) / 2;
        center[1] = (bmBoundary[0] + bmBoundary[1]) / 2;
        mTwiceZoomType = 1;
        zoomRate = zoom / 100.0f;
        mZoomScroller.startScroll(zoom, 0, -(zoom - 100), 0, 300);
        int endX = 0;
        int endY = 0;
        mTranX = 0;
        mTranY = 0;
        endX = width / 2 - (int) (center[0]);
        endY = height / 2 - (int) (center[1]);
        mTranScroller.startScroll(0, 0, endX, endY, 300);
        invalidate();
    }

    private void zoomUp(MotionEvent event) {
        center[0] = event.getX();
        center[1] = event.getY();
        if (Threshold < bmBoundary[2] || bmBoundary[3] - width > Threshold) {
            center[0] = width / 2;
        }
        if (Threshold < bmBoundary[0] || bmBoundary[1] - height > Threshold) {
            center[1] = height / 2;
        }
        zoomRate = 1.0f;
        mTwiceZoomType = 2;
        mZoomScroller.startScroll(100, 0, 100, 0, 300);
    }

    /**
     * modify the boundary of bitmap
     */
    private void modifyBoundary() {
        matrix.postScale(zoomRate, zoomRate, center[0], center[1]);
        bmBoundary[2] = center[0] + (bmBoundary[2] - center[0]) * zoomRate;
        bmBoundary[3] = center[0] + (bmBoundary[3] - center[0]) * zoomRate;
        bmBoundary[0] = center[1] + (bmBoundary[0] - center[1]) * zoomRate;
        bmBoundary[1] = center[1] + (bmBoundary[1] - center[1]) * zoomRate;
    }

    /**
     * init the bitmap according to the view width and height
     * return the bitmap whose height or width equals with view
     *
     * @param src
     * @return
     */
    private Bitmap initBitmap(Bitmap src) {
        if (src == null)
            return null;
        Bitmap dst = null;
        int w = src.getWidth();
        int h = src.getHeight();
        Matrix m = new Matrix();
        matrix = new Matrix();
        float coe = getScaleCoe(w, h);
        m.preScale(coe, coe);
        dst = Bitmap.createBitmap(src, 0, 0, w, h, m, true);
        bmBoundary[2] = (width - dst.getWidth()) / 2;
        bmBoundary[3] = width - (width - dst.getWidth()) / 2;
        bmBoundary[0] = (height - dst.getHeight()) / 2;
        bmBoundary[1] = height - (height - dst.getHeight()) / 2;
        return dst;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - lastX;
                getParent().requestDisallowInterceptTouchEvent(true);
                if (event.getPointerCount() == 1) {
                    if (bmBoundary[2] >= 0 && dx > 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
//                        ((ViewGroup)getParent()).onTouchEvent(event);
                    } else if (bmBoundary[3] <= width && dx < 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
//                        ((ViewGroup)getParent()).onTouchEvent(event);
                    } else if (bmBoundary[2] > 0 && bmBoundary[3] < width) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private float getScaleCoe(int w, int h) {
        float coeW = width * 1.0f / w;
        float coeH = height * 1.0f / h;
        float coe = Math.min(coeW, coeH);
        if (coe < 4f) {
            zoomMax = coe * 2;
            return coe;
        }
        zoomMax = coe * 1.5f;
        return 1;
    }
}

