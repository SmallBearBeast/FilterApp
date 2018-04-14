package com.example.jason.heartratedetection.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jason.heartratedetection.R;
import com.example.jason.heartratedetection.frame.event.DeleteEvent;
import com.example.jason.heartratedetection.ui.adapter.PhotoAdapter;
import com.example.jason.heartratedetection.ui.popwindow.LoadingWindow;
import com.example.jason.heartratedetection.ui.popwindow.IsDeleteWindow;
import com.example.jason.heartratedetection.ui.popwindow.MoreWindow;
import com.example.jason.heartratedetection.ui.widget.shape.ShapeImageView;
import com.example.jason.heartratedetection.util.Constant;
import com.example.jason.heartratedetection.util.DensityUtil;
import com.example.jason.heartratedetection.util.MyUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/3/12.
 */

public class PhotoAct extends BaseAct {
    @BindView(R.id.tv_date_1)
    TextView tvDate_1;
    @BindView(R.id.tv_date_2)
    TextView tvDate_2;
    @BindView(R.id.siv_filter)
    ShapeImageView sivFilter;
    @BindView(R.id.siv_delete)
    ShapeImageView sivDelete;
    @BindView(R.id.siv_more)
    ShapeImageView sivMore;
    @BindView(R.id.vp_photo_container)
    ViewPager vpPhotoContainer;
    @BindView(R.id.fl_date)
    FrameLayout flDate;
    @BindView(R.id.ll_solve)
    LinearLayout llSolve;
    IsDeleteWindow deleteWindow;
    MoreWindow moreWindow;
    LoadingWindow loadingWindow;
    ObjectAnimator topOa;
    ObjectAnimator bottomOa;
    boolean isShowOa = true;
    List<String> imagePaths = new ArrayList<>();
    PhotoAdapter photoAdapter;

    @Override
    protected void init(Bundle bundle) {
        super.init(bundle);
        deleteWindow = new IsDeleteWindow(this, DensityUtil.getScreenWidth(this), DensityUtil.dip2Px(this, 120));
        moreWindow = new MoreWindow(this, DensityUtil.getScreenWidth(this), DensityUtil.dip2Px(this, 200));
        loadingWindow = new LoadingWindow(this, DensityUtil.getScreenWidth(this), DensityUtil.getScreenHeight(this));
        imagePaths = MyUtil.getChildPath(Constant.SRC_IMAGE_DIR);
        Constant.SOLVE_IMAGE_PATH = imagePaths.get(0);
        photoAdapter = new PhotoAdapter(imagePaths, vpPhotoContainer);
        photoAdapter.setOnClickListener(new PhotoAdapter.OnClickListener() {
            @Override
            public void onClick() {
                anim();
            }
        });
    }

    @Override
    protected int layoutId() {
        return R.layout.act_photo;
    }

    @OnClick({R.id.siv_filter, R.id.siv_delete, R.id.siv_more, R.id.siv_back
            , R.id.vp_photo_container, R.id.rl_container})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.siv_filter:
                startActivity(new Intent(this, SolvePhotoAct.class));
                break;
            case R.id.siv_delete:
                deleteWindow.showAsDropDown(llSolve, 0, 0, Gravity.TOP);
                break;
            case R.id.siv_more:
                moreWindow.showAsDropDown(llSolve, 0, 0, Gravity.TOP);
                break;

            case R.id.vp_photo_container:
            case R.id.rl_container:
                anim();
                break;
            case R.id.siv_back:
                finish();
                break;
        }
    }

    private void anim() {
        if(topOa != null && topOa.isRunning())
            return;
        if (!isShowOa) {
            topOa = ObjectAnimator.ofFloat(flDate, "translationY", -flDate.getHeight(), 0);
            bottomOa = ObjectAnimator.ofFloat(llSolve, "translationY", llSolve.getHeight(), 0);
        } else {
            topOa = ObjectAnimator.ofFloat(flDate, "translationY", 0, -flDate.getHeight());
            bottomOa = ObjectAnimator.ofFloat(llSolve, "translationY", 0, llSolve.getHeight());
        }
        topOa.setDuration(300);
        bottomOa.setDuration(300);
        topOa.start();
        bottomOa.start();
        topOa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isShowOa = !isShowOa;
                if (isShowOa)
                    vpPhotoContainer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isShowOa)
                                anim();
                        }
                    }, 1500);
            }
        });
    }

    @Override
    protected boolean isSupportEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeleteEvent event) {
        if (event.getState() == Constant.DELETE_START)
            loadingWindow.showAtLocation(vpPhotoContainer, Gravity.CENTER, 0, 0);
        else if (event.getState() == Constant.DELETE_END) {
            imagePaths.remove(Constant.SOLVE_IMAGE_PATH);
            loadingWindow.dismiss();
            photoAdapter.notifyDataSetChanged();
        }
    }
}
