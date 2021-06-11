package com.example.nostalgia;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity to show logo everytime the app opens
 */
public class LauncherClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.LaunchTheme);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.black));
        SystemClock.sleep(1000);
        Intent intent = new Intent(LauncherClassActivity.this, IntroductionActivity.class);
        startActivity(intent);
        finish();
    }
}
