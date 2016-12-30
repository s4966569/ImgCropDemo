package com.example.sp.imgcropdemo.imageloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.sp.imgcropdemo.Utils;

import java.io.FileDescriptor;

/**
 * Created by sunpeng on 2016/12/28.
 */

public class ImageResizer {
    private static final String TAG = "ImageResizer";
    public ImageResizer(){}

    public Bitmap decodeSampledBitmapFromRes(Resources res,int resId,int reqWidth,int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resId,options);

        options.inSampleSize = Utils.calculateInSampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res,resId,options);
    }

    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd , int reqWidth,int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null,options);

        options.inSampleSize = Utils.calculateInSampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd,null,options);
    }

    public Bitmap decodeSampledBitmapFromFile(String filePath,int reqWidth,int reqHeight){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,bmOptions);

        int scaleFactor = Utils.calculateInSampleSize(bmOptions,reqWidth,reqHeight);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath,bmOptions);
        return bitmap;
    }
}
