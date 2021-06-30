package my.project.nostalgia.fragments;
import android.Manifest;
import android.app.Activity;
import androidx.appcompat.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.activities.MemoryListActivity;
import my.project.nostalgia.activities.MemoryPagerActivity;
import my.project.nostalgia.R;
import my.project.nostalgia.adapters.MediaGalleryRVAdapter;
import my.project.nostalgia.supplementary.MediaAndURI;
import my.project.nostalgia.supplementary.changeTheme;
import my.project.nostalgia.supplementary.memoryEvents;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Viewing of data. Updates data and responds to user interaction for a selected Memory depending on user actions.
 */
public class MemoryFragment extends Fragment {

    public static final String ARG_memory_ID = "memory_id";

    private Memory mMemory;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String[] applicableEvents ={};

    private Button mDateButton, mPhotoButton;
    private Intent getImage;
    private RecyclerView mPhotoRecyclerView;
    private FloatingActionButton mPhotoFAB;
    private Callbacks mCallbacks;

    private StorageReference mStorageReference;
    private FirebaseAuth mFirebaseAuth;
    private boolean discardPhoto = false;

    public static final String DIALOG_DATE = "DialogDate";
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_GALLERY_PHOTO = 2;
    public static final int REQUEST_GALLERY_ADDITIONALPHOTO = 3;
    private static final String[] DECLARED_GETPHOTO_PERMISSIONS = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int MY_STORAGE_CODE = 102;
    private final String CURRENT_PHOTOS_ABSENT = "Current Memory Photos";
    public static final String CURRENT_MEMORY = "Current Memory";
    private MediaAndURI mMediaAndURI;
    private MediaGalleryRVAdapter mAdapter;

