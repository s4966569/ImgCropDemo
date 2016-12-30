package com.example.sp.imgcropdemo.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by sunpeng on 2016/12/28.
 */

public class LoaderResult {
    public String uri;
    public ImageView imageView;
    public Bitmap bitmap;

    public LoaderResult(String uri, Bitmap bitmap, ImageView imageView) {
        this.uri = uri;
        this.bitmap = bitmap;
        this.imageView = imageView;
    }
}
