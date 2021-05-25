package com.example.nostalgia;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class MemoryListActivity extends SingleFragmentActivity implements MemoryListFragment.Callbacks, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mABDrawerToggle;
    NavigationView mNavigationView;
    public MemoryListFragment MLfragment;
    private TextView mHeaderText;
    private String userName;
    private boolean[] availableEvents;

    public static Intent newIntent(Context context, String text) {
        Intent intent = new Intent(context, MemoryListActivity.class);
        intent.putExtra(Introduction.SEND_USERNAME, text);
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
        userName = getIntent().getStringExtra(Introduction.SEND_USERNAME);
        availableEvents = getIntent().getBooleanArrayExtra(Introduction.APPLICABLE_EVENTS);

        mNavigationView = findViewById(R.id.navigation_view);

        View headerView = mNavigationView.getHeaderView(0);
        mHeaderText = headerView.findViewById(R.id.nav_header_textView);
        mHeaderText.setText("Welcome "+userName);

        Menu menuNav = mNavigationView.getMenu();
        MenuItem studentmenuItem = menuNav.findItem(R.id.studentlife);
        studentmenuItem.setVisible(availableEvents[0]);
        MenuItem workmenuItem = menuNav.findItem(R.id.work);
        workmenuItem.setVisible(availableEvents[1]);
        MenuItem religionmenuItem = menuNav.findItem(R.id.festival);
        religionmenuItem.setVisible(availableEvents[2]);

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
}