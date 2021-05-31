package com.example.nostalgia;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.List;
import java.util.UUID;

/**
 * Contains ViewPager and record of all Memories.<br>
 * Enables users to swipe left or right for next or before Memory
 */
public class MemoryPagerActivity extends AppCompatActivity {

    //region Declarations
    private static final String EXTRA_memory_ID = "com.example.criminalintent.memory_id";
    private static ViewPager mViewPager;
    private List<Memory> mMemories;
    public String[] applicableEvents;
    //endregion

    public static Intent newIntent(Context packageContext, UUID memoryId){
        Intent intent = new Intent(packageContext, MemoryPagerActivity.class);
        intent.putExtra(EXTRA_memory_ID, memoryId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_pager);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String combinedEvents= preferences.getString(Introduction.APPLICABLE_EVENTS, "");
        applicableEvents = combinedEvents.split(",");
        mViewPager = (ViewPager) findViewById(R.id.memory_view_pager);
        mMemories = MemoryLab.get(this).getMemories();
        FragmentManager fragmentManager = getSupportFragmentManager();

        //region setAdapter
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager){
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Memory memory = mMemories.get(position);
                return MemoryFragment.newInstance(memory.getId());
            }
            @Override
            public int getCount() {
                return mMemories.size();
            }
        });
        //endregion
        getCurrentPosition();
    }

    /**
     * Sets the viewPager to show selected Memory. Else, it will show from the first memory every time.
     */
    public void getCurrentPosition() {
        UUID memoryId = (UUID) getIntent().getSerializableExtra(EXTRA_memory_ID);
        for (int i = 0; i < mMemories.size(); i++) {
            if (mMemories.get(i).getId().equals(memoryId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
