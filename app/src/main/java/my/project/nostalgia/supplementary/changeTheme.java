package my.project.nostalgia.supplementary;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.navigation.NavigationView;

import my.project.nostalgia.R;

public class changeTheme {
    private String mTheme;
    private Context mContext;
    public changeTheme(Context context){
        SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
        this.mTheme = getData.getString("GlobalTheme", "Dark");
    }

    public void setUserTheme() {
        //TODO fix add memory button in light theme
        if (mTheme.equals("Dark")){
            mContext.setTheme(R.style.Theme_Reminiscence);
        }
        else
            mContext.setTheme(R.style.Theme_Reminiscence_Light);
    }
    public void colorToNavigationIcons(NavigationView nV) {
        int colorInt;
        if (mTheme.equals("Dark"))
            colorInt = mContext.getResources().getColor(R.color.white);
        else
            colorInt = mContext.getResources().getColor(R.color.black);

        ColorStateList csl = ColorStateList.valueOf(colorInt);
        nV.setItemIconTintList(csl);
    }
    public void setNavigationHeaderTheme(View headerView, TextView headerText) {

        if (mTheme.equals("Dark")) {
            headerView.setBackgroundColor(mContext.getResources().getColor(R.color.medium_black));
        }
        if (mTheme.equals("Light")){
            headerView.setBackgroundColor(mContext.getResources().getColor(R.color.dark_purple));
            headerText.setTextColor(mContext.getResources().getColor(R.color.white));
        }
    }

    public void colorMemoryIcon(@NonNull Menu menu) {
        if (mTheme.equals("Dark")){
            menu.findItem(R.id.delete_memory).setIcon(R.drawable.delete_purple);
            menu.findItem(R.id.share_memory).setIcon(R.drawable.share_purple);
        }
    }
    public void setBackgroundTheme(View v) {
        try {
            if (mTheme.equals("Dark")) {
                v.setBackgroundResource(R.drawable.button_border);
                if (v instanceof Button)
                    ((Button) v).setTextColor(colorFromResources(R.color.light_purple));
                else if (v instanceof EditText){
                    ((EditText) v).setTextColor(colorFromResources(R.color.white));
                }
            } else if (mTheme.equals("Light")) {
                v.setBackgroundResource(R.drawable.button_border_light);
                if (v instanceof Button)
                    ((Button) v).setTextColor(colorFromResources(R.color.white));
                else if (v instanceof Spinner) {
                    TextView oTextView = (TextView) ((Spinner)v).getChildAt(0);
                    oTextView.setTextColor(colorFromResources(R.color.white));
                }
            }
        }
        catch (NullPointerException ignored){}
    }
    private int colorFromResources(int resourceID) {
        return mContext.getResources().getColor(resourceID);
    }

    public void setLayoutTheme(View v){
        if (mTheme.equals("Light"))
            v.setBackground(ContextCompat.getDrawable(mContext,R.drawable.layout_border_light));
        else if (mTheme.equals("Dark"))
            v.setBackground(ContextCompat.getDrawable(mContext,R.drawable.layout_border));
    }
    public void setTextTheme(TextView tv) {
        if (mTheme.equals("Light"))
            tv.setTextColor(mContext.getResources().getColor(R.color.black));
        else if (mTheme.equals("Dark"))
            tv.setTextColor(mContext.getResources().getColor(R.color.light_purple));

    }
}
