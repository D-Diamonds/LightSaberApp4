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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(this);
        soundSwing = soundPool.load(this, R.raw.saberswing, 1);
        soundHit = soundPool.load(this, R.raw.sabercrash, 1);
        soundOn = soundPool.load(this, R.raw.saberon, 1);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);


        findViewById(R.id.crash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundHit, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
            }
        });

        findViewById(R.id.enable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundOn, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
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
            accMag = Math.sqrt(Math.pow(sensorEvent.values[0], 2) + Math.pow(sensorEvent.values[1], 2) + Math.pow(sensorEvent.values[2], 2));
        }
        if (accMag > 10) {
            System.out.println(accMag);
            ((TextView) findViewById(R.id.accMag)).setText(Double.toString(accMag));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int i, int i1) {

    }
}
