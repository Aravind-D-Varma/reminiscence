package com.example.nostalgia;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.nostalgia.Introduction.SEND_USERNAME;
import static com.example.nostalgia.MemoryFragment.CURRENT_MEMORY;

public class SettingsAdapter extends BaseExpandableListAdapter{

    private static final int EDITTEXT_CHILD_TYPE_0 = 0;
    private static final int RV_CHILD_TYPE_1 = 1;
    private static final int TEXT_CHILD_TYPE_2 = 2;

    // 3 Group types
    private static final int USERNAME_GROUP = 0;
    private static final int EVENTS_GROUP = 1;
    private static final int  ABOUT_GROUP = 2;

    private final Context mContext;
    private EditText mUserName;

    public SettingsAdapter(Context context){
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
                //Define how to render the data on the CHILD_TYPE_2 layout
                /*TextView item2 = (TextView) convertView.findViewById(R.id.textView);
                item2.setText("texview of EVENTS_GROUP in getChildView");*/
                break;
            case TEXT_CHILD_TYPE_2:
                //Define how to render the data on the CHILD_TYPE_3 layout
                TextView item3 = (TextView) convertView.findViewById(R.id.textView);
                break;
        }
        if(mUserName!=null){
            if (mUserName.getText().toString().length() > 1) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(SEND_USERNAME, mUserName.getText().toString());
                editor.apply();
            }
        }
        return convertView;
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

}