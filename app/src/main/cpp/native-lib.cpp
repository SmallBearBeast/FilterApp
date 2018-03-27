/*
 * Brief:The CPP file is the main document to realize the heart rate detection,
 * and the introduction of CPP code through the JNI interface is the core of
 * the APP's algorithm function.
 * Author:Jason.Lee
 * Date:2017-10-03
 * CopyRight：Computer vision laboratory of HeFei university of technology.
 * */

#include <jni.h>
#include <GuideFilter.h>
#include <android/bitmap.h>

#define MAKE_RGBA(r, g, b, a) (((a) << 24) | ((r) << 16) | ((g) << 8) | (b))
#define RGBA_A(p) (((p) & 0xFF000000) >> 24)
#define RGBA_R(p) (((p) & 0x00FF0000) >> 16)
#define RGBA_G(p) (((p) & 0x0000FF00) >>  8)
#define RGBA_B(p)  ((p) & 0x000000FF)
using namespace cv;
extern "C"
JNIEXPORT jintArray  JNICALL
Java_com_example_jason_heartratedetection_FormatUtil_stringFromJNI(
        JNIEnv *env, jclass obj, jintArray buf, int w, int h) {
    jint *cbuf;
    cbuf = env->GetIntArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL) {
        return 0;
    }

    Mat src(h, w, CV_8UC4, (unsigned char *) cbuf);
    cvtColor(src, src, CV_BGRA2RGB);

    Mat output = EnhanceBrightness(src, 0.5f);
    output.convertTo(output, CV_8UC3, 255);

    int r = 0, g = 0, b = 0;
    for (int i = 0; i < output.rows; i++) {
        for (int j = 0; j < output.cols; j++) {
            r = output.at<Vec3b>(i, j)[0];
            g = output.at<Vec3b>(i, j)[1];
            b = output.at<Vec3b>(i, j)[2];
            cbuf[i * output.cols + j] = MAKE_RGBA(r, g, b, 255);
        }
    }
    int size = w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, cbuf);
    env->ReleaseIntArrayElements(buf, cbuf, 0);
    return result;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_jason_heartratedetection_FormatUtil_enhanceBrightness(JNIEnv *env, jclass type,
                                                                       jintArray image_, jint w,
                                                                       jint h) {
    jint *image = env->GetIntArrayElements(image_, NULL);
    Mat imgData(h, w, CV_8UC3, (unsigned char *) image);
    Mat output = EnhanceBrightness(imgData, 0.5f);
    int size = output.cols * output.rows;
    jintArray result = env->NewIntArray(size);
    output.convertTo(output, CV_8UC3, 255);
    int g = 0;
    int b = 0;
    int r = 0;
    int outInt[size];
    for (int i = 0; i < output.rows; i++) {
        for (int j = 0; j < output.cols; j++) {
            b = output.at<Vec3b>(i, j)[0];
            g = output.at<Vec3b>(i, j)[1];
            r = output.at<Vec3b>(i, j)[2];
            outInt[i * h + j] = r * 256 * 256 + b * 256 + g;
        }
    }
    env->SetIntArrayRegion(result, 0, size, outInt);
    env->ReleaseIntArrayElements(image_, image, 0);
    return result;
}extern "C"
JNIEXPORT void JNICALL
Java_com_example_jason_heartratedetection_FormatUtil_enhanceBrightness_12(JNIEnv *env, jclass type,
                                                                          jstring uri_, jint rows,
                                                                          jint cols) {
    const char *uri = env->GetStringUTFChars(uri_, 0);
    Mat mat = imread("/storage/emulated/0/pic_8.jpg", CV_LOAD_IMAGE_COLOR);
    Mat output = EnhanceBrightness(mat, 0.5f);
    output.convertTo(output, CV_8UC3, 255);
    imwrite("/storage/emulated/0/mat.jpg", output);
    env->ReleaseStringUTFChars(uri_, uri);
}extern "C"
JNIEXPORT void JNICALL
Java_com_example_jason_heartratedetection_FormatUtil_rgbToGray(JNIEnv *env, jclass type,
                                                               jobject bitmap, jfloat threshold) {
    if (bitmap == NULL) {
        return;
    }
    AndroidBitmapInfo info;
    memset(&info, 0, sizeof(info));
    AndroidBitmap_getInfo(env, bitmap, &info);
    void *pixels = NULL;
    AndroidBitmap_lockPixels(env, bitmap, &pixels);
    Mat src(info.height, info.width, CV_8UC4, pixels);
    cvtColor(src, src, CV_BGRA2RGB);
    Mat output = EnhanceBrightness(src, threshold);
    output.convertTo(output, CV_8UC3, 255);
    int a = 0, r = 0, g = 0, b = 0;
    for (int y = 0; y < info.height; ++y) {
        // From left to right
        for (int x = 0; x < info.width; ++x) {
            int *pixel = NULL;
            pixel = ((int *) pixels) + y * info.width + x;
            r = output.at<Vec3b>(y, x)[0];
            g = output.at<Vec3b>(y, x)[1];
            b = output.at<Vec3b>(y, x)[2];
            a = RGBA_A(*pixel);
            *pixel = MAKE_RGBA(r, g, b, a);
        }
    }
    AndroidBitmap_unlockPixels(env, bitmap);
}