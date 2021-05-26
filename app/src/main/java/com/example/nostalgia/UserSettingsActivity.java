package com.example.nostalgia;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.LinkedList;
import java.util.List;

public class UserSettingsActivity extends AppCompatActivity {
    private EditText mUsername;
    private RadioButton mUserYesStudent, mUserYesWorked, mUserYesReligious;
    private RadioGroup mUserStudent, mUserWorked, mUserReligious;
    private List<String> allEvents = new LinkedList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.settings));
        setContentView(R.layout.settings);
        gettingUserPreferences();
    }

    private void gettingUserPreferences() {
        allEvents.add("Student Life");
        allEvents.add("Work");
        allEvents.add("Festivals");
        allEvents.add("Home");
        allEvents.add("Birthdays");
        allEvents.add("Hangouts");
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
        mUserYesStudent = (RadioButton) findViewById(R.id.yes_student);
        mUserYesWorked = (RadioButton) findViewById(R.id.yes_worked);
        mUserYesReligious = (RadioButton) findViewById(R.id.yes_religious);
        mUserStudent = findViewById(R.id.user_student);
        mUserWorked = findViewById(R.id.user_working);
        mUserReligious = findViewById(R.id.user_religious);
    }

    @Override
    protected void onPause() {
        super.onPause();
        savingUserPreferences();
    }

    private void savingUserPreferences() {
        if (mUserStudent.getCheckedRadioButtonId() != -1) {
            if (!mUserYesStudent.isChecked())
                allEvents.remove("Student Life");
        }
        if (mUserWorked.getCheckedRadioButtonId() != -1) {
            if (!mUserYesWorked.isChecked())
                allEvents.remove("Work");
        }
        if (mUserReligious.getCheckedRadioButtonId() != -1) {
            if (!mUserYesReligious.isChecked())
                allEvents.remove("Festivals");
        }
        if (mUserStudent.getCheckedRadioButtonId() == -1 && mUserWorked.getCheckedRadioButtonId() == -1
                && mUserReligious.getCheckedRadioButtonId() == -1 && mUsername.getText().toString().length() < 1) {
            Toast.makeText(getApplicationContext(), "You didnt make any changes. Keeping your previous settings...", Toast.LENGTH_SHORT).show();
        }
        else if(mUserStudent.getCheckedRadioButtonId() == -1 && mUserWorked.getCheckedRadioButtonId() == -1
                && mUserReligious.getCheckedRadioButtonId() == -1) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putString(Introduction.SEND_USERNAME, mUsername.getText().toString());
            editor.apply();
        }
        else{
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                String[] applicableEvents = {};applicableEvents = allEvents.toArray(applicableEvents);
                StringBuilder combinedEvents = new StringBuilder();
                for (int i = 0; i < applicableEvents.length; i++)
                    combinedEvents.append(applicableEvents[i]).append(",");

                editor.putString(Introduction.APPLICABLE_EVENTS, combinedEvents.toString());
                editor.apply();
        }
    }

}