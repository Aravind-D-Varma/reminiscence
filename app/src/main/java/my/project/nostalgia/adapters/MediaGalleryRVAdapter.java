package my.project.nostalgia.adapters;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import my.project.nostalgia.R;
import my.project.nostalgia.models.Memory;
import my.project.nostalgia.supplementary.CircularViewPager;
import my.project.nostalgia.supplementary.MediaAndURI;
import my.project.nostalgia.supplementary.changeTheme;
import my.project.nostalgia.supplementary.transformationViewPager;

/**
 * Setting up the gridLayout: images and videos.<br>
 * Gets images from Bitmap decoder and videos from Uris
 */
public class MediaGalleryRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private String[] mediaPaths;
    private final List<String> selectedMediaPaths = new LinkedList<>();
    private final Memory mMemory;
    private boolean longClickPressed = false;

    /**
     * Upon initialisation, sets video uris and image bitmaps from a memory's videopaths.
     */
    public MediaGalleryRVAdapter(Context context, Memory memory) {
        this.mContext = context;
        this.mMemory = memory;
        if(memory.getMediaPaths()!=null)
            this.mediaPaths = mMemory.getMediaPaths().split(",");
        else
            this.mediaPaths = new String[]{""};
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
        ImageView imageView = ((MyImageViewHolder) holder).image;
        final MediaAndURI mMediaAndURI = new MediaAndURI(mContext);
        try {
            if(mediaPaths[position].length()>1)
                Glide.with(mContext).load(mMediaAndURI.getMediaUriOf(mediaPaths[position]))
                    .into(imageView);
            else
                imageView.setImageBitmap(null);
        } catch (NullPointerException ignored){}
        CheckBox checkbox = ((MyImageViewHolder) holder).mMediaCheckbox;
        checkbox.setVisibility(View.GONE);
        checkbox.setChecked(false);
        if(mediaPaths[position].length()>1)
            selectedMediaPaths.remove(mediaPaths[position]);
        imageView.setOnClickListener(v -> {
            if(!longClickPressed)
                displayMediaZoomedIn(position);
            else{
                if (checkbox.isChecked()) {
                    checkbox.setVisibility(View.GONE);
                    checkbox.setChecked(false);
                    selectedMediaPaths.remove(mediaPaths[position]);
                }
                else {
                    checkbox.setVisibility(View.VISIBLE);
                    checkbox.setChecked(true);
                    selectedMediaPaths.add(mediaPaths[position]);
                }
            }
        });
        imageView.setOnLongClickListener(v -> {
            longClickPressed = true;
            checkbox.setVisibility(View.VISIBLE);
            checkbox.setChecked(true);
            selectedMediaPaths.add(mediaPaths[position]);
            AskDeleteMedias();
            return true;
        });
    }
    @Override
    public int getItemCount() {
        return mediaPaths.length;
    }

    private static class MyImageViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final CheckBox mMediaCheckbox;
        public MyImageViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.memory_photo);
            mMediaCheckbox = itemView.findViewById(R.id.memory_photo_checkbox);
        }
    }
    private static class MediaDiffUtilCallback extends DiffUtil.Callback {

        private final String[] mOldMediaPaths;
        private final String[] mNewMediaPaths;

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

    private void displayMediaZoomedIn(int position) {
        Dialog dialog = new Dialog(mContext,R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.media_pager_layout);
        viewPagerImplementation(position, dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
    private void viewPagerImplementation(int position, Dialog dialog) {
        ZoomViewPagerAdapter adapter = new ZoomViewPagerAdapter(mContext,mediaPaths);
        ViewPager pager = (ViewPager) dialog.findViewById(R.id.media_view_pager);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new CircularViewPager(pager));
        pager.setCurrentItem(position);

        pager.setPageTransformer(false, new transformationViewPager());
        TabLayout tabLayout = dialog.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pager,true);
    }
    private void AskDeleteMedias(){
        AlertDialog.Builder deleteDialogbuilder = new AlertDialog.Builder(mContext, new changeTheme(mContext).setDialogTheme())
            .setTitle(stringFromResource(R.string.delete_file))
            .setMessage(stringFromResource(R.string.deletion_confirm))
            .setPositiveButton(stringFromResource(R.string.delete), (dialog, whichButton) -> {
                List<String> list = new ArrayList<>(Arrays.asList(mediaPaths));
                list.removeAll(selectedMediaPaths);
                String joined = TextUtils.join(",", list);
                mMemory.setMediaPaths(joined);
                this.updateList(joined.split(","));
                this.notifyDataSetChanged();
                longClickPressed = false;
                dialog.dismiss();
            }).setNegativeButton(stringFromResource(R.string.cancel), (dialog, which) -> {
                this.notifyDataSetChanged();
                longClickPressed = false;
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
    }
    private String stringFromResource(int resourceID) {
        return mContext.getResources().getString(resourceID);
    }
    public void updateList(String[] newMediaPaths) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MediaDiffUtilCallback(this.mediaPaths, newMediaPaths));
        this.mediaPaths = newMediaPaths;
        diffResult.dispatchUpdatesTo(this);
    }

}