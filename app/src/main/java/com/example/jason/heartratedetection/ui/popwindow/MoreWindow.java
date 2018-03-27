package com.example.jason.heartratedetection.ui.popwindow;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.jason.heartratedetection.R;
import com.example.jason.heartratedetection.ui.activity.PhotoMsgAct;

import butterknife.OnClick;

/**
 * Created by Administrator on 2018/3/16.
 */

public class MoreWindow extends BaseWindow {
    public MoreWindow(Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    public int layoutId() {
        return R.layout.item_more;
    }

    @Override
    protected int animStyle() {
        return R.style.bottom_in_out;
    }

    @OnClick({R.id.ll_photo_detail})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ll_photo_detail:
                context.startActivity(new Intent(context, PhotoMsgAct.class));
                dismiss();
                break;
        }
    }
}
