package com.example.nostalgia;
import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

    private String mSuspectNumber;
    private Memory mMemory;
    private List<Memory> mMemories;
    private Button mPhotoButton;
    private ImageView mPhotoView;
    private TextInputLayout mTextInputLayout;
    private TextInputEditText mTitleField;
    private EditText mDetailField;
    private Button mDateButton;
    private FloatingActionButton mPhotoFAB;
    private Button mTimeButton;
    private GridView mPhotoGridView;
    private Spinner mSpinner;
    private Intent getImage;
    public int thumbnailWidth, thumbnailHeight;
    private final String[] paths = {"Student Life" , "Work", "Home", "Birthday", "Hangouts", "Festival"};
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
                for(String path : mMemory.getPhotoPaths().split(",")) {
                    if(!path.equals("")) {
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
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memory, container, false);
        mMemories = MemoryLab.get(getActivity()).getMemories();
        getActivity().setTitle(mMemory.getTitle());
        // region EditText
        mTextInputLayout = (TextInputLayout) v.findViewById(R.id.memory_text_input_layout);
        mTitleField = (TextInputEditText) v.findViewById(R.id.memory_title);
        mTitleField.setText(mMemory.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMemory.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                mTextInputLayout.setError(null);
            }
        });
        mTitleField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(mTitleField.getText().toString().length()==0) {
                        mTextInputLayout.setError("Please enter a title or discard this memory");
                        mTitleField.getBackground().clearColorFilter();
                    }
                    else
                        mTextInputLayout.setError(null);
                }
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
        mPhotoButton = (Button)v.findViewById(R.id.memory_camera);
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
        //region PhotoGridView i = 0 excluded since first string is null
        mPhotoGridView = (GridView) v.findViewById(R.id.photoGridView);
        setPhotogalleryView(mMemory.getPhotoPaths());
        mPhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                ImagePickerFragment iP = ImagePickerFragment.getInstance(
                        PictureUtils.getScaledBitMap(mMemory.getPhotoPaths().split(",")[position+1], getActivity()));
                iP.setTargetFragment(MemoryFragment.this, REQUEST_PHOTO);
                iP.show(fragmentManager, DIALOG_PHOT0);
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
                imagesEncodedList = imagesEncodedList +","+(getImagePath(mImageUri));
            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        imagesEncodedList = imagesEncodedList + ","+(getImagePath(uri));
                    }
                }
            }
            mMemory.setPhotoPaths(imagesEncodedList);
            setPhotogalleryView(imagesEncodedList);
            mPhotoFAB.setVisibility(View.VISIBLE);
            mPhotoFAB.setEnabled(true);
        }
        else if (requestCode == REQUEST_GALLERY_ADDITIONALPHOTO){
            String extraImagesEncodedList = mMemory.getPhotoPaths();
            if(data.getData()!=null){
                Uri mImageUri=data.getData();
                extraImagesEncodedList = extraImagesEncodedList +","+(getImagePath(mImageUri));
            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        extraImagesEncodedList = extraImagesEncodedList + ","+(getImagePath(uri));
                    }
                }
            }
            mMemory.setPhotoPaths(extraImagesEncodedList);
            setPhotogalleryView(extraImagesEncodedList);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        mTitleField.setError(null);
        if(mMemory.getTitle() == null)
            MemoryLab.get(getActivity()).deleteMemory(mMemory);
        MemoryLab.get(getActivity()).updateMemory(mMemory);
    }

    @Override
    public void onResume() {
        super.onResume();
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
    private void setPhotogalleryView(String allPhotoPaths) {
        if(allPhotoPaths!=null) {
            String[] photoPaths = allPhotoPaths.split(",");
            photos = new ArrayList<Bitmap>();
            for (int i = 1; i < photoPaths.length; i++) {
                Bitmap bpimg = BitmapFactory.decodeFile(photoPaths[i]);
                photos.add(bpimg);
            }
            CustomAdapter customAdapter = new CustomAdapter(getContext(), photos);
            mPhotoGridView.setAdapter(customAdapter);
        }
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
    //region update Date and time
    private void updateDate() {
        mDateButton.setText(DateFormat.getDateInstance(DateFormat.FULL).format(mMemory.getDate()));
    }
    private void updateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        mTimeButton.setText(sdf.format(date));
    }
    //endregion
    //endregion
}