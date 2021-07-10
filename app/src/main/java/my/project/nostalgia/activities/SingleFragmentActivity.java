package my.project.nostalgia.activities;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import my.project.nostalgia.R;

/**
 * Extend this if an activity needs a fragment on its own. Creates a fragment and adds it to FragmentManager.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getlayoutresID());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    /**
     * Depending on the screen size, i.e, phone/tablet, this will (should) return the relevant user interface.u
     */
    @LayoutRes
    protected int getlayoutresID() {
        return R.layout.activity_main;
    }
}
