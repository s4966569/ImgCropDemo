package com.example.sp.imgcropdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sp.imgcropdemo.photoLoader.PhotoDirectory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by donglua on 15/6/28.
 */
public class PopupDirectoryListAdapter extends BaseAdapter {

    private Context mContext;
    private List<PhotoDirectory> directories = new ArrayList<>();

    public PopupDirectoryListAdapter(Context context, List<PhotoDirectory> directories) {
        this.directories = directories;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return directories.size();
    }


    @Override
    public PhotoDirectory getItem(int position) {
        return directories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return directories.get(position).hashCode();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
            convertView = mLayoutInflater.inflate(R.layout.picker_item_directory, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bindData(directories.get(position));

        return convertView;
    }

    private class ViewHolder {

        public ImageView ivCover;
        public TextView tvName;
        public TextView tvCount;

        public ViewHolder(View rootView) {
            ivCover = (ImageView) rootView.findViewById(R.id.iv_dir_cover);
            tvName = (TextView) rootView.findViewById(R.id.tv_dir_name);
            tvCount = (TextView) rootView.findViewById(R.id.tv_dir_count);
        }

        public void bindData(PhotoDirectory directory) {
            Glide.with(mContext).load(directory.getCoverPath())
                    .dontAnimate()
                    .thumbnail(0.1f)
                    .placeholder(R.drawable.__picker_ic_photo_black_48dp)
                    .error(R.drawable.__picker_ic_broken_image_black_48dp)
                    .into(ivCover);
            tvName.setText(directory.getName());
            tvCount.setText(tvCount.getContext().getString(R.string.picker_image_count, directory.getPhotos().size()));
        }
    }

}
