package com.example.nostalgia;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Introduction extends AppCompatActivity {

    private EditText mUsername;
    private String toUserName;
    private RadioButton mUserYesStudent, mUserNotStudent,mUserYesWorked, mUserNotWorked
            ,mUserYesReligious,mUserNotReligious;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdetails);
        mUsername = (EditText) findViewById(R.id.user_name);
        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUsername.setText(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mUserYesStudent = (RadioButton) findViewById(R.id.yes_student);

        mUserNotStudent = (RadioButton) findViewById(R.id.not_student);
        mUserYesWorked = (RadioButton) findViewById(R.id.yes_worked);
        mUserNotWorked = (RadioButton) findViewById(R.id.not_worked);
        mUserYesReligious = (RadioButton) findViewById(R.id.yes_religious);
        mUserNotReligious = (RadioButton) findViewById(R.id.not_religious);
        Intent intent = new Intent(Introduction.this, MemoryListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UserDetails.getUserDetails(mUsername.getText().toString());
    }
}
