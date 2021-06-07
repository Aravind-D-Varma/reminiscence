package com.example.nostalgia;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class UserSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_main, rootKey);
    }
}
