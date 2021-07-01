package my.project.nostalgia.supplementary;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.navigation.NavigationView;

import my.project.nostalgia.R;

public class changeTheme {
    private final String mTheme;
    private final Context mContext;
    public changeTheme(Context context){
        SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
        this.mTheme = getData.getString("GlobalTheme", "Dark");
    }

    public void setUserTheme() {
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

    public void setLayoutTheme(View v){
        if (mTheme.equals("Light")) {
            if(v instanceof Spinner) {
                ((Spinner)v).setBackground(ContextCompat.getDrawable(mContext, R.drawable.button_border_light));
                ((TextView)((Spinner) v).getChildAt(0)).setTextColor(mContext.getResources()
                        .getColor(R.color.white));
            }
            else {
                v.setBackground(ContextCompat.getDrawable(mContext, R.drawable.layout_border_light));

                final Button share = (Button) v.findViewById(R.id.cardview_share);
                share.setTextColor(mContext.getResources().getColor(R.color.black));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    ColorStateList csl = ColorStateList.valueOf(
                            mContext.getResources().getColor(R.color.black, mContext.getTheme()));
                    share.setCompoundDrawableTintList(csl);
                }
            }
        }
        else if (mTheme.equals("Dark")) {
            if(!(v instanceof Spinner))
                v.setBackground(ContextCompat.getDrawable(mContext, R.drawable.layout_border));
        }
    }
    public int setDialogTheme() {
        if (mTheme.equals("Dark"))
            return R.style.Theme_AppCompat_DayNight_Dialog_Alert;
        else
            return R.style.Theme_AppCompat_Light_Dialog_Alert;
    }
}
