package com.example.nostalgia;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MemoryFragment extends Fragment {

    // region Declarations
    public static final String ARG_memory_ID = "memory_id";

    private Memory mMemory;
    private SharedPreferences prefs;
    private String[] applicableEvents ={};
    
    private EditText mTitleField;
    private EditText mDetailField;
    private Button mDateButton;
    private Button mTimeButton;
    private Spinner mSpinner;
    private Button mPhotoButton;
        private Intent getImage;
    private RecyclerView mPhotoRecyclerView;
    private FloatingActionButton mPhotoFAB;
    
    private boolean discardPhoto = false;

    public static final String DIALOG_DATE = "DialogDate";
    public static final String DIALOG_TIME = "DialogTime";
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_TIME = 1;
    public static final int REQUEST_GALLERY_PHOTO = 2;
    public static final int REQUEST_GALLERY_ADDITIONALPHOTO = 3;
    private static final String[] DECLARED_GETPHOTO_PERMISSIONS = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int MY_STORAGE_CODE = 102;
    private final String CURRENT_PHOTOS_ABSENT = "Current Memory Photos";
    public static final String CURRENT_MEMORY = "Current Memory";
    
    //endregion
    //region Fragment+Arguments
    public static MemoryFragment newInstance(UUID memoryId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_memory_ID, memoryId);
        MemoryFragment fragment = new MemoryFragment();
        fragment.setArguments(args);

        return fragment;
    }
    //endregion
    //region OverRidden methods

    //region onCreate
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID recievedID = (UUID) getArguments().getSerializable(ARG_memory_ID);
        mMemory = MemoryLab.get(getActivity()).getMemory(recievedID);
        setHasOptionsMenu(true);
    }
    //endregion
    // region Create and select from Menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().setTitle(mMemory.getTitle());
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_MEMORY,mMemory.getTitle());
        try{
            editor.putBoolean(CURRENT_PHOTOS_ABSENT,(mMemory.getMediaPaths().length()==0));
        }
        catch (NullPointerException e){
            editor.putBoolean(CURRENT_PHOTOS_ABSENT,true);
        }
        editor.apply();
        inflater.inflate(R.menu.fragment_memory, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.delete_memory:
                MemoryLab.get(getActivity()).deleteMemory(mMemory);
                startActivity(new Intent(getActivity(), MemoryListActivity.class));
                return true;
            case R.id.share_memory:

                try {
                    ArrayList<Uri> mediaUri = getUrisFromPaths();
                    Intent share = shareMemoryIntent(mediaUri);
                    startActivity(Intent.createChooser(share, "Share Memory"));
                }
                catch (NullPointerException e){
                    Toast.makeText(getContext(), "No photos attached!",Toast.LENGTH_SHORT).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memory, container, false);
        getActivity().setTitle(mMemory.getTitle());
        applicableEvents = ((MemoryPagerActivity)getActivity()).applicableEvents;
        // region EditText
        mTitleField = (EditText) v.findViewById(R.id.memory_title);
        mTitleField.setText(mMemory.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMemory.setTitle(s.toString());
                getActivity().setTitle(mMemory.getTitle());
                if (!mMemory.getTitle().trim().equals("")) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(CURRENT_MEMORY,mMemory.getTitle());
                    editor.apply();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion
        //region EditText Details
        mDetailField = (EditText) v.findViewById(R.id.memory_details);
        mDetailField.setText(mMemory.getDetail());
        mDetailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMemory.setDetail(s.toString());

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion
        //region DateButton
        mDateButton = (Button) v.findViewById(R.id.memory_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dp = DatePickerFragment.newInstance(mMemory.getDate());
                dp.setTargetFragment(MemoryFragment.this, REQUEST_DATE);
                dp.show(manager, DIALOG_DATE);
            }
        });
        //endregion
        // region TimeButton
        mTimeButton = (Button) v.findViewById(R.id.memory_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment tp = TimePickerFragment.newInstance(mMemory.getDate());
                tp.setTargetFragment(MemoryFragment.this, REQUEST_TIME);
                tp.show(fm, DIALOG_TIME);
            }
        });
        //endregion
        //region Spinner
        mSpinner = (Spinner) v.findViewById(R.id.memory_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.myspinner, applicableEvents);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setSelection(Arrays.asList(applicableEvents).indexOf(mMemory.getEvent()),false);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mMemory.setEvent(applicableEvents[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //endregion
        PackageManager pM = getActivity().getPackageManager();
        //region PhotoButton
        mPhotoButton = (Button)v.findViewById(R.id.memory_selectphotos);
        try {
            if (mMemory.getMediaPaths().length() != 0)
                mPhotoButton.setText(R.string.photos_reselection);
        }catch (NullPointerException e){}
        getImage = getFromMediaIntent();
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasMediaPermission()) {
                    startActivityForResult(Intent.createChooser(getImage, "Select Images/Videos"), REQUEST_GALLERY_PHOTO);
                }
                else{
                    requestPermissions(DECLARED_GETPHOTO_PERMISSIONS, MY_STORAGE_CODE);
                    if(hasMediaPermission()){
                        startActivityForResult(Intent.createChooser(getImage, "Select Images/Videos"), REQUEST_GALLERY_PHOTO);
                    }
                }
            }
        });
        //endregion
        //region PhotoGridView
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photoGridView);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        try{
            setMediaRecyclerView();
        }
        catch (NullPointerException e){}
        ItemClickSupport.addTo(mPhotoRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                displayMediaZoomedIn(position);
            }
        });
        ItemClickSupport.addTo(mPhotoRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                String[] filePaths = individualFilePaths(mMemory);
                AlertDialog diaBox = AskDeleteMedia(filePaths[position]);
                diaBox.show();
                return false;
            }
        });
        //endregion
        //region photoFAB
        mPhotoFAB = (FloatingActionButton) v.findViewById(R.id.photo_fab);
        behaviourOfAddingMedia(v);
        Intent getmoreImage = getFromMediaIntent();
        mPhotoFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(Intent.createChooser(getmoreImage, "Select Image"), REQUEST_GALLERY_ADDITIONALPHOTO);
                }
            });
        //endregion
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode!= Activity.RESULT_OK)
            return;
        if(requestCode == REQUEST_DATE){
           Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mMemory.setDate(date);
            updateDate();
        }
        else if (requestCode == REQUEST_TIME){
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mMemory.setDate(time);
            updateTime();
        }
        else if (requestCode == REQUEST_GALLERY_PHOTO){
            String joinedFilePaths = "";
            if(data.getData()!=null){
                Uri mMediaUri=data.getData();
                joinedFilePaths = joinedFilePaths +(getMediaPath(mMediaUri));
            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri mMediaUri = item.getUri();
                        if(joinedFilePaths.equals(""))
                            joinedFilePaths = joinedFilePaths +(getMediaPath(mMediaUri));
                        else
                            joinedFilePaths = joinedFilePaths + "," + (getMediaPath(mMediaUri));
                    }
                }
            }
            mMemory.setMediaPaths(joinedFilePaths);
            setMediaRecyclerView();
            mPhotoFAB.setVisibility(View.VISIBLE);
            mPhotoFAB.setEnabled(true);
        }
        else if (requestCode == REQUEST_GALLERY_ADDITIONALPHOTO){
            String extraFilePaths = mMemory.getMediaPaths();

            if(data.getData()!=null){
                    Uri mMediaUri=data.getData();
                    String newPhoto = getMediaPath(mMediaUri);
                    if(isDuplicate(newPhoto))
                        discardPhoto = true;
                    else {
                        extraFilePaths = extraFilePaths + "," + (getMediaPath(mMediaUri));
                        discardPhoto = false;
                    }
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri mMediaUri = item.getUri();
                            String newPhoto = getMediaPath(mMediaUri);
                            if(isDuplicate(newPhoto))
                                discardPhoto = true;
                            else {
                                extraFilePaths = extraFilePaths + "," + newPhoto;
                                discardPhoto = false;
                            }
                        }
                    }
                }
            mMemory.setMediaPaths(extraFilePaths);
            setMediaRecyclerView();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        MemoryLab.get(getActivity()).updateMemory(mMemory);
    }
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
        getView().setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String title = preferences.getString(CURRENT_MEMORY, "");
                    boolean photosAbsent = preferences.getBoolean(CURRENT_PHOTOS_ABSENT,true);
                    if (title.equals("")&&photosAbsent) {
                        AskDiscardMemory().show();
                        return true;
                    }
                    else
                        return false;
                }
                return false;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_STORAGE_CODE:
                startActivityForResult(Intent.createChooser(getImage, "Select Image"), REQUEST_GALLERY_PHOTO);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //endregion
