package com.example.jason.heartratedetection.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jason.heartratedetection.ui.App;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/3/13.
 */

public class MyUtil {
    public static ExecutorService executors = Executors.newCachedThreadPool();

    public static void run(Runnable run) {
        executors.execute(run);
    }

    public static void showToast(String text) {
        Toast.makeText(App.getApp(), text, Toast.LENGTH_SHORT).show();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getChildPath(String dir) {
        File file = new File(dir);
        File[] files = file.listFiles();
        List<String> list = new ArrayList<>();
        if (files != null && files.length != 0) {
            for (int i = 0; i < files.length; i++) {
                list.add(files[i].getAbsolutePath());
            }
            String[] T = new String[list.size()];
            Arrays.sort(list.toArray(T), new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o2.compareTo(o1);
                }
            });
            list.clear();
            for (int i = 0; i < T.length; i++) {
                list.add(T[i]);
            }
        }
        return list;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return deleteFile(file);
    }

    public static boolean deleteFile(File file) {
        if (file != null)
            return file.delete();
        return false;
    }

    public static Bitmap scaleBitmap(Bitmap bm, ImageView iv) {
        int bmW = bm.getWidth();
        int bmH = bm.getHeight();
        int ivW = iv.getWidth();
        int ivH = iv.getHeight();
        float scale = Math.max(ivW * 1f / bmW, ivH * 1f / bmH);
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap scaleBm = Bitmap.createBitmap(bm, 0, 0, bmW, bmH, matrix, false);
        return scaleBm;
    }

    public static Bitmap scaleBitmap(String path, int[] size) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, op);
        float scale = Math.max(op.outWidth * 1f / size[0], op.outHeight * 1f / size[1]);
        op.inSampleSize = (int) (scale + 1);
        op.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, op);
    }

    public static boolean saveBitmap(String path, String bmName, byte[] bytes) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return false;
        File dir = new File(path);
        FileOutputStream fos = null;
        if (!dir.exists())
            dir.mkdirs();
        try {
            fos = new FileOutputStream(dir.getAbsolutePath() + File.separator + bmName);
            fos.write(bytes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean saveBitmap(String path, String bmName, Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saveBitmap(path, bmName, bytes);
    }

    public static String createTimeFileName(String suffix) {
        return System.currentTimeMillis() + "." + suffix;
    }

    public static Bitmap scaleBitmap(String path, ImageView iv) {
        return scaleBitmap(path, new int[]{iv.getWidth(), iv.getHeight()});
    }
}
