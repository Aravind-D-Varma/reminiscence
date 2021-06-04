package com.example.nostalgia;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Setup of allowing users to change their name or their event preferences.
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
        SettingsAdapter mSettingsAdapter = new SettingsAdapter(this);
        mListView.setAdapter((ExpandableListAdapter) mSettingsAdapter);

        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext()," Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}