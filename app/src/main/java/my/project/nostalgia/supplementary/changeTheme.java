package my.project.nostalgia.supplementary;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
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

    public void setLayoutTheme(View v){
        if (mTheme.equals("Light"))
            v.setBackground(ContextCompat.getDrawable(mContext,R.drawable.layout_border_light));
        else if (mTheme.equals("Dark"))
            v.setBackground(ContextCompat.getDrawable(mContext,R.drawable.layout_border));
    }
    public int setDialogTheme() {
        //TODO fix add memory button in light theme
        if (mTheme.equals("Dark")){
            return R.style.DarkDialog;
        }
        else
            return R.style.LightDialog;
    }
}
