package com.example.sp.imgcropdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by sp on 16-12-8.
 */

public class ImageCropActivity extends Activity implements View.OnClickListener{

    ImageCropOverView image_over_view;
    ImageView mImageView;
    String photoPath;
    Bitmap mBitmap;
    Button btn_save,btn_cancel;
    public static Bitmap bitmap;
    public static final String PHOTO_PATH = "photoPath";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        mImageView = (ImageView) findViewById(R.id.image);
        image_over_view = (ImageCropOverView) findViewById(R.id.image_over_view);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        photoPath = getIntent().getStringExtra(PHOTO_PATH);
        mBitmap = (Bitmap) getIntent().getExtras().get("data");
    }

    private void setPic(String photoPath){
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath,bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW,photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath,bmOptions);
        mImageView.setImageBitmap(bitmap);
        mBitmap = bitmap;
    }

    private void setPic(Bitmap bmp){
        mImageView.setImageBitmap(bmp);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            setPic(photoPath);
//        setPic(mBitmap);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_save:
                Bitmap crop_bitmap = Bitmap.createBitmap(mBitmap,image_over_view.getLeft(),image_over_view.getTop(),image_over_view.getWidth(),image_over_view.getHeight());
                Log.i("left:",image_over_view.getLeft()+"");
                Log.i("top:",image_over_view.getTop()+"");
                Log.i("right:",image_over_view.getRight()+"");
                Log.i("bottom:",image_over_view.getBottom()+"");
                Log.i("X:",image_over_view.getX()+"");
                Log.i("Y",image_over_view.getY()+"");
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                crop_bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
//                byte[] b = byteArrayOutputStream.toByteArray();
//                Intent intent = new Intent();
//                intent.putExtra("bitmap",b);
//                setResult(RESULT_OK,intent);
                bitmap = crop_bitmap;
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("bitmap",crop_bitmap);
//                Intent intent = new Intent();
//                intent.putExtras(bundle);
//                setResult(RESULT_OK,intent);
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
                break;
        }
    }
}
