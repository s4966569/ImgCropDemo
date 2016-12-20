package com.example.sp.imgcropdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by sp on 16-12-19.
 */

public class CapatureActivity extends Activity implements View.OnClickListener{

    Camera mCamera;
    CameraPreview mCameraPreview;
    Button btn_capture;
    int mCameraState;
    private static final int  STATE_FROZEN = 0x01;
    private static final int  STATE_BUSY = 0x02;
    private static final int STATE_PREVIEW = 0x03;
    int screentWidth,screentHeight;
    DisplayMetrics dm;
    public static Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        mCameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        btn_capture = (Button) findViewById(R.id.btn_capture);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screentWidth = dm.widthPixels;
        screentHeight = dm.heightPixels;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(safeCameraOpen()){
            mCameraPreview.setCamera(mCamera);
        }
    }

    private boolean safeCameraOpen(){
        boolean qOpened = false;
        try{
            releaseCameraAndPreview();
            mCamera = Camera.open();
            qOpened = (mCamera != null);
        }catch (Exception e){
            Log.e("camera","failed to open Camera");
            e.printStackTrace();
        }
        return qOpened;
    }

    private void releaseCameraAndPreview(){
        mCameraPreview.setCamera(null);
        if(mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           case  R.id.btn_capture:
                if(mCameraState != STATE_BUSY){
                    mCamera.takePicture(null,new MyPictureCallBack(),null);
                    mCameraState = STATE_BUSY;
                }
        }
    }

    private Bitmap decodeBitmap(byte[] data,int width,int height){

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data,0,data.length,bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        if(photoW > photoH)
            width = height /2;

        int scaleFactor = Utils.calculateInSampleSize(bmOptions,width,height);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length,bmOptions);
        return bitmap;
    }

    class MyPictureCallBack implements Camera.PictureCallback{

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            bitmap = decodeBitmap(data,screentWidth,screentHeight);
            setResult(RESULT_OK);
        }
    }
}
