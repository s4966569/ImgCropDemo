package com.example.sp.imgcropdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.example.sp.imgcropdemo.photoLoader.MediaStoreHelper;
import com.example.sp.imgcropdemo.photoLoader.Photo;
import com.example.sp.imgcropdemo.photoLoader.PhotoDirectory;
import com.example.sp.imgcropdemo.photoLoader.PhotoLoaderConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunpeng on 2016/12/30.
 */

public class PhotoSelectActivity extends Activity {
    private RecyclerView recyclerView;
    private final int COLUMN = 3;
    PhotoGridAdapter photoGridAdapter;
    List<Photo> photos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        photoGridAdapter = new PhotoGridAdapter(this,photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(COLUMN, OrientationHelper.VERTICAL);
//        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoGridAdapter);

        int hasPer = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(hasPer == PackageManager.PERMISSION_GRANTED){

        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        Bundle args = new Bundle();
        args.putBoolean(PhotoLoaderConstant.EXTRA_SHOW_GIF,false);
        MediaStoreHelper.getPhotoDirs(this,args,photosResultCallback);
    }

    private MediaStoreHelper.PhotosResultCallback photosResultCallback = new MediaStoreHelper.PhotosResultCallback() {
        @Override
        public void onResultCallback(List<PhotoDirectory> directories) {
            refreshData(directories);
        }
    };

    private void refreshData(List<PhotoDirectory> directories) {
        if(directories == null || directories.isEmpty())
            return;
        photos.clear();
        for(PhotoDirectory directory : directories){
            photos.addAll(directory.getPhotos());
        }
        photoGridAdapter.notifyDataSetChanged();
    }
}