//region User-defined methods
    private Intent getFromMediaIntent() {
        Intent getmoreImage = new Intent(Intent.ACTION_GET_CONTENT);
        getmoreImage.setType("*/*");
        getmoreImage.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
        getmoreImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        return getmoreImage;
    }
    private Intent shareMemoryIntent(ArrayList<Uri> mediaUri) {
        Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediaUri);
        share.setType("*/*");
        share.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
        share.putExtra(Intent.EXTRA_TEXT, mMemory.getTitle());
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return share;
    }
    private ArrayList<Uri> getUrisFromPaths() {
        ArrayList<Uri> mediaUri = new ArrayList<Uri>();
        for (String path : individualFilePaths(mMemory)) {
            File file = new File(path);
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", file);
            mediaUri.add(uri);
        }

        return mediaUri;
    }
    private Uri getSpecificContentUri(Uri mImageUri) {
        if(isImage(mImageUri))
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        else
            return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    }

    private String getMediaPath(Uri mMediaUri) {

        String imageEncoded;
        Uri contentUri;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        String[] selectionArgs = getSelectionArguments(mMediaUri);
        String selection = "_id=?";
        contentUri = getSpecificContentUri(mMediaUri);

        Cursor cursor = getContext().getContentResolver().query(contentUri,filePathColumn, selection, selectionArgs, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
        imageEncoded  = cursor.getString(columnIndex);
        cursor.close();

        return imageEncoded;
    }
    private String getMimeType(Uri uri) {
        String mimeType = "";
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = getContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }
    private String[] getSelectionArguments(Uri mImageUri) {
        String docId = DocumentsContract.getDocumentId(mImageUri);
        String[] split = docId.split(":");
        return new String[] {split[1]};
    }
    private String[] individualFilePaths(Memory givenMemory){
        return givenMemory.getMediaPaths().split(",");
    }

    private void updateDate() {
        mDateButton.setText(DateFormat.getDateInstance(DateFormat.FULL).format(mMemory.getDate()));
    }
    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        mTimeButton.setText(sdf.format(mMemory.getDate()));
    }
    private void setMediaRecyclerView() {
        MyGalleryAdapter adapter = new MyGalleryAdapter(getContext(), individualFilePaths(mMemory));
        mPhotoRecyclerView.setAdapter(adapter);
    }
    private void displayMediaZoomedIn(int position) {
        Dialog dialog = new Dialog(getActivity(),R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_memory_pager);
        GalleryViewPagerAdapter adapter = new GalleryViewPagerAdapter(getActivity(),individualFilePaths(mMemory));
        ViewPager pager = (ViewPager) dialog.findViewById(R.id.memory_view_pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
    private void behaviourOfAddingMedia(View v) {
        mPhotoFAB.setVisibility(mMemory.getMediaPaths()==null? View.GONE:View.VISIBLE);
        mPhotoFAB.setEnabled(mMemory.getMediaPaths()!=null);
        TextView mAddphoto = v.findViewById(R.id.addphotos);
        mAddphoto.setVisibility(mMemory.getMediaPaths()==null? View.GONE:View.VISIBLE);
    }

    private boolean isDuplicate(String filePath) {
        String[] duplicateCheck = individualFilePaths(mMemory);
        return Arrays.asList(duplicateCheck).contains(filePath);
    }
    private boolean isImage(Uri mImageUri) {
        return getMimeType(mImageUri).startsWith("image");
    }
    private boolean hasMediaPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), DECLARED_GETPHOTO_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }
//endregion
    //region AlertDialogs
    public AlertDialog AskDiscardMemory()
    {
        AlertDialog discardMemoryDialogBox = new AlertDialog.Builder(getContext())
                .setTitle("Discard Memory")
                .setMessage("You did not set a title or chosen any photos. Do you want to discard this memory?")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(getActivity(), MemoryListActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return discardMemoryDialogBox;
    }
    private AlertDialog AskDeleteMedia(String toDeleteMediapath)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getContext(),R.style.PauseDialog)
                .setTitle("Deletion")
                .setMessage("Do you want to delete this photo?")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String[] allPhotoPaths = individualFilePaths(mMemory);
                        List<String> list = new ArrayList<String>(Arrays.asList(allPhotoPaths));
                        list.remove(toDeleteMediapath);
                        String joined = TextUtils.join(",", list);
                        mMemory.setMediaPaths(joined);
                        setMediaRecyclerView();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        myQuittingDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return myQuittingDialogBox;
    }
    //endregion
}
