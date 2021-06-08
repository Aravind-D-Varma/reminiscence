package com.example.nostalgia;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

/**
 * Contains the Navigation Drawer containing a welcome text, event lists and settings to change these two.<br>
 * Displays the list of memories user has added in its own fragment MemoryListFragment
 */
public class MemoryListActivity extends SingleFragmentActivity
        implements MemoryListFragment.Callbacks, MemoryFragment.Callbacks, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mABDrawerToggle;
    NavigationView mNavigationView;
    public MemoryListFragment MLfragment;
    private TextView mHeaderText;
    private String userName;
    public String[] applicableEvents = {};

    @Override
    public void onMemoryUpdated(Memory Memory) {
        MemoryListFragment listFragment = (MemoryListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        if(isDeviceTablet())
            listFragment.updateUIForTablet();
        else
            listFragment.updateUI();
    }

    private boolean isDeviceTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MemoryListActivity.class);
        return intent;
    }

    @Override
    protected int getlayoutresID() {
        return R.layout.activity_masterdetail;
    }

    /**
     * If device is phone, start new intent when a memory is selected. Else, show it on right part of Tablet
     * Declared from MemoryListFragment only.
     * @see MemoryListFragment
     * @param memory
     */
    @Override
    public void onMemorySelected(Memory memory) {
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = MemoryPagerActivity.newIntent(this, memory.getId());
            startActivity(intent);
        }
        else{
            Fragment newDetail = MemoryFragment.newInstance(memory.getId());
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, newDetail).commit();
        }
    }

    @Override
    protected Fragment createFragment() {
        MLfragment = new MemoryListFragment();
        return MLfragment;
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String themeValues = getData.getString("GlobalTheme", "Dark");

        if (themeValues.equals("Dark"))
            theme.applyStyle(R.style.Theme_Reminiscence, true);

        if (themeValues.equals("Light"))
            theme.applyStyle(R.style.Theme_Reminiscence_Light, true);

        return theme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGeneralInfo();
        mNavigationView = findViewById(R.id.navigation_view);

        int colorInt;
        SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String themeValues = getData.getString("GlobalTheme", "Dark");
        if (themeValues.equals("Dark"))
            colorInt = getResources().getColor(R.color.white);
        else
            colorInt = getResources().getColor(R.color.black);
        ColorStateList csl = ColorStateList.valueOf(colorInt);
        mNavigationView.setItemIconTintList(csl);

        setHeaderWelcomeUser(userName);
        showMenuEvents();
        drawerAndToggle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGeneralInfo();
        setHeaderWelcomeUser(userName);
        showMenuEvents();
    }

    private void setHeaderWelcomeUser(String userName) {
        View headerView = mNavigationView.getHeaderView(0);

        mHeaderText = headerView.findViewById(R.id.nav_header_textView);
        mHeaderText.setText("Welcome "+userName);
        SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String themeValues = getData.getString("GlobalTheme", "Dark");

        if (themeValues.equals("Dark")) {
            headerView.setBackgroundColor(getResources().getColor(R.color.purple_700));
            mHeaderText.setTextColor(getResources().getColor(R.color.black));
        }
        if (themeValues.equals("Light")){
            headerView.setBackgroundColor(getResources().getColor(R.color.purple_200));
            mHeaderText.setTextColor(getResources().getColor(R.color.white));
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String[] currentEvents = prefs.getString(IntroductionActivity.APPLICABLE_EVENTS, "").split(",");

        if(item.getItemId() == R.id.all )
            return filterOnSelected(R.string.all);
        else if (item.getItemId() == R.id.user_settings )
                goToSettings();
        else {
            MLfragment.eventFilter(currentEvents[item.getItemId()]);
            onBackPressed();
            return true;
        }
        return true;
    }

    /**
     * Gets a string array of applicable Events and adds them as menu items.
     * These items have ids starting from zero and increment till size of array.
     */
    private void showMenuEvents() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String[] currentEvents = prefs.getString(IntroductionActivity.APPLICABLE_EVENTS, "").split(",");
        Menu eventMenu = mNavigationView.getMenu();
        eventMenu.removeGroup(R.id.events);
        int menuID = 0;
        for (String string:currentEvents){
            eventMenu.add(R.id.events,menuID,1,string);
            menuID++;
        }
    }

    private void goToSettings() {
        Intent intent = new Intent(MemoryListActivity.this, UserSettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Updates list of memories depending on what the user selected in menu of Navigation Drawer
     * @param NavigationItem
     * @return
     */
    private boolean filterOnSelected(int NavigationItem) {
        MLfragment.eventFilter(getString(NavigationItem));
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                return closeAndOpenDrawer();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean closeAndOpenDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
            mDrawerLayout.openDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void getGeneralInfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userName = preferences.getString(IntroductionActivity.SEND_USERNAME,"");
        String userevents = preferences.getString(IntroductionActivity.APPLICABLE_EVENTS, "");
        applicableEvents = userevents.split(",");
    }
}