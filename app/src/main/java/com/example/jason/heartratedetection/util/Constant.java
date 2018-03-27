package com.example.jason.heartratedetection.util;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/12/11.
 */

public class Constant {

    public static String SOLVE_IMAGE_PATH = null;
    public static String SRC_IMAGE_DIR = null;
    public static String SOLVE_IMAGE_DIR = null;
    //Event
    public static final int DELETE_START = 0x11;
    public static final int DELETE_END = 0x22;
    public static final int SOLVE_START = 0x33;
    public static final int SOLVE_END = 0x44;
    public static final int SOLVE_COMPLETE = 0x55;
    public static final int SOLVE_RESET = 0x66;
    public static final int SOLVE_CANCEL = 0x77;

    //Bitmap
    public static Bitmap SRC_BITMAP;
    public static Bitmap SOLVE_BITMAP;

    public static void clearBitmap(){
        if(SRC_BITMAP != null) {
            SRC_BITMAP.recycle();
            SRC_BITMAP = null;

        }
        if(SOLVE_BITMAP != null){
            SOLVE_BITMAP.recycle();
            SOLVE_BITMAP = null;
        }
    }
}
