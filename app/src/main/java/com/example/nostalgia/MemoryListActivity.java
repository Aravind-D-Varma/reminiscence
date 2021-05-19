package com.example.nostalgia;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MemoryListActivity extends SingleFragmentActivity implements MemoryListFragment.Callbacks, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mABDrawerToggle;
    NavigationView mNavigationView;
    @Override
    protected Fragment createFragment() {
        return new MemoryListFragment();
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

        mNavigationView = findViewById(R.id.navigation_view);
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
            case R.id.studentlife:
                Toast.makeText(this, "Clicked Student Life", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.work:
                Toast.makeText(this, "Clicked Work", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.home:
                Toast.makeText(this, "Clicked Home", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.birthday:
                Toast.makeText(this, "Clicked Birthday", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.hangouts:
                Toast.makeText(this, "Clicked Hangouts", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.festival:
                Toast.makeText(this, "Clicked Festival", Toast.LENGTH_SHORT).show();
                return true;
        }
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
}