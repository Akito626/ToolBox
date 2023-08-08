package com.alha_app.toolbox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Objects;

public class CompassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorEventListener sensorEventListener = new SensorEventListener() {
            float[] accelerometerValues = new float[3];
            float[] magnetometerValues = new float[3];

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor == accelerometer) {
                    accelerometerValues = event.values;
                } else if (event.sensor == magnetometer) {
                    magnetometerValues = event.values;
                }

                float[] rotationMatrix = new float[9];
                float[] orientationValues = new float[3];
                SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues);
                SensorManager.getOrientation(rotationMatrix, orientationValues);

                float degree = (float) Math.toDegrees(orientationValues[0]);
                if(degree < 0){
                    degree += 360;
                }
                updateCompass(degree);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // 何もしない
            }
        };

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuButton){
        boolean result = true;
        int buttonId = menuButton.getItemId();
        switch(buttonId){
            //戻るボタンが押されたとき
            case android.R.id.home:
                //画面を終了させる
                finish();
                break;
            //それ以外の時
            default:
                result = super.onOptionsItemSelected(menuButton);
                break;
        }
        return result;
    }

    private void updateCompass(float degree){
        String[] directions = {
                "北", "北北東", "北東", "東北東", "東", "東南東", "南東", "南南東",
                "南", "南南西", "南西", "西南西", "西", "西北西", "北西", "北北西", "北"
        };

        TextView azimuthText = findViewById(R.id.azimuth_text);
        TextView degreeText = findViewById(R.id.degree_text);
        degreeText.setText(String.format("%.1f", degree) + "度");

        int index = (int) ((degree + 11.25) % 360 / 22.5);
        azimuthText.setText(directions[index]);
    }
}