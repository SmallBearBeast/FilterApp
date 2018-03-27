package com.example.jason.heartratedetection.ui.adapter;

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

public class PhotoAdapter1 extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private List<ZoomImageView> views = new ArrayList<>();
    private int index = 0;
    private List<String> imagePaths = new ArrayList<>();
    private ViewPager viewPager;
    private final int Length = 3;
    private int[] size = new int[2];

    public PhotoAdapter1(List<String> path, ViewPager vp) {
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
                    for (int i = 0; i < Length; i++) {
                        if (i < imagePaths.size()) {
                            ZoomImageView ziv = new ZoomImageView(viewPager.getContext());
                            ziv.setBitmap(MyUtil.scaleBitmap(imagePaths.get(i), size));
                            views.add(ziv);
                        }
                    }
                    viewPager.setAdapter(PhotoAdapter1.this);
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
        if (index == position + 1) {
            if (index == imagePaths.size() - 2) {
                cacheView = views.get(0);
            } else {
                cacheView = removeView;
                views.add(0, cacheView);
                cacheView.setBitmap(MyUtil.scaleBitmap(imagePaths.get(index + 1), size));
            }
        } else if (index == position - 1) {
            if (position <= 2)
                cacheView = views.get(position);
            else
                cacheView.setBitmap(MyUtil.scaleBitmap(imagePaths.get(index + 1), size));
        } else if (position == 0) {
            cacheView = views.get(0);
        }
        container.addView(cacheView);
        return cacheView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (index == position + 2) {
            cacheView = views.get(0);
            container.removeView(cacheView);
            if (index == imagePaths.size() - 1) {
                removeView = new ZoomImageView(viewPager.getContext());
            } else {
                views.remove(0);
                views.add(cacheView);
            }
        } else if (index == position - 2) {
            if (index == 0) {
                container.removeView(views.get(2));
                removeView = new ZoomImageView(viewPager.getContext());
            } else {
                container.removeView(views.get(3));
                removeView = views.remove(3);
            }
        }
    }

    public String getCurPhotoPath() {
        return imagePaths.get(index);
    }
}
