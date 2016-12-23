package com.example.sp.imgcropdemo;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sp on 16-12-23.
 */

public class WriteBitmapToFileWorkerTask extends AsyncTask<Object,Integer,File> {
    private OnTaskCompleteListener<File> onTaskCompleteListener;

    public WriteBitmapToFileWorkerTask(OnTaskCompleteListener<File> onTaskCompleteListener) {
        this.onTaskCompleteListener = onTaskCompleteListener;
    }

    @Override
    protected File doInBackground(Object... params) {
        String filePath = (String) params[0];
        Bitmap bitmap = (Bitmap) params[1];
        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    @Override
    protected void onPostExecute(File file) {
        if(onTaskCompleteListener != null)
            onTaskCompleteListener.onComplete(file);
    }
}
