package com.example.nostalgia;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nostalgia.SettingsAdapter;
import java.util.LinkedList;
import java.util.List;

/**
 * Setup of allowing users to change their name or their event preferences.
 */
public class UserSettingsActivity extends AppCompatActivity {
    private EditText mUsername;
    private ListView mListView;
    private SettingsAdapter mSettingsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.settings));
        setContentView(R.layout.settings_all);
        mListView = (ExpandableListView) findViewById(R.id.expandable_listview);
        mSettingsAdapter = new SettingsAdapter(this);
        mListView.setAdapter((ListAdapter) mSettingsAdapter);
    }

}