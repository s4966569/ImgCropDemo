package com.example.sp.imgcropdemo;

import android.graphics.Bitmap;

/**
 * Created by sp on 16-12-16.
 */

public interface OnTaskCompleteListener<T> {
    void onComplete(T t);
}
