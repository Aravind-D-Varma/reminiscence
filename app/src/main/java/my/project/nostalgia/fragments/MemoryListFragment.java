package my.project.nostalgia.fragments;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.R;
import my.project.nostalgia.activities.MemoryListActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static my.project.nostalgia.activities.LoginActivity.FIRST_TIME;
import static my.project.nostalgia.adapters.RecyclerViewGalleryAdapter.isImageFile;
import static my.project.nostalgia.adapters.RecyclerViewGalleryAdapter.isVideoFile;

/**
 * Contains list layout of RecyclerView and floating action button to show all memories and to add a new one.
 */
public class MemoryListFragment extends Fragment {

    //region Declarations
    private static final String SAVE_SUBTITLE = "save_subtitle";
    public static final String MEMORIES_KEY = "Memories";
    private static final String USERID_KEY = "UserID";
    private RecyclerView mRecyclerView;
    private Memory mNewMemory;
    private MemoryAdapter mAdapter;
    private Callbacks mCallbacks;
    private int itemChangedposition;
    private boolean firstTime = true;

    private static final String[] DECLARED_GETPHOTO_PERMISSIONS = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int MY_STORAGE_CODE = 102;
    //endregion

    /**
     * MemoryListFragment has a way to call methods on its hosting activity. It does not matter which
     * activity is the host. As long as the activity implements CrimeListFragment.Callbacks, everything in CrimeListFragment can work the same.
     */
    public interface Callbacks{
        void onMemorySelected(Memory Memory);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private boolean isDeviceTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_memory_list, menu);
        MenuItem searchItem = menu.findItem(R.id.memory_search_menu);

        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.memory_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    mAdapter.searchMemoriesByTitle(query);
                }
                catch (NullPointerException e){
                    Toast.makeText(getContext(), stringResource(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    mAdapter.searchMemoriesByTitle(newText);
                }
                catch (NullPointerException e){
                    if(newText.length()>=1)
                        Toast.makeText(getContext(), stringResource(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private String stringResource(int p) {
        return getResources().getString(p);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        List<Memory> memories = MemoryLab.get(getActivity()).getMemories();
        View view;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isFirstTime = prefs.getBoolean(FIRST_TIME,true);

        if (isFirstTime) {
            view = inflater.inflate(R.layout.fragment_memory_list, container, false);
            setListAndAddButton(view);
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("Users");
            DocumentReference userDocument = usersCollection.document(userid);
            userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            if(!hasMediaPermission()) {
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                                alertBuilder.setCancelable(true);
                                alertBuilder.setTitle("Storage permission necessary");
                                alertBuilder.setMessage("In order to load your images and videos of previous memories" +
                                        ", please grant storage permissions");
                                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(DECLARED_GETPHOTO_PERMISSIONS, MY_STORAGE_CODE);
                                    }
                                });
                                AlertDialog alert = alertBuilder.create();
                                alert.show();
                            }
                            prefs.edit().putBoolean(FIRST_TIME,false).apply();
                            MemoryLab memoryLab = MemoryLab.get(getActivity());
                            Map<String,Object> dataReceived = documentSnapshot.getData();
                            List<HashMap> hashMaps = (List<HashMap>) dataReceived.get(MEMORIES_KEY);
                            for(HashMap hashMap:hashMaps){
                                Memory memory = new Memory();
                                memory.setTitle(hashMap.get("title").toString());
                                memory.setDetail(hashMap.get("detail").toString());
                                try{
                                    memory.setMediaPaths(hashMap.get("mediaPaths").toString());
                                }catch (NullPointerException e){
                                    memory.setMediaPaths("");
                                }
                                memory.setEvent(hashMap.get("event").toString());
                                memoryLab.addMemory(memory);
                            }
                            updateByDevice();
                        }
                    }
                }
            });
        }
        else if(memories.size()==0){

            view = inflater.inflate(R.layout.empty_list_page, container, false);
            Button noMemoryButton = (Button) view.findViewById(R.id.no_memory_button);

            noMemoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View noMemoryView) {
                    mNewMemory = new Memory();
                    MemoryLab.get(getActivity()).addMemory(mNewMemory);
                    if(isDeviceTablet()) {
                        container.removeAllViews();
                        noMemoryView = inflater.inflate(R.layout.fragment_memory_list, container, false);
                        container.addView(noMemoryView);
                        setListAndAddButton(noMemoryView);
                        updateUIForTablet();
                    }
                    mCallbacks.onMemorySelected(mNewMemory);
                }
            });
        }
        else{
            view = inflater.inflate(R.layout.fragment_memory_list, container, false);
            setListAndAddButton(view);
        }
        updateByDevice();
        return view;
    }
    private boolean hasMediaPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), DECLARED_GETPHOTO_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void setListAndAddButton(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.memory_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.memory_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewMemory = new Memory();
                MemoryLab.get(getActivity()).addMemory(mNewMemory);
                if(isDeviceTablet())
                    updateUIForTablet();
                mCallbacks.onMemorySelected(mNewMemory);
            }
        });
        FloatingActionButton upload = (FloatingActionButton) view.findViewById(R.id.memory_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference userDocument = FirebaseFirestore.getInstance().collection("Users")
                        .document(userid);
                ProgressDialog mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setMessage("Uploading...");
                mProgressDialog.show();
                Map<String,List<Memory>> dataToSave = new HashMap<String,List<Memory>>();
                dataToSave.put(MEMORIES_KEY,MemoryLab.get(getActivity()).getMemories());
                userDocument.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),"Upload Successful",Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Upload Failed: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_STORAGE_CODE:
                if(hasMediaPermission()) {
                    startActivity(new Intent(getActivity(), MemoryListActivity.class));
                    getActivity().finish();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Binds RecyclerView to its adapter
     */
    public void updateUIForTablet() {
        MemoryLab memoryLab = MemoryLab.get(getActivity());
        List<Memory> Memorys = memoryLab.getMemories();
        if(mAdapter == null && Memorys.size()!=0) {
            firstTime = false;
            mAdapter = new MemoryAdapter(Memorys);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            if (Memorys.size() != 0) {
                mAdapter.setMemorys(Memorys);
                mAdapter.notifyDataSetChanged();
            }
        }
        updateSubtitle();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(MemoryLab.get(getActivity()).getMemories().size()!=0 && firstTime) {
            MemoryListFragment fragment = new MemoryListFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            startActivity(new Intent(getContext(), MemoryListActivity.class));
            getActivity().finish();
            return;
        }
        updateByDevice();
    }
    private void updateByDevice() {
        if (isDeviceTablet())
            updateUIForTablet();
        else
            updateUI();
    }
    /**
     * Shows how many memories are there on the ActionBar
     */
    private void updateSubtitle(){
        MemoryLab memoryLab = MemoryLab.get(getActivity());
        int MemoryCount = memoryLab.getMemories().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, MemoryCount, MemoryCount);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
    /** Deletes null memories to avoid crashes
     * Deletes all those memories whose title is null and does not contain any photos.
     * Then, hooks up the adapter and RecyclerView.*/
    public void updateUI() {

        MemoryLab memoryLab = MemoryLab.get(getActivity());
        List<Memory> Memorys;
        Memorys = memoryLab.getMemories();
        if(mAdapter == null && Memorys.size()!=0) {
            firstTime = false;
            mAdapter = new MemoryAdapter(Memorys);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            if (Memorys.size() != 0) {
                mAdapter.setMemorys(Memorys);
                mAdapter.notifyItemChanged(itemChangedposition);
            }
        }
        updateSubtitle();
    }
    /**
     * ViewHolder which sets up individual items of record of memories.
     */
    public class MemoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleText;
        private TextView mDetailText;
        private Button mShare;
        private Button mDelete;
        private ImageView mImageView;
        private Memory mMemory;
        private ImageView mImageView2;
        private TextView mExtraText;

        public MemoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_memory, parent, false));
            itemView.setOnClickListener(this);
            setLayoutTheme(itemView);
            mTitleText = (TextView) itemView.findViewById(R.id.cardview_memory_title);
            setTextTheme(mTitleText);
            mDetailText = (TextView) itemView.findViewById(R.id.cardview_memory_detail);
            setTextTheme(mDetailText);
            mShare = (Button) itemView.findViewById(R.id.cardview_share);
            mDelete = (Button) itemView.findViewById(R.id.cardview_delete);
            mImageView = (ImageView) itemView.findViewById(R.id.cardview_image);
            mImageView2 = itemView.findViewById(R.id.cardview_image2);
            mExtraText = itemView.findViewById(R.id.cardview_extramedia);
        }
        private void setLayoutTheme(View v){
            SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
            String themeValues = getData.getString("GlobalTheme", "Dark");

            if (themeValues.equals("Light"))
                v.setBackground(getResources().getDrawable(R.drawable.layout_border_light));
            else if (themeValues.equals("Dark"))
                v.setBackground(getResources().getDrawable(R.drawable.layout_border));
        }
        private void setTextTheme(TextView tv) {
            SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
            String themeValues = getData.getString("GlobalTheme", "Dark");

            if (themeValues.equals("Light"))
                tv.setTextColor(getResources().getColor(R.color.black));
            else if (themeValues.equals("Dark"))
                tv.setTextColor(getResources().getColor(R.color.light_purple));

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
            }catch (NullPointerException e){}
            mShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ArrayList<Uri> mediaUri = getUrisFromPaths();
                        Intent share = shareMemoryIntent(mediaUri);
                        startActivity(Intent.createChooser(share, "Share Memory"));
                    }
                    catch (NullPointerException e){
                        Toast.makeText(getContext(), stringResource(R.string.share_warning),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MemoryLab.get(getActivity()).deleteMemory(mMemory);
                    startActivity(new Intent(getActivity(), MemoryListActivity.class));
                    getActivity().finish();
                }
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
                    mExtraText.setText("+"+ (numberOfMedias-2)+" "+getString(R.string.more));
                }
            }catch (NullPointerException e){
                mImageView.setImageResource(R.drawable.media_notfound_red);
                mImageView2.setImageResource(R.drawable.media_notfound_red);
                mExtraText.setTextSize(16);
                mExtraText.setText(stringResource(R.string.share_warning));
            }
        }

        private void setPreviewImage(String[] mediaPaths, int i, ImageView imageView) {
            if (isImageFile(mediaPaths[i]))
                imageView.setImageBitmap(BitmapFactory.decodeFile(mediaPaths[i]));
            else if (isVideoFile(mediaPaths[i])) {
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mediaPaths[i], MediaStore.Images.Thumbnails.MINI_KIND);
                imageView.setImageBitmap(thumb);
            }
        }

        private ArrayList<Uri> getUrisFromPaths() {
            ArrayList<Uri> mediaUri = new ArrayList<Uri>();
            for (String path : mMemory.getMediaPaths().split(",")) {
                File file = new File(path);
                Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", file);
                mediaUri.add(uri);
            }
            return mediaUri;
        }
        /**
         * Creates an intent which allows user to share the memory: photos/videos and title.
         * @param mediaUri list of all Uri which contain filepaths of photos and videos of a memory.
         * @return
         */
        private Intent shareMemoryIntent(ArrayList<Uri> mediaUri) {
            Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
            share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediaUri);
            share.setType("*/*");
            share.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
            share.putExtra(Intent.EXTRA_TEXT, mMemory.getTitle());
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return share;
        }
        @Override
        public void onClick(View v) {

            int i = 0;
            for (Memory Memory: MemoryLab.get(getActivity()).getMemories()) {
                if (mMemory != null) {
                    if (Memory.getId().equals(mMemory.getId())) {
                        itemChangedposition = i;
                        break;
                    }
                }
                    i++;
            }
            mCallbacks.onMemorySelected(mMemory);
        }
    }
    /**
     * Adapter for RecyclerView to contain record of all memories
     */
    private class MemoryAdapter extends RecyclerView.Adapter<MemoryHolder>{

        private List<Memory> mMemories;
        public MemoryAdapter(List<Memory> Memorys){
            mMemories = Memorys;
        }
        @NonNull
        @Override
        public MemoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
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
        public void searchMemoriesByTitle(String text) {

                List<Memory> searchMemorysList = new ArrayList<>();
                for (Memory Memory : MemoryLab.get(getActivity()).getMemories()) {
                    if (Memory.getTitle().contains(text))
                        searchMemorysList.add(Memory);
                }
            try {
                mAdapter.setMemorys(searchMemorysList);
                notifyDataSetChanged();
            }catch (NullPointerException e){
                Toast.makeText(getContext(), stringResource(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * Updates display of memories depending on the event user has selected in the menu of Navigation Drawer.
     * Is written in fragment code since fragment contains details of memories.
     * @see MemoryListActivity
     */
    public void eventFilter(String event) {
        List<Memory> searchMemorysList = new ArrayList<>();

        for(Memory Memory: MemoryLab.get(getActivity()).getMemories()){
            if(event.equals(getString(R.string.all)))
                searchMemorysList.add(Memory);
            else if(Memory.getEvent().equals(event)){
                searchMemorysList.add(Memory);
            }
        }
        try {
            mAdapter.setMemorys(searchMemorysList);
            mAdapter.notifyDataSetChanged();
        }catch (NullPointerException e){
            Toast.makeText(getContext(), stringResource(R.string.emptyfilter),Toast.LENGTH_SHORT).show();
        }
    }
}
