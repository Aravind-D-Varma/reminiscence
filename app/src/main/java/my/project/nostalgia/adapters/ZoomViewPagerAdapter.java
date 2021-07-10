package my.project.nostalgia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import my.project.nostalgia.R;
import my.project.nostalgia.supplementary.MediaAndURI;

/**
 * Sets up the zoom in and swipe left/right of photos and videos in a memory.
 * Uses isVideoFile(filepath) method of RecyclerViewGalleryAdapter.
 */
public class ZoomViewPagerAdapter extends PagerAdapter {
    private final Context mContext;
    private final String[] individualMediaPaths;

    public ZoomViewPagerAdapter(Context context, String[] mediaPaths) {
        mContext = context;
        individualMediaPaths = mediaPaths;
    }

    /**
     * Inflate video through defined method setVideo or inflates image simply through Bitmap
     *
     * @see #setVideo(int, View, VideoView)
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v;
        if (new MediaAndURI().isThisVideoFile(individualMediaPaths[position])) {
            v = inflater.inflate(R.layout.zoom_video, container, false);
            VideoView vv = v.findViewById(R.id.zoomed_videoView);
            if (individualMediaPaths[position].length() > 1)
                setVideo(position, v, vv);
            else
                container.removeView(v);
        } else {
            v = inflater.inflate(R.layout.zoom_image, container, false);
            ImageView iv = v.findViewById(R.id.zoomed_imageView);
            if (individualMediaPaths[position].length() > 1)
                Glide.with(mContext).load(new MediaAndURI(mContext).getMediaUriOf(individualMediaPaths[position]))
                        .into(iv);
            else
                container.removeView(v);
        }
        container.addView(v);
        return v;
    }

    /**
     * Includes videso and images. So should return total count of media files
     */
    @Override
    public int getCount() {
        return individualMediaPaths.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    /**
     * Important to remove the super.destoryItem
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }

    /**
     * Set the logic for pausing and resuming video. User can click anywhere on the screen to pause/resume.
     * Play button appears only when paused and disappears when resumed
     */
    private void setVideo(int position, View v, VideoView vv) {
        FrameLayout buttonLayout = v.findViewById(R.id.play_button_layout);
        ImageButton ib = v.findViewById(R.id.play_button);
        try {
            vv.setVideoURI(new MediaAndURI(mContext).getMediaUriOf(individualMediaPaths[position]));
            vv.seekTo(1);
            buttonLayout.setOnClickListener(v12 -> clickForPauseOrResume(vv, ib));
            ib.setOnClickListener(v1 -> clickForPauseOrResume(vv, ib));
        } catch (NullPointerException ignored) {
        }
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