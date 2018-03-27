package com.example.jason.heartratedetection.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.jason.heartratedetection.util.Constant;
import com.example.jason.heartratedetection.util.MyUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2018/3/9.
 */

public class CameraPhotoView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPhotoView";
    private ImageReader imageReader;
    private Handler backHandler;
    private HandlerThread handlerThread;
    private CameraDevice cameraDevice;
    private SurfaceHolder surfaceHolder;
    private CaptureRequest.Builder previewBuilder;
    private CaptureRequest.Builder takePicBuilder;
    private CameraCaptureSession captureSession;
    private CameraCharacteristics characteristics;
    private CameraManager cameraManager;
    private Context context;
    private Size previewSize;

    private int width = 0;
    private int height = 0;
    private int cameraId = 0;
    private int flashState = 0;
    private String imageDir = "SRC_IMAGE_DIR";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraPhotoView(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        imageDir = context.getExternalCacheDir().getAbsolutePath() + File.separator + imageDir;
        initMemory();
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initMemory() {
        handlerThread = new HandlerThread("CameraPhotoView");
        handlerThread.start();
        backHandler = new Handler(handlerThread.getLooper());
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setKeepScreenOn(true);
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            // 获取指定摄像头的特性
            characteristics = cameraManager.getCameraCharacteristics(cameraId + "");
            previewSize = Collections.max(Arrays.asList(characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG)), new Comparator<Size>() {
                @Override
                public int compare(Size o1, Size o2) {
                    return Long.signum((long) o1.getWidth() * o1.getHeight() -
                            (long) o2.getWidth() * o2.getHeight());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
            Log.e(TAG, "openCamera: " + Thread.currentThread());
            cameraManager.openCamera("" + cameraId, deviceStateCallback, backHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePreview() {
        try {
            imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(imageAvailableListener, backHandler);
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            previewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            previewBuilder.addTarget(surfaceHolder.getSurface());
            cameraDevice.createCaptureSession(
                    Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()),
                    sessionStateCallback, backHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void takePicture() {
        if (cameraDevice == null) return;
        try {
            takePicBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            takePicBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            takePicBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            takePicBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);
            takePicBuilder.addTarget(imageReader.getSurface());
            captureSession.capture(takePicBuilder.build(), null, backHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        String s = null;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopCamera();
    }

    private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);//由缓冲区存入字节数组
            MyUtil.saveBitmap(imageDir, MyUtil.createTimeFileName("jpg"), bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if(onCameraListener != null){
                onCameraListener.onBitmap(bitmap);
            }
            reader.close();
            takePreview();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            takePreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            stopCamera();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            stopCamera();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                captureSession = session;
                captureSession.setRepeatingRequest(previewBuilder.build(), null, backHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    public void reverseCamera() {
        cameraId = (cameraId == 0 ? 1 : 0);
        stopCamera();
        openCamera();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (height != h)
            height = h;
        if (width != w)
            width = w;
        if (width == w && height == h) {
            surfaceHolder.setFixedSize(previewSize.getWidth(), previewSize.getHeight());
            setMeasuredDimension(previewSize.getWidth(), previewSize.getHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void stopCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
    }

    public void changeFlash(int flashState) {
        try {
            takePicBuilder.set(CaptureRequest.CONTROL_AE_MODE, flashState);
            previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, flashState);
            captureSession.setRepeatingRequest(previewBuilder.build(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static interface OnCameraListener{
        void onBitmap(Bitmap bitmap);
    }

    private OnCameraListener onCameraListener;

    public void setOnCameraListener(OnCameraListener onCameraListener) {
        this.onCameraListener = onCameraListener;
    }
}

