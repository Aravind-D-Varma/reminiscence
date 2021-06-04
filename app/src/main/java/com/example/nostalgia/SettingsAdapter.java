package com.example.nostalgia;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.TextView;

public class SettingsAdapter extends BaseExpandableListAdapter{

    private static final int TEXT_CHILD_TYPE_1 = 0;
    private static final int EDITTEXT_CHILD_TYPE_2 = 1;
    private static final int RV_CHILD_TYPE_3 = 2;
    private static final int CHILD_TYPE_UNDEFINED = 3;

    // 3 Group types
    private static final int USERNAME_GROUP = 0;
    private static final int EVENTS_GROUP = 1;
    private static final int  ABOUT_GROUP = 2;
    private final Context mContext;

    public SettingsAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getGroupCount() {
        return 3;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 4;
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

        if (convertView == null || convertView.getTag() != groupType) {
            switch (groupType) {
                case USERNAME_GROUP :
                    convertView = inflater.inflate(R.layout.settings_username, null);
                    break;
                case EVENTS_GROUP:
                    // Am using the same layout cause am lasy and don't wanna create other ones but theses should be different
                    // or the group type shouldnt exist
                    convertView = inflater.inflate(R.layout.settings_event, null);
                    break;
                case ABOUT_GROUP:
                    // Am using the same layout cause am lasy and don't wanna create other ones but theses should be different
                    // or the group type shouldnt exist
                    convertView = inflater.inflate(R.layout.settings_myself, null);
                    break;
                default:
                    // Maybe we should implement a default behaviour but it should be ok we know there are 3 group types right?
                    break;
            }
        }
        // We'll reuse the existing one
        else {
            // There is nothing to do here really we just need to set the content of view which we do in both cases
        }

        switch (groupType) {
            case USERNAME_GROUP :
                TextView item = (TextView) convertView.findViewById(R.id.textView);
                item.setText("Texview of USERNAME_GROUP in getGroupView");
                break;
            case EVENTS_GROUP:
                TextView item2 = (TextView) convertView.findViewById(R.id.textView);
                item2.setText("texview of EVENTS_GROUP in getGroupView");
                break;
            case ABOUT_GROUP:
                TextView item3 = (TextView) convertView.findViewById(R.id.textView);
                item3.setText("texview of ABOUT_GROUP in getGroupView");
                break;
            default:
                // Maybe we should implement a default behaviour but it should be ok we know there are 3 group types right?
                break;
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        Integer childType = getChildType(groupPosition, childPosition);

        // We need to create a new "cell container"
        if (convertView == null || convertView.getTag() != childType) {
            switch (childType) {
                case TEXT_CHILD_TYPE_1:
                    convertView = inflater.inflate(R.layout.settings_username, null);
                    convertView.setTag(childType);
                    break;
                case EDITTEXT_CHILD_TYPE_2:
                    convertView = inflater.inflate(R.layout.settings_myself, null);
                    convertView.setTag(childType);
                    break;
                case RV_CHILD_TYPE_3:
                    convertView = inflater.inflate(R.layout.settings_myself, null);
                    convertView.setTag(childType);
                    break;
                case CHILD_TYPE_UNDEFINED:
                    convertView = inflater.inflate(R.layout.settings_myself, null);
                    convertView.setTag(childType);
                    break;
                default:
                    // Maybe we should implement a default behaviour but it should be ok we know there are 4 child types right?
                    break;
            }
        }
        // We'll reuse the existing one
        else {
            // There is nothing to do here really we just need to set the content of view which we do in both cases
        }

        switch (childType) {
            case TEXT_CHILD_TYPE_1:
                TextView item = (TextView) convertView.findViewById(R.id.textView);
                item.setText("texview of USERNAME_GROUP in getChildView");
                break;
            case EDITTEXT_CHILD_TYPE_2:
                //Define how to render the data on the CHILD_TYPE_2 layout
                TextView item2 = (TextView) convertView.findViewById(R.id.textView);
                item2.setText("texview of EVENTS_GROUP in getChildView");
                break;
            case RV_CHILD_TYPE_3:
                //Define how to render the data on the CHILD_TYPE_3 layout
                TextView item3 = (TextView) convertView.findViewById(R.id.textView);
                item3.setText("texview of ABOUT_GROUP in getGroupView");
                break;
            case CHILD_TYPE_UNDEFINED:
                //Define how to render the data on the CHILD_TYPE_UNDEFINED layout
                break;
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
                        return TEXT_CHILD_TYPE_1;
                    case 1:
                        return EDITTEXT_CHILD_TYPE_2;
                    case 2:
                        return CHILD_TYPE_UNDEFINED;
                }
                break;
            case EVENTS_GROUP:
                switch (childPosition) {
                    case 0:
                        return TEXT_CHILD_TYPE_1;
                    case 1:
                        return RV_CHILD_TYPE_3;
                    case 2:
                        return CHILD_TYPE_UNDEFINED;
                }
                break;
            case ABOUT_GROUP:
                switch (childPosition) {
                    case 0:
                        return TEXT_CHILD_TYPE_1;
                    default:
                        return CHILD_TYPE_UNDEFINED;
                }
            default:
                return CHILD_TYPE_UNDEFINED;
        }
        return CHILD_TYPE_UNDEFINED;
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
        return super.getChildTypeCount();
    }

    @Override
    public int getGroupTypeCount() {
        return super.getGroupTypeCount();
    }

}
