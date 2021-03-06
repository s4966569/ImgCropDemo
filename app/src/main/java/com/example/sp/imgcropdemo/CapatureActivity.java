package com.example.sp.imgcropdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

/**
 * Created by sp on 16-12-19.
 */

public class CapatureActivity extends Activity implements View.OnClickListener {

    Camera mCamera;
    CameraPreview mCameraPreview;
    Button btn_capture;
    int screentWidth, screentHeight;
    DisplayMetrics dm;
    private Bitmap mBitmap;
    public static Bitmap savedBitmap;
    OrientationEventListener orientationEventListener;
    private int backCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private boolean isOrientationUnknown = true;  //拍照时是否能检测到手机的旋转角度（华为系列的手机安装APP之后会弹出是否允许读取运动权限的请求，如果禁用则读取不到旋转的角度）

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

//                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN || mCamera == null) {
//                    isOrientationUnknown = true;
//                    return;  //手机平放时或者读取运动权限被禁止，检测不到有效的角度
//                }else{
//                    isOrientationUnknown = false;
//                }
                android.hardware.Camera.CameraInfo info =
                        new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
                orientation = (orientation + 45) / 90 * 90;
                int rotation = info.orientation;
//                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                    rotation = (info.orientation - orientation + 360) % 360;
//                } else {  // back-facing camera
//                    rotation = (info.orientation + orientation) % 360;
//                }
                Log.i("ori",orientation+"");
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setRotation(90);
                mCamera.setParameters(parameters);
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
            mCamera = Camera.open();
            qOpened = (mCamera != null);
            if(qOpened){
                mCamera.getParameters();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"打开摄像头失败,请检查权限",Toast.LENGTH_SHORT).show();
            finish();
            return false;
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
                mCamera.takePicture(null,null,new MyPictureCallBack());
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MainActivity.REQUEST_CROP_PHOTO && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }

    class MyPictureCallBack implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data != null) {
                mBitmap = decodeBitmap(data, screentWidth, screentHeight);
                File imageFile = null;
                try {
                    imageFile = FileUtils.createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                WriteByteToFileWorkerTask writeByteToFileWorkerTask = new WriteByteToFileWorkerTask(new FileWriteCompleteListener());
                writeByteToFileWorkerTask.execute(imageFile,data);
            }
        }
    }

    class FileWriteCompleteListener implements OnTaskCompleteListener<File>{
        @Override
        public void onComplete(File file) {
            if(file == null){
                Toast.makeText(CapatureActivity.this,"保存照片失败!",Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
            int orientation = ExifInterface.ORIENTATION_NORMAL;
            ExifInterface exifInterface ;
            try {
                exifInterface = new ExifInterface(file.getAbsolutePath());
                orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
                switch (orientation){
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        orientation = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        orientation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        orientation = 270;
                        break;
                }
                Log.i("jpegorientation",orientation+"");
            } catch (IOException e) {
                e.printStackTrace();
            }
                //一般的手机，上面在orientationEventListener中，根据屏幕旋转的角度直接设置rotation就可以直接旋转图片 ，但是对于三星系列的手机，setRotation没有效果，但是这个rotation会
                //写入到exif信息中。
                // 一般手机，不管在相机中调没调用params.setRotaion()，exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
                //都会返回ExifInterface.ORIENTATION_UNDEFINED；而三星的手机如果调用了会返回实际设置的rotation（横屏拍照返回ExifInterface.ORIENTATION_NORMAL），没调用的话，会返回ExifInterface.ORIENTATION_NORMAL（非设置的默认值）
                //下面主要分为两种情况处理：
                //第一种情况是拍照的时候有旋转角度但是生成的照片并没有旋转（主要针对三星系列手机）；第二种情况为拍摄的时候检测不到旋转角度（手机平放或者读取运动数据权限被禁止）
//                if(orientation != ExifInterface.ORIENTATION_UNDEFINED && orientation != ExifInterface.ORIENTATION_NORMAL){
////                if(isOrientationUnknown){
////                    //如果在上面的OrientationEventListener中检测不到旋转角度的话，所有的照片默认旋转90度。
////                    orientation = 90;
////                }
//                Matrix matrix = new Matrix();
//                matrix.setRotate(orientation);
//                Bitmap bmpTemp = Bitmap.createBitmap(mBitmap,0,0, mBitmap.getWidth(), mBitmap.getHeight(),matrix,true);
//                mBitmap = bmpTemp;
//            }
            if(mBitmap.getWidth() > mBitmap.getHeight()){
                Matrix matrix = new Matrix();
                matrix.setRotate(90);
                Bitmap bmpTemp1 = Bitmap.createBitmap(mBitmap,0,0, mBitmap.getWidth(), mBitmap.getHeight(),matrix,true);
                mBitmap = bmpTemp1;
            }
            savedBitmap = mBitmap;
            Intent imageCropIntent = new Intent(CapatureActivity.this,ImageCropActivity.class);
            startActivityForResult(imageCropIntent,MainActivity.REQUEST_CROP_PHOTO);
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
