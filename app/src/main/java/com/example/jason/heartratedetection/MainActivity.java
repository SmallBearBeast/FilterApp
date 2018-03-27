package com.example.jason.heartratedetection;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ImageView ivSrc;

    private ImageView ivDest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivSrc = findViewById(R.id.iv_src);
        ivDest = findViewById(R.id.iv_dest);

        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inSampleSize = 5;
        op.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/pic.jpg", op);
        ivSrc.setImageBitmap(bitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pix = new int[width * height];
        bitmap.getPixels(pix, 0, bitmap.getWidth(), 0, 0 , width, height);
        int[] resultPixes =  FormatUtil.enhanceBrightness(pix, width, height);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(resultPixes, 0, width, 0, 0, width, height);
        ivDest.setImageBitmap(bitmap);
    }
}
