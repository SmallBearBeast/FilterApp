package com.example.jason.heartratedetection.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jason.heartratedetection.R;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/3/25.
 */

public class WelcomeAct extends BaseAct {
    @BindView(R.id.iv_welcome)
    ImageView ivWelcome;

    int time = 3;
    ObjectAnimator oa;
    ObjectAnimator ob;

    @Override
    protected void init(Bundle bundle) {
        super.init(bundle);
        askPermission(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        });
    }

    @Override
    protected int layoutId() {
        return R.layout.act_welcome;
    }

    @Override
    protected void permissionOk(boolean ok) {
        if(!ok){
            oa = ObjectAnimator.ofFloat(ivWelcome, "scaleX", 1f, 1.2f);
            ob = ObjectAnimator.ofFloat(ivWelcome, "scaleY", 1f, 1.2f);
            oa.setDuration(3000);
            ob.setDuration(3000);
            oa.start();
            ob.start();
        }
        else {
            if(oa == null) {
                oa = ObjectAnimator.ofFloat(ivWelcome, "scaleX", 1f, 1.2f);
                ob = ObjectAnimator.ofFloat(ivWelcome, "scaleY", 1f, 1.2f);
                oa.setDuration(1500);
                ob.setDuration(1500);
                oa.start();
                ob.start();
                ivWelcome.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(WelcomeAct.this, CameraAct.class));
                    }
                }, 1500);
            }
            else {
                finish();
                startActivity(new Intent(this, CameraAct.class));
            }
        }
    }
}

