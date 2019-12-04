package com.example.myracenew;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView leftBtn;
    private ImageView rightBtn;
    private ImageView car;
    int stepCar = getScreenWidth() / 3;
    int mySum = 0;

    private ImageView bob1;
    private ImageView bob2;
    private ImageView bob3;
    private ImageView[] bobs = new ImageView[3];

    private ImageView heart1;
    private ImageView heart2;
    private ImageView heart3;
    private ImageView[] hearts = new ImageView[3];

    private ImageView fire;
    private ImageView house;

    private ImageView playPauseBTN;
    private boolean isStop = false;
    private boolean onPauseState = false;

    private MySignal signal;
    private Context context;

    ValueAnimator[] animations = new ValueAnimator[3];
    private Random random;

    int life = 2;
    private TextView txtScore;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;

    private boolean moveAgain = true;
    private boolean useArrows = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        context = this;
        random = new Random();

        // Setup the sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        leftBtn = findViewById(R.id.leftBTN);
        rightBtn = findViewById(R.id.rightBTN);
        car = findViewById(R.id.car);
        txtScore = findViewById(R.id.scoreText);
        bob1 = findViewById(R.id.bob1);
        bob2 = findViewById(R.id.bob2);
        bob3 = findViewById(R.id.bob3);
        house = findViewById(R.id.house);
        playPauseBTN = findViewById(R.id.playPauseBTN);

        bob1.setY(-200);
        bob2.setY(-200);
        bob3.setY(-200);

        bobs[0] = bob1;
        bobs[1] = bob2;
        bobs[2] = bob3;

        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);

        hearts[0] = heart1;
        hearts[1] = heart2;
        hearts[2] = heart3;

        fire = findViewById(R.id.fire);

        animations[0] = ValueAnimator.ofInt(-200, getScreenHeight());
        animations[1] = ValueAnimator.ofInt(-200, getScreenHeight());
        animations[2] = ValueAnimator.ofInt(-200, getScreenHeight());

        animatIt(bobs[0], animations[0], 1000);
        animatIt(bobs[1], animations[1], 2000);
        animatIt(bobs[2], animations[2], 3000);

        signal = new MySignal(this);
        scoreLoop();

        // move the car ic_left_arrow by press on the button
        leftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moveLeft();
            }
        });

        // move the car ic_right_arrow  by press on the button
        rightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moveRight();
            }
        });

        // go to home page
        house.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ValueAnimator animation : animations) {
                    animation.removeAllUpdateListeners();
                    animation.end();
                }

                Intent startActivityIntent = new Intent(GameActivity.this, StartActivity.class);
                startActivity(startActivityIntent);
            }
        });

        // stop the game
        playPauseBTN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStop) {
                    pauseApp();
                } else {
                    resumeApp();
                }
            }
        });
    }

    public void moveLeft() {
        float pos = (car.getX() - stepCar);
        if (pos > 0) {
            car.setX(pos);
            fire.setX(pos);
        }
    }

    public void moveRight() {
        float pos = (car.getX() + stepCar);
        if (pos < getScreenWidth()) {
            car.setX(pos);
            fire.setX(pos);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (!useArrows)
            sensorMode(event);
    }

    private void sensorMode(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                break;
            default:
                return;
        }
        if (mGravity == null) {
            return;
        }
        if (mGeomagnetic == null) {
            return;
        }
        float R[] = new float[9];
        if (!SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic)) {
            return;
        }

        float orientation[] = new float[9];
        SensorManager.getOrientation(R, orientation);
        // Orientation contains: azimuth, pitch and roll - we'll use roll
        float roll = orientation[2];
        int rollDeg = (int) Math.round(Math.toDegrees(roll));

        final long maxCounter = 200;
        long diff = 1000;
        CountDownTimer c = new CountDownTimer(maxCounter, diff) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                moveAgain = true;
            }
        };

        if (rollDeg > 20 && moveAgain) {
            moveRight();
            moveAgain = false;
            c.start();
        } else if (rollDeg < -20 && moveAgain) {
            moveLeft();
            moveAgain = false;
            c.start();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //get screen width
    private static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    //get screen hight
    private static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    //updating the score
    private void sumChange() {
        if (!isStop) {
            mySum += 10;
            String score = String.format(Locale.ENGLISH, "%d", mySum);
            txtScore.setText(score);
        }
    }

    //updating the score after hitting bob
    private void sumAdd(int add) {
        mySum += add;
        if (mySum < 0) {
            mySum = 0;
        }
        String score = String.format(Locale.ENGLISH, "%d", mySum);
        txtScore.setText(score);
    }

    public void pauseApp() {
        for (ValueAnimator animation : animations) {
            animation.pause();
        }
        leftBtn.setEnabled(false);
        rightBtn.setEnabled(false);
        playPauseBTN.setImageResource(R.drawable.ic_play);
        isStop = true;
    }

    public void resumeApp() {
        for (ValueAnimator animation : animations) {
            animation.resume();
        }
        leftBtn.setEnabled(true);
        rightBtn.setEnabled(true);
        playPauseBTN.setImageResource(R.drawable.ic_stop);
        isStop = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isStop) {
            onPauseState = true;
        } else {
            onPauseState = false;
        }
        pauseApp();
        sensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onPauseState) {
            return;
        }
        resumeApp();
        sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener((SensorEventListener) this, magnetometer, SensorManager.SENSOR_DELAY_UI);

    }

    //animation of the game
    private void animatIt(final ImageView bob, ValueAnimator animation, int delay) {
        animation.setDuration(3000).setRepeatCount(Animation.INFINITE);
        animation.setStartDelay(delay);
        animation.start();
        animation.setInterpolator(new LinearInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int newPosition = (int) animation.getAnimatedValue();
                bob.setTranslationY(newPosition);
                if (checkCollision(bob, car)) {
                    sumAdd(-30);
                    Toast.makeText(context, "score -30", Toast.LENGTH_SHORT).show();
                    if (!removeHeart()) {
                        gameOver();
                    }
                    bob.setTranslationY(-200);
                    fire.setVisibility(View.VISIBLE);
                    signal.vibrate(500);
                    animation.start();
                }
                checkEndOfRoad(bob, animation);
            }
        });
    }

    private void checkEndOfRoad(ImageView bob, ValueAnimator updatedAnimation) {
        if (bob.getY() > car.getY() + car.getHeight()) {
            updatedAnimation.start();
        }
    }

    //remove heart
    private boolean removeHeart() {
        if (life == 0) {
            return false;
        }
        hearts[life].setVisibility(View.INVISIBLE);
        life--;
        return true;
    }

    //loop that update the score, and check  the Fire
    private void scoreLoop() {
        final Handler handler = new Handler();
        Runnable myRun = new Runnable() {
            @Override
            public void run() {
                sumChange();
                checkFire();
                scoreLoop();
            }
        };
        handler.postDelayed(myRun, 400);
    }

    //check if fire visible or not
    private void checkFire() {
        if (fire.getVisibility() == View.VISIBLE) {
            fire.setVisibility(View.INVISIBLE);
        }
    }

    //check Collision between bob and car
    private boolean checkCollision(View bob, View car) {
        int[] locationBob = new int[2];
        bob.getLocationOnScreen(locationBob);
        int[] locationCar = new int[2];
        car.getLocationOnScreen(locationCar);

        Rect rect1 = new Rect(locationBob[0], locationBob[1], (int) (locationBob[0] +
                bob.getWidth()), (int) (locationBob[1] + bob.getHeight()));
        Rect rect2 = new Rect(locationCar[0], locationCar[1], (int) (locationCar[0] +
                car.getWidth()), (int) (locationCar[1] + car.getHeight()));
        return Rect.intersects(rect1, rect2);
    }

    // end the animation and move to differnt activity
    private void gameOver() {
        for (ValueAnimator animation : animations) {
            animation.removeAllUpdateListeners();
            animation.end();
        }
        Intent endActivityIntent = new Intent(GameActivity.this, EndActivity.class);
        endActivityIntent.putExtra("score", mySum);
        startActivity(endActivityIntent);
        GameActivity.this.finish();
    }
}