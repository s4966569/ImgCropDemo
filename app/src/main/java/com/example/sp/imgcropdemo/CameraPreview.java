package com.example.sp.imgcropdemo;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

import static android.view.OrientationEventListener.ORIENTATION_UNKNOWN;

/**
 * Created by sp on 16-12-19.
 */

public class CameraPreview extends FrameLayout implements SurfaceHolder.Callback {

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Camera mCamera;
    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;
    private boolean isSupportAutoFocus = false;
    private int orientation;

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

    private void init(Context context) {
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);
//
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        }
        Log.i("lifeCircle:","SurfaceCreated");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera == null)
            return;
        initCameraParams(mCamera);
        mCamera.startPreview();
        if (isSupportAutoFocus)
            mCamera.autoFocus(null);
        Log.i("lifeCircle:","SurfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null)
            mCamera.stopPreview();
        Log.i("lifeCircle:","SurfaceDestroyed");
    }

    public void setCamera(Camera camera) {
        if (mCamera == camera) {
            return;
        }

        stopPreviewAndFreeCamera();

        mCamera = camera;
        if (mCamera != null) {
            List<Camera.Size> supportPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mPreviewSize = Utils.getCloselyPreSize(Utils.getScreenWidth(), Utils.getScreenHeight(), supportPreviewSizes);

            List<Camera.Size> supportPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
            mPictureSize = Utils.getCloselyPreSize(Utils.getScreenWidth(), Utils.getScreenHeight(), supportPictureSizes);

            initCameraParams(mCamera);
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

    }

    private void initCameraParams(Camera camera){
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            isSupportAutoFocus = true;
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        camera.setDisplayOrientation(90);
        requestLayout();
        camera.setParameters(parameters);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                try {
                    mCamera.autoFocus(null);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void stopPreviewAndFreeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

}
