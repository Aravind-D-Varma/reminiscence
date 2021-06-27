package my.project.nostalgia.adapters;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import my.project.nostalgia.R;
import my.project.nostalgia.activities.MemoryListActivity;
import my.project.nostalgia.activities.MemoryPagerActivity;
import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.supplementary.MediaAndURI;
import my.project.nostalgia.supplementary.changeTheme;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryHolder>{

    private Activity mActivity;
    private List<Memory> mMemories;
    private Context mContext;
    public MemoryAdapter(Context context, Activity activity, List<Memory> Memorys){
        mContext = context;
        mActivity = activity;
        mMemories = Memorys;
    }

    @NonNull
    @Override
    public MemoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        return new MemoryHolder(layoutInflater, parent);
    }
    @Override
    public void onBindViewHolder(@NonNull MemoryHolder holder, int position) {
        Memory Memory = mMemories.get(position);
        holder.bind(Memory);
    }
    @Override
    public int getItemCount() {
        return mMemories.size();
    }
    public void setMemorys(List<Memory> Memorys){
        mMemories = Memorys;
    }
    /**Updates display of memories depending on the event user has selected in the menu of Navigation Drawer.
     * Is written in fragment code since fragment contains details of memories.
     * @see MemoryListActivity*/

    public class MemoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleText;
        private TextView mDetailText;
        private Button mShare;
        private Button mDelete;
        private ImageView mImageView;
        private Memory mMemory;
        private ImageView mImageView2;
        private TextView mExtraText;
        private MediaAndURI mMediaAndURI;

        public MemoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_memory, parent, false));
            itemView.setOnClickListener(this);
            changeTheme cT = new changeTheme(mContext);
            cT.setLayoutTheme(itemView);
            mTitleText = itemView.findViewById(R.id.cardview_memory_title);
            cT.setTextTheme(mTitleText);
            mDetailText = itemView.findViewById(R.id.cardview_memory_detail);
            cT.setTextTheme(mDetailText);
            mShare = itemView.findViewById(R.id.cardview_share);
            mDelete = itemView.findViewById(R.id.cardview_delete);
            mImageView = itemView.findViewById(R.id.cardview_image);
            mImageView2 = itemView.findViewById(R.id.cardview_image2);
            mExtraText = itemView.findViewById(R.id.cardview_extramedia);
        }
        public void bind(Memory Memory){
            mMemory = Memory;
            try{
                if (mMemory.getTitle()==null || mMemory.getTitle().equals(""))
                    mTitleText.setText(R.string.no_title_set);
                else
                    mTitleText.setText(mMemory.getTitle());
                if(mMemory.getDetail()==null || mMemory.getDetail().equals(""))
                    mDetailText.setText(R.string.no_details_set);
                else
                    mDetailText.setText(mMemory.getDetail());
            }catch (NullPointerException ignored){}
            mShare.setOnClickListener(v -> {
                try {
                    mMediaAndURI = new MediaAndURI();
                    ArrayList<Uri> mediaUri = mMediaAndURI.getUrisFromPaths(mMemory.getMediaPaths().split(","));
                    Intent share = mMediaAndURI.shareMemoryIntent(mediaUri,mMemory.getTitle());
                    mContext.startActivity(Intent.createChooser(share, "Share Memory"));
                }
                catch (NullPointerException e){
                    Toast.makeText(mContext, stringResource(R.string.share_warning),Toast.LENGTH_SHORT).show();
                }
            });
            mDelete.setOnClickListener(v -> {
                MemoryLab.get(mActivity).deleteMemory(mMemory);
                mContext.startActivity(new Intent(mActivity, MemoryListActivity.class));
                mActivity.finish();
            });
            try{
                String[] mediaPaths = mMemory.getMediaPaths().split(",");
                int numberOfMedias = mediaPaths.length;
                if(numberOfMedias == 1) {
                    setPreviewImage(mediaPaths, 0, mImageView);
                    mImageView2.setImageBitmap(null);
                    mExtraText.setText("");
                }
                else if (numberOfMedias == 2) {
                    setPreviewImage(mediaPaths, 0, mImageView);
                    setPreviewImage(mediaPaths, 1, mImageView2);
                    mExtraText.setText("");
                }
                else if (numberOfMedias > 2){
                    setPreviewImage(mediaPaths, 0, mImageView);
                    setPreviewImage(mediaPaths, 1, mImageView2);
                    mExtraText.setText("+"+ (numberOfMedias-2)+" "+mContext.getString(R.string.more));
                }
            }catch (NullPointerException e){
                mImageView.setImageResource(R.drawable.media_notfound_red);
                mImageView2.setImageResource(R.drawable.media_notfound_red);
                mExtraText.setTextSize(16);
                mExtraText.setText(stringResource(R.string.share_warning));
            }
        }

        private void setPreviewImage(String[] mediaPaths, int i, ImageView imageView) {
            if (new MediaAndURI().isThisImageFile(mediaPaths[i]))
                imageView.setImageBitmap(BitmapFactory.decodeFile(mediaPaths[i]));
            else if (new MediaAndURI().isThisVideoFile(mediaPaths[i])) {
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mediaPaths[i], MediaStore.Images.Thumbnails.MINI_KIND);
                imageView.setImageBitmap(thumb);
            }
        }
        @Override
        public void onClick(View v) {
            Intent intent = MemoryPagerActivity.newIntent(mContext, mMemory.getId());
            mContext.startActivity(intent);
        }
    }
    private String stringResource(int p) {
        return mContext.getResources().getString(p);
    }
}