package com.example.sp.imgcropdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by sp on 16-12-22.
 */

public class MyApplication extends Application {

    private static Context context =null;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static Context getContext() {
        return context;
    }
}
