package com.example.sp.imgcropdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.sp.imgcropdemo.photoLoader.Photo;

import java.util.List;

/**
 * Created by sunpeng on 2017/1/3.
 */

public class PhotoGridAdapter1 extends BaseAdapter {
    private Context mContext;
    private List<Photo> photos;
    private LayoutInflater inflater;
    private int itemWidth;

    public PhotoGridAdapter1(Context context, List<Photo> photos) {
        this.mContext = context;
        this.photos = photos;
        inflater = LayoutInflater.from(context);
        itemWidth = context.getResources().getDisplayMetrics().widthPixels;
        itemWidth = itemWidth / 3;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_photo_grid,parent,false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_photo);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext)
                .load(photos.get(position).getPath()).asBitmap()
                .placeholder(R.drawable.__picker_ic_photo_black_48dp)
                .error(R.drawable.__picker_ic_broken_image_black_48dp)
                .override(itemWidth,itemWidth).into(holder.imageView);

        return convertView;
    }

    private class ViewHolder{
        ImageView imageView;
    }
}
