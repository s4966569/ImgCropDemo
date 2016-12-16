package com.example.sp.imgcropdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sp on 16-12-8.
 */

public class ImageCropActivity extends Activity implements View.OnClickListener{

    ImageCropOverView image_over_view;
    ImageView mImageView;
    String photoPath;
    Bitmap mBitmap;
    TextView tv_crop, tv_cancel;
    View rl_crop;
    public static Bitmap bitmap;
    public static final String PHOTO_PATH = "photoPath";
    DisplayMetrics dm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        mImageView = (ImageView) findViewById(R.id.image);
        image_over_view = (ImageCropOverView) findViewById(R.id.image_over_view);
        tv_crop = (TextView) findViewById(R.id.tv_crop);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        rl_crop = findViewById(R.id.rl_crop);
        tv_crop.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        photoPath = getIntent().getStringExtra(PHOTO_PATH);

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        image_over_view.setTargetView(mImageView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
//            setPic(photoPath);
            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(new OnDecodeCompleteListener() {
                @Override
                public void onComplete(Bitmap bitmap) {
                    mImageView.setImageBitmap(bitmap);
                    mBitmap = bitmap;
                    image_over_view.postInvalidate();
                }
            });
            bitmapWorkerTask.execute(photoPath,String.valueOf(rl_crop.getWidth()),String.valueOf(rl_crop.getHeight()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_crop:

                int w = dm.widthPixels;
                int h = dm.heightPixels;

//                Bitmap sourceBmp = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();

                Rect rect = image_over_view.getRect();

                Bitmap sourceBmp = Bitmap.createScaledBitmap(mBitmap,mImageView.getWidth(),mImageView.getHeight(),false);

                Bitmap crop_bitmap = Bitmap.createBitmap(sourceBmp,rect.left,rect.top,
                        rect.width(),rect.height());


                Log.i("left:",image_over_view.getLeft()+"");
                Log.i("top:",image_over_view.getTop()+"");
                Log.i("right:",image_over_view.getRight()+"");
                Log.i("bottom:",image_over_view.getBottom()+"");
                Log.i("X:",image_over_view.getX()+"");
                Log.i("Y",image_over_view.getY()+"");

                Log.i("width",mBitmap.getWidth()+"");
                Log.i("height",mBitmap.getHeight()+"");
                Log.i("bitmapdensity",mBitmap.getDensity()+"");
                Log.i("screendensity",dm.densityDpi+"");
                bitmap = crop_bitmap;
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.tv_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
                break;
        }
    }

    private void setPic(String photoPath){
        int targetW = rl_crop.getWidth();
        int targetH = rl_crop.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath,bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        if(photoW > photoH)
            targetH = targetH /2;

        int scaleFactor = Utils.calculateInSampleSize(bmOptions,targetW,targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath,bmOptions);
        mImageView.setImageBitmap(bitmap);
        mBitmap = bitmap;
    }

    private void setPic(Bitmap bmp){
        mImageView.setImageBitmap(bmp);
    }
}
