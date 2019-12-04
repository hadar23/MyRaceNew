package com.example.myracenew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class StartActivity extends AppCompatActivity {
    ToggleButton arrowsSensorsToggle;
    private boolean useArrows = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        // initiate toggle button's
        arrowsSensorsToggle = (ToggleButton) findViewById(R.id.arrowsSensorsToggle);
    }

    public void onClickStart(View view) {
        Intent gameActivityIntent = new Intent(StartActivity.this, GameActivity.class);
        useArrows = arrowsSensorsToggle.isChecked();
        gameActivityIntent.putExtra("useArrows", useArrows);
        startActivity(gameActivityIntent);
        StartActivity.this.finish();
    }
}
