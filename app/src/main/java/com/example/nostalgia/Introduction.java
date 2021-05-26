package com.example.nostalgia;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

public class Introduction extends AppCompatActivity {

    private EditText mUsername;
    private Button mContinue;
    public static final String SEND_USERNAME= "username";
    public static final String APPLICABLE_EVENTS = "true_events";
    public static String globalPreference = "com.exampl.nostalgia";
    private RadioButton mUserYesStudent, mUserYesWorked,mUserYesReligious;
    private List<String> allEvents = new LinkedList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdetails);
        allEvents.add("Student Life");allEvents.add("Work");allEvents.add("Festivals");allEvents.add("Home");
        allEvents.add("Birthdays");allEvents.add("Hangouts");
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
        mUserYesWorked = (RadioButton) findViewById(R.id.yes_worked);
        mUserYesReligious = (RadioButton) findViewById(R.id.yes_religious);
        mContinue = (Button) findViewById(R.id.continue_button);
        mContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!mUserYesStudent.isChecked())
                    allEvents.remove("Student Life");
                if(!mUserYesWorked.isChecked())
                    allEvents.remove("Work");
                if(!mUserYesReligious.isChecked())
                    allEvents.remove("Festivals");

                String[] applicableEvents = {};applicableEvents = allEvents.toArray(applicableEvents);
                StringBuilder combinedEvents = new StringBuilder();
                for (int i = 0; i<applicableEvents.length; i++)
                    combinedEvents.append(applicableEvents[i]).append(",");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putString(SEND_USERNAME, mUsername.getText().toString());
                editor.putString(APPLICABLE_EVENTS, combinedEvents.toString());
                editor.apply();
                Intent intent = MemoryListActivity.newIntent(getApplicationContext());
                startActivity(intent);
            }
        });
    }
}
