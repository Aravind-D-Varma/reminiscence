package com.example.nostalgia;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Setting up the gridLayout: images and videos
 * Gets images from Bitmap decoder and videos from Uris
 */

public class MyGalleryAdapter extends RecyclerView.Adapter {

    Context context;
    List<Bitmap> photos;
    String[] mediaPaths;
    List<Uri> videos;
    private static final int IMAGE = 0;
    private static final int VIDEO = 1;

    public MyGalleryAdapter(Context applicationContext, String[] mediaPaths) {
        this.context = applicationContext;
        this.mediaPaths = mediaPaths;
        this.photos = getPhotoBitmaps(mediaPaths);
        this.videos = getVideoURIs(mediaPaths);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch(viewType){
            case VIDEO:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.videogallery_item, parent, false);
                return new MyVideoViewHolder(v);
            case IMAGE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photogallery_item, parent, false);
                return new MyImageViewHolder(v);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case VIDEO:
                try {
                    VideoView vv = ((MyVideoViewHolder) holder).video;
                    vv.setVideoURI(videos.get(position));
                    vv.seekTo(1);
                    vv.setZOrderOnTop(true);
                }
                catch(NullPointerException e){}
                break;
            case IMAGE:
                try{
                    ((MyImageViewHolder)holder).image.setImageBitmap(photos.get(position));
                }
                catch (NullPointerException e){}
                break;
        }
    }
    
    @Override
    public int getItemCount() {
        return photos.size();
    }
    
    @Override
    public int getItemViewType(int position) {
       if(isVideoFile(mediaPaths[position]))
            return VIDEO;
       else if(isImageFile(mediaPaths[position]));
            return IMAGE;
    }

    public class MyImageViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public MyImageViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.memory_photo);
        }
    }
    public class MyVideoViewHolder extends RecyclerView.ViewHolder{
        VideoView video;
        public MyVideoViewHolder(View itemView) {
            super(itemView);
            video = (VideoView) itemView.findViewById(R.id.memory_video);
        }
    }
                
    private List<Bitmap> getPhotoBitmaps(String[] photoPaths) {
          
        photos = new ArrayList<Bitmap>();
        for (int i = 0; i < photoPaths.length; i++) {
            if(isImageFile(photoPaths[i])) {
                Bitmap bpimg = BitmapFactory.decodeFile(photoPaths[i]);
                photos.add(bpimg);
            }
            else photos.add(null);
        }
        return photos;
    }
  
    private List<Uri> getVideoURIs(String[] photoPaths) {
          
        videos = new ArrayList<Uri>();
        for (int i = 0; i < photoPaths.length; i++) {
            if(isVideoFile(photoPaths[i])) {
                Uri uriVid = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(photoPaths[i]));
                videos.add(uriVid);
            }
            else
                videos.add(null);
        }
        return videos;
    }          
               
    public static boolean isImageFile(String path) {
        String mimeType = getMimeType(path);
        return mimeType != null && mimeType.startsWith("image");
    }
    public static boolean isVideoFile(String path) {
        String mimeType = getMimeType(path);
        return mimeType != null && mimeType.startsWith("video");
    }
    public static String getMimeType(String path) {
        String mimeType = "";
        String extension = getExtension(path);
        if (MimeTypeMap.getSingleton().hasExtension(extension)) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return mimeType;
    }
    public static String getExtension(String fileName){
        char[] arrayOfFilename = fileName.toCharArray();
        for(int i = arrayOfFilename.length-1; i > 0; i--){
            if(arrayOfFilename[i] == '.'){
                return fileName.substring(i+1, fileName.length());
            }
        }
        return "";
    }
}

