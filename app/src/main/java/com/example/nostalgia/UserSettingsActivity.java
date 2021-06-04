package com.example.nostalgia;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.LinkedList;
import java.util.List;

/**
 * Setup of allowing users to change their name or their event preferences.
 */
public class UserSettingsActivity extends AppCompatActivity {
    private EditText mUsername;
    private ListView mListView;
    private List<String> allEvents = new LinkedList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.settings));
        setContentView(R.layout.all_settings);
        mListView = (ExpandableListView) findViewById(R.id.expandable_listview);
        SettingsAdapter settingsAdapter = new SettingsAdapter();
        gettingUserPreferences();
    }

    private void gettingUserPreferences() {
        allEvents = initializeEvents(allEvents);
        setUserName();
    }

    private void setUserName() {
        mUsername = (EditText) findViewById(R.id.user_name);
        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private List<String> initializeEvents(List<String> allEvents) {
        allEvents.add("Student Life");
        allEvents.add("Work");
        allEvents.add("Festivals");
        allEvents.add("Home");
        allEvents.add("Birthdays");
        allEvents.add("Hangouts");
        return allEvents;
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveUserPreferences();
    }

    private void saveUserPreferences() {
        if (noActivityInSettings()) {
            Toast.makeText(getApplicationContext(), "You didnt make any changes. Keeping your previous settings...", Toast.LENGTH_SHORT).show();
        }
        else if(onlyChangedName()) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putString(Introduction.SEND_USERNAME, mUsername.getText().toString());
            editor.apply();
        }
        else{
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();

                editor.putString(Introduction.APPLICABLE_EVENTS, listOfStringsToString(allEvents));
                editor.apply();
        }
    }

    private String listOfStringsToString(List<String> allEvents) {
        String[] applicableEvents = {};
        applicableEvents = allEvents.toArray(applicableEvents);
        StringBuilder combinedEvents = new StringBuilder();
        for (int i = 0; i < applicableEvents.length; i++)
            combinedEvents.append(applicableEvents[i]).append(",");
        return combinedEvents.toString();
    }

    private boolean onlyChangedName() {
        return true;
    }

    private boolean noActivityInSettings() {
        return mUsername.getText().toString().length() < 1;
    }

}