package com.example.nostalgia;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
            LinearLayout introLL = findViewById(R.id.intro_layout);
            int[] introTextViews = {R.id.welcome_text,R.id.intro,R.id.intro2};
            int[] introQuestionViews = {R.id.entername_text,R.id.user_name,R.id.student_text
                    ,R.id.user_student,R.id.work_text,R.id.user_working,R.id.religion_text,R.id.user_religious};
            int[] introConclusionViews = {R.id.thankyou_text,R.id.continue_button};
            int delay = 1;
            setContentView(R.layout.userdetails);
            allEvents.add("Student Life");
            allEvents.add("Work");
            allEvents.add("Festivals");
            allEvents.add("Home");
            allEvents.add("Birthdays");
            allEvents.add("Hangouts");

            for(int viewID:introTextViews) {
                Animation a = AnimationUtils.loadAnimation(this, R.anim.introduction);
                switch (viewID) {
                    case R.id.welcome_text:
                        setText(a, R.id.welcome_text, delay);
                        break;
                    case R.id.intro:
                        setText(a, R.id.intro, delay);
                        break;
                    case R.id.intro2:
                        delay = delay + 4;
                        setText(a, R.id.intro2, delay);
                        break;
                }
                delay++;
            }
            introLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int delay = 10;
                    for(int viewID:introQuestionViews) {
                        Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.introduction);
                        switch (viewID) {
                            case R.id.entername_text:
                                setText(a, R.id.entername_text, delay);
                                break;
                            case R.id.user_name:
                                delay = delay + 2;
                                setgetUsername(a, delay);
                                break;
                            case R.id.student_text:
                                setText(a, R.id.student_text, delay);
                                break;
                            case R.id.user_student:
                                RadioGroup muserstudent = (RadioGroup) findViewById(R.id.user_student);
                                a.setStartOffset(delay * 1000);
                                muserstudent.startAnimation(a);
                                mUserYesStudent = (RadioButton) findViewById(R.id.yes_student);
                                break;
                            case R.id.work_text:
                                setText(a, R.id.work_text, delay);
                                break;
                            case R.id.user_working:
                                RadioGroup muserwork = (RadioGroup) findViewById(R.id.user_working);
                                a.setStartOffset(delay * 1000);
                                muserwork.startAnimation(a);
                                mUserYesWorked = (RadioButton) findViewById(R.id.yes_worked);
                                break;
                            case R.id.religion_text:
                                setText(a, R.id.religion_text, delay);
                                break;
                            case R.id.user_religious:
                                RadioGroup muserreligion = (RadioGroup) findViewById(R.id.user_religious);
                                a.setStartOffset(delay * 1000);
                                muserreligion.startAnimation(a);
                                mUserYesReligious = (RadioButton) findViewById(R.id.yes_religious);
                                break;
                        }
                        delay++;
                    }
                }
            });

            Animation a = AnimationUtils.loadAnimation(this, R.anim.introduction);
            setText(a,R.id.thankyou_text,delay);
            delay++;
            setContinue(a,delay);

        }
    }

    private void setText(Animation a, int textViewID, int delay){
        TextView mtextView1 = (TextView) findViewById(textViewID);
        mtextView1.append("");
        a.setStartOffset(delay*1000);mtextView1.startAnimation(a);
    }

    private void setContinue(Animation a,int delay) {
        mContinue = (Button) findViewById(R.id.continue_button);
        a.setStartOffset(delay*1000);mContinue.setAnimation(a);
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

    private void setgetUsername(Animation a, int delay) {
        mUsername = (EditText) findViewById(R.id.user_name);
        a.setStartOffset(delay*1000);
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
        mUsername.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||actionId == EditorInfo.IME_ACTION_DONE || event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {


                        return true;
                    }
                }
                return false;
            }
        });

    }
}
