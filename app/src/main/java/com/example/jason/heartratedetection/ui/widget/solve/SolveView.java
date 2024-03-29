package com.example.jason.heartratedetection.ui.widget.solve;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jason.heartratedetection.FormatUtil;
import com.example.jason.heartratedetection.R;
import com.example.jason.heartratedetection.frame.event.SolveEvent;
import com.example.jason.heartratedetection.ui.widget.shape.ShapeHelper;
import com.example.jason.heartratedetection.ui.widget.shape.ShapeTextView;
import com.example.jason.heartratedetection.util.Constant;
import com.example.jason.heartratedetection.util.MyUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/3/17.
 */

public class SolveView extends LinearLayout {
    private View contentView;
    private Context context;
    private float threshold;
    @BindView(R.id.tv_case_title)
    TextView tvCaseTitle;
    @BindViews({R.id.stv_0, R.id.stv_25, R.id.stv_50, R.id.stv_75, R.id.stv_100})
    ShapeTextView[] stvs;

    public SolveView(Context context) {
        this(context, null);
    }

    public SolveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        contentView = LayoutInflater.from(context).inflate(R.layout.item_solve, this);
        ButterKnife.bind(this, contentView);
    }

    @OnClick({R.id.stv_cancel, R.id.stv_complete, R.id.fl_backlight, R.id.fl_src
            , R.id.stv_0, R.id.stv_25, R.id.stv_50, R.id.stv_75, R.id.stv_100})
    public void onClick(View view) {
        ShapeTextView stv = null;
        switch (view.getId()) {
            case R.id.stv_complete:
                EventBus.getDefault().post(new SolveEvent(Constant.SOLVE_COMPLETE, null));
                break;
            case R.id.stv_cancel:
                EventBus.getDefault().post(new SolveEvent(Constant.SOLVE_CANCEL, null));
                break;
            case R.id.fl_backlight:
                break;
            case R.id.fl_src:
                EventBus.getDefault().post(new SolveEvent(Constant.SOLVE_RESET, null));
                break;
            case R.id.stv_0:
                stv = (ShapeTextView) view;
                checkStv(stv);
                EventBus.getDefault().post(new SolveEvent(Constant.SOLVE_RESET, null));
                break;
            case R.id.stv_25:
            case R.id.stv_50:
            case R.id.stv_75:
            case R.id.stv_100:
                stv = (ShapeTextView) view;
                checkStv(stv);
                threshold = Float.valueOf(String.valueOf(stv.getText())) / 100 * 0.8f;
                EventBus.getDefault().post(new SolveEvent(Constant.SOLVE_START, null));
                MyUtil.run(new Runnable() {
                    @Override
                    public void run() {
                        Constant.SOLVE_BITMAP = Constant.SRC_BITMAP.copy(Constant.SRC_BITMAP.getConfig(), true);
                        FormatUtil.rgbToGray(Constant.SOLVE_BITMAP, threshold);
                        EventBus.getDefault().post(new SolveEvent(Constant.SOLVE_END, null));
                    }
                });
                break;
        }
    }

    private void checkStv(ShapeTextView stv) {
        resetStv();
        stv.getShapeHelper()
                .setSolid(ContextCompat.getColor(context, R.color.cl_gray_5))
                .setShape(ShapeHelper.OVAL)
                .complete();
        stv.setTextColor(ContextCompat.getColor(context, R.color.cl_white));
    }

    private void resetStv() {
        for (int i = 0; i < stvs.length; i++) {
            stvs[i].getShapeHelper()
                    .setSolid(ContextCompat.getColor(context, R.color.cl_white))
                    .complete();
            stvs[i].setTextColor(ContextCompat.getColor(context, R.color.cl_gray_5));
        }
    }

    public float getThreshold() {
        return threshold;
    }
}
