package my.project.nostalgia.activities;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import my.project.nostalgia.models.Memory;
import my.project.nostalgia.R;
import my.project.nostalgia.fragments.MemoryFragment;
import my.project.nostalgia.fragments.MemoryListFragment;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.supplementary.changeTheme;
import my.project.nostalgia.supplementary.memoryEvents;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static my.project.nostalgia.fragments.MemoryListFragment.MEMORIES_KEY;
//TODO Accessing hidden method Landroid/database/sqlite/SQLiteDatabase
/**
 * Contains the Navigation Drawer containing a welcome text, event lists and settings to change these two.<br>
 * Displays the list of memories user has added in its own fragment MemoryListFragment
 * @see MemoryListFragment
 */
@SuppressWarnings("JavaDoc")
public class MemoryListActivity extends SingleFragmentActivity
        implements MemoryListFragment.Callbacks, MemoryFragment.Callbacks, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private MemoryListFragment MLfragment;
    private String userName;
    private changeTheme mTheme;

    /**
     * Create a fragment which contains a list of memories (using recycler view).
     * Updates UI (displays all memories and if its a tablet shows additional memory if about to be added).
     * @see MemoryListFragment
     * @see MemoryListFragment#updateUI()
     */
    @Override
    public void onMemoryUpdated(Memory Memory) {
        MemoryListFragment listFragment = (MemoryListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        if (listFragment != null) {
            listFragment.updateUI();
        }
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
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = new changeTheme(this);
        mTheme.setUserTheme();
        super.onCreate(savedInstanceState);

        mNavigationView = findViewById(R.id.navigation_view);
        mTheme.colorToNavigationIcons(mNavigationView);

        getGeneralInfo();
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
        TextView headerText = headerView.findViewById(R.id.nav_header_textView);
        headerText.setText(String.format("%s %s!",
                getResources().getString(R.string.welcome).substring(0,7), userName));
        mTheme.setNavigationHeaderTheme(headerView, headerText);
    }
    private void drawerAndToggle() {
        mDrawerLayout = findViewById(R.id.main_drawerLayout);
        mNavigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle ABDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.NDopen, R.string.NDclose);
        mDrawerLayout.addDrawerListener(ABDrawerToggle);
        ABDrawerToggle.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ABDrawerToggle.syncState();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String[] currentEvents = new memoryEvents(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())).getIndividualEvents();

        if(item.getItemId() == R.id.all ){
            filterEvent(getString(R.string.all));
            return true;
        }
        else if (item.getItemId() == R.id.user_settings )
            goToSettings();
        else if (item.getItemId() == R.id.userprofile_save)
            saveDataToProfile();
        else if (item.getItemId() == R.id.note_developer)
            showNote();
        else {
            filterEvent(currentEvents[item.getItemId()]);
            return true;
        }
        return true;
    }

    private void showNote() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this
                ,new changeTheme(this).setDialogTheme());
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.minute_please);
        alertBuilder.setMessage(R.string.lengthy_note_developer);
        alertBuilder.setPositiveButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void saveDataToProfile() {
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocument = FirebaseFirestore.getInstance().collection("Users")
                .document(userid);
        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.show();
        Map<String, List<Memory>> dataToSave = new HashMap<>();
        dataToSave.put(MEMORIES_KEY, MemoryLab.get(this).getMemories());
        userDocument.set(dataToSave).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        });
    }

    private void filterEvent(String currentEvent) {
        MLfragment.eventFilter(currentEvent);
        onBackPressed();
    }

    /**
     * Gets a string array of applicable Events and adds them as menu items.
     * These items have ids starting from zero and increment till size of array.
     */
    private void showMenuEvents() {
        String[] currentEvents = new memoryEvents(getApplicationContext(),PreferenceManager.getDefaultSharedPreferences(getApplicationContext()))
                .getIndividualEvents();
        Menu eventMenu = mNavigationView.getMenu();
        SubMenu subMenu = eventMenu.getItem(0).getSubMenu();
        subMenu.removeGroup(R.id.events);
        int menuID = 0;
        for (String string:currentEvents){
            subMenu.add(R.id.events, menuID, 1, string);
            menuID++;
        }
    }
    private void goToSettings() {
        Intent intent = new Intent(this, UserSettingsActivity.class);
        startActivity(intent);
        this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            return closeAndOpenDrawer();
        }
        return super.onOptionsItemSelected(item);
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
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userName = pref.getString(LoginActivity.SEND_USERNAME,"");
        String languages = pref.getString(LoginActivity.LANGUAGE, "English");
        if (languages.equals("English")){
            setLanguage("en");
        }
        else{
            setLanguage("nl");
        }
    }
    private void setLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().
                updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

}