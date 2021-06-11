package com.example.nostalgia.adapters;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.nostalgia.activities.MemoryListActivity;
import com.example.nostalgia.R;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.example.nostalgia.activities.IntroductionActivity.APPLICABLE_EVENTS;
import static com.example.nostalgia.activities.IntroductionActivity.FIRST_TIME;
import static com.example.nostalgia.activities.IntroductionActivity.SEND_USERNAME;
import static com.example.nostalgia.activities.IntroductionActivity.USER_ID;

/**
 * Setup of IntroductionActivity viewPager adapter. Shows layout depending on where the user is at.<br>
 * Finally, gets and saves user details.
 */

public class IntroductionPagerAdapter extends PagerAdapter {

    Context mContext;
    private EditText mUsername;
    public IntroductionPagerAdapter(Context context){
        this.mContext = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.introduction,container,false);

        switch(position){
            case 0:
                LinearLayout welcomeLL = getLayoutAndFix(v, R.id.welcome);
                setText(welcomeLL, R.id.welcome_text, 1000);
                setText(welcomeLL, R.id.intro, 3000);
                container.addView(welcomeLL);
                return welcomeLL;
            case 1:
                LinearLayout questionsLL = getLayoutAndFix(v, R.id.questions);
                mUsername = (EditText) questionsLL.findViewById(R.id.user_name);
                container.addView(questionsLL);
                return questionsLL;
            case 2:
                LinearLayout conclusionLL = getLayoutAndFix(v, R.id.conclusion);
                setContinuebutton(conclusionLL);
                container.addView(conclusionLL);
                return conclusionLL;
        }
        return v;
    }

    /**
     * Initialises layout depending on where the user is in viewPager.
     * Fixes IllegalStateException: the child already has parent
     * @param v
     * @param LinearLayoutID
     * @return
     */
    private LinearLayout getLayoutAndFix(View v, int LinearLayoutID) {
        LinearLayout welcomLL = v.findViewById(LinearLayoutID);
        if (welcomLL.getParent() != null) {
            ((ViewGroup) welcomLL.getParent()).removeView(welcomLL); // <- fix
        }
        return welcomLL;
    }

    @Override
    public int getCount() {
        return 3;
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

    /**
     * Concatenates all events which are applicable into one string so that it can be stored in SharedPreferences.
     * @return
     */
    private String joinAllApplicableEvents() {
        List<String> allEvents = new LinkedList<String>();

        allEvents = initializeListOfEvents(allEvents);

        return stringListToString(allEvents);
    }

    /**
     * Sets up the username and applicable events for access across application by using SharedPreferences.
     * @param userName
     * @param combinedEvents
     */
    private void setGeneralInfo(String userName, String combinedEvents) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putBoolean(FIRST_TIME,true);
        editor.putString(SEND_USERNAME, userName);
        editor.putString(APPLICABLE_EVENTS, combinedEvents);
        editor.putString(USER_ID, UUID.randomUUID().toString());
        editor.apply();
    }

    private List<String> initializeListOfEvents(List<String> allEvents) {
        allEvents.add(mContext.getResources().getString(R.string.student_life));
        allEvents.add(mContext.getResources().getString(R.string.home));
        allEvents.add(mContext.getResources().getString(R.string.hangouts));
        allEvents.add(mContext.getResources().getString(R.string.celebrations));

        return allEvents;
    }

    private String stringListToString(List<String> allEvents) {
        String[] applicableEvents = {};
        applicableEvents = allEvents.toArray(applicableEvents);
        StringBuilder combinedEvents = new StringBuilder();
        for (int i = 0; i < applicableEvents.length; i++)
            combinedEvents.append(applicableEvents[i]).append(",");

        return combinedEvents.toString();
    }

}
