package com.example.sp.imgcropdemo;

import android.content.Context;
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
}
