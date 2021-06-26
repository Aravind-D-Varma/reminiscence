package my.project.nostalgia.adapters;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import my.project.nostalgia.R;
import my.project.nostalgia.supplementary.MediaAndURI;

import java.util.List;

/**
 * Setting up the gridLayout: images and videos.<br>
 * Gets images from Bitmap decoder and videos from Uris
 */
public class RecyclerViewGalleryAdapter extends RecyclerView.Adapter {

    Context context;
    List<Bitmap> photos;
    String[] mediaPaths;
    List<Uri> videos;
    private static final int IMAGE = 0;
    private static final int VIDEO = 1;
    /**
     * Upon initialisation, sets video uris and image bitmaps from a memory's videopaths.
     */
    public RecyclerViewGalleryAdapter(Context applicationContext, String[] mediaPaths) {
        this.context = applicationContext;
        this.mediaPaths = mediaPaths;
        this.photos = new MediaAndURI().getPhotoBitmaps(mediaPaths);
        this.videos = new MediaAndURI(applicationContext).getVideoURIs(mediaPaths);
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
    /**
     * Binds video or image depending on what type of item it is.
     * If video, sets video using its Uri. If image, uses Bitmap to set from filepath.
     * @see #getItemViewType(int)
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case VIDEO:
                try {
                    VideoView vv = ((MyVideoViewHolder) holder).video;
                    vv.setVideoURI(videos.get(position));
                    vv.seekTo(1);
                }
                catch(NullPointerException ignored){}
                break;
            case IMAGE:
                try{
                    ((MyImageViewHolder)holder).image.setImageBitmap(photos.get(position));
                }
                catch (NullPointerException ignored){}
                break;
        }
    }
    
    @Override
    public int getItemCount() {
        return mediaPaths.length;
    }
    /**@return constant depending on whether item is video or image.*/
    @Override
    public int getItemViewType(int position) {
       if(new MediaAndURI().isThisVideoFile(mediaPaths[position]))
            return VIDEO;
       else
            return IMAGE;
    }

    private class MyImageViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public MyImageViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.memory_photo);
        }
    }
    private class MyVideoViewHolder extends RecyclerView.ViewHolder{
        VideoView video;
        public MyVideoViewHolder(View itemView) {
            super(itemView);
            video = itemView.findViewById(R.id.memory_video);
        }
    }
    private class MediaDiffUtilCallback extends DiffUtil.Callback{

        private List<Bitmap> mNewPhotos;
        private List<Uri> mNewVideos;
        private final List<Bitmap> mOldphotos;
        private final List<Uri> mOldvideos;

        public MediaDiffUtilCallback(List<Bitmap> newphotos,List<Uri> newvideos){
            mOldphotos = photos;
            mOldvideos = videos;
            this.mNewPhotos = newphotos;
            this.mNewVideos = newvideos;
        }

        @Override
        public int getOldListSize() {
            return (mOldphotos!=null ? mOldphotos.size():0)+(mOldvideos!=null ? mOldvideos.size():0);
        }

        @Override
        public int getNewListSize() {
            return (mNewPhotos!=null ? mNewPhotos.size():0)+(mNewVideos!=null ? mNewVideos.size():0);
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldphotos.get(oldItemPosition).sameAs(mNewPhotos.get(newItemPosition))
                    || mOldvideos.get(oldItemPosition).toString().equals(mNewVideos.get(newItemPosition).toString());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }
    }
}