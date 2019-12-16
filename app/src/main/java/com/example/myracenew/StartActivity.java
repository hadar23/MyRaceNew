package com.example.myracenew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {
    int carNumber = 0;

    private boolean useArrows = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void onClickSetting(View view) {
        Intent settingActivityIntent;
        settingActivityIntent = new Intent(StartActivity.this, SettingActivity.class);
        startActivity(settingActivityIntent);
        StartActivity.this.finish();
    }

    public void onClickStart(View view) {
        Intent gameIntent;
        gameIntent = new Intent(StartActivity.this, GameActivity.class);
        startActivity(gameIntent);
        StartActivity.this.finish();
    }
}
