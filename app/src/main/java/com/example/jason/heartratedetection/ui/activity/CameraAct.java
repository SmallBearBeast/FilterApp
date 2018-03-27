package com.example.jason.heartratedetection.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.jason.heartratedetection.R;
import com.example.jason.heartratedetection.ui.widget.CameraPhotoView;
import com.example.jason.heartratedetection.ui.widget.PhotoCameraView;
import com.example.jason.heartratedetection.util.Constant;
import com.example.jason.heartratedetection.util.MyUtil;

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
        cpvCamera.setOnCameraListener(new CameraPhotoView.OnCameraListener() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                CameraAct.this.bitmap = MyUtil.scaleBitmap(bitmap, ivPhoto);
                Log.e(TAG, "Thread.currentThread() = " + Thread.currentThread());
                ivPhoto.post(new Runnable() {
                    @Override
                    public void run() {
                        ivPhoto.setImageBitmap(CameraAct.this.bitmap);
                    }
                });
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
