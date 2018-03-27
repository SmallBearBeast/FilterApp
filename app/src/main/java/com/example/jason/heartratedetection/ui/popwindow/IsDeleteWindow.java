package com.example.jason.heartratedetection.ui.popwindow;

import android.content.Context;
import android.view.View;

import com.example.jason.heartratedetection.R;
import com.example.jason.heartratedetection.frame.event.DeleteEvent;
import com.example.jason.heartratedetection.util.Constant;
import com.example.jason.heartratedetection.util.MyUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.OnClick;

/**
 * Created by Administrator on 2018/3/16.
 */

public class IsDeleteWindow extends BaseWindow {
    public IsDeleteWindow(Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    public int layoutId() {
        return R.layout.item_is_delete;
    }

    @Override
    protected int animStyle() {
        return R.style.bottom_in_out;
    }

    @OnClick({R.id.tv_comfirm, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_comfirm:
                dismiss();
                EventBus.getDefault().post(new DeleteEvent(Constant.DELETE_START, null));
                if (Constant.SOLVE_IMAGE_PATH != null) {
                    MyUtil.run(new Runnable() {
                        @Override
                        public void run() {
//                            MyUtil.deleteFile(Constant.SOLVE_IMAGE_PATH);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            EventBus.getDefault().post(new DeleteEvent(Constant.DELETE_END, null));
                        }
                    });
                }
                break;

            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
