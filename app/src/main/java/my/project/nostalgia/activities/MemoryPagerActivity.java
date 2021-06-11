package my.project.nostalgia.activities;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.R;
import my.project.nostalgia.fragments.MemoryFragment;

import java.util.List;
import java.util.UUID;

/**
 * Contains ViewPager and record of all Memories.<br>
 * Enables users to swipe left or right for next or before Memory
 */
public class MemoryPagerActivity extends AppCompatActivity implements MemoryFragment.Callbacks {

    //region Declarations
    private static final String EXTRA_memory_ID = "com.example.criminalintent.memory_id";
    private static ViewPager mViewPager;
    private List<Memory> mMemories;
    public String[] applicableEvents;

    @Override
    public void onMemoryUpdated(Memory Memory) {

    }
    //endregion

    /**
     * Starts this ViewPager activity from MemoryListActivity if device is phone
     * @see MemoryListActivity#onMemorySelected(Memory)
     */
    public static Intent newIntent(Context packageContext, UUID memoryId){
        Intent intent = new Intent(packageContext, MemoryPagerActivity.class);
        intent.putExtra(EXTRA_memory_ID, memoryId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_pager);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String combinedEvents= preferences.getString(IntroductionActivity.APPLICABLE_EVENTS, "");
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
}
