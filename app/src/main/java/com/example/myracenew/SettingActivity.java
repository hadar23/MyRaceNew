package com.example.myracenew;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class SettingActivity extends AppCompatActivity {
    private ToggleButton arrowsTB;
    private ToggleButton threelineTB;
    private ToggleButton fastTB;
    private ToggleButton vibrationTB;
    private ToggleButton musicTB;

    private RelativeLayout speedRelative;

    private ImageView[] cars = new ImageView[5];
    private int[] carsColors = new int[5];

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //find
        arrowsTB = findViewById(R.id.arrowsSensorsTB);
        threelineTB = findViewById(R.id.lineOptionsTB);
        fastTB = findViewById(R.id.speedOptionsTB);
        vibrationTB = findViewById(R.id.vibratinTB);
        musicTB = findViewById(R.id.musicTB);
        speedRelative = findViewById(R.id.speedLayer);

        arrowsTB.setChecked(Setting.isArrows);
        threelineTB.setChecked(Setting.isThreeLine);
        fastTB.setChecked(Setting.isFast);
        vibrationTB.setChecked(Setting.isVibration);
        musicTB.setChecked(Setting.isMusic);

        cars[0] = findViewById(R.id.car0);
        cars[1] = findViewById(R.id.car1);
        cars[2] = findViewById(R.id.car2);
        cars[3] = findViewById(R.id.car3);
        cars[4] = findViewById(R.id.car4);

        for (int i = 0; i < cars.length; i++){
            ColorDrawable viewColor = (ColorDrawable) cars[i].getBackground();
            carsColors[i] = viewColor.getColor();
        }

        hideShowSpeedLayout();
        setTags();
        colorBackground(Setting.carNumber);
    }

    public void clickToHome(View view) {
        Setting.isArrows = arrowsTB.isChecked();
        Setting.isFast = fastTB.isChecked();
        Setting.isMusic = musicTB.isChecked();
        Setting.isVibration = vibrationTB.isChecked();
        Setting.isThreeLine = threelineTB.isChecked();
        Intent startActivityIntent;
        startActivityIntent = new Intent(SettingActivity.this, StartActivity.class);
        startActivity(startActivityIntent);
        SettingActivity.this.finish();
    }

    public void movementClicked(View view){
        hideShowSpeedLayout();
    }

    public void hideShowSpeedLayout(){
        if(!arrowsTB.isChecked()) {
            Setting.isFast = false;
            fastTB.setChecked(false);
            speedRelative.setVisibility(View.INVISIBLE);
        }
        else {
            speedRelative.setVisibility(View.VISIBLE);
        }
    }

    public void setTags() {
        for (int i = 0; i < cars.length; i++) {
            cars[i].setTag(Setting.resourceCars[i]);
        }
    }

    public void clickToChangeCar(View car) {
        int chosenCarTag = (int) car.getTag();
        Setting.carNumber = chosenCarTag;
        colorBackground(chosenCarTag);
    }

    public void colorBackground(int chosenCarTag) {
        for (int i = 0; i < cars.length; i++) {
            if ((int) cars[i].getTag() != chosenCarTag)
                cars[i].setBackgroundColor(carsColors[i]);
            else
                cars[i].setBackgroundColor(0xFF00FF00);
        }
    }
}
