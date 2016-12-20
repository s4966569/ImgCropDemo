package com.example.sp.imgcropdemo;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

/**
 * Created by sp on 16-12-19.
 */

public class CameraPreview extends FrameLayout implements SurfaceHolder.Callback{

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;

    public CameraPreview(Context context) {
        super(context);
        init(context);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(width,height);
        requestLayout();
        mCamera.setParameters(parameters);

        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mCamera != null)
            mCamera.stopPreview();
    }

    public void setCamera(Camera camera){
        if(mCamera == camera){return;}

        stopPreviewAndFreeCamera();

        mCamera = camera;
        if(mCamera != null){
            List<Camera.Size> locaSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPreviewSizes = locaSizes;
            requestLayout();
        }
        try {
            mCamera.setPreviewDisplay(mHolder);
        }catch (IOException e){
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    private void stopPreviewAndFreeCamera() {
        if(mCamera !=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
