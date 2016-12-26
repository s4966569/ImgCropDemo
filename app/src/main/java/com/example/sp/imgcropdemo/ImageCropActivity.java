package com.example.sp.imgcropdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public static final String PHOTO_PATH = "data";
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
        if(!TextUtils.isEmpty(photoPath)){
            photoPath = getIntent().getStringExtra(PHOTO_PATH);
        }else {
            mBitmap = CapatureActivity.savedBitmap;
            mImageView.setImageBitmap(mBitmap);
        }
        image_over_view.setTargetView(mImageView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && !TextUtils.isEmpty(photoPath)){
//            setPic(photoPath);
            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(new OnTaskCompleteListener<Bitmap>() {
                @Override
                public void onComplete(Bitmap bitmap) {
                    if(bitmap == null){
                        Toast.makeText(ImageCropActivity.this,"读取照片失败",Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED);
                        finish();
                        return;
                    }
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

                Rect rect = image_over_view.getRect();

                Bitmap sourceBmp = Bitmap.createScaledBitmap(mBitmap,mImageView.getWidth(),mImageView.getHeight(),false);

                Bitmap crop_bitmap = Bitmap.createBitmap(sourceBmp,rect.left,rect.top,
                        rect.width(),rect.height());
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

}
