package com.example.jason.heartratedetection.ui.activity;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jason.heartratedetection.R;
import com.example.jason.heartratedetection.util.Constant;
import com.example.jason.heartratedetection.util.DensityUtil;
import com.example.jason.heartratedetection.util.MyUtil;

import java.io.IOException;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/3/25.
 */

public class PhotoMsgAct extends BaseAct {
    @BindView(R.id.tv_take_time)
    TextView tvTakeTime;
    @BindView(R.id.tv_width_height)
    TextView tvWidthHeight;
    @BindView(R.id.tv_file_msg)
    TextView tvFileMsg;
    @BindView(R.id.tv_photo_param)
    TextView tvPhotoParam;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;

    @Override
    protected void init(Bundle bundle) {
        super.init(bundle);
        Bitmap bm = MyUtil.scaleBitmap(Constant.SOLVE_IMAGE_PATH, new int[]{
                DensityUtil.dip2Px(this, 40), DensityUtil.dip2Px(this, 40)
        });
        ivPhoto.setImageBitmap(bm);
        String msg = null;
        try {
            ExifInterface exif = new ExifInterface(Constant.SOLVE_IMAGE_PATH);
            msg = exif.getAttribute(ExifInterface.TAG_DATETIME);
            tvTakeTime.setText(msg);
            msg = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) + " × " + exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) + " px";
            tvWidthHeight.setText(msg);
            msg = Constant.SOLVE_IMAGE_PATH;
            tvFileMsg.setText(msg);
            msg = "model : " + exif.getAttribute(ExifInterface.TAG_MODEL)
                    + "\nmake : " + exif.getAttribute(ExifInterface.TAG_MAKE)
                    + "\niso : " + exif.getAttribute(ExifInterface.TAG_ISO)
                    + "\nflash : " + exif.getAttribute(ExifInterface.TAG_FLASH);
            tvPhotoParam.setText(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.act_photo_msg;
    }
}
