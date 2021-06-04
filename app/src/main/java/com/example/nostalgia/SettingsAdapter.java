package com.example.nostalgia;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public class SettingsAdapter extends BaseExpandableListAdapter {

    private static final int TEXT_CHILD_TYPE_1 = 0;
    private static final int EDITTEXT_CHILD_TYPE_2 = 1;
    private static final int RV_CHILD_TYPE_3 = 2;
    private static final int CHILD_TYPE_UNDEFINED = 3;

    // 3 Group types
    private static final int USERNAME_GROUP = 0;
    private static final int EVENTS_GROUP = 1;
    private static final int  ABOUT_GROUP = 2;
    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
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
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return super.getChildType(groupPosition, childPosition);
    }

    @Override
    public int getGroupType(int groupPosition) {
        return super.getGroupType(groupPosition);
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
