package com.example.nostalgia;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Launcherclass extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.LaunchTheme);
        Intent intent = new Intent(Launcherclass.this, MemoryListActivity.class);
        startActivity(intent);
        finish();
    }
}
