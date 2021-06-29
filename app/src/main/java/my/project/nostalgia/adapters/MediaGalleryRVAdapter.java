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
public class MediaGalleryRVAdapter extends RecyclerView.Adapter {

    private MediaAndURI mMediaAndURI;
    private Context mContext;
    private String[] mediaPaths;

    /**
     * Upon initialisation, sets video uris and image bitmaps from a memory's videopaths.
     */
    public MediaGalleryRVAdapter(Context context, String[] mediaPaths) {
        this.mContext = context;
        this.mediaPaths = mediaPaths;
        mMediaAndURI = new MediaAndURI(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photogallery_item, parent, false);
        return new MyImageViewHolder(v);
    }
    /**
     * Binds video or image depending on what type of item it is.
     * If video, sets video using its Uri. If image, uses Bitmap to set from filepath.
     *
     * @see #getItemViewType(int)
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            Glide.with(mContext).load(mMediaAndURI.getMediaUriOf(mediaPaths[position]))
                    .into(((MyImageViewHolder) holder).image);
        } catch (NullPointerException ignored){}
    }

    @Override
    public int getItemCount() {
        return mediaPaths.length;
    }

    private static class MyImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MyImageViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.memory_photo);
        }
    }

    public void updateList(String[] newMediaPaths) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MediaDiffUtilCallback(this.mediaPaths, newMediaPaths));
        this.mediaPaths = newMediaPaths;
        diffResult.dispatchUpdatesTo(this);
    }
    private static class MediaDiffUtilCallback extends DiffUtil.Callback {

        private String[] mOldMediaPaths;
        private String[] mNewMediaPaths;

        private MediaDiffUtilCallback(String[] oldMediaPaths, String[] newMediaPaths) {
            mOldMediaPaths = oldMediaPaths;
            mNewMediaPaths = newMediaPaths;
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
            return mOldMediaPaths[oldItemPosition].equals(mNewMediaPaths[newItemPosition]);
        }
    }
}