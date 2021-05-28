package com.example.nostalgia;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class IntroPagerAdapter extends PagerAdapter {

    Context mContext;
    Activity mActivity;

    public IntroPagerAdapter(Context context,Activity activity){
        this.mContext = context;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.introduction,container,false);
        int resID = 0;
        switch(position){
            case 0:
                resID = R.id.welcome;
                LinearLayout welcomLL = v.findViewById(resID);
                if(welcomLL.getParent() != null) {
                    ((ViewGroup)welcomLL.getParent()).removeView(welcomLL); // <- fix
                }
                container.addView(welcomLL);
                return welcomLL;
            case 1:
                resID = R.id.questions;
                LinearLayout questionsLL = v.findViewById(resID);
                if(questionsLL.getParent() != null) {
                    ((ViewGroup)questionsLL.getParent()).removeView(questionsLL); // <- fix
                }
                container.addView(questionsLL);
                return questionsLL;
            case 2:
                resID = R.id.event_preferences;
                LinearLayout eventsLL = v.findViewById(resID);
                if(eventsLL.getParent() != null) {
                    ((ViewGroup)eventsLL.getParent()).removeView(eventsLL); // <- fix
                }
                container.addView(eventsLL);
                return eventsLL;

            case 3:
                resID = R.id.conclusion;
                LinearLayout conclusionLL = v.findViewById(resID);
                if(conclusionLL.getParent() != null) {
                    ((ViewGroup)conclusionLL.getParent()).removeView(conclusionLL); // <- fix
                }
                container.addView(conclusionLL);
                return conclusionLL;
        }
        return v;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }
}
