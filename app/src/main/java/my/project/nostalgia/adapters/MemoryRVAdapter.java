package my.project.nostalgia.adapters;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import my.project.nostalgia.R;
import my.project.nostalgia.activities.MemoryPagerActivity;
import my.project.nostalgia.fragments.MemoryFragment;
import my.project.nostalgia.fragments.MemoryListFragment;
import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.supplementary.MediaAndURI;
import my.project.nostalgia.supplementary.changeTheme;

public class MemoryRVAdapter extends RecyclerView.Adapter<MemoryRVAdapter.MemoryHolder> implements MemoryListFragment.Callbacks {

    private final Activity mActivity;
    private List<Memory> mMemories;
    private final Context mContext;
    private final List<Memory> selectedMemories = new LinkedList<>();
    private boolean mLongClickPressed = false;
    private ActionMode aM;

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
        CheckBox checkbox = holder.mCheckBox;
        checkbox.setVisibility(View.GONE);
        checkbox.setChecked(false);
        try{selectedMemories.remove(Memory);}catch(NullPointerException ignored){}
        setTitleAndDetail(holder, Memory);
        holder.mShare.setOnClickListener(v -> shareMemory(Memory));
        setImagesAndText(holder, Memory);
        holder.itemView.setOnLongClickListener(v -> {
            mLongClickPressed = true;
            checkbox.setVisibility(View.VISIBLE);
            checkbox.setChecked(true);
            selectedMemories.add(Memory);
            if (aM == null) {
                aM = mActivity.startActionMode(new ActionModeCallback());
                aM.setTitle(String.valueOf(selectedMemories.size()));
            }
            else {
                aM.setTitle(String.valueOf(selectedMemories.size()));
                aM.invalidate();
            }
            return true;
        });
        holder.itemView.setOnClickListener(v -> {
            if (!mLongClickPressed) {
                onMemorySelected(Memory);
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
                if(aM!=null)
                    aM.setTitle(String.valueOf(selectedMemories.size()));
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
            for (int i = 0; i < numberOfMedias && i <= 3; i++) {
                if(mediaPaths[i].length()>1) {
                    final Uri mediaUriOf = mMediaAndURI.getMediaUriOf(mediaPaths[i]);
                    Glide.with(mContext).load(mediaUriOf).into(holder.ImageViews[i]);
                }
            }
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

    @Override
    public void onMemorySelected(Memory Memory) {
        if(((FragmentActivity)mContext).findViewById(R.id.detail_fragment_container) == null){
            try {
                Intent intent = MemoryPagerActivity.newIntent(mContext, Memory.getId());
                mContext.startActivity(intent);
            }catch (Exception e){Toast.makeText(mContext,"App crashes before going to memory page",Toast.LENGTH_SHORT).show();}
        }
        else{
            Fragment newDetail = MemoryFragment.newInstance(Memory.getId());
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, newDetail).commit();
        }
    }

    public class MemoryHolder extends RecyclerView.ViewHolder {

        private final TextView mTitleText;
        private final TextView mDetailText;
        private final TextView mExtraText;
        private final Button mShare;
        private final CheckBox mCheckBox;
        private final ImageView[] ImageViews = new ImageView[4];

        public MemoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_memory, parent, false));
            changeTheme cT = new changeTheme(mContext);
            cT.setLayoutTheme(itemView);
            mTitleText = itemView.findViewById(R.id.cardview_memory_title);
            mDetailText = itemView.findViewById(R.id.cardview_memory_detail);
            mShare = itemView.findViewById(R.id.cardview_share);
            ImageViews[0] = itemView.findViewById(R.id.cardview_image);
            ImageViews[1] = itemView.findViewById(R.id.cardview_image2);
            ImageViews[2] = itemView.findViewById(R.id.cardview_image3);
            ImageViews[3] = itemView.findViewById(R.id.cardview_image4);
            mExtraText = itemView.findViewById(R.id.cardview_extramedia);
            mCheckBox = itemView.findViewById(R.id.cardview_checkbox);
        }

    }

    public static class MemoryDiffUtilCallback extends DiffUtil.Callback {

        private final List<Memory> mOldMemories;
        private final List<Memory> mNewMemories;

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
                final List<Memory> todeleteMemories = selectedMemories;
                AlertDialog.Builder deleteDialogbuilder = new AlertDialog.Builder(mContext, new changeTheme(mContext).setDialogTheme())
                        .setTitle(stringFromResource(R.string.delete_memories))
                        .setMessage(stringFromResource(R.string.deletion_memories_confirm))
                        .setPositiveButton(stringFromResource(R.string.delete), (dialog, whichButton) -> {
                            MemoryLab memoryLab = MemoryLab.get(mContext);
                            for(Memory memory:todeleteMemories)
                                memoryLab.deleteMemory(memory);
                            mLongClickPressed = false;
                            final List<Memory> memories = memoryLab.getMemories();
                            updateList(memories);
                            mode.finish();
                            dialog.dismiss();
                        }).setNegativeButton(stringFromResource(R.string.cancel), (dialog, which) -> {
                            notifyDataSetChanged();
                            mLongClickPressed = false;
                            dialog.dismiss();
                        });
                AlertDialog deleteDialog = deleteDialogbuilder.create();
                deleteDialog.setCancelable(false);
                Window window = deleteDialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.TOP;
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setAttributes(wlp);
                deleteDialog.show();
                return true;
            }else if (item.getItemId() == android.R.id.home) {
                mLongClickPressed = false;
                selectedMemories.clear();
                mode.finish();
            }
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {

            final List<Memory> memories = MemoryLab.get(mContext).getMemories();
            int MemoryCount = memories.size();
            String subtitle = mContext.getResources().getQuantityString(R.plurals.subtitle_plural
                    , MemoryCount, MemoryCount);
            ((AppCompatActivity) mActivity).getSupportActionBar().setSubtitle(subtitle);
            updateList(memories);
            mLongClickPressed = false;
            selectedMemories.clear();
            aM = null;
        }
    }

    private String stringFromResource(int resourceID) {
        return mContext.getResources().getString(resourceID);
    }
}