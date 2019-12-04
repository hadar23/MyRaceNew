package com.example.myracenew;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {
    private Button house;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        house=findViewById(R.id.house_end);

        TextView scoreView = findViewById(R.id.score_text);
        Intent intent = getIntent();
        scoreView.setText("YOUR SCORE: " + intent.getIntExtra("score",0));


        house.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startActivityIntent = new Intent(EndActivity.this, StartActivity.class);
                startActivity(startActivityIntent);
                EndActivity.this.finish();

            }
        });
    }
    public void onClickStartAgain(View view) {
        Intent gameActivityIntent = new Intent(EndActivity.this, GameActivity.class);
        startActivity(gameActivityIntent);
        EndActivity.this.finish();
    }
}
