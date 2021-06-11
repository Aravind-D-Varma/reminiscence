package com.example.nostalgia.activities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.nostalgia.R;
import com.example.nostalgia.activities.MemoryListActivity;
import com.example.nostalgia.fragments.UserSettingsFragment;

import java.util.Objects;

/**
 * Setup of allowing users to change their name, their event preferences and tells about me.
 */
public class UserSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new UserSettingsFragment()).commit();
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.settings));

    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String themeValues = getData.getString("GlobalTheme", "Dark");

        if (themeValues.equals("Dark"))
            theme.applyStyle(R.style.Theme_Reminiscence, true);

        if (themeValues.equals("Light"))
            theme.applyStyle(R.style.Theme_Reminiscence_Light, true);

        return theme;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MemoryListActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}