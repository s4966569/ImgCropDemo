package com.example.sp.imgcropdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {

    Button btn_take_photo;
    ImageView mImageView;
    private String mCurrentPhotoPath;
    private final int REQUEST_TAKE_PHOTO = 0x01;
    private final int REQUEST_CROP_PHOTO = 0x02;
    private final int REQUEST_CAPTURE = 0x03;
    public static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image);
        btn_take_photo = (Button) findViewById(R.id.btn_take_photo);
        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
//                dispatchSimplePictureIntent();
            }
        });
    }


    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File phtoFile = null;
            try{
                phtoFile = createImageFile();
            }catch (IOException e){

            }
            if(phtoFile !=null){
                Uri photoURI = Uri.fromFile(phtoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void dispatchSimplePictureIntent() {
        Intent takePictureIntent = new Intent(this,CapatureActivity.class);
        startActivityForResult(takePictureIntent, REQUEST_CAPTURE);
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String imageFileName = "JPEG_"+timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
//        File storageDir = new File(Environment.getExternalStorageDirectory() + "/images");
//        File image = new File(storageDir,imageFileName+".jpg");
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            String str1 = Environment.getExternalStorageDirectory() + "/yanxiu/8k_image.jpg";
            Intent imageCropIntent = new Intent(this,ImageCropActivity.class);
            imageCropIntent.putExtra(ImageCropActivity.PHOTO_PATH,mCurrentPhotoPath);
            startActivityForResult(imageCropIntent,REQUEST_CROP_PHOTO);
        }else if(requestCode == REQUEST_CROP_PHOTO && resultCode == RESULT_OK){
            String filePath = Environment.getExternalStorageDirectory() + "/yanxiu/crop_image.jpg";
            WriteBitmapToFileWorkerTask workerTask = new WriteBitmapToFileWorkerTask(null);
            workerTask.execute(filePath,ImageCropActivity.bitmap);
            mImageView.setImageBitmap(ImageCropActivity.bitmap);
        }else if(requestCode == REQUEST_CAPTURE && resultCode == RESULT_OK){
            mImageView.setImageBitmap(CapatureActivity.bitmap);
        }

    }
}
