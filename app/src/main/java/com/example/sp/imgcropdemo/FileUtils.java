package com.example.sp.imgcropdemo;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sunpeng on 2016/12/30.
 */

public class FileUtils {

    public static File createImageFile() throws IOException {
        String imageFileName = "JPEG_"+System.currentTimeMillis();
        File storageDir = MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    public static boolean fileIsExists(String path) {
        if (path == null || path.trim().length() <= 0) {
            return false;
        }
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        Log.e("TMG",path+"file not exists");
        return true;
    }
}
