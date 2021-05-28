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
    private List<String> allEvents = new LinkedList<String>();
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
                Animation a = AnimationUtils.loadAnimation(mContext, R.anim.introduction);
                Animation b = AnimationUtils.loadAnimation(mContext, R.anim.introduction);
                LinearLayout welcomLL = v.findViewById(R.id.welcome);
                if(welcomLL.getParent() != null) {
                    ((ViewGroup)welcomLL.getParent()).removeView(welcomLL); // <- fix
                }
                TextView welcome = (TextView) welcomLL.findViewById(R.id.welcome_text);
                a.setStartOffset(1000);welcome.startAnimation(a);
                TextView brief = (TextView) welcomLL.findViewById(R.id.intro);
                b.setStartOffset(3000);brief.startAnimation(b);
                container.addView(welcomLL);
                return welcomLL;
            case 1:
                LinearLayout questionsLL = v.findViewById(R.id.questions);
                if(questionsLL.getParent() != null) {
                    ((ViewGroup)questionsLL.getParent()).removeView(questionsLL); // <- fix
                }
                setUserName(questionsLL);
                container.addView(questionsLL);
                return questionsLL;
            case 2:
                LinearLayout eventsLL = v.findViewById(R.id.event_preferences);
                if(eventsLL.getParent() != null) {
                    ((ViewGroup)eventsLL.getParent()).removeView(eventsLL); // <- fix
                }
                setRadioButtons(eventsLL);
                container.addView(eventsLL);
                return eventsLL;

            case 3:
                LinearLayout conclusionLL = v.findViewById(R.id.conclusion);
                if(conclusionLL.getParent() != null) {
                    ((ViewGroup)conclusionLL.getParent()).removeView(conclusionLL); // <- fix
                }
                setContinuebutton(conclusionLL);
                container.addView(conclusionLL);
                return conclusionLL;
        }
        return v;
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
        allEvents.add("Student Life");
        allEvents.add("Work");
        allEvents.add("Festivals");
        allEvents.add("Home");
        allEvents.add("Birthdays");
        allEvents.add("Hangouts");
        Button mContinue = (Button)v.findViewById(R.id.continue_button);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mUserYesStudent.isChecked())
                    allEvents.remove("Student Life");
                if (!mUserYesWorked.isChecked())
                    allEvents.remove("Work");
                if (!mUserYesReligion.isChecked())
                    allEvents.remove("Festivals");

                String[] applicableEvents = {};
                applicableEvents = allEvents.toArray(applicableEvents);
                StringBuilder combinedEvents = new StringBuilder();
                for (int i = 0; i < applicableEvents.length; i++)
                    combinedEvents.append(applicableEvents[i]).append(",");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                editor.putBoolean(FIRST_TIME,true);
                editor.putString(SEND_USERNAME, mUsername.getText().toString());
                editor.putString(APPLICABLE_EVENTS, combinedEvents.toString());
                editor.apply();
                Intent intent = MemoryListActivity.newIntent(mContext);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    private void setRadioButtons(View v) {
        mUserYesStudent = (RadioButton) v.findViewById(R.id.yes_student);
        mUserYesWorked = (RadioButton) v.findViewById(R.id.yes_worked);
        mUserYesReligion = (RadioButton) v.findViewById(R.id.yes_religious);
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
}
