package my.project.nostalgia.activities;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import my.project.nostalgia.R;

import my.project.nostalgia.fragments.UserSettingsFragment;
import my.project.nostalgia.supplementary.changeTheme;

import java.util.Objects;
/**
 * Setup of allowing users to change their name, their event preferences and tells about me.
 */
public class UserSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new changeTheme(this).setUserTheme();
        setContentView(R.layout.activity_fragment);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new UserSettingsFragment()).commit();
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.settings));
    }
    /**Refreshes previous activity so that if the user changes theme/language, it is reflected.
     * If not refreshed, it will show previous theme/language*/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MemoryListActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}