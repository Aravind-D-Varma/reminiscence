package com.example.nostalgia;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntToDoubleFunction;

public class MemoryListActivity extends SingleFragmentActivity implements MemoryListFragment.Callbacks, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mABDrawerToggle;
    NavigationView mNavigationView;
    public MemoryListFragment MLfragment;
    private TextView mHeaderText;
    private String userName;
    private String[] allEventpaths={};

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MemoryListActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        MLfragment = new MemoryListFragment();
        return MLfragment;
    }

    @Override
    protected int getlayoutresID() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onMemorySelected(Memory memory) {
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = MemoryPagerActivity.newIntent(this, memory.getId());
            startActivity(intent);
        }
        else{
            Fragment newDetail = new MemoryFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.detail_fragment_container, newDetail);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalInfo();

        mNavigationView = findViewById(R.id.navigation_view);

        headerandmenuSetting(userName,allEventpaths);

        drawerAndToggle();

    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalInfo();
        headerandmenuSetting(userName,allEventpaths);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.all:
                MLfragment.eventFilter(getString(R.string.all));
                onBackPressed();
                return true;
            case R.id.studentlife:
                MLfragment.eventFilter(getString(R.string.studentlife));
                onBackPressed();
                return true;
            case R.id.work:
                MLfragment.eventFilter(getString(R.string.work));
                onBackPressed();
                return true;
            case R.id.home:
                MLfragment.eventFilter(getString(R.string.home));
                onBackPressed();
                return true;
            case R.id.birthday:
                MLfragment.eventFilter(getString(R.string.birthday));
                onBackPressed();
                return true;
            case R.id.hangouts:
                MLfragment.eventFilter(getString(R.string.hangouts));
                onBackPressed();
                return true;
            case R.id.festival:
                MLfragment.eventFilter(getString(R.string.festival));
                onBackPressed();
                return true;
            case R.id.user_settings:
                Intent intent = new Intent(MemoryListActivity.this, UserSettingsActivity.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                else
                    mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void GlobalInfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userName = preferences.getString(Introduction.SEND_USERNAME,"");
        String userevents = preferences.getString(Introduction.APPLICABLE_EVENTS, "");
        allEventpaths = userevents.split(",");
    }

    private void headerandmenuSetting(String userName, String[] availableEvents) {

        List<String> list = Arrays.asList(availableEvents);

        View headerView = mNavigationView.getHeaderView(0);
        mHeaderText = headerView.findViewById(R.id.nav_header_textView);
        mHeaderText.setText("Welcome "+userName);

        Menu menuNav = mNavigationView.getMenu();
        MenuItem studentmenuItem = menuNav.findItem(R.id.studentlife);
        studentmenuItem.setVisible(list.contains("Student Life"));
        MenuItem workmenuItem = menuNav.findItem(R.id.work);
        workmenuItem.setVisible(list.contains("Work"));
        MenuItem religionmenuItem = menuNav.findItem(R.id.festival);
        religionmenuItem.setVisible(list.contains("Festivals"));
    }

    private void drawerAndToggle() {
        mDrawerLayout = findViewById(R.id.main_drawerLayout);
        mNavigationView.setNavigationItemSelectedListener(this);
        mABDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.NDopen, R.string.NDclose);
        mDrawerLayout.addDrawerListener(mABDrawerToggle);
        mABDrawerToggle.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mABDrawerToggle.syncState();
    }

}