    /**
     * Used to update UI for a given fragment. Function depends on whether device is tablet or phone.
     * @see MemoryListActivity#onMemoryUpdated(Memory)
     */
    public interface Callbacks{
        void onMemoryUpdated(Memory Memory);
    }
    private void updateMemory() {
        MemoryLab.get(getActivity()).updateMemory(mMemory);
        mCallbacks.onMemoryUpdated(mMemory);
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
    /**
     * Create a new MemoryFragment either from MemoryListActivity or MemoryPagerActivity depending on Tablet/Phone
     * @see MemoryListActivity#onMemorySelected(Memory)
     * @see MemoryPagerActivity
     */
    public static MemoryFragment newInstance(UUID memoryId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_memory_ID, memoryId);
        MemoryFragment fragment = new MemoryFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID recievedID = (UUID) getArguments().getSerializable(ARG_memory_ID);
        mMemory = MemoryLab.get(getActivity()).getMemory(recievedID);
        setHasOptionsMenu(true);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditor = mSharedPreferences.edit();
    }
    /**
     * Sets the title of memory on action bar. Allows share and delete of memory
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().setTitle(mMemory.getTitle());
        mEditor.putString(CURRENT_MEMORY,mMemory.getTitle());
        try{
            mEditor.putBoolean(CURRENT_PHOTOS_ABSENT,(mMemory.getMediaPaths().length()==0));
        }
        catch (NullPointerException e){
            mEditor.putBoolean(CURRENT_PHOTOS_ABSENT,true);
        }
        mEditor.apply();
        inflater.inflate(R.menu.fragment_memory, menu);
        new changeTheme(getContext()).colorMemoryIcon(menu);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.delete_memory) {
            MemoryLab.get(getActivity()).deleteMemory(mMemory);
            startActivity(new Intent(getActivity(), MemoryListActivity.class));
            getActivity().finish();
            return true;
        } else if (itemId == R.id.share_memory) {
            try {
                ArrayList<Uri> mediaUri = mMediaAndURI.getUrisFromPaths(individualFilePaths(mMemory));
                Intent share = new MediaAndURI().shareMemoryIntent(mediaUri,mMemory.getTitle());
                startActivity(Intent.createChooser(share, "Share Memory"));
            } catch (NullPointerException e) {
                Toast.makeText(getContext(), stringFromResource(R.string.share_warning), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memory, container, false);
        getActivity().setTitle(mMemory.getTitle());
        mMediaAndURI = new MediaAndURI(getContext());
        try {
            applicableEvents = getMemoryEvents().getIndividualEvents();
            applicableEvents = getMemoryEvents().addStringToArray(stringFromResource(R.string.add_event),
                    applicableEvents);
        }catch (ClassCastException c){
            applicableEvents = getMemoryEvents().getIndividualEvents();
            applicableEvents = getMemoryEvents().addStringToArray(stringFromResource(R.string.add_event),
                    applicableEvents);
        }

        // region EditText
        EditText titleField = (EditText) v.findViewById(R.id.memory_title);
        titleField.setText(mMemory.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMemory.setTitle(s.toString());
                getActivity().setTitle(mMemory.getTitle());
                if (!mMemory.getTitle().trim().equals("")) {
                    mEditor.putString(CURRENT_MEMORY,mMemory.getTitle());
                    mEditor.apply();
                }
                updateMemory();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion
        //region EditText Details
        EditText detailField = (EditText) v.findViewById(R.id.memory_details);
        detailField.setText(mMemory.getDetail());
        detailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMemory.setDetail(s.toString());
                updateMemory();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mDateButton = (Button) v.findViewById(R.id.memory_date);
        updateDate();
        mDateButton.setOnClickListener(v1 -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            DatePickerDialogFragment dp = DatePickerDialogFragment.newInstance(mMemory.getDate());
            dp.setTargetFragment(MemoryFragment.this, REQUEST_DATE);
            dp.show(manager, DIALOG_DATE);
        });
        Spinner spinner = (Spinner) v.findViewById(R.id.memory_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.myspinner, applicableEvents);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(Arrays.asList(applicableEvents).indexOf(mMemory.getEvent()),false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(applicableEvents[position].equals(stringFromResource(R.string.add_event))) {
                    getMemoryEvents().getAndSetNewEvent(getView(),getActivity(),mMemory);
                }
                else {
                    mMemory.setEvent(applicableEvents[position]);
                    updateMemory();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mPhotoButton = (Button)v.findViewById(R.id.memory_selectphotos);
        try {
            if (mMemory.getMediaPaths().length() != 0)
                mPhotoButton.setText(R.string.photos_reselection);
        }catch (NullPointerException ignored){}
        getImage = new MediaAndURI().getFromMediaIntent();
        mPhotoButton.setOnClickListener(v13 -> {
            if(hasMediaPermission()) {
                startActivityForResult(Intent.createChooser(getImage, "Select Images/Videos"), REQUEST_GALLERY_PHOTO);
            }
            else{
                requestPermissions(DECLARED_GETPHOTO_PERMISSIONS, MY_STORAGE_CODE);
                if(hasMediaPermission()){
                    startActivityForResult(Intent.createChooser(getImage, "Select Images/Videos"), REQUEST_GALLERY_PHOTO);
                }
            }
        });
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photoGridView);
        mPhotoRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        try{
            mAdapter = new MediaGalleryRVAdapter(getActivity(), mMemory);
            mPhotoRecyclerView.setAdapter(mAdapter);
        }
        catch (NullPointerException ignored){}
        mPhotoFAB = (FloatingActionButton) v.findViewById(R.id.photo_fab);
        behaviourBeforeAddingMedia();
        Intent getmoreImage = new MediaAndURI().getFromMediaIntent();
        mPhotoFAB.setOnClickListener(v16 -> startActivityForResult(Intent.createChooser(getmoreImage, "Select Image"), REQUEST_GALLERY_ADDITIONALPHOTO));
        FloatingActionButton uploadFAB = (FloatingActionButton) v.findViewById(R.id.upload_fab);
        uploadFAB.setOnClickListener(v17 -> {
            ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();
            mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mFirebaseAuth.getCurrentUser();
            String userID = user.getUid();
            mStorageReference = FirebaseStorage.getInstance().getReference();
            String s = mMemory.getId().toString();

            for(String path:individualFilePaths(mMemory)){
                if(path!=null) {
                    Uri uri = Uri.fromFile(new File(path));

                    char[] arrayOfFilename = path.toCharArray();
                    for(int i = arrayOfFilename.length-1; i>0; i--){
                        if(arrayOfFilename[i] == '/'){
                            path = path.substring(i+1);
                            break;
                        }
                    }
                    StorageReference storageReference = mStorageReference.child(userID+"/"+ s +"/"+path);
                    storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                        try {
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }catch (NullPointerException ignored){}
                    }).addOnFailureListener(e -> {
                        try{
                            Toast.makeText(getContext(),"Upload failed",Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();}catch (NullPointerException ignored){}
                    });
                }
            }

        });
        return v;
    }
    private void behaviourBeforeAddingMedia() {
        mPhotoFAB.setVisibility(mMemory.getMediaPaths()==null? View.GONE:View.VISIBLE);
        mPhotoFAB.setEnabled(mMemory.getMediaPaths()!=null);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode!= Activity.RESULT_OK)
            return;
        if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerDialogFragment.EXTRA_DATE);
            mMemory.setDate(date);
            updateMemory();
            updateDate();
        }
        else if (requestCode == REQUEST_GALLERY_PHOTO){
            StringBuilder joinedFilePaths = new StringBuilder();
            if(data.getData()!=null){
                Uri mMediaUri=data.getData();
                joinedFilePaths.append(mMediaAndURI.getMediaPathFromUri(mMediaUri));
            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri mMediaUri = item.getUri();
                        if(joinedFilePaths.toString().equals(""))
                            joinedFilePaths.append(mMediaAndURI.getMediaPathFromUri(mMediaUri));
                        else
                            joinedFilePaths.append(",").append(mMediaAndURI.getMediaPathFromUri(mMediaUri));
                    }
                }
            }
            mMemory.setMediaPaths(joinedFilePaths.toString());
            updateMemory();
            try {
                mAdapter.updateList(joinedFilePaths.toString().split(","));
            }catch(NullPointerException e){
                mAdapter = new MediaGalleryRVAdapter(getActivity(), mMemory);
                mPhotoRecyclerView.setAdapter(mAdapter);
            }
            behaviourAfterAddingMedia();
        }
        else if (requestCode == REQUEST_GALLERY_ADDITIONALPHOTO){
            StringBuilder extraFilePaths = new StringBuilder(mMemory.getMediaPaths());
            if(data.getData()!=null){
                Uri mMediaUri=data.getData();
                String newPhoto = mMediaAndURI.getMediaPathFromUri(mMediaUri);
                if(mMediaAndURI.isDuplicate(newPhoto,individualFilePaths(mMemory)))
                    discardPhoto = true;
                else {
                    extraFilePaths.append(",").append(mMediaAndURI.getMediaPathFromUri(mMediaUri));
                    discardPhoto = false;
                }
            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri mMediaUri = item.getUri();
                        String newPhoto = mMediaAndURI.getMediaPathFromUri(mMediaUri);
                        if(mMediaAndURI.isDuplicate(newPhoto,individualFilePaths(mMemory)))
                            discardPhoto = true;
                        else {
                            extraFilePaths.append(",").append(newPhoto);
                            discardPhoto = false;
                        }
                    }
                }
            }
            mMemory.setMediaPaths(extraFilePaths.toString());
            updateMemory();
            mAdapter.updateList(extraFilePaths.toString().split(","));
        }
    }
    private void updateDate() {
        mDateButton.setText(DateFormat.getDateInstance(DateFormat.FULL).format(mMemory.getDate()));
    }
    private void behaviourAfterAddingMedia() {
        mEditor.putBoolean(CURRENT_PHOTOS_ABSENT,false);
        mEditor.apply();

        mPhotoButton.setText(R.string.photos_reselection);

        mPhotoFAB.setVisibility(View.VISIBLE);
        mPhotoFAB.setEnabled(true);
    }
    @Override
    public void onPause() {
        super.onPause();
        MemoryLab.get(getActivity()).updateMemory(mMemory);
    }
    /**
     * Dialog box to discard memory if both title AND photos are not set.
     * Shows a message if duplicate media files are selected.
     */
    @Override
    public void onResume() {
        super.onResume();
        if(discardPhoto)
            Toast.makeText(getContext(),"Duplicate(s) of some photo(s) found! Discarded them to save space"
                    ,Toast.LENGTH_SHORT).show();
        if(getView() == null){
            return;
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                String title = mSharedPreferences.getString(CURRENT_MEMORY, "");
                boolean photosAbsent = mSharedPreferences.getBoolean(CURRENT_PHOTOS_ABSENT,true);
                if (title.equals("")&&photosAbsent) {
                    AskDiscardMemory();
                    return true;
                }
                else
                    return false;
            }
            return false;
        });
    }
    private void AskDiscardMemory(){
        AlertDialog.Builder discardMemoryDialogBox = new AlertDialog.Builder(getContext(),new changeTheme(getContext()).setDialogTheme()) ;
        discardMemoryDialogBox.setTitle(stringFromResource(R.string.delete_memory))
                .setMessage(stringFromResource(R.string.delete_memory_confirm))
                .setPositiveButton(stringFromResource(R.string.discard), (dialog, whichButton) -> {
                    MemoryLab.get(getActivity()).deleteMemory(mMemory);
                    Intent intent = new Intent(getActivity(), MemoryListActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                })
                .setNegativeButton(stringFromResource(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .create().show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_STORAGE_CODE) {
            if (hasMediaPermission())
                startActivityForResult(Intent.createChooser(getImage, "Select Image"), REQUEST_GALLERY_PHOTO);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private String[] individualFilePaths(Memory givenMemory){
        return givenMemory.getMediaPaths().split(",");
    }
    private memoryEvents getMemoryEvents() {
        return new memoryEvents(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
    }
    private String stringFromResource(int resourceID) {
        return getResources().getString(resourceID);
    }
    private boolean hasMediaPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), DECLARED_GETPHOTO_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}