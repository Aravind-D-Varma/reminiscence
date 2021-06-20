package my.project.nostalgia.supplementary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import my.project.nostalgia.R;
import my.project.nostalgia.activities.UserSettingsActivity;

import static my.project.nostalgia.activities.IntroductionActivity.APPLICABLE_EVENTS;

public class MemoryEventHandling {

    private Context mContext;
    private SharedPreferences mPreferences;

    private String joinedCurrentEvents;

    /**
     * Constructor with context as parameter indicates getting events from Preferences
     * @param context
     */
    public MemoryEventHandling(Context context, SharedPreferences preference) {
        this.mContext = context;
        this.mPreferences =  preference;
        this.joinedCurrentEvents = mPreferences.getString(APPLICABLE_EVENTS,"");
    }
    /**
     * Empty constructor indicates default Events
     */
    public MemoryEventHandling(Context context){
        this.mContext = context;
        this.joinedCurrentEvents = defaultEventsJoined();
    }

    public String getJoinedEvents() {
        return joinedCurrentEvents;
    }
    /**
     * Concatenates all events which are applicable into one string so that it can be stored in SharedPreferences.
     */
    public String defaultEventsJoined() {
        List<String> allEvents = new LinkedList<String>();

        allEvents.add(stringResource(R.string.student_life));
        allEvents.add(stringResource(R.string.home));
        allEvents.add(stringResource(R.string.hangouts));
        allEvents.add(stringResource(R.string.celebrations));
        allEvents.add(stringResource(R.string.add_event));

        return stringListToString(allEvents);
    }

    private void saveNewEvent(String input) {
        List<String> wordList = stringListOfCurrentEvents();
        wordList.add(input);
        saveInPreference(wordList);
    }
    public void removeFromEvents(int finalI) {
        List<String> wordList = stringListOfCurrentEvents();
        wordList.remove(finalI);
        saveInPreference(wordList);
    }
    public void getAndSetNewEvent() {

        AlertDialog.Builder inputEventDialog = new AlertDialog.Builder(mContext);
        inputEventDialog.setTitle("New Custom Event");
        final EditText input = new EditText(mContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_NORMAL);
        inputEventDialog.setView(input);

        inputEventDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String inputString = input.getText().toString();
                if(inputString.length()>1){
                    saveNewEvent(inputString);
                }
                dialog.dismiss();
                mContext.startActivity(new Intent(mContext, UserSettingsActivity.class));
            }
        });
        inputEventDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create();
        inputEventDialog.show();
    }
    public void askDiscardEvent(int finalI){

        String themeValues = mPreferences.getString("GlobalTheme", "Dark");
        AlertDialog.Builder discardMemoryDialogBox;
        if(themeValues.equals("Light"))
            discardMemoryDialogBox = new AlertDialog.Builder(mContext, R.style.LightDialog)
                    .setIcon(R.drawable.delete_black);
        else
            discardMemoryDialogBox = new AlertDialog.Builder(mContext, R.style.DarkDialog)
                    .setIcon(R.drawable.delete_purple);

        discardMemoryDialogBox.setTitle(stringResource(R.string.discard_event))
                .setMessage(stringResource(R.string.discard_event_confirm))
                .setPositiveButton(stringResource(R.string.discard), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        removeFromEvents(finalI);
                        dialog.dismiss();
                        mContext.startActivity(new Intent(mContext,UserSettingsActivity.class));
                    }
                })
                .setNegativeButton(stringResource(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        discardMemoryDialogBox.show();
    }

    private String stringResource(int resourceID) {
        return mContext.getResources().getString(resourceID);
    }

    private List<String> stringListOfCurrentEvents() {
        List<String> currentEventsList = Arrays.asList(joinedCurrentEvents.split(","));
        return new ArrayList<>(currentEventsList);
    }

    private void saveInPreference(List<String> wordList) {
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString(APPLICABLE_EVENTS, stringListToString(wordList));
        mEditor.apply();
    }
    private String stringListToString(List<String> allEvents) {
        String[] applicableEvents = {};
        applicableEvents = allEvents.toArray(applicableEvents);
        StringBuilder combinedEvents = new StringBuilder();
        for (String string:applicableEvents)
            combinedEvents.append(string).append(",");

        return combinedEvents.toString();
    }
}
