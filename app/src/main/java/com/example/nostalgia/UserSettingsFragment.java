package com.example.nostalgia;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

import static com.example.nostalgia.IntroductionActivity.SEND_USERNAME;

public class UserSettingsFragment extends PreferenceFragmentCompat{

    private DropDownPreference mEvents;
    private int calls;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        calls = 0;
        PreferenceScreen mScreen = getPreferenceManager().createPreferenceScreen(getActivity());
        setPreferenceScreen(mScreen);

        PreferenceCategory mChoices = new PreferenceCategory(mScreen.getContext());
        mChoices.setTitle("Personal Settings");
        mScreen.addPreference(mChoices);

        EditTextPreference username = setUserName(mScreen);
        mChoices.addPreference(username);

        DropDownPreference events = getDropDownPreference(mScreen);
        mChoices.addPreference(events);

        ListPreference themes = new ListPreference(mScreen.getContext());
        themes.setKey("GlobalTheme");
        themes.setTitle("Themes");
        themes.setSummary("Change app themes");
        CharSequence[] entries = {"Light","Dark"};
        CharSequence[] entryValues = {"Light","Dark"};
        themes.setEntries(entries);
        themes.setEntryValues(entryValues);
        themes.setIcon(R.drawable.settingstheme_white);
        themes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = themes.findIndexOfValue(newValue.toString());
                if (themes.getEntries()[index].equals("Light"))
                    getContext().setTheme(R.style.Theme_Reminiscence_Light);
                else
                    getContext().setTheme(R.style.Theme_Reminiscence);
                return false;
            }
        });
        mChoices.addPreference(themes);

        PreferenceCategory help = new PreferenceCategory(mScreen.getContext());
        help.setTitle("Help");
        mScreen.addPreference(help);

        Preference sendFeedback = sendFeedbackPref(mScreen);
        help.addPreference(sendFeedback);

        Preference aboutMe = myselfPref(mScreen);
        help.addPreference(aboutMe);

    }

    private Preference myselfPref(PreferenceScreen mScreen) {
        Preference aboutMe = new Preference(mScreen.getContext());
        aboutMe.setTitle("About me");
        aboutMe.setIcon(R.drawable.aboutme_white);
        aboutMe.setSummary("A brief description of this application");
        aboutMe.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                aboutMeAlertDialog();
                return true;
            }
        });
        return aboutMe;
    }
    private Preference sendFeedbackPref(PreferenceScreen mScreen) {
        Preference sendFeedback = new Preference(mScreen.getContext());
        sendFeedback.setTitle("Send Feedback");
        sendFeedback.setIcon(R.drawable.feedback_white);
        sendFeedback.setSummary("Report an issue or suggest a new feature");
        sendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
        return sendFeedback;
    }
    private EditTextPreference setUserName(PreferenceScreen mScreen) {
        EditTextPreference username = new EditTextPreference(mScreen.getContext());
        username.setKey(SEND_USERNAME);
        username.setIcon(R.drawable.username_white);
        username.setTitle("Name");
        username.setSummary("Change your name");
        return username;
    }

    private void aboutMeAlertDialog() {
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(getContext())
                .setTitle("About me");
        final TextView info = new EditText(getContext());
        info.setText(R.string.aboutme);
        info.setFocusable(false);
        infoDialog.setView(info)
                .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create();
        infoDialog.show();
    }
    private DropDownPreference getDropDownPreference(PreferenceScreen screen) {

        mEvents = new DropDownPreference(screen.getContext());
        mEvents.setTitle("Events");
        mEvents.setSummary("Tap on event to delete it. Click Add to add a customized event");
        mEvents.setIcon(R.drawable.ic_menu_delete);
        updateDropDownEvents();
        mEvents.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = mEvents.findIndexOfValue(newValue.toString());
                if(mEvents.getEntries()[index].equals("Add Event"))
                    getAndSetNewEvent();
                else{
                    if(calls == 0){
                        calls = 1;
                        return false;
                    }
                    else
                        askDiscardEvent(index).show();
                }
                return true;
            }
        });
        return mEvents;
    }
    private AlertDialog askDiscardEvent(int finalI){
        AlertDialog discardMemoryDialogBox = new AlertDialog.Builder(getContext())
                .setTitle("Discard Event")
                .setMessage("Do you want to discard this event?")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        removeFromEvents(finalI);
                        updateDropDownEvents();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return discardMemoryDialogBox;
    }
    private void updateDropDownEvents() {
        String[] userevents = getEntries();
        String[] entryValues = getEntryValues(userevents);
        mEvents.setEntries(userevents);
        mEvents.setEntryValues(entryValues);
    }
    private void removeFromEvents(int finalI) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        String currentEvents = prefs.getString(IntroductionActivity.APPLICABLE_EVENTS, "");
        List<String> wordList = new ArrayList<String>(Arrays.asList(currentEvents.split(",")));
        wordList.remove(finalI);
        editor.putString(IntroductionActivity.APPLICABLE_EVENTS,stringListToString(wordList));
        editor.apply();
    }
    private String[] getEntryValues(String[] userevents) {
        List<String> stringList = new ArrayList<String>();
        for(int i = 0; i < userevents.length; i++)
            stringList.add(String.valueOf(i));
        return stringList.toArray( new String[0] );
    }
    private String[] getEntries() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String[] userevents = preferences.getString(IntroductionActivity.APPLICABLE_EVENTS, "").split(",");
        List<String> usereventsList = new LinkedList<String>(Arrays.asList(userevents));
        usereventsList.add("Add Event");
        userevents = usereventsList.toArray(userevents);
        return userevents;
    }
    private void getAndSetNewEvent() {

        AlertDialog.Builder inputEventDialog = new AlertDialog.Builder(getContext());
        inputEventDialog.setTitle("New Custom Event");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_NORMAL);
        inputEventDialog.setView(input);

        inputEventDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if(input.getText().toString().length()>1){
                    saveNewEvent(input);
                    updateDropDownEvents();
                }
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
