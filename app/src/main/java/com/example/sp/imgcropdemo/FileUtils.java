package com.example.sp.imgcropdemo;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sp on 16-12-16.
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
}
