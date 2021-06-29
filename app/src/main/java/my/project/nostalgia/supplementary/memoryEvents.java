package my.project.nostalgia.supplementary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import my.project.nostalgia.R;
import my.project.nostalgia.activities.MemoryPagerActivity;
import my.project.nostalgia.activities.UserSettingsActivity;
import my.project.nostalgia.fragments.MemoryListFragment;
import my.project.nostalgia.models.Memory;

import static my.project.nostalgia.activities.LoginActivity.APPLICABLE_EVENTS;

public class memoryEvents implements MemoryListFragment.Callbacks{

    private Context mContext;
    private SharedPreferences mPreferences;

    private String joinedCurrentEvents;

    /**Constructor with context and preferences as parameters indicates getting events from Preferences*/
    public memoryEvents(Context context, SharedPreferences preference) {
        this.mContext = context;
        this.mPreferences =  preference;
        this.joinedCurrentEvents = mPreferences.getString(APPLICABLE_EVENTS,"");
    }
    /**Only context as parameter indicates default Events*/
    public memoryEvents(Context context){
        this.mContext = context;
        this.joinedCurrentEvents = defaultEventsJoined();
    }
    public String getJoinedEvents() {
        return joinedCurrentEvents;
    }
    public String[] getIndividualEvents(){ return joinedCurrentEvents.split(",");}
    /** Concatenates all events which are applicable into one string so that it can be stored in SharedPreferences.*/
    public String defaultEventsJoined() {
        List<String> allEvents = new LinkedList<>();

        allEvents.add(stringResource(R.string.student_life));
        allEvents.add(stringResource(R.string.home));
        allEvents.add(stringResource(R.string.hangouts));
        allEvents.add(stringResource(R.string.celebrations));

        return stringListToString(allEvents);
    }

    public void removeFromEvents(int finalI) {
        List<String> wordList = stringListOfCurrentEvents();
        wordList.remove(finalI);
        saveInPreference(wordList);
    }
    public void getAndSetNewEvent(View view, Activity activity,Memory memory) {

        AlertDialog.Builder inputEventDialog = new AlertDialog.Builder(mContext,new changeTheme(mContext).setDialogTheme());
        inputEventDialog.setTitle(R.string.new_custom_event);
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText input = new EditText(mContext);
        input.setHint(R.string.new_custom_event_hint);
        input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_NORMAL);
        layout.addView(input);
        layout.setPadding(35,35,35,35);
        inputEventDialog.setView(layout);

        inputEventDialog.setPositiveButton(R.string.create, (dialog, whichButton) -> {

            String inputString = input.getText().toString();
            if(inputString.length()>1){
                addNewEvent(inputString);
                refreshSnackbar(view,activity,memory);
            }
            dialog.dismiss();
        });
        inputEventDialog.setNegativeButton("cancel", (dialog, which) -> dialog.cancel()).create();
        inputEventDialog.show();
    }
    public void askDiscardEvent(View view, Activity activity,int finalI){

        AlertDialog.Builder discardMemoryDialogBox = new AlertDialog.Builder(mContext, new changeTheme(mContext).setDialogTheme());
                discardMemoryDialogBox.setTitle(stringResource(R.string.discard_event))
                .setMessage(stringResource(R.string.discard_event_confirm))
                .setPositiveButton(stringResource(R.string.discard), (dialog, whichButton) -> {
                    removeFromEvents(finalI);
                    refreshSnackbar(view,activity,null);
                    dialog.dismiss();
                })
                .setNegativeButton(stringResource(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .create().show();
    }
    public void refreshSnackbar(View view, Activity activity, Memory memory){
        Snackbar.make(view,stringResource(R.string.refreshPage),Snackbar.LENGTH_SHORT)
                .setAction(stringResource(R.string.refreshButton), v -> {

                    String activityName = activity.getClass().getSimpleName();
                    if(activityName.equals("UserSettingsActivity"))
                        mContext.startActivity(new Intent(mContext,UserSettingsActivity.class));

                    else if(activityName.equals("MemoryPagerActivity")) {
                        activity.finish();
                        onMemorySelected(memory);
                    }
                }).show();

    }

    @Override
    public void onMemorySelected(Memory memory) {
        Intent intent = MemoryPagerActivity.newIntent(mContext, memory.getId());
        mContext.startActivity(intent);
    }

    private void addNewEvent(String input) {
        List<String> wordList = stringListOfCurrentEvents();
        wordList.add(input);
        saveInPreference(wordList);
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
    public String[] addStringToArray(String string, String[] strings){
        List<String> mylist = new LinkedList<>(Arrays.asList(strings));
        mylist.add(string);
        return mylist.toArray(new String[0]);
    }

}
