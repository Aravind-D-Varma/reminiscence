package my.project.nostalgia.fragments;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import my.project.nostalgia.BuildConfig;
import my.project.nostalgia.R;
import my.project.nostalgia.activities.LoginActivity;
import my.project.nostalgia.activities.UserSettingsActivity;
import my.project.nostalgia.models.Memory;
import my.project.nostalgia.models.MemoryLab;
import my.project.nostalgia.supplementary.changeTheme;
import my.project.nostalgia.supplementary.memoryEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static my.project.nostalgia.activities.LoginActivity.LANGUAGE;
import static my.project.nostalgia.activities.LoginActivity.SEND_USERNAME;
import static my.project.nostalgia.fragments.MemoryListFragment.MEMORIES_KEY;

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

        Preference username = setUserName(mScreen);
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
    }

    private Preference deleteAccountPref(PreferenceScreen mScreen) {
        //TODO delete user files in storage and database
        Preference pref = new Preference(mScreen.getContext());
        pref.setTitle(stringResource(R.string.delete_account));
        pref.setSummary(stringResource(R.string.delete_account_summary));
        pref.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder deleted_account = new AlertDialog.Builder(
                    getContext(),new changeTheme(getContext()).setDialogTheme());
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            final TextView confirmation = new TextView(getContext());
            confirmation.setText(stringResource(R.string.delete_account_confirm));
            confirmation.setTypeface(null, Typeface.BOLD);
            confirmation.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
            layout.addView(confirmation);
            final EditText email = new EditText(getContext());
            email.setHint(R.string.email_address_hint);
            email.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_NORMAL);
            layout.addView(email);
            final EditText password = new EditText(getContext());
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            password.setHint(R.string.password_hint);
            layout.addView(password);
            layout.setPadding(35,35,35,35);
            deleted_account.setView(layout);
            deleted_account.setPositiveButton(stringResource(R.string.delete), (dialog, whichButton) -> {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email.getText().toString(), password.getText().toString());
                user.reauthenticate(credential).addOnSuccessListener(aVoid -> {
                    user.delete();
                    //TODO make a toast on successful deletion
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                    dialog.dismiss();
                });
            }).setNegativeButton(stringResource(R.string.cancel), (dialog, which) -> dialog.dismiss()).create();
            deleted_account.show();
            return false;
        });
        return pref;
    }

    private String stringResource(int p) {
        return getResources().getString(p);
    }

    private Preference signOutPref(PreferenceScreen mScreen) {
        Preference pref = new Preference(mScreen.getContext());
        pref.setTitle(stringResource(R.string.sign_out));
        pref.setSummary(stringResource(R.string.sign_out_summary));
        pref.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder confirmSignOut = new AlertDialog.Builder(getContext(),new changeTheme(getContext()).setDialogTheme());
            confirmSignOut.setTitle(R.string.sign_out);
            confirmSignOut.setMessage(R.string.sign_out_confirm);
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_NORMAL);
            confirmSignOut.setView(input);
            confirmSignOut.setPositiveButton(R.string.save_and_confirm, (dialog, whichButton) -> {
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference userDocument = FirebaseFirestore.getInstance().collection("Users")
                        .document(userid);
                ProgressDialog mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setMessage("Uploading...");
                mProgressDialog.show();
                Map<String,List<Memory>> dataToSave = new HashMap<>();
                dataToSave.put(MEMORIES_KEY, MemoryLab.get(getActivity()).getMemories());
                userDocument.set(dataToSave).addOnSuccessListener(aVoid -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                    mProgressDialog.dismiss();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                });
                dialog.dismiss();
            });
            confirmSignOut.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel()).create();
            confirmSignOut.show();

            return false;
        });
        return pref;
    }

    private Preference setUserName(PreferenceScreen mScreen) {
        Preference pref = new Preference(mScreen.getContext());
        pref.setKey(SEND_USERNAME);
        pref.setTitle(stringResource(R.string.settings_name));
        pref.setSummary(stringResource(R.string.settings_name_summary));
        pref.setIcon(R.drawable.settings_username);pref.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder changeName = new AlertDialog.Builder(
                    getContext(),new changeTheme(getContext()).setDialogTheme());
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            final TextView message = new TextView(getContext());
            message.setText(stringResource(R.string.settings_name));
            message.setTypeface(null, Typeface.BOLD);
            message.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
            layout.addView(message);
            final EditText name = new EditText(getContext());
            name.setHint(R.string.settings_name_summary);
            name.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_NORMAL);
            layout.addView(name);
            layout.setPadding(35,35,35,35);
            changeName.setView(layout);
            changeName.setPositiveButton(stringResource(R.string.delete_account_confirm), (dialog, whichButton) -> {
                PreferenceManager.getDefaultSharedPreferences(getContext())
                        .edit().putString(SEND_USERNAME,name.getText().toString()).apply();
            }).setNegativeButton(stringResource(R.string.cancel), (dialog, which) -> dialog.dismiss()).create();
            changeName.show();
            return false;
        });
        return pref;
    }
    private ListPreference setThemePref(PreferenceScreen mScreen) {
        ListPreference pref = new ListPreference(mScreen.getContext());
        pref.setKey("GlobalTheme");
        pref.setTitle(stringResource(R.string.themes));
        pref.setSummary(stringResource(R.string.themes_summary));
        pref.setIcon(R.drawable.settings_themes);
        CharSequence[] entries = {"Light","Dark"};
        CharSequence[] entryValues = {"Light","Dark"};
        pref.setEntries(entries);
        pref.setEntryValues(entryValues);

        pref.setOnPreferenceChangeListener((preference, newValue) -> {
            int index = pref.findIndexOfValue(newValue.toString());
            if (pref.getEntries()[index].equals("Light")) {
                pref.setValue("Light");
            }
            else {
                pref.setValue("Dark");
            }
            Intent intent = new Intent(getContext(), UserSettingsActivity.class);
            startActivity(intent);
            getActivity().finish();
            return false;
        });
        return pref;
    }
    private ListPreference setLanguagePref(PreferenceScreen mScreen) {
        ListPreference pref = new ListPreference(mScreen.getContext());
        pref.setKey(LANGUAGE);
        pref.setTitle(stringResource(R.string.language));
        pref.setSummary(stringResource(R.string.language_summary));
        pref.setIcon(R.drawable.settings_language);
        CharSequence[] entries = {"English","Dutch"};
        CharSequence[] entryValues = {"English","Dutch"};
        pref.setEntries(entries);
        pref.setEntryValues(entryValues);

        pref.setOnPreferenceChangeListener((preference, newValue) -> {
            int index = pref.findIndexOfValue(newValue.toString());
            if (pref.getEntries()[index].equals("English")){
                setLanguage(pref, "English", "en");
            }
            else{
                setLanguage(pref, "Dutch", "nl");
            }
            Intent intent = new Intent(getContext(), UserSettingsActivity.class);
            startActivity(intent);
            getActivity().finish();
            return false;
        });
        return pref;
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
        Preference pref = new Preference(mScreen.getContext());
        pref.setTitle(stringResource(R.string.settings_feedback));
        pref.setSummary(stringResource(R.string.settings_feedback_summary));
        pref.setIcon(R.drawable.settings_feedback);
        pref.setOnPreferenceClickListener(preference -> {
            Intent browse = new Intent( Intent.ACTION_VIEW ,
                    Uri.parse("https://play.google.com/store/apps/details?id=my.project.nostalgia"));
            startActivity( browse );
            return false;
        });
        return pref;
    }
    private Preference invitePeoplePref(PreferenceScreen mScreen) {
        Preference pref = new Preference(mScreen.getContext());
        pref.setTitle(stringResource(R.string.settings_invite));
        pref.setSummary(stringResource(R.string.settings_invite_summary));
        pref.setIcon(R.drawable.settings_invite);
        pref.setOnPreferenceClickListener(preference -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Reminiscence");
            String shareMessage= stringResource(R.string.invite_someone) +"\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id="
                    + BuildConfig.APPLICATION_ID +"\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share app"));
            return false;
        });
        return pref;
    }
    private Preference myselfPref(PreferenceScreen mScreen) {
        Preference pref = new Preference(mScreen.getContext());
        pref.setTitle(stringResource(R.string.settings_aboutme));
        pref.setSummary(stringResource(R.string.settings_aboutme_summary));
        pref.setIcon(R.drawable.settings_aboutme);
        pref.setOnPreferenceClickListener(preference -> {
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse("https://github.com/Aravind-D-Varma"));
            startActivity( browse );
            return true;
        });
        return pref;
    }

    private DropDownPreference getDropDownPreference(PreferenceScreen screen) {

        mEvents = new DropDownPreference(screen.getContext());
        mEvents.setTitle(stringResource(R.string.settings_events));
        mEvents.setSummary(stringResource(R.string.settings_events_summary));
        mEvents.setIcon(R.drawable.settings_customevents);
        updateDropDownEvents();
        mEvents.setOnPreferenceChangeListener((preference, newValue) -> {
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
        List<String> stringList = new ArrayList<>();
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
