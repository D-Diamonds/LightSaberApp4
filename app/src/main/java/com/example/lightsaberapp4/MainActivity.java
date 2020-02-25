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

    private boolean saberOn;

    private boolean hitSoundPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double accMag = 0;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravityVals = sensorEvent.values;
            accMag = Math.sqrt(Math.pow(sensorEvent.values[0], 2) + Math.pow(sensorEvent.values[1], 2) + Math.pow(sensorEvent.values[2], 2));
            if (saberOn && accMag > 20 && !hitSoundPlaying) {
                System.out.println(accMag);
                soundPool.play(soundSwing, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
                hitSoundPlaying = true;
            } else
                hitSoundPlaying = false;
        }

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
        if (orientation != null)
            ((TextView) findViewById(R.id.accMag)).setText(Math.toDegrees(orientation[0]) + " | " + Math.toDegrees(orientation[1]) + " | " + Math.toDegrees(orientation[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int i, int i1) {

    }
}
