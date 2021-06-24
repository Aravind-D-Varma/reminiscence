package my.project.nostalgia.fragments;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import my.project.nostalgia.BuildConfig;
import my.project.nostalgia.R;
import my.project.nostalgia.activities.LoginActivity;
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
        mChoices.setTitle(stringResource(R.string.personal_settings));
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

        PreferenceCategory accounts = new PreferenceCategory(mScreen.getContext());
        accounts.setTitle("Account");
        mScreen.addPreference(accounts);

        Preference signOut = signOutPref(mScreen);
        accounts.addPreference(signOut);

        Preference deleteAccount = deleteAccountPref(mScreen);
        accounts.addPreference(deleteAccount);

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

    private Preference deleteAccountPref(PreferenceScreen mScreen) {
        Preference deleteAccount = new Preference(mScreen.getContext());
        deleteAccount.setTitle(stringResource(R.string.delete_account));
        deleteAccount.setSummary(stringResource(R.string.delete_account_summary));
        deleteAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder deleted_account = new AlertDialog.Builder(getContext());
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                final TextView confirmation = new TextView(getContext());
                confirmation.setText(stringResource(R.string.delete_account_confirm));
                layout.addView(confirmation);
                final EditText email = new EditText(getContext());
                email.setHint("Re-enter your email address");
                email.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_NORMAL);
                layout.addView(email);
                final EditText password = new EditText(getContext());
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setHint("Re-enter your password");
                layout.addView(password);
                deleted_account.setView(layout);
                deleted_account.setPositiveButton(stringResource(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(), password.getText().toString());
                        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                user.delete();
                                //TODO make a toast on successful deletion
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                                dialog.dismiss();
                            }
                        });
                    }
                }).setNegativeButton(stringResource(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                deleted_account.show();
                return false;
            }
        });
        return deleteAccount;
    }

    private String stringResource(int p) {
        return getResources().getString(p);
    }

    private Preference signOutPref(PreferenceScreen mScreen) {
        Preference signOut = new Preference(mScreen.getContext());
        signOut.setTitle(stringResource(R.string.sign_out));
        signOut.setSummary(stringResource(R.string.sign_out_summary));
        signOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                return false;
            }
        });
        return signOut;
    }

    private EditTextPreference setUserName(PreferenceScreen mScreen) {
        EditTextPreference username = new EditTextPreference(mScreen.getContext());
        username.setKey(SEND_USERNAME);
        username.setTitle(stringResource(R.string.settings_name));
        username.setSummary(stringResource(R.string.settings_name_summary));
        return username;
    }
    private ListPreference setThemePref(PreferenceScreen mScreen) {
        ListPreference themes = new ListPreference(mScreen.getContext());
        themes.setKey("GlobalTheme");
        themes.setTitle(stringResource(R.string.themes));
        themes.setSummary(stringResource(R.string.themes_summary));
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
        languages.setTitle(stringResource(R.string.language));
        languages.setSummary(stringResource(R.string.language_summary));
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
        sendFeedback.setTitle(stringResource(R.string.settings_feedback));
        sendFeedback.setSummary(stringResource(R.string.settings_feedback_summary));
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
        pref.setTitle(stringResource(R.string.settings_invite));
        pref.setSummary(stringResource(R.string.settings_invite_summary));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Reminiscence");
                String shareMessage= stringResource(R.string.invite_someone) +"\n\n";
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
        aboutMe.setTitle(stringResource(R.string.settings_aboutme));
        aboutMe.setSummary(stringResource(R.string.settings_aboutme_summary));
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
        mEvents.setTitle(stringResource(R.string.settings_events));
        mEvents.setSummary(stringResource(R.string.settings_events_summary));
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
        applicableEvents = getMemoryEventHandling().addStringToArray(stringResource(R.string.add_event),
                applicableEvents);
        return applicableEvents;
    }

}
