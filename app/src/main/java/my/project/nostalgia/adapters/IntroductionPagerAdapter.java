package my.project.nostalgia.adapters;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import my.project.nostalgia.activities.MemoryListActivity;
import my.project.nostalgia.R;
import my.project.nostalgia.supplementary.memoryEvents;

import java.util.UUID;

import static my.project.nostalgia.activities.IntroductionActivity.APPLICABLE_EVENTS;
import static my.project.nostalgia.activities.IntroductionActivity.FIRST_TIME;
import static my.project.nostalgia.activities.IntroductionActivity.SEND_USERNAME;
import static my.project.nostalgia.activities.IntroductionActivity.USER_ID;

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
    /**
     * Inflate three layouts depending on where the user is at.
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.introduction_pages,container,false);

        switch(position){
            case 0:
                LinearLayout welcomeLL = getLayoutAndFix(v, R.id.welcome);
                container.addView(welcomeLL);
                return welcomeLL;
            case 1:
                LinearLayout questionsLL = getLayoutAndFix(v, R.id.questions);
                mUsername = (EditText) questionsLL.findViewById(R.id.user_name);
                container.addView(questionsLL);
                return questionsLL;
            case 2:
                ConstraintLayout conclusionLL = v.findViewById(R.id.conclusion);
                if (conclusionLL.getParent() != null) {
                    ((ViewGroup) conclusionLL.getParent()).removeView(conclusionLL); // <- fix
                }
                setContinuebutton(conclusionLL);
                container.addView(conclusionLL);
                return conclusionLL;
        }
        return v;
    }

    /**
     * Initialises layout depending on where the user is in viewPager.
     * Fixes IllegalStateException: the child already has parent
     */
    private LinearLayout getLayoutAndFix(View v, int LinearLayoutID) {
        LinearLayout welcomLL = v.findViewById(LinearLayoutID);
        if (welcomLL.getParent() != null) {
            ((ViewGroup) welcomLL.getParent()).removeView(welcomLL); // <- fix
        }
        return welcomLL;
    }
    /**
     * Three layouts: welcome, username and continue
     * @return
     */
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    /**
     * important to remove the super.destoryItem method
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }

    private void setContinuebutton(View v) {
        Button mContinue = (Button)v.findViewById(R.id.continue_button);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String combinedEvents = (new memoryEvents(mContext)).getJoinedEvents();
                String userName = mUsername.getText().toString();

                setGeneralInfo(userName, combinedEvents);

                Intent intent = new Intent(mContext, MemoryListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
            }
        });
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
}
