package com.example.sp.imgcropdemo;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sp on 16-12-26.
 */

public class WriteByteToFileWorkerTask extends AsyncTask<Object,Integer,File> {
    private OnTaskCompleteListener<File> onTaskCompleteListener;

    public WriteByteToFileWorkerTask(OnTaskCompleteListener<File> onTaskCompleteListener) {
        this.onTaskCompleteListener = onTaskCompleteListener;
    }

    @Override
    protected File doInBackground(Object... params) {
        File file = (File) params[0];
        byte[] data = (byte[]) params[1];
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                fos.flush();
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
        if(onTaskCompleteListener != null){
            onTaskCompleteListener.onComplete(file);
        }
    }
}
