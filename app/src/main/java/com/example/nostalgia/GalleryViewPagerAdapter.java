package com.example.nostalgia;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private String[] allphotoPaths;

    public GalleryViewPagerAdapter(Context context, String[] photoPaths) {
        mContext = context;
        allphotoPaths = photoPaths;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v;
        if(MyGalleryAdapter.isVideoFile(allphotoPaths[position])){
            v = inflater.inflate(R.layout.zoom_video,container,false);
            VideoView vv = v.findViewById(R.id.zoomed_videoView);
            MediaController mc = new MediaController(mContext);
            vv.setMediaController(mc);
            vv.requestFocus();
            vv.setVideoURI(getVideoURI(allphotoPaths).get(position));
            vv.setZOrderOnTop(true);
        }
        else {
            v = inflater.inflate(R.layout.zoom_image, container, false);
            ImageView iv = v.findViewById(R.id.zoomed_imageView);
            iv.setImageBitmap(BitmapFactory.decodeFile(allphotoPaths[position]));
        }
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
    private List<Uri> getVideoURI(String[] photoPaths) {

        List<Uri> videos = new ArrayList<Uri>();
        for (int i = 0; i < photoPaths.length; i++) {
            if(MyGalleryAdapter.isVideoFile(photoPaths[i])) {
                Uri uriVid = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", new File(photoPaths[i]));
                videos.add(uriVid);
            }
        }
        return videos;
    }
}
