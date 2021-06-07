package com.example.nostalgia;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.nostalgia.IntroductionActivity.SEND_USERNAME;

public class UserSettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String[] userevents = preferences.getString(IntroductionActivity.APPLICABLE_EVENTS, "").split(",");
        List<String> usereventsList = new LinkedList<String>(Arrays.asList(userevents));
        usereventsList.add("Add Event");
        userevents = usereventsList.toArray(userevents);
        List<String> stringList = new ArrayList<String>();
        for(int i = 0; i < userevents.length; i++)
            stringList.add(String.valueOf(i));
        String[] entryValues = stringList.toArray( new String[0] );

        DropDownPreference events = new DropDownPreference(screen.getContext());
        events.setTitle("Events");
        events.setSummary("Tap on event to edit/delete it. Click Add to add a customized event");
        events.setEntries(userevents);
        events.setEntryValues(entryValues);
        events.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = events.findIndexOfValue(newValue.toString());
                if(events.getEntries()[index].equals("Add Event"))
                    getAndSetNewEvent();
                Toast.makeText(getContext(), events.getEntries()[index]+" clicked", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        choices.addPreference(events);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        return false;
    }
    private void getAndSetNewEvent() {
        final String[] toBeReturnedEvent = new String[1];
        AlertDialog.Builder inputEventDialog = new AlertDialog.Builder(getContext());
        inputEventDialog.setTitle("New Custom Event");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_NORMAL);
        inputEventDialog.setView(input);

        inputEventDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                saveNewEvent(input);
                toBeReturnedEvent[0] = input.getText().toString();
                if(toBeReturnedEvent[0].length()>1)
                    addNewEvent(toBeReturnedEvent[0]);
                dialog.dismiss();
            }
        });
        inputEventDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create();
        inputEventDialog.show();
    }

    private void addNewEvent(String s) {
    }

    private void saveNewEvent(EditText input) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        String currentEvents = prefs.getString(IntroductionActivity.APPLICABLE_EVENTS, "");
        List<String> wordList = new ArrayList<String>(Arrays.asList(currentEvents.split(",")));
        wordList.add(input.getText().toString());
        editor.putString(IntroductionActivity.APPLICABLE_EVENTS,stringListToString(wordList));
        editor.apply();
    }
    private String stringListToString(List<String> allEvents) {
        String[] applicableEvents = {};
        applicableEvents = allEvents.toArray(applicableEvents);
        StringBuilder combinedEvents = new StringBuilder();
        for (int i = 0; i < applicableEvents.length; i++)
            combinedEvents.append(applicableEvents[i]).append(",");

        return combinedEvents.toString();
    }
}
