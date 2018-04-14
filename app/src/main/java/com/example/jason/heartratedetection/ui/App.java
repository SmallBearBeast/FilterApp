package com.example.jason.heartratedetection.ui;

import android.app.Application;
import android.os.Environment;

import com.example.jason.heartratedetection.util.Constant;

import java.io.File;

/**
 * Created by Administrator on 2018/3/9.
 */

public class App extends Application {
    private static App app;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initConstant();
    }

    private void initConstant() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Constant.SRC_IMAGE_DIR = getExternalCacheDir().getAbsolutePath() + File.separator + "SRC_IMAGE_DIR";
            Constant.SOLVE_IMAGE_DIR = getExternalCacheDir().getAbsolutePath() + File.separator + "SOLVE_IMAGE_DIR";
        }
    }

    public static App getApp(){
        return app;
    }
}
