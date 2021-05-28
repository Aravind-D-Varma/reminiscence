package com.example.nostalgia;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

public class Introduction extends AppCompatActivity {

    private EditText mUsername;
    private Button mContinue;
    public static final String FIRST_TIME = "firsttime";
    public static final String SEND_USERNAME= "username";
    public static final String APPLICABLE_EVENTS = "true_events";
    public static String globalPreference = "com.example.nostalgia";
    private RadioButton mUserYesStudent, mUserYesWorked,mUserYesReligious;
    private List<String> allEvents = new LinkedList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sp.getBoolean(FIRST_TIME, false)) {
            Intent intent = MemoryListActivity.newIntent(getApplicationContext());
            startActivity(intent);
        } else {
            final Animation a = AnimationUtils.loadAnimation(this, R.anim.introduction);
            setContentView(R.layout.userdetails);
            allEvents.add("Student Life");
            allEvents.add("Work");
            allEvents.add("Festivals");
            allEvents.add("Home");
            allEvents.add("Birthdays");
            allEvents.add("Hangouts");
            TextView mtextView1 = (TextView) findViewById(R.id.welcome_text);
            mtextView1.startAnimation(a);
            TextView menterName = (TextView) findViewById(R.id.entername_text);
            menterName.startAnimation(a);
            TextView mintroText = (TextView) findViewById(R.id.intro);
            mintroText.startAnimation(a);
            TextView mstudentText = (TextView) findViewById(R.id.student_text);
            mstudentText.startAnimation(a);
            TextView mworkText = (TextView) findViewById(R.id.work_text);
            mworkText.startAnimation(a);
            TextView mreligionText = (TextView) findViewById(R.id.religion_text);
            mreligionText.startAnimation(a);
            mUsername = (EditText) findViewById(R.id.user_name);
            mUsername.startAnimation(a);
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
            RadioGroup muserstudent = (RadioGroup) findViewById(R.id.user_student);
            muserstudent.startAnimation(a);
            mUserYesStudent = (RadioButton) findViewById(R.id.yes_student);
            RadioGroup muserwork = (RadioGroup) findViewById(R.id.user_working);
            muserwork.startAnimation(a);
            mUserYesWorked = (RadioButton) findViewById(R.id.yes_worked);
            RadioGroup muserreligion = (RadioGroup) findViewById(R.id.user_religious);
            muserreligion.startAnimation(a);
            mUserYesReligious = (RadioButton) findViewById(R.id.yes_religious);
            TextView mtextView2 = findViewById(R.id.thankyou_text);
            mtextView2.setAnimation(a);
            mContinue = (Button) findViewById(R.id.continue_button);
            mContinue.setAnimation(a);
            mContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mUserYesStudent.isChecked())
                        allEvents.remove("Student Life");
                    if (!mUserYesWorked.isChecked())
                        allEvents.remove("Work");
                    if (!mUserYesReligious.isChecked())
                        allEvents.remove("Festivals");

                    String[] applicableEvents = {};
                    applicableEvents = allEvents.toArray(applicableEvents);
                    StringBuilder combinedEvents = new StringBuilder();
                    for (int i = 0; i < applicableEvents.length; i++)
                        combinedEvents.append(applicableEvents[i]).append(",");
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    editor.putBoolean(FIRST_TIME,true);
                    editor.putString(SEND_USERNAME, mUsername.getText().toString());
                    editor.putString(APPLICABLE_EVENTS, combinedEvents.toString());
                    editor.apply();
                    Intent intent = MemoryListActivity.newIntent(getApplicationContext());
                    startActivity(intent);
                }
            });
        }
    }
}
