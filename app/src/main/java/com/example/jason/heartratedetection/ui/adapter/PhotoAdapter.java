package com.example.jason.heartratedetection.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.jason.heartratedetection.R;
import com.example.jason.heartratedetection.ui.activity.PhotoAct;
import com.example.jason.heartratedetection.ui.widget.ZoomImageView;
import com.example.jason.heartratedetection.util.MyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/16.
 */

public class PhotoAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private List<ZoomImageView> views = new ArrayList<>();
    private int index = 0;
    private int viewPos = 0;
    private List<String> imagePaths = new ArrayList<>();
    private ViewPager viewPager;
    private final int Length = 3;
    private int[] size = new int[2];


    public PhotoAdapter(List<String> path, ViewPager vp) {
        this.imagePaths = path;
        this.viewPager = vp;
        viewPager.addOnPageChangeListener(this);
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (size[0] == 0 && size[1] == 0) {
                    size[0] = viewPager.getWidth() / 2;
                    size[1] = viewPager.getHeight() / 2;
                    for (int i = 0; i < 3; i++) {
                        if (i < imagePaths.size()) {
//                        ZoomImageView ziv = (ZoomImageView) LayoutInflater.from(viewPager.getContext()).inflate(R.layout.item_photo, null);
                            ZoomImageView ziv = new ZoomImageView(viewPager.getContext());
                            ziv.setBitmap(MyUtil.scaleBitmap(imagePaths.get(i), size));
                            views.add(ziv);
                        }
                    }
                    viewPager.setAdapter(PhotoAdapter.this);
                }
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    boolean filter = false;

    @Override
    public void onPageSelected(int position) {
        if (filter) {
            filter = false;
            return;
        }

        if (viewPos < position) {
            index++;
            if (position == 2 && index < imagePaths.size() - 1) {
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ZoomImageView ziv = null;
                        for (int i = 0; i < views.size() - 1; i++) {
                            ziv = views.get(i);
                            ziv.setBitmap(MyUtil.scaleBitmap(imagePaths.get(index - 1 + i), size));
                        }
                        filter = true;
                        viewPager.setCurrentItem(1, false);
                        ziv = views.get(views.size() - 1);
                        ziv.setBitmap(MyUtil.scaleBitmap(imagePaths.get(index + 1), size));
                    }
                }, 500);
                viewPos = 1;
            } else
                viewPos = position;
        } else if (viewPos > position) {
            index--;
            if (position == 0 && index > 0) {
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ZoomImageView ziv = null;
                        for (int i = views.size() - 1; i > 0; i--) {
                            ziv = views.get(i);
                            ziv.setBitmap(MyUtil.scaleBitmap(imagePaths.get(index + i - 1), size));
                        }
                        filter = true;
                        viewPager.setCurrentItem(1, false);
                        ziv = views.get(0);
                        ziv.setBitmap(MyUtil.scaleBitmap(imagePaths.get(index - 1), size));
                    }
                }, 500);
                viewPos = 1;
            } else
                viewPos = position;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    public String getCurPhotoPath(){
        return imagePaths.get(index);
    }
}
