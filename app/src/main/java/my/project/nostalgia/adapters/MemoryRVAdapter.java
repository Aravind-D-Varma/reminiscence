package my.project.nostalgia.adapters;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import my.project.nostalgia.R;
import my.project.nostalgia.activities.MemoryPagerActivity;
import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.supplementary.MediaAndURI;
import my.project.nostalgia.supplementary.changeTheme;

public class MemoryRVAdapter extends RecyclerView.Adapter<MemoryRVAdapter.MemoryHolder> {

    private Activity mActivity;
    private List<Memory> mMemories;
    private Context mContext;
    private List<Memory> selectedMemories = new LinkedList<>();
    private boolean mLongClickPressed = false;

    public MemoryRVAdapter(Context context, Activity activity, List<Memory> Memorys) {
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
        mLongClickPressed = holder.longClickPressed;
        CheckBox checkbox = holder.mCheckBox;
        ActionMode aM;
        setTitleAndDetail(holder, Memory);
        holder.mShare.setOnClickListener(v -> {
            shareMemory(Memory);
        });
        holder.mDelete.setOnClickListener(v -> {
            MemoryLab.get(mActivity).deleteMemory(Memory);
            updateList(MemoryLab.get(mActivity).getMemories());
        });
        setImagesAndText(holder, Memory);
        /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mLongClickPressed = true;
                checkbox.setVisibility(View.VISIBLE);
                checkbox.setChecked(true);
                selectedMemories.add(Memory);
                if (aM == null)
                    aM = mActivity.startActionMode(new ActionModeCallback());

                if (selectedMemories.size() == 0) {
                    aM.finish();
                } else {
                    aM.setTitle(selectedMemories.size());
                    aM.invalidate();
                }
                return false;
            }
        });*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLongClickPressed) {
                    Intent intent = MemoryPagerActivity.newIntent(mContext, Memory.getId());
                    mContext.startActivity(intent);
                } else {
                    if (checkbox.isChecked()) {
                        checkbox.setVisibility(View.GONE);
                        checkbox.setChecked(false);
                        selectedMemories.remove(Memory);
                    } else {
                        checkbox.setVisibility(View.VISIBLE);
                        checkbox.setChecked(true);
                        selectedMemories.add(Memory);
                    }
                }
            }
        });

    }

    private void setImagesAndText(@NonNull MemoryHolder holder, Memory memory) {
        MediaAndURI mMediaAndURI = new MediaAndURI(mContext);
        holder.mExtraText.setText("");
        for (ImageView img : holder.ImageViews) {
            img.setImageBitmap(null);
        }
        try {
            String[] mediaPaths = memory.getMediaPaths().split(",");
            int numberOfMedias = mediaPaths.length;
            for (int i = 0; i < numberOfMedias && i <= 3; i++)
                Glide.with(mContext).load(mMediaAndURI.getMediaUriOf(mediaPaths[i])).into(holder.ImageViews[i]);
            if (numberOfMedias > 4)
                holder.mExtraText.setText(mContext.getString(R.string.cardview_extratext, (numberOfMedias - 4)));
        } catch (NullPointerException e) {
            holder.mExtraText.setText(mContext.getResources().getString(R.string.share_warning));
        }
    }

    private void shareMemory(Memory memory) {
        MediaAndURI mMediaAndURI = new MediaAndURI(mContext);
        try {
            ArrayList<Uri> mediaUri = mMediaAndURI.getUrisFromPaths(memory.getMediaPaths().split(","));
            Intent share = mMediaAndURI.shareMemoryIntent(mediaUri, memory.getTitle());
            mContext.startActivity(Intent.createChooser(share, "Share Memory"));
        } catch (NullPointerException e) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.share_warning),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setTitleAndDetail(@NonNull MemoryHolder holder, Memory memory) {
        try {
            if (memory.getTitle() == null || memory.getTitle().equals(""))
                holder.mTitleText.setText(R.string.no_title_set);
            else
                holder.mTitleText.setText(memory.getTitle());
            if (memory.getDetail() == null || memory.getDetail().equals(""))
                holder.mDetailText.setText(R.string.no_details_set);
            else
                holder.mDetailText.setText(memory.getDetail());
        } catch (NullPointerException ignored) {
        }
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
        diffResult.dispatchUpdatesTo(this);
    }

    public class MemoryHolder extends RecyclerView.ViewHolder {

        private TextView mTitleText, mDetailText, mExtraText;
        private Button mShare, mDelete;
        private CheckBox mCheckBox;
        private boolean longClickPressed = false;
        private ImageView[] ImageViews = new ImageView[4];

        public MemoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_memory, parent, false));
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
            mCheckBox = itemView.findViewById(R.id.cardview_checkbox);
        }

    }

    public static class MemoryDiffUtilCallback extends DiffUtil.Callback {

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

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.fragment_memory_list_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.memory_delete_menu) {
                MemoryLab memoryLab = MemoryLab.get(mContext);
                for(Memory memory:selectedMemories)
                    memoryLab.deleteMemory(memory);
                mLongClickPressed = false;
                mode.finish();
                return true;
            }
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            updateList(MemoryLab.get(mContext).getMemories());
        }
    }
}