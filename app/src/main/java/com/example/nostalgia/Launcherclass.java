package com.example.nostalgia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Launcherclass extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.LaunchTheme);
        SystemClock.sleep(1000);
        Intent intent = new Intent(Launcherclass.this, Introduction.class);
        startActivity(intent);
        finish();
    }
}
