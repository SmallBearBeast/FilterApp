package com.example.jason.heartratedetection.ui.activity;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.jason.heartratedetection.FormatUtil;
import com.example.jason.heartratedetection.R;
import com.example.jason.heartratedetection.frame.event.SolveEvent;
import com.example.jason.heartratedetection.ui.popwindow.LoadingWindow;
import com.example.jason.heartratedetection.ui.widget.shape.ShapeTextView;
import com.example.jason.heartratedetection.ui.widget.solve.FilterView;
import com.example.jason.heartratedetection.ui.widget.solve.OnSolveListener;
import com.example.jason.heartratedetection.ui.widget.solve.SolveView;
import com.example.jason.heartratedetection.util.Constant;
import com.example.jason.heartratedetection.util.DensityUtil;
import com.example.jason.heartratedetection.util.MyUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/3/12.
 */

public class SolvePhotoAct extends BaseAct {
    @BindView(R.id.stv_save)
    ShapeTextView stvSave;
    @BindView(R.id.fl_solve_case)
    FrameLayout flSolveContainer;
    @BindView(R.id.ll_solve_case)
    LinearLayout llSolveContainer;
    @BindView(R.id.iv_solve_photo)
    ImageView ivSolvePhoto;
    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    SolveView solveView;
    View curSolveView;
    LoadingWindow loadingWindow;

    @Override
    protected void init(Bundle bundle) {
        super.init(bundle);
        initSolveView();
        loadingWindow = new LoadingWindow(this, DensityUtil.getScreenWidth(this)
                , DensityUtil.getScreenHeight(this));
        loadingWindow.setText("正在保存...");
        ivSolvePhoto.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Constant.SRC_BITMAP == null) {
                    Constant.SRC_BITMAP = MyUtil.scaleBitmap(Constant.SOLVE_IMAGE_PATH, ivSolvePhoto);
                    Constant.SOLVE_BITMAP = Constant.SRC_BITMAP.copy(Constant.SRC_BITMAP.getConfig(), true);
                    ivSolvePhoto.setImageBitmap(Constant.SRC_BITMAP);
                }
            }
        });
    }

    private void initSolveView() {
        solveView = new SolveView(this);
    }

    @Override
    protected int layoutId() {
        return R.layout.act_solve;
    }

    @OnClick({R.id.filter, R.id.stv_save, R.id.stv_cancel, R.id.siv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter:
                curSolveView = solveView;
                llSolveContainer.setVisibility(View.GONE);
                flSolveContainer.addView(solveView);
                break;

            case R.id.stv_save:
                loadingWindow.setText("正在保存...");
                loadingWindow.showAtLocation(ivSolvePhoto, Gravity.CENTER, 0, 0);
                MyUtil.run(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.sleep(3000);
//                        Bitmap bm = BitmapFactory.decodeFile(Constant.SOLVE_IMAGE_PATH);
//                        FormatUtil.rgbToGray(bm, solveView.getThreshold());
//                        MyUtil.saveBitmap(Constant.SOLVE_IMAGE_DIR, MyUtil.createTimeFileName("jpg"), bm);
                        loadingWindow.dismiss();
                        finish();
                    }
                });
                break;

            case R.id.stv_cancel:
            case R.id.siv_back:
                Constant.clearBitmap();
                finish();
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SolveEvent event) {
        switch (event.getState()) {
            case Constant.SOLVE_START:
                solveStart();
                break;
            case Constant.SOLVE_END:
                solveEnd();
                break;
            case Constant.SOLVE_COMPLETE:
                complete();
                break;
            case Constant.SOLVE_CANCEL:
                cancel();
                break;
            case Constant.SOLVE_RESET:
                reset();
                break;
        }
    }

    public void complete() {
        Constant.SRC_BITMAP = Constant.SOLVE_BITMAP.copy(Constant.SOLVE_BITMAP.getConfig(), true);
        ivSolvePhoto.setImageBitmap(Constant.SRC_BITMAP);
        llSolveContainer.setVisibility(View.VISIBLE);
        flSolveContainer.removeView(curSolveView);
    }

    public void cancel() {
        ivSolvePhoto.setImageBitmap(Constant.SRC_BITMAP);
        llSolveContainer.setVisibility(View.VISIBLE);
        flSolveContainer.removeView(curSolveView);
    }

    public void solveStart() {
        loadingWindow.setText("正在处理中 ...");
        loadingWindow.showAtLocation(ivSolvePhoto, Gravity.CENTER, 0, 0);
    }

    public void solveEnd() {
        ivSolvePhoto.setImageBitmap(Constant.SOLVE_BITMAP);
        loadingWindow.dismiss();
    }

    public void reset() {
        ivSolvePhoto.setImageBitmap(Constant.SRC_BITMAP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.clearBitmap();
    }

    @Override
    protected boolean isSupportEventBus() {
        return true;
    }
}
