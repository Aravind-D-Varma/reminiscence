package com.example.nostalgia;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Sets up the zoom in and swipe left/right of photos and videos in a memory.
 * Uses isVideoFile(filepath) method of RecyclerViewAdapterGallery.
 */
public class PagerAdapterGalleryView extends PagerAdapter {
    private Context mContext;
    private String[] individualMediaPaths;

    public PagerAdapterGalleryView(Context context, String[] mediaPaths) {
        mContext = context;
        individualMediaPaths = mediaPaths;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v;
        if(RecyclerViewAdapterGallery.isVideoFile(individualMediaPaths[position])){
            v = inflater.inflate(R.layout.zoom_video,container,false);
            VideoView vv = v.findViewById(R.id.zoomed_videoView);
            setVideo(position, v, vv);
        }
        else {
            v = inflater.inflate(R.layout.zoom_image, container, false);
            ImageView iv = v.findViewById(R.id.zoomed_imageView);
            iv.setImageBitmap(BitmapFactory.decodeFile(individualMediaPaths[position]));
        }
        container.addView(v);
        return v;
    }

    @Override
    public int getCount() {
        return individualMediaPaths.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==object);
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }

    /**
     * Extracts a list of Uris to set video from a memory's filePaths.<br>
     * The Uri is null at those positions where the filepath is an image.
     *
     * @param photoPaths
     * @return
     */
    private List<Uri> getVideoURI(String[] photoPaths) {

        List<Uri> videos = new ArrayList<Uri>();
        for (int i = 0; i < photoPaths.length; i++) {
            if(RecyclerViewAdapterGallery.isVideoFile(photoPaths[i])) {
                Uri uriVid = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", new File(photoPaths[i]));
                videos.add(uriVid);
            }
            else
                videos.add(null);
        }
        return videos;
    }

    /**
     * Set the logic for pausing and resuming video. User can click anywhere on the screen to pause/resume.
     * Play button appears only when paused and disappears when resumed
     * @param position
     * @param v
     * @param vv
     */
    private void setVideo(int position, View v, VideoView vv) {
        FrameLayout buttonLayout = v.findViewById(R.id.play_button_layout);
        ImageButton ib = v.findViewById(R.id.play_button);
        try {
            vv.setVideoURI(getVideoURI(individualMediaPaths).get(position));
            vv.seekTo(1);
            buttonLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickForPauseOrResume(vv, ib);
                }
            });
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickForPauseOrResume(vv, ib);
                }
            });
        }
        catch (NullPointerException e){}
    }

    private void clickForPauseOrResume(VideoView vv, ImageButton ib) {
        if (vv.isPlaying()) {
            ib.setVisibility(View.VISIBLE);
            vv.pause();
        } else {
            vv.start();
            ib.setVisibility(View.GONE);
        }
    }
}
