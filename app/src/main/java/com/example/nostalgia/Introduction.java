package com.example.nostalgia;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Introduction extends AppCompatActivity {

    private EditText mUsername;
    private Button mContinue;
    public static final String SEND_USERNAME= "username";
    public static final String APPLICABLE_EVENTS = "true_events";
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
        mContinue = (Button) findViewById(R.id.continue_button);
        mContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = MemoryListActivity.newIntent(getApplicationContext(), mUsername.getText().toString());
                boolean[] applicableEvents = {mUserYesStudent.isChecked(),mUserYesWorked.isChecked(),mUserYesReligious.isChecked()};
                intent.putExtra(APPLICABLE_EVENTS,applicableEvents);
                startActivity(intent);
            }
        });
    }
}
