package my.project.nostalgia.activities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import my.project.nostalgia.R;
import my.project.nostalgia.adapters.IntroductionPagerAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * Appears when the user visits the app for the first time.<br>
 * Creates a viewPager to show what the user sees and sets it up to its adapter.<br>
 * Uses TabLayout to show dots indicating where the user is in the viewPager.
 */
public class IntroductionActivity extends AppCompatActivity {

    public static final String FIRST_TIME = "firsttime";
    public static final String USER_ID = "userid";
    public static final String SEND_USERNAME= "username";
    public static final String LANGUAGE = "GlobalLanguage";
    public static final String APPLICABLE_EVENTS = "true_events";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduction);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (prefs.getBoolean(FIRST_TIME, false)) {
            Intent intent = new Intent(this, MemoryListActivity.class);
            startActivity(intent);
        } else {
            IntroductionPagerAdapter introductionPagerAdapter = new IntroductionPagerAdapter(this);
            ViewPager pager = findViewById(R.id.pager);
            pager.setClipToPadding(false);
            pager.setPadding(20,100,20,100);
            pager.setPageMargin(20);

            pager.setAdapter(introductionPagerAdapter);
            pager.setPageTransformer(false, new ViewPager.PageTransformer() {
                @Override
                public void transformPage(@NonNull View page, float position) {
                    final float normalizedposition = Math.abs(Math.abs(position) - 1);
                    page.setScaleX(normalizedposition / 2 + 0.5f);
                    page.setScaleY(normalizedposition / 2 + 0.5f);
                }
            });
            TabLayout tabLayout = findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(pager,true);
        }
    }

}
