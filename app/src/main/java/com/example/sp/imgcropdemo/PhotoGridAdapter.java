package com.example.sp.imgcropdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.sp.imgcropdemo.imageloader.ImageLoader;
import com.example.sp.imgcropdemo.photoLoader.OnItemClickListener;
import com.example.sp.imgcropdemo.photoLoader.Photo;

import java.util.List;

/**
 * Created by sunpeng on 2016/12/30.
 */

public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder> {
    private Context mContext;
    private List<Photo> photos;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    ImageLoader imageLoader;
    private int width;

    public PhotoGridAdapter(Context context, List<Photo> photos) {
        this.mContext = context;
        this.photos = photos;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.build(context);
        width = context.getResources().getDisplayMetrics().widthPixels;
        width = width /3;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(inflater.inflate(R.layout.item_photo_grid,null));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
       imageLoader.bindBitmap(photos.get(position).getPath(),holder.iv_photo,width,width);
        Log.i("position",position+"");
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_photo;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            iv_photo = (ImageView) itemView.findViewById(R.id.iv_photo);
        }
    }

}
