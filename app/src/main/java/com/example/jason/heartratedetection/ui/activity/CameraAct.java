package com.example.jason.heartratedetection.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CaptureRequest;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.jason.heartratedetection.R;
import com.example.jason.heartratedetection.ui.widget.CameraPhotoView;
import com.example.jason.heartratedetection.ui.widget.PhotoCameraView;
import com.example.jason.heartratedetection.util.Constant;
import com.example.jason.heartratedetection.util.MyUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/3/9.
 */

public class CameraAct extends BaseAct {
    private static final String TAG = "CameraAct";
    @BindView(R.id.cpv_camera)
    CameraPhotoView cpvCamera;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    private Bitmap bitmap;

    @Override
    protected int layoutId() {
        return R.layout.act_camera;
    }

    @Override
    protected void init(Bundle bundle) {
        super.init(bundle);
        ivPhoto.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                List<String> pathes = MyUtil.getChildPath(Constant.SRC_IMAGE_DIR);
                String firstPath = null;
                if(pathes.size() != 0)
                    firstPath = pathes.get(0);
                ivPhoto.setImageBitmap(MyUtil.scaleBitmap(firstPath, ivPhoto));
            }
        });
        cpvCamera.setOnCameraListener(new CameraPhotoView.OnCameraListener() {
            @Override
            public void onBitmap(final byte[] bytes) {
                MyUtil.run(new Runnable() {
                    @Override
                    public void run() {
                        String name = MyUtil.createTimeFileName("jpg");
                        MyUtil.saveBitmap(Constant.SRC_IMAGE_DIR, name, bytes);
                        try {
                            ExifInterface exif = new ExifInterface(Constant.SRC_IMAGE_DIR + File.separator + name);
                            String value = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                            exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
                            exif.saveAttributes();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bitmap = MyUtil.scaleBitmap(Constant.SRC_IMAGE_DIR + File.separator + name, ivPhoto);
                        ivPhoto.post(new Runnable() {
                            @Override
                            public void run() {
                                ivPhoto.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
                // TODO: 2018/4/2 下面这句代码有毒，一调用就会放大SurfaceView 
                //ivPhoto.setImageBitmap(CameraAct.this.bitmap);
            }
        });
    }

    @OnClick({R.id.iv_take_photo, R.id.iv_reverse_camera, R.id.iv_flash
            , R.id.iv_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_take_photo:
                cpvCamera.takePicture();
                break;

            case R.id.iv_reverse_camera:
                cpvCamera.reverseCamera();
                break;

            case R.id.iv_flash:
                cpvCamera.changeFlash(CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
                break;

            case R.id.iv_photo:
                if (!MyUtil.getChildPath(Constant.SRC_IMAGE_DIR).isEmpty())
                    startActivity(new Intent(this, PhotoAct.class));
                break;
        }
    }
}
