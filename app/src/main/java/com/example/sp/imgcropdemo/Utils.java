package com.example.sp.imgcropdemo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

/**
 * Created by sp on 16-12-8.
 */

public class Utils {
    public static int convertDpToPx(Context context, int dp) {
        if (context == null)
            return 0;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * displayMetrics.density);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
