package my.project.nostalgia.adapters;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import my.project.nostalgia.R;
import my.project.nostalgia.activities.MemoryListActivity;
import my.project.nostalgia.activities.MemoryPagerActivity;
import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.supplementary.MediaAndURI;
import my.project.nostalgia.supplementary.changeTheme;

public class MemoryRVAdapter extends RecyclerView.Adapter<MemoryRVAdapter.MemoryHolder>{

    private Activity mActivity;
    private List<Memory> mMemories;
    private Context mContext;
    public MemoryRVAdapter(Context context, Activity activity, List<Memory> Memorys){
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
    public void updateList(List<Memory> newMemories) {
        List<Memory> oldMemories = this.mMemories;
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new MemoryDiffUtilCallback(oldMemories, newMemories));
        this.mMemories = newMemories;
        if(oldMemories.size()!=newMemories.size())
            diffResult.dispatchUpdatesTo(this);
    }
    public class MemoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleText, mDetailText,mExtraText;
        private Button mShare, mDelete;
        private ImageView[] ImageViews= new ImageView[4];
        private Memory mMemory;
        private MediaAndURI mMediaAndURI;

        public MemoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_memory, parent, false));
            itemView.setOnClickListener(this);
            changeTheme cT = new changeTheme(mContext);
            cT.setLayoutTheme(itemView);
            mTitleText = itemView.findViewById(R.id.cardview_memory_title);
            mDetailText = itemView.findViewById(R.id.cardview_memory_detail);
            mShare = itemView.findViewById(R.id.cardview_share);
            mDelete = itemView.findViewById(R.id.cardview_delete);
            ImageViews[0] = itemView.findViewById(R.id.cardview_image);
            ImageViews[1] = itemView.findViewById(R.id.cardview_image2);
            ImageViews[2] = itemView.findViewById(R.id.cardview_image3);
            ImageViews[3] = itemView.findViewById(R.id.cardview_image4);
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
                    mMediaAndURI = new MediaAndURI(mContext);
                    ArrayList<Uri> mediaUri = mMediaAndURI.getUrisFromPaths(mMemory.getMediaPaths().split(","));
                    Intent share = mMediaAndURI.shareMemoryIntent(mediaUri,mMemory.getTitle());
                    mContext.startActivity(Intent.createChooser(share, "Share Memory"));
                } catch (NullPointerException e){
                    Toast.makeText(mContext, stringResource(R.string.share_warning),Toast.LENGTH_SHORT).show();
                }
            });
            mDelete.setOnClickListener(v -> {
                MemoryLab.get(mActivity).deleteMemory(mMemory);
                updateList(MemoryLab.get(mActivity).getMemories());
            });
            mExtraText.setText("");
            for (ImageView img:ImageViews) {
                img.setImageBitmap(null);
            }
            try{
                String[] mediaPaths = mMemory.getMediaPaths().split(",");
                int numberOfMedias = mediaPaths.length;
                for (int i = 0; i < numberOfMedias && i <=3 ; i++)
                    Glide.with(mContext).load(new MediaAndURI(mContext).getMediaUriOf(mediaPaths[i])).into(ImageViews[i]);
                if(numberOfMedias > 4)
                    mExtraText.setText(mContext.getString(R.string.cardview_extratext,(numberOfMedias-4)));
            }catch (NullPointerException e){
                mExtraText.setText(stringResource(R.string.share_warning));
            }
        }
        @Override
        public void onClick(View v) {
            Intent intent = MemoryPagerActivity.newIntent(mContext, mMemory.getId());
            mContext.startActivity(intent);
        }
        private String stringResource(int resourceID) {
            return mContext.getResources().getString(resourceID);
        }
    }
    private static class MemoryDiffUtilCallback extends DiffUtil.Callback {

        private List<Memory> mOldMemories, mNewMemories;

        private MemoryDiffUtilCallback(List<Memory> oldMemories, List<Memory> newMemories) {
            mOldMemories = oldMemories;
            mNewMemories = newMemories;
        }

        @Override
        public int getOldListSize() {
            return mOldMemories.size();
        }

        @Override
        public int getNewListSize() {
            return mNewMemories.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldMemories.get(oldItemPosition).getId() == mNewMemories.get(newItemPosition).getId();
        }
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldMemories.get(oldItemPosition).equals(mNewMemories.get(newItemPosition));
        }
    }
}