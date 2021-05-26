package com.example.nostalgia;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    public static final String DIALOG_DATE = "DialogDate";
    public static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOT0 = "DialogPhoto";
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_TIME = 1;
    public static final int REQUEST_PHOTO = 3;
    public static final int REQUEST_GALLERY_PHOTO = 4;
    public static final int REQUEST_GALLERY_ADDITIONALPHOTO = 5;

    private static final String[] DECLARED_GETPHOTO_PERMISSIONS = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int MY_STORAGE_CODE = 102;
    private final String CURRENT_PHOTOS_ABSENT = "Current Memory Photos";

    private Memory mMemory;
    private SharedPreferences prefs;
    private Button mPhotoButton;
    private EditText mTitleField;
    private EditText mDetailField;
    private Button mDateButton;
    private FloatingActionButton mPhotoFAB;
    private Button mTimeButton;
    private RecyclerView mPhotoRecyclerView;
    private Spinner mSpinner;
    private Intent getImage;
    private boolean discardPhoto = false;
    private String[] paths ={};
    private List<Bitmap> photos = new ArrayList<Bitmap>();

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
        UUID memoryId = (UUID) getArguments().getSerializable(ARG_memory_ID);
        mMemory = MemoryLab.get(getActivity()).getMemory(memoryId);
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
        editor.putString("Current Memory",mMemory.getTitle());
        try{
            editor.putBoolean(CURRENT_PHOTOS_ABSENT,(mMemory.getPhotoPaths().length()==0));
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
                Intent intent = new Intent(getActivity(), MemoryListActivity.class);
                startActivity(intent);
                return true;
            case R.id.share_memory:
                ArrayList<Uri> imageUris = new ArrayList<Uri>();
                try {
                    for (String path : mMemory.getPhotoPaths().split(",")) {
                        if (!path.equals("")) {
                            File file = new File(path);
                            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", file);
                            imageUris.add(uri);
                        }
                    }
                    Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, mMemory.getTitle());
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "Share Memory"));
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
        paths = ((MemoryPagerActivity)getActivity()).paths;
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
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Current Memory",mMemory.getTitle());
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
        mDetailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(mDetailField.getText().toString().length()==0){
                        mDetailField.setError("It is preferred to write some details of the memory");
                    }
                    else
                        mDetailField.setError(null);
                }
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
        updateTime(mMemory.getDate());
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.myspinner,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(Arrays.asList(paths).indexOf(mMemory.getEvent()),false);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mMemory.setEvent(paths[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //endregion
        PackageManager pM = getActivity().getPackageManager();
        //region PhotoButton
        mPhotoButton = (Button)v.findViewById(R.id.memory_addphotos);
        try {
            if (mMemory.getPhotoPaths().length() != 0)
                mPhotoButton.setText(R.string.photos_reselection);
        }catch (NullPointerException e){}
        getImage = new Intent(Intent.ACTION_GET_CONTENT);
        getImage.setType("image/*");
        getImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPhotoPermission()) {
                    startActivityForResult(Intent.createChooser(getImage, "Select Image"), REQUEST_GALLERY_PHOTO);
                }
                else{
                    requestPermissions(DECLARED_GETPHOTO_PERMISSIONS, MY_STORAGE_CODE);
                    if(hasPhotoPermission()){
                        startActivityForResult(Intent.createChooser(getImage, "Select Image"), REQUEST_GALLERY_PHOTO);
                    }
                }
            }
        });
        //endregion
        //region PhotoGridView
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photoGridView);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        CustomAdapter customAdapter = new CustomAdapter(getContext(), setPhotogalleryView(mMemory.getPhotoPaths()));
        mPhotoRecyclerView.setAdapter(customAdapter);
        ItemClickSupport.addTo(mPhotoRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                FragmentManager fragmentManager = getFragmentManager();
                ImagePickerFragment iP = ImagePickerFragment.getInstance(
                        PictureUtils.getScaledBitMap(mMemory.getPhotoPaths().split(",")[position], getActivity()));
                iP.setTargetFragment(MemoryFragment.this, REQUEST_PHOTO);
                iP.show(fragmentManager, DIALOG_PHOT0);
            }
        });
        ItemClickSupport.addTo(mPhotoRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                AlertDialog diaBox = AskDeletePhoto(mMemory.getPhotoPaths().split(",")[position]);
                diaBox.show();
                return false;
            }
        });
        //endregion
        //region photoFAB
            mPhotoFAB = (FloatingActionButton) v.findViewById(R.id.photo_fab);
            mPhotoFAB.setVisibility(mMemory.getPhotoPaths()==null? View.GONE:View.VISIBLE);
            mPhotoFAB.setEnabled(mMemory.getPhotoPaths()!=null);
            Intent getmoreImage = new Intent(Intent.ACTION_GET_CONTENT);
            getmoreImage.setType("image/*");
            getmoreImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
            updateTime(time);
        }
        else if (requestCode == REQUEST_GALLERY_PHOTO){

            String imagesEncodedList = "";
            if(data.getData()!=null){
                Uri mImageUri=data.getData();
                imagesEncodedList = imagesEncodedList +(getImagePath(mImageUri));
            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        if(imagesEncodedList.equals(""))
                            imagesEncodedList = imagesEncodedList +(getImagePath(uri));
                        else
                            imagesEncodedList = imagesEncodedList +","+(getImagePath(uri));
                    }
                }
            }
            mMemory.setPhotoPaths(imagesEncodedList);
            CustomAdapter customAdapter = new CustomAdapter(getContext(), setPhotogalleryView(imagesEncodedList));
            mPhotoRecyclerView.setAdapter(customAdapter);
            mPhotoFAB.setVisibility(View.VISIBLE);
            mPhotoFAB.setEnabled(true);
        }
        else if (requestCode == REQUEST_GALLERY_ADDITIONALPHOTO){
            String extraImagesEncodedList = mMemory.getPhotoPaths();
            String[] duplicateCheck = extraImagesEncodedList.split(",");
            if(data.getData()!=null){
                Uri mImageUri=data.getData();
                String newPhoto = getImagePath(mImageUri);
                if(Arrays.asList(duplicateCheck).contains(newPhoto))
                    discardPhoto = true;
                else {
                    extraImagesEncodedList = extraImagesEncodedList + "," + (getImagePath(mImageUri));
                    discardPhoto = false;
                }
            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        String newPhoto = getImagePath(uri);
                        if(Arrays.asList(duplicateCheck).contains(newPhoto))
                            discardPhoto = true;
                        else {
                            extraImagesEncodedList = extraImagesEncodedList + "," + newPhoto;
                            discardPhoto = false;
                        }
                    }
                }
            }
            mMemory.setPhotoPaths(extraImagesEncodedList);
            CustomAdapter customAdapter = new CustomAdapter(getContext(), setPhotogalleryView(extraImagesEncodedList));
            mPhotoRecyclerView.setAdapter(customAdapter);
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
            Toast.makeText(getContext(),"Selected same photo(s) again! Discarded them to save space"
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
                    String title = preferences.getString("Current Memory", "");
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
    private List<Bitmap> setPhotogalleryView(String allPhotoPaths) {
        if(allPhotoPaths!=null) {
            String[] photoPaths = allPhotoPaths.split(",");
            photos = new ArrayList<Bitmap>();
            for (int i = 0; i < photoPaths.length; i++) {
                if(!photoPaths[i].equals("")) {
                    Bitmap bpimg = BitmapFactory.decodeFile(photoPaths[i]);
                    photos.add(bpimg);
                }
            }
        }
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(CURRENT_PHOTOS_ABSENT, (mMemory.getPhotoPaths().length() == 0));
            editor.apply();
        }
        catch (NullPointerException e){}
        return photos;
    }
    private String getImagePath(Uri mImageUri) {

        String imageEncoded;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        String docId = DocumentsContract.getDocumentId(mImageUri);
        String[] split = docId.split(":");
        String selection = "_id=?";
        String[] selectionArgs = new String[] {split[1]};
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContext().getContentResolver().query(contentUri,filePathColumn, selection, selectionArgs, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
        imageEncoded  = cursor.getString(columnIndex);
        cursor.close();

        return imageEncoded;
    }
    private boolean hasPhotoPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), DECLARED_GETPHOTO_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void updateDate() {
        mDateButton.setText(DateFormat.getDateInstance(DateFormat.FULL).format(mMemory.getDate()));
    }
    private void updateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        mTimeButton.setText(sdf.format(date));
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
    private AlertDialog AskDeletePhoto(String tobedeletedphotopath)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getContext())
                .setTitle("Deletion")
                .setMessage("Do you want to delete this photo?")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String[] allPhotoPaths = mMemory.getPhotoPaths().split(",");
                        List<String> list = new ArrayList<String>(Arrays.asList(allPhotoPaths));
                        list.remove(tobedeletedphotopath);
                        String joined = TextUtils.join(",", list);
                        mMemory.setPhotoPaths(joined);
                        CustomAdapter customAdapter = new CustomAdapter(getContext(), setPhotogalleryView(mMemory.getPhotoPaths()));
                        mPhotoRecyclerView.setAdapter(customAdapter);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }
    //endregion
}