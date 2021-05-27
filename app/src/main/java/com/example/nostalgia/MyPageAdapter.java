package com.example.nostalgia;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class MyPageAdapter extends PagerAdapter {
    Context mContext;
    int resId = 0;
    String[] allphotoPaths;

    public MyPageAdapter(Context context, String[] photoPaths) {
        mContext = context;
        allphotoPaths = photoPaths;

    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.zoom_image,container,false);
        ImageView iv = v.findViewById(R.id.zoomed_imageView);
        iv.setImageBitmap(BitmapFactory.decodeFile(allphotoPaths[position]));
        container.addView(v);
        return v;
    }

    @Override
    public int getCount() {
        return allphotoPaths.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }
}
