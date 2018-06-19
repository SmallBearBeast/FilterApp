package com.example.jason.heartratedetection.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
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
import android.util.SparseIntArray;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.jason.heartratedetection.util.Constant;
import com.example.jason.heartratedetection.util.MyUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private int sensorOrientation = 0;
    private int displayRotation = 0;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraPhotoView(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint({"MissingPermission", "WrongConstant"})
    private void openCamera() {
        try {
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size radioSize = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea()
            );
            displayRotation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
            sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            boolean swapped = false;
            switch (displayRotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    if (sensorOrientation == 90 || sensorOrientation == 270) {
                        swapped = true;
                    }
                    break;
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    if (sensorOrientation == 0 || sensorOrientation == 180) {
                        swapped = true;
                    }
                    break;
                default:
                    Log.e(TAG, "Display rotation is invalid: " + displayRotation);
            }
            Size maxSize = new Size(width, height);
            if(swapped)
                maxSize = new Size(height, width);
            previewSize = getProperPreSize(characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(SurfaceHolder.class), maxSize, radioSize);
            surfaceHolder.setFixedSize(radioSize.getWidth() , radioSize.getHeight());
            if(swapped)
                setAspectRatio(previewSize.getHeight() , previewSize.getWidth());
            else
                setAspectRatio(previewSize.getWidth() , previewSize.getHeight());
            cameraManager.openCamera("" + cameraId, deviceStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void rePreview() {
        try {
            captureSession.setRepeatingRequest(previewBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePreview() {
        try {
            imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(imageAvailableListener, null);
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            surfaceHolder.setFixedSize(previewSize.getWidth() , previewSize.getHeight());
            previewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            previewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            previewBuilder.addTarget(surfaceHolder.getSurface());
            cameraDevice.createCaptureSession(
                    Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()),
                    sessionStateCallback, null);
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
            takePicBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
            if(cameraId == 0)
                takePicBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);
            else
                takePicBuilder.set(CaptureRequest.JPEG_ORIENTATION, 270);
            takePicBuilder.addTarget(imageReader.getSurface());
            captureSession.capture(takePicBuilder.build(), null, null);
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
            if(onCameraListener != null){
                onCameraListener.onBitmap(bytes);
            }
            image.close();
            rePreview();
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
            MyUtil.showToast("无法启动相机");
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                captureSession = session;
                captureSession.setRepeatingRequest(previewBuilder.build(), null, null);
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


    private Size getProperPreSize(Size[] sizes, Size maxSize, Size ratioSize) {
        int w = ratioSize.getWidth();
        int h = ratioSize.getHeight();
        List<Size> result = new ArrayList<>();
        for (Size size : sizes) {
            if (size.getWidth() <= maxSize.getWidth() && size.getHeight() <= maxSize.getHeight() &&
                    size.getHeight() == size.getWidth() * h / w) {
                result.add(size);
            }
        }
        if (result.size() > 0) {
            return Collections.max(result, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return sizes[0];
        }
    }

    private int ratioWidth = 0;
    private int ratioHeight = 0;

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        ratioWidth = width;
        ratioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == ratioWidth || 0 == ratioHeight) {
            setMeasuredDimension(width, height);
        } else {
            float scale = Math.min(width * 1f / ratioWidth, height * 1f / ratioHeight);
            setMeasuredDimension((int) (ratioWidth * scale), (int) (ratioHeight * scale));
        }
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

    private int getOrientation(int rotation) {
        return (ORIENTATIONS.get(rotation) + sensorOrientation + 270) % 360;
    }

    public static interface OnCameraListener{
        void onBitmap(byte[] bytes);
    }

    private OnCameraListener onCameraListener;

    public void setOnCameraListener(OnCameraListener onCameraListener) {
        this.onCameraListener = onCameraListener;
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }
}

