package com.example.nostalgia;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGeneralInfo();

        mNavigationView = findViewById(R.id.navigation_view);

        setHeaderWelcomeUser(userName);

        drawerAndToggle();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getGeneralInfo();
        setHeaderWelcomeUser(userName);
    }

    private void setHeaderWelcomeUser(String userName) {
        View headerView = mNavigationView.getHeaderView(0);
        mHeaderText = headerView.findViewById(R.id.nav_header_textView);
        mHeaderText.setText("Welcome "+userName);
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
        switch (item.getItemId()){
            case R.id.all:
                return filterOnSelected(R.string.all);
            case R.id.studentlife:
                return filterOnSelected(R.string.studentlife);
            case R.id.work:
                return filterOnSelected(R.string.work);
            case R.id.home:
                return filterOnSelected(R.string.home);
            case R.id.birthday:
                return filterOnSelected(R.string.birthday);
            case R.id.hangouts:
                return filterOnSelected(R.string.hangouts);
            case R.id.festival:
                return filterOnSelected(R.string.festival);
            case R.id.add_custom_event:
                String newEvent = createEventDialogBox();
                addEventToMenu(newEvent);
                return true;
            case R.id.user_settings:
                goToSettings();

        }
        return true;
    }

    private void addEventToMenu(String newEvent) {
        Menu eventMenu = mNavigationView.getMenu();
        eventMenu.add(R.id.events,Menu.NONE,1,newEvent);
    }

    private void goToSettings() {
        Intent intent = new Intent(MemoryListActivity.this, UserSettingsActivity.class);
        startActivity(intent);
    }

    private String createEventDialogBox() {
        AlertDialog.Builder inputEventDialog = new AlertDialog.Builder(this);
        inputEventDialog.setTitle("New Custom Event");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_NORMAL);
        inputEventDialog.setView(input);

        inputEventDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                saveNewEvent(input);
                dialog.dismiss();
            }
        });
        inputEventDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        inputEventDialog.show();
        return input.getText().toString();
    }

    private void saveNewEvent(EditText input) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        String currentEvents = prefs.getString(Introduction.APPLICABLE_EVENTS, "");
        List<String> wordList = new ArrayList<String>(Arrays.asList(currentEvents.split(",")));
        wordList.add(input.getText().toString());
        editor.putString(Introduction.APPLICABLE_EVENTS,stringListToString(wordList));
        editor.apply();
    }

    private String stringListToString(List<String> allEvents) {
        String[] applicableEvents = {};
        applicableEvents = allEvents.toArray(applicableEvents);
        StringBuilder combinedEvents = new StringBuilder();
        for (int i = 0; i < applicableEvents.length; i++)
            combinedEvents.append(applicableEvents[i]).append(",");

        return combinedEvents.toString();
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
        userName = preferences.getString(Introduction.SEND_USERNAME,"");
        String userevents = preferences.getString(Introduction.APPLICABLE_EVENTS, "");
        applicableEvents = userevents.split(",");
    }

}