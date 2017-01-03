package com.example.sp.imgcropdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

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
    private GridView gridView;
    private final int COLUMN = 3;
    private TextView tv_switch_dir;
    private ListPopupWindow listPopupWindow;
    //目录弹出框的一次最多显示的目录数目
    public static int COUNT_MAX = 4;
    PhotoGridAdapter photoGridAdapter;
    PhotoGridAdapter1 photoGridAdapter1;
    PopupDirectoryListAdapter popDirListAdapter;
    List<PhotoDirectory> directories = new ArrayList<>();
    List<Photo> photos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        gridView = (GridView) findViewById(R.id.gridView);
//        photoGridAdapter1 = new PhotoGridAdapter1(this,photos);
//        gridView.setAdapter(photoGridAdapter1);
        tv_switch_dir = (TextView) findViewById(R.id.tv_switch_dir);
        popDirListAdapter = new PopupDirectoryListAdapter(this,directories);
        photoGridAdapter = new PhotoGridAdapter(this,photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(COLUMN, OrientationHelper.VERTICAL);
//        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoGridAdapter);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());

        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        listPopupWindow.setAnchorView(tv_switch_dir);
        listPopupWindow.setAdapter(popDirListAdapter);
        listPopupWindow.setModal(true);
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();

                PhotoDirectory directory = directories.get(position);

                tv_switch_dir.setText(directory.getName());
                photos.clear();
                photos.addAll(directories.get(position).getPhotos());
                photoGridAdapter.notifyDataSetChanged();
            }
        });

        tv_switch_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPopupWindow.isShowing()) {
                    listPopupWindow.dismiss();
                } else if (!isFinishing()) {
                    adjustHeight();
                    listPopupWindow.show();
                }
            }
        });
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
        this.directories.clear();
        this.directories.addAll(directories);
        popDirListAdapter.notifyDataSetChanged();
        photos.clear();
//        for(PhotoDirectory directory : directories){
//            photos.addAll(directory.getPhotos());
//        }
        photos.addAll(directories.get(MediaStoreHelper.INDEX_ALL_PHOTOS).getPhotos());
        Log.i("count",photos.size()+"");
        photoGridAdapter.notifyDataSetChanged();
//        photoGridAdapter1.notifyDataSetChanged();
    }

    public void adjustHeight() {
        if (popDirListAdapter == null) return;
        int count = popDirListAdapter.getCount();
        count = count < COUNT_MAX ? count : COUNT_MAX;
        if (listPopupWindow != null) {
            listPopupWindow.setHeight(count * getResources().getDimensionPixelOffset(R.dimen.__picker_item_directory_height));
        }
    }
}
