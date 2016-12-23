package com.example.sp.imgcropdemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

/**
 * Created by sp on 16-12-19.
 */

public class CapatureActivity extends Activity implements View.OnClickListener {

    Camera mCamera;
    CameraPreview mCameraPreview;
    Button btn_capture;
    int mCameraState;
    private static final int STATE_FROZEN = 0x01;
    private static final int STATE_BUSY = 0x02;
    private static final int STATE_PREVIEW = 0x03;
    int screentWidth, screentHeight;
    DisplayMetrics dm;
    public static Bitmap bitmap;
    OrientationEventListener orientationEventListener;
    private int backCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        mCameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        btn_capture = (Button) findViewById(R.id.btn_capture);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screentWidth = dm.widthPixels;
        screentHeight = dm.heightPixels;

        btn_capture.setOnClickListener(this);
        Log.i("lifeCircle:","onCreate");
        backCameraId = getBackCameraId();
        orientationEventListener = new OrientationEventListener(this,SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {

                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN || mCamera == null) {
                    return;  //手机平放时，检测不到有效的角度
                }
                android.hardware.Camera.CameraInfo info =
                        new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(backCameraId, info);
                orientation = (orientation + 45) / 90 * 90;
                int rotation = 0;
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    rotation = (info.orientation - orientation + 360) % 360;
                } else {  // back-facing camera
                    rotation = (info.orientation + orientation) % 360;
                }
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setRotation(rotation);
//                parameters.set("rotation",rotation);
                mCamera.setParameters(parameters);
                Log.i("rotation ", "rotation:" + rotation);
                Log.i("cameraOritation ", "cameraOritation:" + info.orientation);
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("lifeCircle:","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (safeCameraOpen()) {
            mCameraPreview.setCamera(mCamera);
        }
        orientationEventListener.enable();
        Log.i("lifeCircle:","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationEventListener.disable();
        releaseCameraAndPreview();
        Log.i("lifeCircle:","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("lifeCircle:","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("lifeCircle:","onDestroy");
    }

    private boolean safeCameraOpen() {
        boolean qOpened = false;
        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(backCameraId);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"打开摄像头失败",Toast.LENGTH_SHORT).show();
            finish();
        }
        return qOpened;
    }

    private void releaseCameraAndPreview() {
        mCameraPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:
                if (mCameraState != STATE_BUSY) {
                    mCamera.takePicture(null,null,new MyPictureCallBack());
                    mCameraState = STATE_BUSY;
                }
        }
    }

    private Bitmap decodeBitmap(byte[] data, int width, int height) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, bmOptions);
        int scaleFactor = Utils.calculateInSampleSize(bmOptions, width, height);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, bmOptions);
        return bitmap;
    }


    class MyPictureCallBack implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data != null) {
                bitmap = decodeBitmap(data, screentWidth, screentHeight);
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private int getBackCameraId(){
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return -1;
    }
}
