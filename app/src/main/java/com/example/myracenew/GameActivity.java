package com.example.myracenew;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.PropertyValuesHolder;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView leftBtn;
    private ImageView rightBtn;
    private ImageView car;
    private int numRoads = (Setting.isThreeLine ? 3 : 5);
    private int stepCar = getScreenWidth() / numRoads;
    private int mySum = 0;
    private int life = 2;
    private int speed = (Setting.isFast ? 2000 : 3000);

    private ImageView bob1;
    private ImageView bob2;
    private ImageView bob3;
    private ImageView bob4;
    private ImageView bob5;
    private ImageView[] bobs = new ImageView[5];

    private ImageView heart1;
    private ImageView heart2;
    private ImageView heart3;
    private ImageView[] hearts = new ImageView[3];

    private ImageView fire;
    private ImageView house;
    private ImageView dollar;

    private ImageView playPauseBTN;
    private boolean isStop = false;
    private boolean onPauseState = false;
    private boolean threeLines = true;

    private MySignal signal;
    private Context context;
    Random rand = new Random();

    private ValueAnimator[] animations = new ValueAnimator[6];
    private PropertyValuesHolder holder;
    private TextView txtScore;

    private SensorManager sensorManager;
    private Sensor sensor;

    private boolean moveAgain = true;
    private boolean useArrows = Setting.isArrows;
    private boolean localIsFast = Setting.isFast;
    private boolean[] objSpeed = new boolean[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        context = this;
        // chang the screen to 3 lines from 5 lines
        if (numRoads == 3) {
            findViewById(R.id.mainWindow).setBackgroundResource(R.drawable.grass);
            ((LinearLayout) findViewById(R.id.raceLevel)).setWeightSum(3f);
            findViewById(R.id.side4).setVisibility(View.GONE);
            findViewById(R.id.side5).setVisibility(View.GONE);
        }

        // adjust car image to the chosen one
        car = findViewById(R.id.car);
        car.setImageResource(Setting.carNumber);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        leftBtn = findViewById(R.id.leftBTN);
        rightBtn = findViewById(R.id.rightBTN);

        if (!useArrows) {
            hideArrows();
        }

        txtScore = findViewById(R.id.scoreText);
        bob1 = findViewById(R.id.bob1);
        bob2 = findViewById(R.id.bob2);
        bob3 = findViewById(R.id.bob3);
        bob4 = findViewById(R.id.bob4);
        bob5 = findViewById(R.id.bob5);
        dollar = findViewById(R.id.dollar);
        house = findViewById(R.id.house);
        playPauseBTN = findViewById(R.id.playPauseBTN);
        fire = findViewById(R.id.fire);

        bob1.setY(-200);
        bob2.setY(-200);
        bob3.setY(-200);
        bob4.setY(-200);
        bob5.setY(-200);
        dollar.setY(-200);

        bobs[0] = bob1;
        bobs[1] = bob2;
        bobs[2] = bob3;
        bobs[3] = bob4;
        bobs[4] = bob5;

        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);

        hearts[0] = heart1;
        hearts[1] = heart2;
        hearts[2] = heart3;

        for (int i = 0; i < 6; i++) {
            objSpeed[i] = localIsFast;
        }

        holder = PropertyValuesHolder.ofInt("scale",
                -300, getScreenHeight());
        for (int i = 0; i < 6; i++) {
            animations[i] = ValueAnimator.ofInt(-300, getScreenHeight());
            animations[i].setValues(holder);
        }

        //set up the animation for each value in animation array
        for (int i = 0; i < 5; i++) {
            animatIt(bobs[i], animations[i], (i + 1) * 1000, speed / 1, -30,
                    -300, 500, i / 1);
        }

        animatIt(dollar, animations[5], 6000, speed / 2, 30,
                -300, 150, 5);

        signal = new MySignal(this);
        scoreLoop();

        // move the car ic_left_arrow by press on the button
        leftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moveLeft(car);
            }
        });

        // move the car ic_right_arrow  by press on the button
        rightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moveRight(car);
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
                GameActivity.this.finish();
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

    public void moveLeft(View view) {
        float pos = (view.getX() - stepCar);
        if (pos > 0) {
            view.setX(pos);
            if (view.equals(car)) {
                fire.setX(pos);
            }
        }
    }

    public void moveRight(View view) {
        float pos = (view.getX() + stepCar);
        if (pos < getScreenWidth()) {
            view.setX(pos);
            if (view.equals(car)) {
                fire.setX(pos);
            }
        }
    }

    public void moveDollarLeftRight() {
        int random = rand.nextInt(3);
        if (random == 0)
            moveLeft(dollar);
        if (random == 2)
            moveRight(dollar);
    }

    public void onSensorChanged(SensorEvent event) {
        if (!useArrows)
            sensorMode(event);
    }

    private void sensorMode(SensorEvent event) {
        float xVal = 0;
        float yVal = 0;
        final long maxCounter = 200;
        long diff = 1000;
        CountDownTimer c = new CountDownTimer(maxCounter, diff) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                moveAgain = true;
            }
        };

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xVal = event.values[0];
            yVal = event.values[1];
        }

        if (xVal < -1.5 && moveAgain) {
            moveRight(car);
            moveAgain = false;
            c.start();
        } else if (xVal > 1.5 && moveAgain) {
            moveLeft(car);
            moveAgain = false;
            c.start();
        }

        if (yVal < 1.5) {
            localIsFast = true;
        } else {
            localIsFast = false;
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
        sensorManager.registerListener((SensorEventListener) this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void resetAnimation(ValueAnimator animation, int animationDuration, int delay) {
        animation.setDuration(animationDuration).setRepeatCount(Animation.INFINITE);
        if(!Setting.isArrows)
            animation.setDuration((long) (localIsFast ? animationDuration * 0.5 :
                    animationDuration));
        animation.setStartDelay(delay);
        animation.setValues(holder);
        animation.start();
    }

    //animation of the game
    private void animatIt(final ImageView obj, ValueAnimator animation, final int delay,
                          final int animationDuration, final int addedScore, final int initialPlace,
                          final int vibrationDuration, final int index) {
        final double velocity = ((getScreenHeight() + 300) * 1.0) / animationDuration;
        obj.setTranslationY(initialPlace);
        animation.setDuration(animationDuration).setRepeatCount(Animation.INFINITE);
        animation.setStartDelay(delay);
        animation.start();
        animation.setInterpolator(new LinearInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int newPosition = (int) animation.getAnimatedValue("scale");
                obj.setTranslationY(newPosition);
                if (localIsFast != objSpeed[index]) {
                    animation.cancel();
                    PropertyValuesHolder newHolder = PropertyValuesHolder.ofInt("scale",
                            newPosition, getScreenHeight());
                    double newDuration = (getScreenHeight() - newPosition) / velocity;
                    animation.setValues(newHolder);
                    animation.setStartDelay(0);
                    animation.setDuration((long) (localIsFast ? newDuration * 0.5 :
                            newDuration));
                    objSpeed[index] = localIsFast;
                    animation.start();
                }
                if (checkCollision(obj, car)) {
                    sumAdd(addedScore);
                    Toast.makeText(context, "score " + (addedScore > 0 ? "+" : "-")
                            + addedScore, Toast.LENGTH_SHORT).show();
                    if (obj != dollar && !removeHeart()) {
                        gameOver();
                    }
                    obj.setTranslationY(initialPlace);
                    if (!obj.equals(dollar))
                        fire.setVisibility(View.VISIBLE);
                    else
                        moveDollarLeftRight();
                    if (Setting.isVibration) {
                        signal.vibrate(vibrationDuration);
                    }
                    animation.start();
                }
                checkEndOfRoad(obj, animation, animationDuration, delay);
            }
        });
    }

    private void checkEndOfRoad(ImageView obj, ValueAnimator updatedAnimation, int animationDuration, int delay) {
        if (obj.getY() > car.getY() + car.getHeight()) {
            resetAnimation(updatedAnimation, animationDuration, delay);
            if (obj.equals(dollar))
                moveDollarLeftRight();
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
        // stop all animations
        for (ValueAnimator animation : animations) {
            animation.removeAllUpdateListeners();
            animation.end();
        }

        // create new endIntent
        Intent endActivityIntent = new Intent(GameActivity.this, EndActivity.class);
        endActivityIntent.putExtra("score", mySum);
        endActivityIntent.putExtra("useArrows", useArrows);
        endActivityIntent.putExtra("threeLines", threeLines);
        startActivity(endActivityIntent);
        GameActivity.this.finish();
    }

    private void hideArrows() {
        leftBtn.setVisibility(View.INVISIBLE);
        rightBtn.setVisibility(View.INVISIBLE);
    }
}