package com.example.nostalgia;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

/**
 * Appears when the user visits the app for the first time.<br>
 * Creates a viewPager to show what the user sees and sets it up to its adapter.<br>
 * Uses TabLayout to show dots indicating where the user is in the viewPager.
 */
public class IntroductionActivity extends AppCompatActivity {

    public static final String FIRST_TIME = "firsttime";
    public static final String SEND_USERNAME= "username";
    public static final String APPLICABLE_EVENTS = "true_events";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduction);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sp.getBoolean(FIRST_TIME, false)) {
            Intent intent = MemoryListActivity.newIntent(getApplicationContext());
            startActivity(intent);
        } else {
            PagerAdapterIntroduction pagerAdapterIntroduction = new PagerAdapterIntroduction(this);
            ViewPager pager = findViewById(R.id.pager);
            pager.setAdapter(pagerAdapterIntroduction);
            TabLayout tabLayout = findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(pager,true);
        }

    }
}
