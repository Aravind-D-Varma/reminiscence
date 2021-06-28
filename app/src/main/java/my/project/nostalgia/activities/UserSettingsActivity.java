package my.project.nostalgia.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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
        new changeTheme(this).setUserTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new UserSettingsFragment()).commit();
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.settings));
    }
    /**Refreshes previous activity so that if the user changes theme/language, it is reflected.
     * If not refreshed, it will show previous theme/language*/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MemoryListActivity.class));
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            startActivity(new Intent(this,MemoryListActivity.class));
        return super.onOptionsItemSelected(item);
    }
}