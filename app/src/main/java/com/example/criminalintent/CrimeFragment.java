package com.example.criminalintent;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.criminalintent.ImagePickerFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CrimeFragment extends Fragment {

    // region Declarations
    public static final String ARG_CRIME_ID = "crime_id";
    public static final String DIALOG_DATE = "DialogDate";
    public static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOT0 = "DialogPhoto";
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_TIME = 1;
    public static final int REQUEST_CONTACT = 2;
    public static final int REQUEST_PHOTO = 3;

    private static final String[] DECLARED_CONTACT_PERMISSIONS = new String[] {Manifest.permission.READ_CONTACTS};
    private static final String[] DECLARED_PHOTO_PERMISSIONS = new String[] {Manifest.permission.CAMERA};
    private static final int MY_READ_CONTACTS_CODE = 100;
    private static final int MY_CAMERA_CODE = 101;

    private String mSuspectNumber;
    private Crime mCrime;
    private List<Crime> mCrimes;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private TextInputLayout mTextInputLayout;
    private TextInputEditText mTitleField;
    private EditText mDetailField;
    private Button mDateButton;
    private Button mSendReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private static Button mFirstCrime;
    private static Button mLastCrime;

    private File mPhotoFile;
    private String mSuspectId;
    public int thumbnailWidth, thumbnailHeight;
    //endregion

    //region Fragment+Arguments
    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }
    //endregion

    //region OverRidden methods

    //region onCreate
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        setHasOptionsMenu(true);
    }
    //endregion

    // region Create and select from Menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.delete_crime:
                //CrimeLab.get(getActivity()).getCrimes().remove(mCrime);
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                Intent intent = new Intent(getActivity(), CrimeListActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        // region EditText
        mTextInputLayout = (TextInputLayout) v.findViewById(R.id.crime_text_input_layout);
        mTitleField = (TextInputEditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
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
                        mTextInputLayout.setError("Please enter a title or discard this crime");
                        mTitleField.getBackground().clearColorFilter();
                    }
                    else
                        mTextInputLayout.setError(null);
                }
            }
        });
        //endregion
        //region EditText Details
        mDetailField = (EditText) v.findViewById(R.id.crime_details);
        mDetailField.setText(mCrime.getDetail());
        mDetailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setDetail(s.toString());
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
                        mDetailField.setError("It is preferred to write some details of the crime");
                    }
                    else
                        mDetailField.setError(null);
                }
            }
        });
        //endregion
        //region DateButton
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dp = DatePickerFragment.newInstance(mCrime.getDate());
                dp.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dp.show(manager, DIALOG_DATE);
            }
        });
        //endregion
        // region TimeButton
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime(mCrime.getDate());
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment tp = TimePickerFragment.newInstance(mCrime.getDate());
                tp.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                tp.show(fm, DIALOG_TIME);
            }
        });
        //endregion
        //region SolvedCheckbox
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        //endregion
        //region First and Last Button
        mFirstCrime = (Button) v.findViewById(R.id.first_crime);
        mLastCrime = (Button) v.findViewById(R.id.last_crime);
        mFirstCrime.setText("First Crime");
        mLastCrime.setText("Last crime");
        if ( mCrime.getId().equals( mCrimes.get(0).getId() ) )
            mFirstCrime.setVisibility(View.GONE);
        else {
            mFirstCrime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CrimePagerActivity.getCurrentPosition(0);
                }
            });
        }
        if( mCrime.getId().equals( mCrimes.get(mCrimes.size()-1).getId() ) )
            mLastCrime.setVisibility(View.GONE);
        else {
            mLastCrime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CrimePagerActivity.getCurrentPosition(CrimeLab.get(getActivity()).getCrimes().size() - 1);
                }
            });
        }
        //endregion
        //region SendReport Button
        mSendReportButton = (Button) v.findViewById(R.id.crime_report);
        mSendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = ShareCompat.IntentBuilder.from(getActivity()).setType("text/plain")
                        .setChooserTitle(getString(R.string.send_report))
                        .setSubject(getString(R.string.crime_report_subject))
                        .setText(getCrimeReport())
                        .createChooserIntent();
                startActivity(intent);
            }
        });
        //endregion
        //region SuspectButton
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasContactPermission()) {
                    startActivityForResult(pickContact, REQUEST_CONTACT);
                }
                else {
                    requestPermissions(DECLARED_CONTACT_PERMISSIONS, MY_READ_CONTACTS_CODE);
                    if(hasContactPermission())
                        startActivityForResult(pickContact, REQUEST_CONTACT);
                }


            }
        });
        if(mCrime.getSuspect()!=null)
            mSuspectButton.setText("Suspect: "+mCrime.getSuspect());
        else
            mSuspectButton.setText("Choose Suspect");

        //endregion
        //region CallButton
        mCallButton = (Button) v.findViewById(R.id.call_suspect);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:"+mCrime.getNumber());
                Intent callContact = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callContact);
            }
        });
        //endregion
        PackageManager pM = getActivity().getPackageManager();
        //region PhotoButton
        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPhotoPermission()) {
                    takePhoto(captureImage);
                }
                else{
                    requestPermissions(DECLARED_PHOTO_PERMISSIONS, MY_CAMERA_CODE);
                    if(hasPhotoPermission()){
                        takePhoto(captureImage);
                    }
                }
            }
        });
        //endregion
        //region PhotoView
        mPhotoView = (ImageView)v.findViewById(R.id.crime_photo);
        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                thumbnailHeight = mPhotoView.getHeight();
                thumbnailWidth =  mPhotoView.getWidth();
                updatePhotoView(thumbnailHeight, thumbnailWidth);
            }
        });
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                ImagePickerFragment iP = ImagePickerFragment.getInstance(PictureUtils.getScaledBitMap(mPhotoFile.getPath(), getActivity()));
                iP.setTargetFragment(CrimeFragment.this, REQUEST_PHOTO);
                iP.show(fragmentManager, DIALOG_PHOT0);
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
            mCrime.setDate(date);
            updateDate();
        }
        else if (requestCode == REQUEST_TIME){
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(time);
            updateTime(time);
        }
        else if (requestCode == REQUEST_CONTACT && data!=null){
            getSuspectName(data);
            getSuspectNunber();
        }
        else if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.criminalintent.fileprovider",mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView(thumbnailHeight, thumbnailWidth);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mTitleField.setError(null);
        if(mCrime.getTitle() == null)
            CrimeLab.get(getActivity()).deleteCrime(mCrime);
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode) {
            case MY_READ_CONTACTS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            case MY_CAMERA_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //endregion

    //region User-defined methods
    private void updatePhotoView(float destHeight, float destWidth) {
        if(mPhotoFile == null || !mPhotoFile.exists())
            mPhotoView.setImageDrawable(null);
        else {
            Bitmap definedBitMap = PictureUtils.getScaledBitMap(mPhotoFile.getPath(), (int) destHeight, (int) destWidth);
            mPhotoView.setImageBitmap(definedBitMap);
        }
    }
    private void takePhoto(Intent captureImage) {
        Uri uri = FileProvider.getUriForFile(getActivity()
                , "com.example.criminalintent.fileprovider", mPhotoFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo activity : cameraActivities) {
            getActivity().grantUriPermission(activity.activityInfo.packageName
                    , uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        startActivityForResult(captureImage, REQUEST_PHOTO);
    }
    private void getSuspectNunber() {
        Uri callNumberURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] queryFieldsNumber = {ContactsContract.Data.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selectionClause = ContactsContract.Data.CONTACT_ID + "= ?";
        String selectionArgs[] = {""};
        selectionArgs[0] = mSuspectId;
        Cursor numberCursor = getActivity().getContentResolver()
                .query(callNumberURI, queryFieldsNumber, selectionClause, selectionArgs, null);
        try{
            if(numberCursor.getCount()==0)
                return;
            numberCursor.moveToFirst();
            mCrime.setNumber( numberCursor.getString(1));
        }
        finally {
            numberCursor.close();
        }
    }
    private boolean hasContactPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), DECLARED_CONTACT_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private boolean hasPhotoPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), DECLARED_PHOTO_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    //region update Date and time
    private void updateDate() {
        mDateButton.setText(DateFormat.getDateInstance(DateFormat.FULL).format(mCrime.getDate()));
    }
    private void updateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        mTimeButton.setText(sdf.format(date));
    }
    //endregion
    private String getCrimeReport(){
        String solvedString;
        if(mCrime.isSolved())
            solvedString = getString(R.string.crime_solved_label);
        else
            solvedString = getString(R.string.crime_report_unsolved);

        String dateString = DateFormat.getDateInstance(DateFormat.FULL).format(mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if(suspect!=null)
            suspect = getString(R.string.crime_report_suspect, mCrime.getSuspect());
        else
            suspect = getString(R.string.crime_report_no_suspect);

        return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
    }
    private void getSuspectName( Intent data) {
        Uri contactURI = data.getData();
        String[] queryFields = new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
        Cursor c = getActivity().getContentResolver().query(contactURI, queryFields, null, null, null);

        try{
            if(c.getCount()==0)
                return;
            c.moveToFirst();
            mSuspectId = c.getString(0);
            mCrime.setSuspect(c.getString(1));
            mSuspectButton.setText("Suspect: "+mCrime.getSuspect());
        }
        finally {
            c.close();
        }
        return;
    }
    //endregion
}