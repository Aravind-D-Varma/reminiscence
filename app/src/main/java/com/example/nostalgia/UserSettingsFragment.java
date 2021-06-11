package com.example.nostalgia;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;
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
        mChoices.setTitle(getResources().getString(R.string.personal_settings));
        mScreen.addPreference(mChoices);

        EditTextPreference username = setUserName(mScreen);
        mChoices.addPreference(username);

        DropDownPreference events = getDropDownPreference(mScreen);
        mChoices.addPreference(events);

        ListPreference themes = new ListPreference(mScreen.getContext());
        themes.setKey("GlobalTheme");
        themes.setTitle(getResources().getString(R.string.themes));
        themes.setSummary(getResources().getString(R.string.themes_summary));
        CharSequence[] entries = {"Light","Dark"};
        CharSequence[] entryValues = {"Light","Dark"};
        themes.setEntries(entries);
        themes.setEntryValues(entryValues);

        themes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = themes.findIndexOfValue(newValue.toString());
                if (themes.getEntries()[index].equals("Light"))
                    themes.setValue("Light");
                else
                    themes.setValue("Dark");
                Intent intent = new Intent(getContext(), UserSettingsActivity.class);
                startActivity(intent);
                return false;
            }
        });
        mChoices.addPreference(themes);

        PreferenceCategory help = new PreferenceCategory(mScreen.getContext());
        help.setTitle("Help");
        mScreen.addPreference(help);

        Preference sendFeedback = sendFeedbackPref(mScreen);
        help.addPreference(sendFeedback);
        
        Preference invitePeople = invitePeoplePref(mScreen);
        help.addPreference(invitePeople);

        Preference aboutMe = myselfPref(mScreen);
        help.addPreference(aboutMe);

        SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
        String themeValues = getData.getString("GlobalTheme", "Dark");
        if (themeValues.equals("Dark")) {
            aboutMe.setIcon(R.drawable.aboutme_white);
            themes.setIcon(R.drawable.settingstheme_white);
            sendFeedback.setIcon(R.drawable.feedback_white);
            invitePeople.setIcon(R.drawable.invite_white);
            username.setIcon(R.drawable.username_white);
            mEvents.setIcon(R.drawable.delete_white);
        }
        else if (themeValues.equals("Light")) {
            themes.setIcon(R.drawable.settingstheme_black);
            aboutMe.setIcon(R.drawable.aboutme_black);
            sendFeedback.setIcon(R.drawable.feedback_black);
            invitePeople.setIcon(R.drawable.invite_black);
            username.setIcon(R.drawable.username_black);
            mEvents.setIcon(R.drawable.delete_black);
        }

    }

    private Preference myselfPref(PreferenceScreen mScreen) {
        Preference aboutMe = new Preference(mScreen.getContext());
        aboutMe.setTitle(getResources().getString(R.string.settings_aboutme));
        aboutMe.setSummary(getResources().getString(R.string.settings_aboutme_summary));
        aboutMe.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse("https://github.com/Aravind-D-Varma"));
                startActivity( browse );
                return true;
            }
        });
        return aboutMe;
    }
    private Preference sendFeedbackPref(PreferenceScreen mScreen) {
        Preference sendFeedback = new Preference(mScreen.getContext());
        sendFeedback.setTitle(getResources().getString(R.string.settings_feedback));

        sendFeedback.setSummary(getResources().getString(R.string.settings_feedback_summary));
        sendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
        return sendFeedback;
    }
    private Preference invitePeoplePref(PreferenceScreen mScreen) {
        Preference pref = new Preference(mScreen.getContext());
        pref.setTitle(getResources().getString(R.string.settings_invite));

        pref.setSummary(getResources().getString(R.string.settings_invite_summary));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
        return pref;
    }
    private EditTextPreference setUserName(PreferenceScreen mScreen) {
        EditTextPreference username = new EditTextPreference(mScreen.getContext());
        username.setKey(SEND_USERNAME);
        username.setTitle(getResources().getString(R.string.settings_name));
        username.setSummary(getResources().getString(R.string.settings_name_summary));
        return username;
    }

    private DropDownPreference getDropDownPreference(PreferenceScreen screen) {

        mEvents = new DropDownPreference(screen.getContext());
        mEvents.setTitle(getResources().getString(R.string.settings_events));
        mEvents.setSummary(getResources().getString(R.string.settings_events_summary));

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
                        askDiscardEvent(index);
                }
                return true;
            }
        });
        return mEvents;
    }
    private void askDiscardEvent(int finalI){
        SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
        String themeValues = getData.getString("GlobalTheme", "Dark");
        AlertDialog.Builder discardMemoryDialogBox;
        if(themeValues.equals("Light"))
            discardMemoryDialogBox = new AlertDialog.Builder(getContext(), R.style.LightDialog)
                    .setIcon(R.drawable.delete_black);
        else
            discardMemoryDialogBox = new AlertDialog.Builder(getContext(), R.style.DarkDialog)
                    .setIcon(R.drawable.delete_purple);

        discardMemoryDialogBox.setTitle(getResources().getString(R.string.discard_event))
                .setMessage(getResources().getString(R.string.discard_event_confirm))
                .setPositiveButton(getResources().getString(R.string.discard), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        removeFromEvents(finalI);
                        updateDropDownEvents();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        discardMemoryDialogBox.show();
    }
    private void updateDropDownEvents() {
        CharSequence[] userevents = getEntries();
        CharSequence[] entryValues = getEntryValues(userevents);
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
    private String[] getEntryValues(CharSequence[] userevents) {
        List<String> stringList = new ArrayList<String>();
        for(int i = 0; i < userevents.length; i++)
            stringList.add(String.valueOf(i));
        return stringList.toArray( new String[0] );
    }
    private CharSequence[] getEntries() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        CharSequence[] userevents = preferences.getString(IntroductionActivity.APPLICABLE_EVENTS, "").split(",");
        List<CharSequence> usereventsList = new LinkedList<CharSequence>(Arrays.asList(userevents));

        String addEvent = getResources().getString(R.string.add_event);
        Spannable summary = new SpannableString(addEvent);
        summary.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_purple)), 0, summary.length(), 0);
        usereventsList.add(summary);
        CharSequence[] cs = usereventsList.toArray(new CharSequence[usereventsList.size()]);
        //userevents = usereventsList.toArray(userevents);
        return cs;
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
