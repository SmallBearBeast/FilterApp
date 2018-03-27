package com.example.jason.heartratedetection.ui.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.jason.heartratedetection.util.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/14.
 */

public class BaseAct extends AppCompatActivity {
    private static final String TAG = "BaseAct";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layoutId() != 0) {
            setContentView(layoutId());
            ButterKnife.bind(this);
            init(savedInstanceState);
        }
    }

    protected void init(Bundle bundle) {

    }

    protected int layoutId() {
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isSupportEventBus())
            EventBus.getDefault().register(this);
    }

    //是否开启eventbus,没有订阅注解开启会crash
    protected boolean isSupportEventBus() {
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isSupportEventBus())
            EventBus.getDefault().unregister(this);
    }

    protected void fixStatusHeight(View view) {
        int barHeight = StatusBarUtil.getStatusBarHeight(this);
        view.setPadding(0, barHeight, 0, 0);
    }

    protected void askPermission(String permission) {
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, permission))
            ActivityCompat.requestPermissions(this, new String[]{permission}, 0x11);
    }

    protected void askPermission(String[] permissions) {
        List<String> checkPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, permissions[i])) {
                checkPermissions.add(permissions[i]);
            }
        }
        if (!checkPermissions.isEmpty())
            ActivityCompat.requestPermissions(this, checkPermissions.toArray(new String[checkPermissions.size()]), 0x11);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            Log.e(TAG, "permissions[" + i + "] = " + permissions[i]);
            Log.e(TAG, "grantResults[" + i + "] = " + grantResults[i]);
        }
    }
}
