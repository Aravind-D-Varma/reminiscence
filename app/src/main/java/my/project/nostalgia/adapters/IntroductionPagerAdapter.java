package my.project.nostalgia.adapters;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import my.project.nostalgia.R;
import my.project.nostalgia.activities.IntroductionActivity;

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
                LinearLayout welcomeLL = v.findViewById(R.id.welcome);
                if (welcomeLL.getParent() != null) {
                    ((ViewGroup) welcomeLL.getParent()).removeView(welcomeLL); // <- fix
                }
                container.addView(welcomeLL);
                return welcomeLL;
            case 1:
                ConstraintLayout conclusionLL = v.findViewById(R.id.conclusion);
                if (conclusionLL.getParent() != null) {
                    ((ViewGroup) conclusionLL.getParent()).removeView(conclusionLL); // <- fix
                }
                container.addView(conclusionLL);
                return conclusionLL;
        }
        return v;
    }
    /**Two layouts: welcome and sign in*/
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    /**important to remove the super.destoryItem method*/
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }
    /*private void setContinuebutton(View v) {
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
    }*/
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
