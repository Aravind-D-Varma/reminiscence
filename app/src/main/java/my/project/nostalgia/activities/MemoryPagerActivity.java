package my.project.nostalgia.activities;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.R;
import my.project.nostalgia.fragments.MemoryFragment;
import my.project.nostalgia.supplementary.changeTheme;

import java.util.List;
import java.util.UUID;
/**
 * Contains ViewPager and record of all Memories.<br>
 * Enables users to swipe left or right for next or before Memory
 */
public class MemoryPagerActivity extends AppCompatActivity implements MemoryFragment.Callbacks {

    private static final String EXTRA_memory_ID = "my.project.memory_id";
    private static ViewPager2 mViewPager2;
    private List<Memory> mMemories;
    /**
     * Not sure. To be inspected
     */
    @Override
    public void onMemoryUpdated(Memory Memory) {
    }
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
        new changeTheme(this).setUserTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_pager_layout);
        mViewPager2 = findViewById(R.id.memory_viewpager2);
        mMemories = MemoryLab.get(this).getMemories();

        mViewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Memory memory = mMemories.get(position);
                return MemoryFragment.newInstance(memory.getId());
            }
            @Override
            public int getItemCount() {
                return mMemories.size();
            }
        });
        getCurrentPosition();
    }
    /**
     * Sets the viewPager to show selected Memory. Else, it will show from the first memory every time.
     */
    public void getCurrentPosition() {
        UUID memoryId = (UUID) getIntent().getSerializableExtra(EXTRA_memory_ID);
        int i = 0;
        for (Memory memory:mMemories) {
            if (memory.getId().equals(memoryId)) {
                mViewPager2.setCurrentItem(i,false);
                break;
            }
            i++;
        }
    }
    /**Ensures that the keyboard goes down if user presses anywhere outside the edit text box.
     * Applicable to all edittexts in this activity*/
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(v instanceof EditText){
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)ev.getRawX(),(int)ev.getRawY())){
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
