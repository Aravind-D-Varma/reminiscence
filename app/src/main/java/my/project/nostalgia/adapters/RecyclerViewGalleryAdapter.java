package my.project.nostalgia.adapters;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import my.project.nostalgia.R;
import my.project.nostalgia.supplementary.MediaAndURI;

import java.util.List;

/**
 * Setting up the gridLayout: images and videos.<br>
 * Gets images from Bitmap decoder and videos from Uris
 */
public class RecyclerViewGalleryAdapter extends RecyclerView.Adapter {

    private MediaAndURI mMediaAndURI;
    private Context mContext;
    private List<Uri> photos, videos;
    private String[] mediaPaths;
    private static final int IMAGE = 0;
    private static final int VIDEO = 1;

    /**
     * Upon initialisation, sets video uris and image bitmaps from a memory's videopaths.
     */
    public RecyclerViewGalleryAdapter(Context context, String[] mediaPaths) {
        this.mContext = context;
        this.mediaPaths = mediaPaths;
        mMediaAndURI = new MediaAndURI(context);
        this.photos = mMediaAndURI.getPhotoUris(mediaPaths);
        this.videos = mMediaAndURI.getVideoURIs(mediaPaths);
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
                    Glide.with(mContext)
                            .load(photos.get(position))
                            .into(((MyImageViewHolder)holder).image);
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
    public void updateList(String[] newMediaPaths) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MediaDiffUtilCallback(this.mediaPaths,newMediaPaths));
        diffResult.dispatchUpdatesTo(this);
    }
    private class MediaDiffUtilCallback extends DiffUtil.Callback{

        private String[] mOldMediaPaths;
        private String[] mNewMediaPaths;
        private List<Uri> oldphotos,oldvideos,newphotos,newvideos;

        public MediaDiffUtilCallback(String[] oldMediaPaths,String[] newMediaPaths){
            mOldMediaPaths = oldMediaPaths;
            mNewMediaPaths = newMediaPaths;
            oldphotos = mMediaAndURI.getPhotoUris(mOldMediaPaths);
            newphotos = mMediaAndURI.getPhotoUris(mNewMediaPaths);
            oldvideos = mMediaAndURI.getVideoURIs(mOldMediaPaths);
            newvideos = mMediaAndURI.getVideoURIs(mNewMediaPaths);
        }

        @Override
        public int getOldListSize() {
            return mOldMediaPaths.length;
        }

        @Override
        public int getNewListSize() {
            return mNewMediaPaths.length;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldMediaPaths[oldItemPosition].equals(mNewMediaPaths[newItemPosition]);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            boolean contentsAreSame = false;
            try{
                if(oldphotos.get(oldItemPosition).equals(newphotos.get(newItemPosition)))
                    contentsAreSame = true;
                else if (oldvideos.get(oldItemPosition).equals(newvideos.get(newItemPosition)))
                    contentsAreSame = true;
            }catch (NullPointerException ignored){}
            return contentsAreSame;
        }
    }
}