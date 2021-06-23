package my.project.nostalgia.fragments;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import my.project.nostalgia.BuildConfig;
import my.project.nostalgia.R;
import my.project.nostalgia.activities.UserSettingsActivity;
import my.project.nostalgia.supplementary.memoryEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static my.project.nostalgia.activities.LoginActivity.LANGUAGE;
import static my.project.nostalgia.activities.LoginActivity.SEND_USERNAME;

/**
 * Creates and sets application according to choices made here.
 */
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

        ListPreference themes = setThemePref(mScreen);
        mChoices.addPreference(themes);

        ListPreference language = setLanguagePref(mScreen);
        mChoices.addPreference(language);

        PreferenceCategory help = new PreferenceCategory(mScreen.getContext());
        help.setTitle("Help");
        mScreen.addPreference(help);

        Preference sendFeedback = sendFeedbackPref(mScreen);
        help.addPreference(sendFeedback);
        
        Preference invitePeople = invitePeoplePref(mScreen);
        help.addPreference(invitePeople);

        Preference aboutMe = myselfPref(mScreen);
        help.addPreference(aboutMe);

        SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getContext());
        String themeValues = getData.getString("GlobalTheme", "Dark");
        if (themeValues.equals("Dark")) {
            aboutMe.setIcon(R.drawable.aboutme_white);
            themes.setIcon(R.drawable.settingstheme_white);
            language.setIcon(R.drawable.language_white);
            sendFeedback.setIcon(R.drawable.feedback_white);
            invitePeople.setIcon(R.drawable.invite_white);
            username.setIcon(R.drawable.username_white);
            mEvents.setIcon(R.drawable.swap_white);
        }
        else if (themeValues.equals("Light")) {
            themes.setIcon(R.drawable.settingstheme_black);
            language.setIcon(R.drawable.language_black);
            aboutMe.setIcon(R.drawable.aboutme_black);
            sendFeedback.setIcon(R.drawable.feedback_black);
            invitePeople.setIcon(R.drawable.invite_black);
            username.setIcon(R.drawable.username_black);
            mEvents.setIcon(R.drawable.swap_black);
        }
    }
    private EditTextPreference setUserName(PreferenceScreen mScreen) {
        EditTextPreference username = new EditTextPreference(mScreen.getContext());
        username.setKey(SEND_USERNAME);
        username.setTitle(getResources().getString(R.string.settings_name));
        username.setSummary(getResources().getString(R.string.settings_name_summary));
        return username;
    }
    private ListPreference setThemePref(PreferenceScreen mScreen) {
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
                getActivity().finish();
                return false;
            }
        });
        return themes;
    }
    private ListPreference setLanguagePref(PreferenceScreen mScreen) {
        ListPreference languages = new ListPreference(mScreen.getContext());
        languages.setKey(LANGUAGE);
        languages.setTitle(getResources().getString(R.string.language));
        languages.setSummary(getResources().getString(R.string.language_summary));
        CharSequence[] entries = {"English","Dutch"};
        CharSequence[] entryValues = {"English","Dutch"};
        languages.setEntries(entries);
        languages.setEntryValues(entryValues);

        languages.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = languages.findIndexOfValue(newValue.toString());
                if (languages.getEntries()[index].equals("English")){
                    setLanguage(languages, "English", "en");
                }
                else{
                    setLanguage(languages, "Dutch", "nl");
                }
                Intent intent = new Intent(getContext(), UserSettingsActivity.class);
                startActivity(intent);
                getActivity().finish();
                return false;
            }
        });
        return languages;
    }
    private void setLanguage(ListPreference languages, String language, String lang) {
        languages.setValue(language);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getContext().getResources().
                updateConfiguration(config, getContext().getResources().getDisplayMetrics());
    }
    private Preference sendFeedbackPref(PreferenceScreen mScreen) {
        Preference sendFeedback = new Preference(mScreen.getContext());
        sendFeedback.setTitle(getResources().getString(R.string.settings_feedback));
        sendFeedback.setSummary(getResources().getString(R.string.settings_feedback_summary));
        sendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browse = new Intent( Intent.ACTION_VIEW ,
                        Uri.parse("https://play.google.com/store/apps/details?id=my.project.nostalgia"));
                startActivity( browse );
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
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Reminiscence");
                String shareMessage= getResources().getString(R.string.invite_someone)+"\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id="
                        + BuildConfig.APPLICATION_ID +"\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share app"));
                return false;
            }
        });
        return pref;
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

    private DropDownPreference getDropDownPreference(PreferenceScreen screen) {

        mEvents = new DropDownPreference(screen.getContext());
        mEvents.setTitle(getResources().getString(R.string.settings_events));
        mEvents.setSummary(getResources().getString(R.string.settings_events_summary));
        updateDropDownEvents();
        mEvents.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = mEvents.findIndexOfValue(newValue.toString());

                if(mEvents.getEntries()[index].equals("Add Event")) {
                    getMemoryEventHandling().getAndSetNewEvent(getView(),getActivity(),null);
                }
                else{
                    if(calls == 0){
                        calls = 1;
                        return false;
                    }
                    else {
                        getMemoryEventHandling().askDiscardEvent(getView(),getActivity(),index);
                    }
                }
                return true;
            }
        });
        return mEvents;
    }

    private memoryEvents getMemoryEventHandling() {
        return new memoryEvents(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
    }

    private void updateDropDownEvents() {
        CharSequence[] userevents = getEntries();
        CharSequence[] entryValues = getEntryValues(userevents);
        mEvents.setEntries(userevents);
        mEvents.setEntryValues(entryValues);
    }

    private String[] getEntryValues(CharSequence[] userevents) {
        List<String> stringList = new ArrayList<String>();
        for(CharSequence sequence:userevents)
            stringList.add(String.valueOf(sequence));
        return stringList.toArray( new String[0] );
    }
    private String[] getEntries() {
        String[] applicableEvents =getMemoryEventHandling().getJoinedEvents().split(",");
        applicableEvents = getMemoryEventHandling().addStringToArray(getResources().getString(R.string.add_event),
                applicableEvents);
        return applicableEvents;
    }

}
