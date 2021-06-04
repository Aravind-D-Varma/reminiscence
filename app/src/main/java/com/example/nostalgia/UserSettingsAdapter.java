package com.example.nostalgia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.nostalgia.Introduction.SEND_USERNAME;
import static com.example.nostalgia.MemoryFragment.CURRENT_MEMORY;

public class UserSettingsAdapter extends BaseExpandableListAdapter{

    private static final int EDITTEXT_CHILD_TYPE_0 = 0;
    private static final int RV_CHILD_TYPE_1 = 1;
    private static final int TEXT_CHILD_TYPE_2 = 2;

    // 3 Group types
    private static final int USERNAME_GROUP = 0;
    private static final int EVENTS_GROUP = 1;
    private static final int  ABOUT_GROUP = 2;

    private final Context mContext;
    private EditText mUserName;

    public UserSettingsAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getGroupCount() {
        return 3;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        Integer groupType = getGroupType(groupPosition);

        if (convertView == null || convertView.getTag() != groupType)
            convertView = inflater.inflate(R.layout.settings_groupnames, null);

        switch (groupType) {
            case USERNAME_GROUP :
                TextView item = (TextView) convertView.findViewById(R.id.groupnames);
                item.setText("Change name (getGroupView)");
                break;
            case EVENTS_GROUP:
                TextView item2 = (TextView) convertView.findViewById(R.id.groupnames);
                item2.setText("Add/Delete Events (getGroupView)");
                break;
            case ABOUT_GROUP:
                TextView item3 = (TextView) convertView.findViewById(R.id.groupnames);
                item3.setText("About me (getGroupView)");
                break;
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        Integer childType = getChildType(groupPosition, childPosition);

        if (convertView == null || convertView.getTag() != childType) {
            switch (childType) {
                case EDITTEXT_CHILD_TYPE_0:
                    convertView = inflater.inflate(R.layout.settings_username, null);
                    convertView.setTag(childType);
                    break;
                case RV_CHILD_TYPE_1:
                    convertView = inflater.inflate(R.layout.settings_event, null);
                    convertView.setTag(childType);
                    break;
                case TEXT_CHILD_TYPE_2:
                    convertView = inflater.inflate(R.layout.settings_myself, null);
                    convertView.setTag(childType);
                    break;
                default:
                    break;
            }
        }
        switch (childType) {
            case EDITTEXT_CHILD_TYPE_0:
                mUserName = (EditText) convertView.findViewById(R.id.user_name);
                Toast.makeText(mContext,"Input is "+mUserName.getText().toString(),Toast.LENGTH_SHORT).show();
                break;
            case RV_CHILD_TYPE_1:
                Button addCustomEvent = (Button) convertView.findViewById(R.id.add_custom_event);
                View finalConvertView = convertView;
                addCustomEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAndSetNewEvent(finalConvertView);
                    }
                });
                break;
            case TEXT_CHILD_TYPE_2:
                //Define how to render the data on the CHILD_TYPE_3 layout
                TextView item3 = (TextView) convertView.findViewById(R.id.textView);
                break;
        }
        putNewEventGlobal();

        return convertView;
    }

    private void putNewEventGlobal() {
        if(mUserName!=null){
            if (mUserName.getText().toString().length() > 1) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(SEND_USERNAME, mUserName.getText().toString());
                editor.apply();
            }
        }
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case USERNAME_GROUP:
                switch (childPosition) {
                    case 0:
                        return EDITTEXT_CHILD_TYPE_0;

                }
                break;
            case EVENTS_GROUP:
                switch (childPosition) {
                    case 0:
                        return RV_CHILD_TYPE_1;
                }
                break;
            case ABOUT_GROUP:
                switch (childPosition) {
                    case 0:
                        return TEXT_CHILD_TYPE_2;

                }
                break;
        }
        return TEXT_CHILD_TYPE_2;
    }

    @Override
    public int getGroupType(int groupPosition) {
        switch (groupPosition) {
            case 0:
                return USERNAME_GROUP;
            case 1:
                return EVENTS_GROUP;
            case 2:
                return ABOUT_GROUP;
        }
        return ABOUT_GROUP;
    }

    @Override
    public int getChildTypeCount() {
        return 3;
    }

    @Override
    public int getGroupTypeCount() {
        return super.getGroupTypeCount();
    }

    private void getAndSetNewEvent(View finalConvertView) {
        final String[] toBeReturnedEvent = new String[1];
        AlertDialog.Builder inputEventDialog = new AlertDialog.Builder(mContext);
        inputEventDialog.setTitle("New Custom Event");
        final EditText input = new EditText(mContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_NORMAL);
        inputEventDialog.setView(input);

        inputEventDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                saveNewEvent(input);
                toBeReturnedEvent[0] = input.getText().toString();
                if(toBeReturnedEvent[0].length()>1)
                    addNewEvent(toBeReturnedEvent[0], finalConvertView);
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
    private void addNewEvent(String newEvent, View finalConvertView) {
        TextView mTextView = newTextAppearanceSettings(newEvent);
        LinearLayout mLLayout = (LinearLayout) finalConvertView.findViewById(R.id.settings_events);
        mLLayout.addView(mTextView);
    }

    private TextView newTextAppearanceSettings(String newEvent) {
        TextView mTextView = new TextView(mContext);
        mTextView.setText(newEvent);
        mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        mTextView.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(pixelFromDP(8f),pixelFromDP(32f),0,0);
        mTextView.setLayoutParams(params);
        return mTextView;
    }
    public int pixelFromDP(float dip) {
        Resources r = mContext.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }

    private void saveNewEvent(EditText input) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        String currentEvents = prefs.getString(Introduction.APPLICABLE_EVENTS, "");
        List<String> wordList = new ArrayList<String>(Arrays.asList(currentEvents.split(",")));
        wordList.add(input.getText().toString());
        editor.putString(Introduction.APPLICABLE_EVENTS,stringListToString(wordList));
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