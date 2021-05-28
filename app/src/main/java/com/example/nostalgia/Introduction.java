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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

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
        setContentView(R.layout.introduction);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sp.getBoolean(FIRST_TIME, false)) {
            Intent intent = MemoryListActivity.newIntent(getApplicationContext());
            startActivity(intent);
        } else {
            IntroPagerAdapter introPagerAdapter = new IntroPagerAdapter(getApplicationContext(),getParent());
            ViewPager pager = findViewById(R.id.pager);
            pager.setAdapter(introPagerAdapter);
        }

    }
   /* private void setContinue(Animation a,int delay) {
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
                        int[] introButtonViews = {R.id.student_text,R.id.user_student,R.id.work_text,R.id.user_working
                                ,R.id.religion_text,R.id.user_religious};
                        int delay = 1;Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.introduction);
                        RadioGroup muserstudent, muserwork, muserreligion;
                        muserstudent = (RadioGroup) findViewById(R.id.user_student);
                        muserwork = (RadioGroup) findViewById(R.id.user_working);
                        muserreligion = (RadioGroup) findViewById(R.id.user_religious);
                        for (int viewID:introButtonViews){
                            switch (viewID){
                                case R.id.student_text:
                                    setText(a, R.id.student_text, delay);
                                    break;
                                case R.id.user_student:
                                    delay = delay + 5;
                                    a.setStartOffset(delay * 1000);
                                    muserstudent.startAnimation(a);
                                    mUserYesStudent = (RadioButton) findViewById(R.id.yes_student);
                                    break;
                                case R.id.work_text:
                                    setText(a, R.id.work_text, delay);
                                    break;
                                case R.id.user_working:
                                    delay = delay + 5;
                                    a.setStartOffset(delay * 1000);muserwork.startAnimation(a);
                                    mUserYesWorked = (RadioButton) findViewById(R.id.yes_worked);
                                    break;
                                case R.id.religion_text:
                                    delay = delay + 5;
                                    setText(a, R.id.religion_text, delay);
                                    break;
                                case R.id.user_religious:
                                    delay = delay + 5;
                                    a.setStartOffset(delay * 1000);muserreligion.startAnimation(a);
                                    mUserYesReligious = (RadioButton) findViewById(R.id.yes_religious);
                                    break;
                            }
                            delay++;
                        }
                        if(muserstudent.getCheckedRadioButtonId()!=-1 && muserwork.getCheckedRadioButtonId()!=-1
                        && muserreligion.getCheckedRadioButtonId()!=-1){
                            Animation ab = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.introduction);
                            delay = 1;
                            setText(a,R.id.thankyou_text,delay);
                            delay++;
                            setContinue(a,delay);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }*/
}
