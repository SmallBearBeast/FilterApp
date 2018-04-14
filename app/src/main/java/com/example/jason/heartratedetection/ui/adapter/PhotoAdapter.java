package com.example.jason.heartratedetection.ui.adapter;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.jason.heartratedetection.ui.widget.ZoomImageView;
import com.example.jason.heartratedetection.util.Constant;
import com.example.jason.heartratedetection.util.MyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/16.
 */

public class PhotoAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private int index = 0;
    private List<String> imagePaths = new ArrayList<>();
    private ViewPager viewPager;
    private int[] size = new int[2];


    public PhotoAdapter(List<String> path, ViewPager vp) {
        this.imagePaths = path;
        this.viewPager = vp;
        viewPager.addOnPageChangeListener(this);
        removeView = new ZoomImageView(viewPager.getContext());
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (size[0] == 0 && size[1] == 0) {
                    size[0] = viewPager.getWidth() / 2;
                    size[1] = viewPager.getHeight() / 2;
                    viewPager.setAdapter(PhotoAdapter.this);
                }
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
        Constant.SOLVE_IMAGE_PATH = imagePaths.get(position);
        index = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    ZoomImageView removeView;
    ZoomImageView cacheView;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZoomImageView ziv = null;
        if(MyUtil.isContainView(container, cacheView))
            ziv = new ZoomImageView(container.getContext());
        else if (cacheView == null)
            ziv = new ZoomImageView(container.getContext());
        else ziv = cacheView;
        run(ziv, position);
        container.addView(ziv);
        ziv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick();
                }
            }
        });
        return ziv;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        cacheView = (ZoomImageView) object;
    }

    public String getCurPhotoPath() {
        return imagePaths.get(index);
    }

    private void run(final ZoomImageView ziv, final int pos) {
        MyUtil.run(new Runnable() {
            @Override
            public void run() {
                final Bitmap bm = MyUtil.scaleBitmap(imagePaths.get(pos), size);
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        ziv.setBitmap(bm);
                    }
                });
            }
        });
    }

    public interface OnClickListener {
        void onClick();
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
