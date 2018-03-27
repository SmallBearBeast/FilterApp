package com.example.jason.heartratedetection.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Administrator on 2018/1/29.
 */

public class PhotoCameraView extends SurfaceView implements SurfaceHolder.Callback {
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Context context;
    private int orientation = 0;
    private PhotoListener photoListener;

    public PhotoCameraView(Context context) {
        this(context, null);
    }

    public PhotoCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopCamera();
    }

    private void initCamera() {
        followScreenOrientation(context, camera);
        Camera.Parameters params = camera.getParameters();
        params.set("orientation", "portrait");
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        params.setSceneMode(Camera.Parameters.SCENE_MODE_BARCODE);
        camera.setParameters(params);
    }


    private void startCamera() {
        try {
            openCamera(orientation);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopCamera() {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    /**
     * open camera 0 is back camera, 1 is font camera
     *
     * @param cameraId
     * @return
     */
    private void openCamera(int cameraId) {
        try {
            if (camera != null)
                return;
            camera = Camera.open(cameraId);
            initCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takePicture() {
        camera.takePicture(photoListener, null, photoListener);
        camera.startPreview();
    }

    private void followScreenOrientation(Context context, Camera camera) {
        final int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
        }
    }

    public void reverseCamera() {
        try {
            stopCamera();
            openCamera((orientation = (orientation == 0 ? 1 : 0)));
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static abstract class PhotoListener implements Camera.PictureCallback, Camera.ShutterCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }

        @Override
        public void onShutter() {

        }
    }

    public void setPhotoListener(PhotoListener photoListener) {
        this.photoListener = photoListener;
    }


    public Camera getCamera() {
        return camera;
    }

    public Camera.Parameters getParametaers() {
        return camera.getParameters();
    }
}
