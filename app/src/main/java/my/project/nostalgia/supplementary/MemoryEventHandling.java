package my.project.nostalgia.supplementary;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;

import androidx.preference.Preference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import my.project.nostalgia.R;

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

    public String getJoinedDefaultEvents() {
        return joinedCurrentEvents;
    }
    /**
     * Concatenates all events which are applicable into one string so that it can be stored in SharedPreferences.
     */
    public String defaultEventsJoined() {
        List<String> allEvents = new LinkedList<String>();

        allEvents.add(mContext.getResources().getString(R.string.student_life));
        allEvents.add(mContext.getResources().getString(R.string.home));
        allEvents.add(mContext.getResources().getString(R.string.hangouts));
        allEvents.add(mContext.getResources().getString(R.string.celebrations));

        return stringListToString(allEvents);
    }

    private void saveNewEvent(EditText input) {
        List<String> wordList = stringListOfEvents();
        wordList.add(input.getText().toString());
        saveInPreference(wordList);
    }

    private void removeFromEvents(int finalI) {
        List<String> wordList = stringListOfEvents();
        wordList.remove(finalI);
        saveInPreference(wordList);
    }

    private List<String> stringListOfEvents() {
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
