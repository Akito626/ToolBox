package com.alha_app.toolbox;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.NumberPicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class TimerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        NumberPicker numberPicker1 = findViewById(R.id.numberPiker1);
        NumberPicker numberPicker2 = findViewById(R.id.numberPiker2);
        NumberPicker numberPicker3 = findViewById(R.id.numberPiker3);
        String hours[] = new String[100];
        String minutes[] = new String[60];
        String seconds[] = new String[60];

        for(int i = 0; i < hours.length; i++){
            if(i < minutes.length){
                if(i < 10){
                    minutes[i] = "0" + String.valueOf(i);
                    seconds[i] = "0" + String.valueOf(i);
                }else{
                    minutes[i] = String.valueOf(i);
                    seconds[i] = String.valueOf(i);
                }
            }

            if(i < 10){
                hours[i] = "0" + String.valueOf(i);
            }else{
                hours[i] = String.valueOf(i);
            }
        }
        numberPicker1.setMaxValue(99);
        numberPicker1.setMinValue(0);
        numberPicker2.setMaxValue(59);
        numberPicker2.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setMinValue(0);

        numberPicker1.setDisplayedValues(hours);
        numberPicker2.setDisplayedValues(minutes);
        numberPicker3.setDisplayedValues(seconds);
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
}
