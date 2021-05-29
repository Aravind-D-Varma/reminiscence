package com.example.nostalgia;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedList;
import java.util.List;

public class Introduction extends AppCompatActivity {

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
            IntroPagerAdapter introPagerAdapter = new IntroPagerAdapter(getApplicationContext());
            ViewPager pager = findViewById(R.id.pager);
            pager.setAdapter(introPagerAdapter);
            TabLayout tabLayout = findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(pager,true);
        }

    }
}
