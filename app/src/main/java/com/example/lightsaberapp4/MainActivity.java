package com.example.lightsaberapp4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener, SensorEventListener {

    private static final int PRIORITY_HIGH = 2;
    private static final int PRIORITY_LOW = 1;
    private static final int PLAY_ONCE = 0;
    private static final int LOOP = -1;
    private static final float RATE_NORMAL = 1f;

    private SensorManager sensorManager;
    private Sensor accSensor;

    private SoundPool soundPool;

    private int soundSwing;
    private int soundHit;
    private int soundOn;
    private int soundOff;

    float[] gravityVals;
    float[] geomagneticVals;
    float orientation[];

    private boolean saberOn = false;

    private boolean hitSoundPlaying;
    private boolean swingSoundPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // the priority is supposed to determine what sound plays over another but the api says this is for future implementations
        // the priority does work in the current version of android
        // another issue is that due to this some sounds will play multiple times because there is no way to determine when
        // a sound has finished playing
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(this);
        soundSwing = soundPool.load(this, R.raw.saberswing, 1);
        soundHit = soundPool.load(this, R.raw.sabercrash, 2);
        soundOn = soundPool.load(this, R.raw.saberon, 4);
        soundOff = soundPool.load(this, R.raw.saberoff, 4);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        findViewById(R.id.crash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundHit, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
            }
        });

        findViewById(R.id.enable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The saber turns on and off based on orientation... refere to onSensorChanged() for further detail
                if (!saberOn && orientation != null && Math.toDegrees(orientation[1]) < -70) {
                    soundPool.play(soundOn, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
                    saberOn = true;
                    ((TextView) view).setText("Turn off");
                }
                else if (saberOn && orientation != null && Math.toDegrees(orientation[1]) > 30) {
                    soundPool.play(soundOff, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
                    saberOn = false;
                    ((TextView) view).setText("Turn on");
                }

            }
        });

        findViewById(R.id.Swing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundSwing, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
            }
        });
    }

    private double prevAcc = 0;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double accMag = 0;
        double accDirection = 0;
        // This part gets the device's current acceleration... if it has a high enough magnitude and jerk it should make a crash sound...
        // if it only has a high enough magnitude it will make a swing noise
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravityVals = sensorEvent.values;
            accMag = Math.sqrt(Math.pow(sensorEvent.values[0], 2) + Math.pow(sensorEvent.values[1], 2) + Math.pow(sensorEvent.values[2], 2));
            accDirection = sensorEvent.values[0] + sensorEvent.values[1] + sensorEvent.values[2];

//            if (accDirection < 0 && accMag > prevAcc) {
//                ((TextView) findViewById(R.id.accMag)).setText(accDirection + " | " + (accMag - prevAcc));
//                prevAcc = accMag;
//            }


            if (saberOn && accMag - prevAcc > 30 && accMag > 25) {
                soundPool.stop(soundSwing);
                soundPool.play(soundHit, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
                hitSoundPlaying = true;
                ((TextView) findViewById(R.id.accMag)).setText("Crash\n" + accMag + " | " + (accMag - prevAcc));

            }
            else if (saberOn && accMag > 20 && !hitSoundPlaying) {
                ((TextView) findViewById(R.id.accMag)).setText("Swing\n" + accMag + " | " + (accMag - prevAcc));

                soundPool.play(soundSwing, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
            }
        }

        if (accMag < 10)
            hitSoundPlaying = false;

        // the magnetic field sensor is important for getting the device's orientation
        // the api uses the gravitational acceleration matrix and magnetic field matrix to determine this
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagneticVals = sensorEvent.values;
        }
        if (gravityVals != null && geomagneticVals != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            orientation = new float[3];
            SensorManager.getRotationMatrix(R, null, gravityVals, geomagneticVals);
            SensorManager.getOrientation(R, orientation);
            //System.out.print(orientation[0] + " | " + orientation[1] + " | " + orientation[2]);
        }
//        if (orientation != null)
//            ((TextView) findViewById(R.id.accMag)).setText(Math.toDegrees(orientation[0]) + " | " + Math.toDegrees(orientation[1]) + " | " + Math.toDegrees(orientation[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int i, int i1) {

    }
}
