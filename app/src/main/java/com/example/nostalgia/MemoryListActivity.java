package com.example.nostalgia;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class MemoryListActivity extends SingleFragmentActivity implements MemoryListFragment.Callbacks {
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
    }
}
