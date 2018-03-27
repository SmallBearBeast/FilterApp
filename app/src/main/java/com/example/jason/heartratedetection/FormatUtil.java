package com.example.jason.heartratedetection;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * welcome layout Activity
 * Created by VideoMedicine Group on 2017/9/3.
 * 设置APP video存储，进行心率计算
 *
 * @author GqGAO
 */

public class FormatUtil {
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    public static native int[] stringFromJNI(int[] image, int w, int h);

    public static native int[] enhanceBrightness(int[] image, int w, int h);

    public static native void enhanceBrightness_2(String uri, int rows, int cols);

    public static native void rgbToGray(Bitmap bitmap, float threshold);
}
