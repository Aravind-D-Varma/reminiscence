package com.example.nostalgia;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.LinkedList;
import java.util.List;

import static com.example.nostalgia.Introduction.APPLICABLE_EVENTS;
import static com.example.nostalgia.Introduction.FIRST_TIME;
import static com.example.nostalgia.Introduction.SEND_USERNAME;

public class IntroPagerAdapter extends PagerAdapter {

    Context mContext;
    private EditText mUsername;
    private RadioButton mUserYesStudent,mUserYesWorked, mUserYesReligion;
    public IntroPagerAdapter(Context context){
        this.mContext = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.introduction,container,false);

        switch(position){
            case 0:
                LinearLayout welcomLL = getLayoutAndFix(v, R.id.welcome);
                setText(welcomLL, R.id.welcome_text, 1000);
                setText(welcomLL, R.id.intro, 3000);
                container.addView(welcomLL);
                return welcomLL;
            case 1:
                LinearLayout questionsLL = getLayoutAndFix(v, R.id.questions);
                setUserName(questionsLL);
                container.addView(questionsLL);
                return questionsLL;
            case 2:
                LinearLayout eventsLL = getLayoutAndFix(v, R.id.event_preferences);
                setRadioButtons(eventsLL);
                container.addView(eventsLL);
                return eventsLL;

            case 3:
                LinearLayout conclusionLL = getLayoutAndFix(v, R.id.conclusion);
                setContinuebutton(conclusionLL);
                container.addView(conclusionLL);
                return conclusionLL;
        }
        return v;
    }

    private LinearLayout getLayoutAndFix(View v, int LinearLayoutID) {
        LinearLayout welcomLL = v.findViewById(LinearLayoutID);
        if (welcomLL.getParent() != null) {
            ((ViewGroup) welcomLL.getParent()).removeView(welcomLL); // <- fix
        }
        return welcomLL;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }
    private void setText(LinearLayout welcomLL, int textViewID, int delay) {
        Animation a = AnimationUtils.loadAnimation(mContext, R.anim.introduction);
        TextView welcome = (TextView) welcomLL.findViewById(textViewID);
        a.setStartOffset(delay);
        welcome.startAnimation(a);
    }

    private void setRadioButtons(View v) {
        mUserYesStudent = (RadioButton) v.findViewById(R.id.yes_student);
        mUserYesWorked = (RadioButton) v.findViewById(R.id.yes_worked);
        mUserYesReligion = (RadioButton) v.findViewById(R.id.yes_religious);
    }

    private void setUserName(View v) {
        mUsername = (EditText) v.findViewById(R.id.user_name);
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
    }

    private void setContinuebutton(View v) {
        Button mContinue = (Button)v.findViewById(R.id.continue_button);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String combinedEvents = joinAllApplicableEvents();
                String userName = mUsername.getText().toString();

                setGeneralInfo(userName, combinedEvents);

                Intent intent = MemoryListActivity.newIntent(mContext);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
            }
        });
    }

    private void setGeneralInfo(String userName, String combinedEvents) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putBoolean(FIRST_TIME,true);
        editor.putString(SEND_USERNAME, userName);
        editor.putString(APPLICABLE_EVENTS, combinedEvents);
        editor.apply();
    }

    private String joinAllApplicableEvents() {
        List<String> allEvents = new LinkedList<String>();

        allEvents = initializeListOfEvents(allEvents);
        allEvents = cutUnecessaryEvents(allEvents);

        return stringListToString(allEvents);
    }

    private String stringListToString(List<String> allEvents) {
        String[] applicableEvents = {};
        applicableEvents = allEvents.toArray(applicableEvents);
        StringBuilder combinedEvents = new StringBuilder();
        for (int i = 0; i < applicableEvents.length; i++)
            combinedEvents.append(applicableEvents[i]).append(",");

        return combinedEvents.toString();
    }

    private List<String> initializeListOfEvents(List<String> allEvents) {
        allEvents.add("Student Life");
        allEvents.add("Work");
        allEvents.add("Festivals");
        allEvents.add("Home");
        allEvents.add("Birthdays");
        allEvents.add("Hangouts");

        return allEvents;
    }

    private List<String> cutUnecessaryEvents(List<String> allEvents) {
        if (!mUserYesStudent.isChecked())
            allEvents.remove("Student Life");
        if (!mUserYesWorked.isChecked())
            allEvents.remove("Work");
        if (!mUserYesReligion.isChecked())
            allEvents.remove("Festivals");

        return allEvents;
    }


}
