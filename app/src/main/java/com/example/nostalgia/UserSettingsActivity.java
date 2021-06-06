package com.example.nostalgia;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * Setup of allowing users to change their name, their event preferences and tells about me.
 */
public class UserSettingsActivity extends AppCompatActivity {
    private EditText mUsername;
    private ExpandableListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.settings));
        setContentView(R.layout.settings_all);
        mListView = (ExpandableListView) findViewById(R.id.expandable_listview);
        UserSettingsAdapter mUserSettingsAdapter = new UserSettingsAdapter(this);
        mListView.setAdapter((ExpandableListAdapter) mUserSettingsAdapter);

    }
}