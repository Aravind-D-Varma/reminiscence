package com.example.nostalgia;

import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import static com.example.nostalgia.IntroductionActivity.SEND_USERNAME;

public class UserSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getActivity());
        setPreferenceScreen(screen);

        PreferenceCategory choices = new PreferenceCategory(screen.getContext());
        choices.setTitle("Personal Settings");
        screen.addPreference(choices);

        EditTextPreference username = new EditTextPreference(screen.getContext());
        username.setKey(SEND_USERNAME);
        username.setTitle("Name");
        username.setSummary("Change your name");
        choices.addPreference(username);

        ListPreference events = new ListPreference(screen.getContext());
        events.setTitle("Events");
        events.setSummary("Add/Delete Events");
        choices.addPreference(events);
    }
}
